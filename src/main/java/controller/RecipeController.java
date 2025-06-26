package controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import model.*;
import view.RecipeView;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Controller für das Rezeptverwaltungsfenster.
 * Behandelt Benutzeraktionen wie Hinzufügen, Bearbeiten, Löschen und Speichern von Rezepten.
 */
public class RecipeController {

    private final RecipeManager recipeManager;
    private final RecipeView recipeView;
    private Recipe currentRecipe;

    /**
     * Initialisiert den Controller und verbindet alle Aktionen mit der Benutzeroberfläche.
     *
     * @param recipeManager Zentrale Rezeptverwaltung
     * @param recipeView    Ansicht für die Rezeptbearbeitung
     */
    public RecipeController(RecipeManager recipeManager, RecipeView recipeView) {
        this.recipeManager = recipeManager;
        this.recipeView = recipeView;

        recipeManager.loadData();
        recipeView.getRecipeListView().getItems().addAll(recipeManager.getRecipes());

        recipeView.getRecipeListView().getSelectionModel().selectedItemProperty().addListener((obs, alt, neu) -> {
            currentRecipe = neu;
            recipeView.displayRecipe(neu);
        });

        recipeView.getSaveButton().setOnAction(e -> handleSave());
        recipeView.getAddRecipeButton().setOnAction(e -> handleAdd());
        recipeView.getRemoveRecipeButton().setOnAction(e -> handleDelete());

        recipeView.getAddIngredientButton().setOnAction(e -> handleAddIngredient());
        recipeView.getRemoveIngredientButton().setOnAction(e -> handleRemoveIngredient());

        recipeView.getAddStepButton().setOnAction(e -> handleAddStep());
        recipeView.getRemoveStepButton().setOnAction(e -> handleRemoveStep());

        recipeView.getAddTagButton().setOnAction(e -> handleAddTag());
        recipeView.getRemoveTagButton().setOnAction(e -> handleRemoveTag());

        recipeView.getChangePhotoButton().setOnAction(e -> handleChangePhoto());

        recipeView.getCloseButton().setOnAction(e -> {
            Stage stage = (Stage) recipeView.getScene().getWindow();
            if (stage != null) stage.close();
        });
    }

    /**
     * Erstellt ein neues leeres Rezept und fügt es der Liste hinzu.
     */
    private void handleAdd() {
        Recipe newRecipe = new Recipe("Neues Rezept");
        newRecipe.setCreatedDate(LocalDate.now().toString());
        recipeManager.addRecipe(newRecipe);
        recipeView.getRecipeListView().getItems().add(newRecipe);
        recipeView.getRecipeListView().getSelectionModel().select(newRecipe);
    }

    /**
     * Speichert das aktuell ausgewählte Rezept und aktualisiert die Anzeige.
     */
    private void handleSave() {
        if (currentRecipe != null) {
            currentRecipe.setName(recipeView.getNameField().getText());
            try {
                int portions = Integer.parseInt(recipeView.getPortionsField().getText());
                currentRecipe.setPortions(portions);
            } catch (NumberFormatException e) {
                currentRecipe.setPortions(2);
            }

            currentRecipe.getTags().clear();
            currentRecipe.getTags().addAll(recipeView.getTagListView().getItems());

            recipeManager.saveRecipe(currentRecipe);

            ObservableList<Recipe> list = recipeView.getRecipeListView().getItems();
            int index = list.indexOf(currentRecipe);
            if (index >= 0) {
                list.set(index, currentRecipe);
            }

            recipeView.getRecipeListView().refresh();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Rezept wurde gespeichert.", ButtonType.OK);
            alert.setHeaderText(null);
            alert.showAndWait();
        }
    }

    /**
     * Löscht das aktuell ausgewählte Rezept aus der Liste.
     */
    private void handleDelete() {
        if (currentRecipe != null) {
            recipeManager.removeRecipe(currentRecipe);
            recipeView.getRecipeListView().getItems().remove(currentRecipe);
            currentRecipe = null;
            recipeView.displayRecipe(null);
        }
    }

    /**
     * Fügt dem Rezept eine neue Zutat hinzu.
     */
    private void handleAddIngredient() {
        if (currentRecipe == null) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Neue Zutat eingeben (z.B. Zucker 100 g)");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            String[] parts = input.split(" ", 3);
            if (parts.length >= 2) {
                String name = parts[0];
                double quantity;
                try {
                    quantity = Double.parseDouble(parts[1]);
                } catch (NumberFormatException e) {
                    quantity = 1.0;
                }
                String unit = (parts.length == 3) ? parts[2] : "";
                Ingredient ing = new Ingredient(name, quantity, unit);
                currentRecipe.addIngredient(ing);
                recipeView.getIngredientListView().getItems().add(ing);
            }
        });
    }

    /**
     * Entfernt die aktuell ausgewählte Zutat.
     */
    private void handleRemoveIngredient() {
        Ingredient selected = recipeView.getIngredientListView().getSelectionModel().getSelectedItem();
        if (currentRecipe != null && selected != null) {
            currentRecipe.removeIngredient(selected);
            recipeView.getIngredientListView().getItems().remove(selected);
        }
    }

    /**
     * Fügt dem Rezept einen neuen Schritt hinzu.
     */
    private void handleAddStep() {
        if (currentRecipe == null) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Neuen Schritt eingeben");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(desc -> {
            Step step = new Step(desc);
            currentRecipe.addStep(step);
            recipeView.getStepListView().getItems().add(step);
        });
    }

    /**
     * Entfernt den aktuell ausgewählten Schritt.
     */
    private void handleRemoveStep() {
        Step selected = recipeView.getStepListView().getSelectionModel().getSelectedItem();
        if (currentRecipe != null && selected != null) {
            currentRecipe.removeStep(selected);
            recipeView.getStepListView().getItems().remove(selected);
        }
    }

    /**
     * Fügt dem Rezept einen neuen Tag hinzu.
     */
    private void handleAddTag() {
        if (currentRecipe == null) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Neuen Tag eingeben");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            Tag tag = new Tag(name);
            currentRecipe.addTag(tag);
            recipeView.getTagListView().getItems().add(tag);
        });
    }

    /**
     * Entfernt den aktuell ausgewählten Tag.
     */
    private void handleRemoveTag() {
        Tag selected = recipeView.getTagListView().getSelectionModel().getSelectedItem();
        if (currentRecipe != null && selected != null) {
            currentRecipe.removeTag(selected);
            recipeView.getTagListView().getItems().remove(selected);
        }
    }

    /**
     * Öffnet einen Dateiauswahldialog und ersetzt das Foto des Rezepts.
     */
    private void handleChangePhoto() {
        if (currentRecipe == null) return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Bild auswählen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Bilddateien", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                File photosDir = new File("photos");
                if (!photosDir.exists()) photosDir.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + file.getName();
                File target = new File(photosDir, fileName);

                Files.copy(file.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);

                currentRecipe.setPhoto(new Photo("photos/" + fileName));
                recipeView.updatePhoto(target.getPath());
            } catch (IOException ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Fehler beim Kopieren des Fotos.");
                alert.showAndWait();
            }
        }
    }
}