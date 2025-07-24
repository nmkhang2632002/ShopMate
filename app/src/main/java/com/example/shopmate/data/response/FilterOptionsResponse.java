package com.example.shopmate.data.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FilterOptionsResponse {
    @SerializedName("priceRanges")
    private List<PriceRange> priceRanges;
    
    @SerializedName("sortOptions")
    private List<SortOption> sortOptions;

    public List<PriceRange> getPriceRanges() {
        return priceRanges;
    }

    public void setPriceRanges(List<PriceRange> priceRanges) {
        this.priceRanges = priceRanges;
    }

    public List<SortOption> getSortOptions() {
        return sortOptions;
    }

    public void setSortOptions(List<SortOption> sortOptions) {
        this.sortOptions = sortOptions;
    }

    public static class PriceRange {
        @SerializedName("value")
        private String value;
        
        @SerializedName("label")
        private String label;
        
        @SerializedName("minPrice")
        private String minPrice;
        
        @SerializedName("maxPrice")
        private String maxPrice;

        // Getters and setters
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getMinPrice() {
            return minPrice;
        }

        public void setMinPrice(String minPrice) {
            this.minPrice = minPrice;
        }

        public String getMaxPrice() {
            return maxPrice;
        }

        public void setMaxPrice(String maxPrice) {
            this.maxPrice = maxPrice;
        }
        
        public String getDisplayName() {
            return label != null ? label : (value != null ? value : "Price Range");
        }
        
        public Double getMinPriceAsDouble() {
            try {
                return minPrice != null ? Double.parseDouble(minPrice) : null;
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        public Double getMaxPriceAsDouble() {
            try {
                return maxPrice != null ? Double.parseDouble(maxPrice) : null;
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    public static class SortOption {
        @SerializedName("value")
        private String value;
        
        @SerializedName("label")
        private String label;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}
