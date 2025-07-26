package com.example.shopmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopmate.data.model.Banner;
import com.example.shopmate.data.model.Category;
import com.example.shopmate.data.model.Product;
import com.example.shopmate.data.repository.CategoryRepository;
import com.example.shopmate.data.repository.ProductRepository;
import com.example.shopmate.data.repository.SearchRepository;
import com.example.shopmate.data.response.ProductStatsResponse;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SearchRepository searchRepository;
    
    private final MediatorLiveData<Boolean> isLoading = new MediatorLiveData<>();
    private final MediatorLiveData<String> errorMessage = new MediatorLiveData<>();
    
    private LiveData<List<Product>> allProducts;
    private LiveData<List<Category>> categories;
    private final MutableLiveData<List<Product>> featuredProducts = new MutableLiveData<>();
    private final MutableLiveData<List<Banner>> banners = new MutableLiveData<>();
    
    public HomeViewModel() {
        productRepository = new ProductRepository();
        categoryRepository = new CategoryRepository();
        searchRepository = new SearchRepository();
        
        // Initialize loading state
        isLoading.setValue(false);
        
        // Set up data sources
        allProducts = productRepository.getProducts();
        categories = categoryRepository.getCategories();
        
        // Load banners and featured products
        loadBanners();
        loadFeaturedProducts();
        
        // Monitor loading states
        isLoading.addSource(productRepository.getIsLoading(), loading -> {
            if (loading != null && loading) {
                isLoading.setValue(true);
            } else {
                checkAndUpdateLoadingState();
            }
        });
        
        isLoading.addSource(categoryRepository.getIsLoading(), loading -> {
            if (loading != null && loading) {
                isLoading.setValue(true);
            } else {
                checkAndUpdateLoadingState();
            }
        });
        
        // Monitor error messages
        errorMessage.addSource(productRepository.getErrorMessage(), message -> {
            if (message != null && !message.isEmpty()) {
                errorMessage.setValue(message);
            }
        });
        
        errorMessage.addSource(categoryRepository.getErrorMessage(), message -> {
            if (message != null && !message.isEmpty()) {
                errorMessage.setValue(message);
            }
        });
    }
    
    private void loadBanners() {
        // In a real app, you would fetch this from an API
        // For now, we'll create some sample banners with placeholder image URLs
        List<Banner> sampleBanners = new ArrayList<>();
        sampleBanners.add(new Banner(
                "1", 
                "Summer Sale", 
                "Up to 50% off on all products", 
                "https://images.unsplash.com/photo-1607082350899-7e105aa886ae?q=80&w=2070&auto=format&fit=crop",
                "/summer-sale"
        ));
        sampleBanners.add(new Banner(
                "2", 
                "New Arrivals", 
                "Check out our latest collection", 
                "https://images.unsplash.com/photo-1607083206968-13611e3d76db?q=80&w=2115&auto=format&fit=crop",
                "/new-arrivals"
        ));
        sampleBanners.add(new Banner(
                "3", 
                "Special Offers", 
                "Limited time deals just for you", 
                "https://images.unsplash.com/photo-1607083208143-e3650c650048?q=80&w=2070&auto=format&fit=crop",
                "/special-offers"
        ));
        
        banners.setValue(sampleBanners);
    }
    
    private void loadFeaturedProducts() {
        // Load most ordered products for featured section
        searchRepository.getMostOrderedProducts(6);
        
        // Observe most ordered products and convert to Product list
        searchRepository.getMostOrderedProducts().observeForever(productStats -> {
            if (productStats != null && !productStats.isEmpty()) {
                List<Product> products = new ArrayList<>();
                for (ProductStatsResponse stats : productStats) {
                    Product product = new Product();
                    product.setId(stats.getProductId());
                    product.setProductName(stats.getProductName());
                    product.setImageURL(stats.getProductImage());
                    product.setPrice(stats.getPrice());
                    product.setCategoryName(stats.getCategory());
                    // Set totalOrdered from API response
                    product.setTotalOrdered(stats.getTotalOrdered());
                    products.add(product);
                }
                featuredProducts.setValue(products);
            }
        });
    }
    
    private void checkAndUpdateLoadingState() {
        Boolean productsLoading = productRepository.getIsLoading().getValue();
        Boolean categoriesLoading = categoryRepository.getIsLoading().getValue();
        
        if ((productsLoading == null || !productsLoading) && 
            (categoriesLoading == null || !categoriesLoading)) {
            isLoading.setValue(false);
        }
    }
    
    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
    }
    
    public LiveData<List<Product>> getFeaturedProducts() {
        return featuredProducts;
    }
    
    public LiveData<List<Category>> getCategories() {
        return categories;
    }
    
    public LiveData<List<Banner>> getBanners() {
        return banners;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
