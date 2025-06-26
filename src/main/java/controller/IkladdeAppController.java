package controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import model.*;
import view.IkladdeAppView;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.Rating;
import database.RecipeDAO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Der Hauptcontroller für das iKladde-Hauptfenster.
 * Verbindet die View mit dem Datenmodell und behandelt alle Benutzeraktionen.
 */
public class IkladdeAppController {

    private final IkladdeAppView view;
    private final ObservableList<Recipe> allRecipes;
    private final RecipeManager recipeManager;
    private List<Ingredient> baseIngredients;
    private int currentStepIndex = 0;
    private Integer selectedPortions = null;

    /**
     * Erstellt den Controller, lädt Daten und verbindet UI-Elemente mit Logik.
     *
     * @param view Die Benutzeroberfläche
     * @param onOpenRecipeManager Callback zum Öffnen des Rezeptmanagers
     * @param recipeManager Die zentrale Rezeptverwaltung
     */
    public IkladdeAppController(IkladdeAppView view, Consumer<RecipeManager> onOpenRecipeManager, RecipeManager recipeManager) {
        this.view = view;
        this.recipeManager = recipeManager;
        this.allRecipes = FXCollections.observableArrayList();

        recipeManager.loadData();
        allRecipes.setAll(recipeManager.getRecipes());

        view.getRecipeListView().setItems(allRecipes);
        view.getRecipeListView().getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentStepIndex = 0;
                showRecipeDetails(newVal);
            }
        });

        view.getFilterField().textProperty().addListener((obs, oldV, newV) -> applyFilters());
        view.getTagCheckCombo().getCheckModel().getCheckedItems().addListener((ListChangeListener<Tag>) c -> applyFilters());

        view.getSortByRatingButton().setOnAction(e -> {
            ObservableList<Recipe> currentList = view.getRecipeListView().getItems();
            FXCollections.sort(currentList, Comparator.comparingInt(Recipe::getRating).reversed());
        });

        view.getPortion2Button().setOnAction(e -> updatePortion(2));
        view.getPortion4Button().setOnAction(e -> updatePortion(4));
        view.getPortion6Button().setOnAction(e -> updatePortion(6));
        view.getPortion12Button().setOnAction(e -> updatePortion(12));

        view.getPrevStepButton().setOnAction(e -> {
            if (currentStepIndex > 0) {
                currentStepIndex--;
                updateStepText();
            }
        });
        view.getNextStepButton().setOnAction(e -> {
            Recipe selected = view.getRecipeListView().getSelectionModel().getSelectedItem();
            if (selected != null && currentStepIndex < selected.getSteps().size() - 1) {
                currentStepIndex++;
                updateStepText();
            }
        });

        view.getRatingControl().ratingProperty().addListener((obs, oldR, newR) -> {
            Recipe selected = view.getRecipeListView().getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.setRating(newR.intValue());
                recipeManager.saveRecipe(selected);
            }
        });

        view.getManageRecipesButton().setOnAction(e -> {
            onOpenRecipeManager.accept(recipeManager);
            refreshRecipes();
            refreshTags();
        });

        List<Tag> allTags = new RecipeDAO().getAllTags();
        view.getTagCheckCombo().getItems().setAll(allTags);
    }

    /**
     * Wendet aktuelle Filter (Text & Tags) auf die Rezeptliste an.
     */
    private void applyFilters() {
        String text = view.getFilterField().getText().toLowerCase();
        ObservableList<Tag> selectedTags = view.getTagCheckCombo().getCheckModel().getCheckedItems();
        ObservableList<Recipe> filtered = FXCollections.observableArrayList();
        for (Recipe r : allRecipes) {
            boolean matchesName = text.isEmpty() || r.getName().toLowerCase().contains(text);
            boolean matchesTags = selectedTags.isEmpty() || r.getTags().stream().anyMatch(selectedTags::contains);
            if (matchesName && matchesTags) {
                filtered.add(r);
            }
        }
        view.getRecipeListView().setItems(filtered);
    }

    /**
     * Zeigt alle Details des ausgewählten Rezepts in der Oberfläche an.
     *
     * @param recipe Das aktuell ausgewählte Rezept
     */
    private void showRecipeDetails(Recipe recipe) {
        view.getCenterRecipeNameLabel().setText(recipe.getName());
        view.getRightRecipeNameLabel().setText(recipe.getName());
        selectedPortions = null;
        if (recipe.getPhoto() != null) {
            view.getRecipeImageView().setImage(new Image("file:" + recipe.getPhoto().getFilePath()));
        } else {
            view.getRecipeImageView().setImage(null);
        }

        if (recipe.getModifiedDate() != null) {
            view.getLastModifiedLabel().setText("Letzte Änderung: " + recipe.getModifiedDate());
        } else {
            view.getLastModifiedLabel().setText("Letzte Änderung: unbekannt");
        }

        view.getRatingControl().setRating(recipe.getRating());

        view.getTagsFlowPane().getChildren().clear();
        for (Tag tag : recipe.getTags()) {
            Label tagLabel = new Label(tag.getName());
            tagLabel.setStyle("-fx-border-color: gray; -fx-padding: 2;");
            view.getTagsFlowPane().getChildren().add(tagLabel);
        }

        currentStepIndex = 0;
        updateStepText();

        baseIngredients = new ArrayList<>(recipe.getIngredients());
        updateIngredientsTable(1);

        view.getLastModifiedLabel().setText("Erstellungsdatum: " + recipe.getCreatedDate());
        updateDisplayedPortions(recipe);
    }

    /**
     * Zeigt Zutaten für die aktuell gewählte Portionsanzahl an.
     *
     * @param factor Multiplikator basierend auf Portionsanzahl
     */
    private void updateIngredientsTable(double factor) {
        ObservableList<Ingredient> list = FXCollections.observableArrayList();
        for (Ingredient ing : baseIngredients) {
            list.add(new Ingredient(
                    ing.getName(),
                    ing.getQuantity() * factor,
                    ing.getUnit()
            ));
        }
        view.getIngredientsTable().setItems(list);
    }

    /**
     * Berechnet den Portionsfaktor neu und aktualisiert Anzeige und Zutaten.
     *
     * @param newPortions Neue Portionsanzahl
     */
    private void updatePortion(int newPortions) {
        Recipe selected = view.getRecipeListView().getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedPortions = newPortions;
            int originalPortions = selected.getPortions();
            if (originalPortions <= 0) originalPortions = 1;
            double factor = (double) newPortions / originalPortions;
            updateIngredientsTable(factor);
            updateDisplayedPortions(selected);
        }
    }

    /**
     * Zeigt den aktuellen Kochschritt an.
     */
    private void updateStepText() {
        Recipe recipe = view.getRecipeListView().getSelectionModel().getSelectedItem();
        if (recipe != null && !recipe.getSteps().isEmpty()) {
            Step step = recipe.getSteps().get(currentStepIndex);
            view.getStepTextArea().setText(step.getDescription());

            String title = "Schritt " + (currentStepIndex + 1);
            view.getStepTitleLabel().setText(title);
        } else {
            view.getStepTextArea().clear();
            view.getStepTitleLabel().setText("");
        }
    }

    /**
     * Aktualisiert die Tag-Auswahl aus der Datenbank.
     */
    public void refreshTags() {
        List<Tag> tags = new RecipeDAO().getAllTags();
        view.getTagCheckCombo().getItems().setAll(tags);
    }

    /**
     * Lädt die Rezepte aus der Datenbank neu und wendet Filter an.
     */
    public void refreshRecipes() {
        recipeManager.loadData();
        allRecipes.setAll(recipeManager.getRecipes());
        applyFilters();
    }

    /**
     * Zeigt die aktuelle Portionsanzahl in der Oberfläche an.
     *
     * @param recipe Das ausgewählte Rezept
     */
    private void updateDisplayedPortions(Recipe recipe) {
        if (recipe == null) return;
        int display = selectedPortions != null ? selectedPortions : recipe.getPortions();
        view.getPortionDisplayLabel().setText("Portionen: " + display);
    }
}