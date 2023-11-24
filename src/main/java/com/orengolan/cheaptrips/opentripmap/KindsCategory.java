package com.orengolan.cheaptrips.opentripmap;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KindsCategory {

    private List<Category> categories;

    public KindsCategory() {
        this.categories = new ArrayList<>();
    }

    public List<Category> getCategories() {
        return categories;
    }
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
    public Category getCategory(@NotNull String name){
        for(Category category : categories){
            if (category.getName().equals(name)){
                return category;
            }
        }
        return new Category(name);
    }

    public static class Category {
        private String name;
        private List<PlaceDetails> places;

        public Category(String name) {
            if(this.categoryValidation(name)) {
                this.name = name;
                this.places = new ArrayList<>();
            }else {
                throw new IllegalArgumentException("Invalid category name:" +name);
            }
        }

        public void updateCategoryList(PlaceDetails placeDetails){
            // If place does not exist in the list add.
            if(!(this.getPlaces().contains(placeDetails))){
                List<PlaceDetails> places = this.getPlaces();
                places.add(placeDetails);
                this.setPlaces(places);
            }
        }

        private boolean categoryValidation(String name){
            List<String> allCategories = Arrays.asList("interesting_places", "amusements", "sport", "tourist_facilities", "accomodations","adult");
            return allCategories.contains(name);
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public List<PlaceDetails> getPlaces() {
            return places;
        }
        public void setPlaces(List<PlaceDetails> places) {
            this.places = places;
        }
        @Override
        public String toString() {
            return "Category{" +
                    "name='" + name +
                    ", places=" + places +
                    '}';
        }
    }

    public void updateCategories(PlaceDetails placeDetails, String kind){
        // Search for category inside categories.
        if (placeDetails.getKinds().contains(kind)) {
            // Add place details into the category.
            if(this.getCategories().isEmpty() || this.getCategory(kind).getPlaces().isEmpty() ){
                Category newCategory = new Category(kind);
                this.categories.add(newCategory);
            }
            for (Category category : this.categories){
                if (category.getName().equals(kind)){
                    category.updateCategoryList(placeDetails);
                }
            }
        }
    }


}
