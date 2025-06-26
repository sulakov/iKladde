package model;

import java.time.LocalDate;
import database.RecipeDAO;
import java.util.ArrayList;
import java.util.List;

/**
 * Verwalter für eine Sammlung von Rezepten.
 * Ermöglicht das Laden, Speichern und Löschen über das zugrundeliegende DAO.
 */
public class RecipeManager {

    private List<Recipe> recipes;
    private final RecipeDAO recipeDAO = new RecipeDAO();

    public RecipeManager() {
        this.recipes = new ArrayList<>();
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void addRecipe(Recipe recipe) {
        recipes.add(recipe);
    }

    public void removeRecipe(Recipe recipe) {
        recipeDAO.deleteRecipe(recipe.getId());
        recipes.remove(recipe);
    }

    public void loadData() {
        recipes = recipeDAO.getAllRecipes();
        for (Recipe r : recipes) {
            r.setIngredients(recipeDAO.getIngredientsForRecipe(r.getId()));
            r.setSteps(recipeDAO.getStepsForRecipe(r.getId()));
            r.setTags(recipeDAO.getTagsForRecipe(r.getId()));
        }
    }

    public void saveRecipe(Recipe recipe) {
        recipe.setModifiedDate(LocalDate.now().toString());
        if (recipe.getId() == 0) {
            recipeDAO.insertRecipe(recipe);
            recipes.add(recipe);
        } else {
            recipeDAO.updateRecipe(recipe);
            int index = recipes.indexOf(recipe);
            if (index >= 0) {
                recipes.set(index, recipe);
            }
        }
    }
}