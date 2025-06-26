package model;

import controller.RecipeController;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.RecipeView;

/**
 * Öffnet Fenster für die Rezeptverwaltung.
 * Wird vom Hauptfenster aufgerufen, um ein separates Bearbeitungsfenster zu starten.
 */
public class WindowManager {

    /**
     * Öffnet ein neues Fenster mit der Rezeptverwaltung.
     *
     * @param recipeManager Zentrale Datenverwaltung
     * @param recipeView    Benutzeroberfläche für die Bearbeitung
     * @param onClose       Callback, das nach dem Schließen ausgeführt wird
     */
    public static void openRecipeManagerWindow(RecipeManager recipeManager, RecipeView recipeView, Runnable onClose) {
        new RecipeController(recipeManager, recipeView);

        Stage stage = new Stage();
        stage.setTitle("Rezeptverwaltung");
        stage.setScene(new Scene(recipeView, 800, 600));
        stage.getScene().getStylesheets().add(WindowManager.class.getResource("/style.css").toExternalForm());

        stage.setOnHidden(e -> {
            if (onClose != null) {
                onClose.run();
            }
        });

        stage.show();
    }
}