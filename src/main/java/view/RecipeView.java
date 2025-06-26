package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import model.*;

/**
 * Ansicht für die Bearbeitung und Verwaltung einzelner Rezepte.
 * Ermöglicht das Ändern von Zutaten, Schritten, Tags und Foto.
 */
public class RecipeView extends BorderPane {

    private final ListView<Recipe> recipeListView = new ListView<>();
    private final TextField nameField = new TextField();
    private final TextField portionsField = new TextField();
    private final ImageView imageView = new ImageView();
    private final Button changePhotoButton = new Button("Foto ändern");
    private final ListView<Ingredient> ingredientListView = new ListView<>();
    private final Button addIngredientButton = new Button("Zutat +");
    private final Button removeIngredientButton = new Button("Zutat -");
    private final ListView<Step> stepListView = new ListView<>();
    private final Button addStepButton = new Button("Schritt +");
    private final Button removeStepButton = new Button("Schritt -");
    private final ListView<Tag> tagListView = new ListView<>();
    private final Button addTagButton = new Button("Tag +");
    private final Button removeTagButton = new Button("Tag -");
    private final Button saveButton = new Button("Speichern");
    private final Button addRecipeButton = new Button("Neu");
    private final Button removeRecipeButton = new Button("Löschen");
    private final Button closeButton = new Button("Schließen");

    /**
     * Initialisiert das Layout für das Rezeptbearbeitungsfenster.
     */
    public RecipeView() {
        this.getStyleClass().add("recipe-editor");

        HBox topBox = new HBox(10, new Label("Name:"), nameField, new Label("Portionen:"), portionsField);
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.getStyleClass().add("top-section");

        imageView.setFitWidth(80);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        VBox photoBox = new VBox(5, imageView, changePhotoButton);
        photoBox.setAlignment(Pos.CENTER);
        photoBox.getStyleClass().add("photo-section");

        VBox leftBox = new VBox(10, new Label("Rezepte:"), recipeListView, addRecipeButton, removeRecipeButton);
        leftBox.setPadding(new Insets(10));
        leftBox.getStyleClass().add("sidebar");

        VBox centerBox = new VBox(10, topBox, photoBox,
                new Label("Zutaten:"), ingredientListView, new HBox(10, addIngredientButton, removeIngredientButton),
                new Label("Schritte:"), stepListView, new HBox(10, addStepButton, removeStepButton),
                new Label("Tags:"), tagListView, new HBox(10, addTagButton, removeTagButton));
        centerBox.setPadding(new Insets(10));
        centerBox.getStyleClass().add("editor-center");

        HBox bottomBar = new HBox(10, saveButton, closeButton);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setPadding(new Insets(10));
        bottomBar.getStyleClass().add("editor-buttons");

        setLeft(leftBox);
        setCenter(centerBox);
        setBottom(bottomBar);
        setPadding(new Insets(10));
    }

    // Getter für Controller-Zugriff

    public ListView<Recipe> getRecipeListView() { return recipeListView; }
    public TextField getNameField() { return nameField; }
    public TextField getPortionsField() { return portionsField; }
    public ImageView getImageView() { return imageView; }
    public Button getChangePhotoButton() { return changePhotoButton; }
    public ListView<Ingredient> getIngredientListView() { return ingredientListView; }
    public Button getAddIngredientButton() { return addIngredientButton; }
    public Button getRemoveIngredientButton() { return removeIngredientButton; }
    public ListView<Step> getStepListView() { return stepListView; }
    public Button getAddStepButton() { return addStepButton; }
    public Button getRemoveStepButton() { return removeStepButton; }
    public ListView<Tag> getTagListView() { return tagListView; }
    public Button getAddTagButton() { return addTagButton; }
    public Button getRemoveTagButton() { return removeTagButton; }
    public Button getSaveButton() { return saveButton; }
    public Button getAddRecipeButton() { return addRecipeButton; }
    public Button getRemoveRecipeButton() { return removeRecipeButton; }
    public Button getCloseButton() { return closeButton; }

    /**
     * Aktualisiert das Bild im Editor.
     *
     * @param path Pfad zur Bilddatei
     */
    public void updatePhoto(String path) {
        imageView.setImage(new javafx.scene.image.Image("file:" + path));
    }

    /**
     * Zeigt die Daten eines ausgewählten Rezepts im Editor an.
     * Falls null übergeben wird, werden alle Felder geleert.
     *
     * @param recipe Das ausgewählte Rezept oder null
     */
    public void displayRecipe(Recipe recipe) {
        if (recipe != null) {
            nameField.setText(recipe.getName());
            portionsField.setText(String.valueOf(recipe.getPortions()));
            imageView.setImage(recipe.getPhoto() != null ? new javafx.scene.image.Image("file:" + recipe.getPhoto().getFilePath()) : null);
            ingredientListView.getItems().setAll(recipe.getIngredients());
            stepListView.getItems().setAll(recipe.getSteps());
            tagListView.getItems().setAll(recipe.getTags());
        } else {
            nameField.clear();
            portionsField.clear();
            imageView.setImage(null);
            ingredientListView.getItems().clear();
            stepListView.getItems().clear();
            tagListView.getItems().clear();
        }
    }
}