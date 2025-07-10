package com.example.shopmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopmate.model.Banner;
import com.example.shopmate.model.Category;
import com.example.shopmate.model.Product;
import com.example.shopmate.repository.CategoryRepository;
import com.example.shopmate.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    private final MediatorLiveData<Boolean> isLoading = new MediatorLiveData<>();
    private final MediatorLiveData<String> errorMessage = new MediatorLiveData<>();
    
    private LiveData<List<Product>> allProducts;
    private LiveData<List<Category>> categories;
    private final MutableLiveData<List<Product>> featuredProducts = new MutableLiveData<>();
    private final MutableLiveData<List<Banner>> banners = new MutableLiveData<>();
    
    public HomeViewModel() {
        productRepository = new ProductRepository();
        categoryRepository = new CategoryRepository();
        
        // Initialize loading state
        isLoading.setValue(false);
        
        // Set up data sources
        allProducts = productRepository.getProducts();
        categories = categoryRepository.getCategories();
        
        // Load banners
        loadBanners();
        
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