package de.gfn.ikladde.ikladde;

import controller.IkladdeAppController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.WindowManager;
import model.RecipeManager;
import view.IkladdeAppView;
import view.RecipeView;

/**
 * Startpunkt der Anwendung.
 * Initialisiert das Hauptfenster, lädt die Rezeptdaten und verbindet View und Controller.
 */
public class IkladdeApp extends Application {

    /**
     * Startet die JavaFX-Anwendung und zeigt das Hauptfenster.
     *
     * @param primaryStage Hauptbühne der Anwendung
     */
    @Override
    public void start(Stage primaryStage) {
        IkladdeAppView view = new IkladdeAppView();
        RecipeManager sharedManager = new RecipeManager();
        sharedManager.loadData();

        final IkladdeAppController[] controllerRef = new IkladdeAppController[1];

        controllerRef[0] = new IkladdeAppController(view, (manager) -> {
            RecipeView recipeView = new RecipeView();
            WindowManager.openRecipeManagerWindow(manager, recipeView, () -> {
                controllerRef[0].refreshRecipes();
                controllerRef[0].refreshTags();
            });
        }, sharedManager);

        Scene scene = new Scene(view, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setTitle("iKladde – Hauptfenster");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Einstiegspunkt der Anwendung.
     *
     * @param args Programmargumente
     */
    public static void main(String[] args) {
        launch(args);
    }
}