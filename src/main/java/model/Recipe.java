package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert ein vollständiges Rezept mit Name, Zutaten, Schritten, Tags, Bild und Metadaten.
 */
public class Recipe {

    private int id;
    private String name;
    private int rating;
    private int portions;
    private String createdDate;
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private List<Tag> tags;
    private Photo photo;
    private String modifiedDate;

    public Recipe() {
        this.name = "";
        this.rating = 0;
        this.portions = 2;
        this.createdDate = null;
        this.ingredients = new ArrayList<>();
        this.steps = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.photo = null;
        this.modifiedDate = null;
    }

    public Recipe(String name) {
        this();
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getPortions() {
        return portions;
    }

    public void setPortions(int portions) {
        this.portions = portions;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public void addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
    }

    public void removeIngredient(Ingredient ingredient) {
        this.ingredients.remove(ingredient);
    }

    public void addStep(Step step) {
        this.steps.add(step);
    }

    public void removeStep(Step step) {
        this.steps.remove(step);
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return id == recipe.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}