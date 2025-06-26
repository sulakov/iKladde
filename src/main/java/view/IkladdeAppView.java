package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import model.*;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.Rating;

/**
 * Hauptansicht des iKladde-Programms.
 * Zeigt die Rezeptliste, Detailansicht, Bewertung, Zutaten, Tags und Kochschritte an.
 */
public class IkladdeAppView extends BorderPane {

    private final TextField filterField = new TextField();
    private final CheckComboBox<Tag> tagCheckCombo = new CheckComboBox<>();
    private final Button sortByRatingButton = new Button("Nach Bewertung sortieren");
    private final ListView<Recipe> recipeListView = new ListView<>();
    private final Button manageRecipesButton = new Button("Rezeptverwaltung");

    private final Label centerRecipeNameLabel = new Label();
    private final ImageView recipeImageView = new ImageView();
    private final TableView<Ingredient> ingredientsTable = new TableView<>();
    private final Button portion2Button = new Button("2");
    private final Button portion4Button = new Button("4");
    private final Button portion6Button = new Button("6");
    private final Button portion12Button = new Button("12");

    private final Label rightRecipeNameLabel = new Label();
    private final Rating ratingControl = new Rating(5);
    private final FlowPane tagsFlowPane = new FlowPane();
    private final TextArea stepTextArea = new TextArea();
    private final Button prevStepButton = new Button("◀");
    private final Button nextStepButton = new Button("▶");

    private final Label lastModifiedLabel = new Label();
    private final Button printButton = new Button("🖨️ Rezept ausdrucken");
    private final Label portionDisplayLabel = new Label();
    private final Label stepTitleLabel = new Label();

    /**
     * Erstellt das Hauptlayout mit linkem Filterbereich, mittlerem Bereich für Zutaten und rechtem Detailbereich.
     */
    public IkladdeAppView() {
        this.getStyleClass().add("ikladde-app");

        // Linker Bereich
        filterField.setPromptText("Suche");
        filterField.getStyleClass().add("filter-field");
        tagCheckCombo.setTitle("Tags");
        recipeListView.getStyleClass().add("recipe-list");
        sortByRatingButton.getStyleClass().addAll("button", "sort-button");
        manageRecipesButton.getStyleClass().addAll("button", "manage-button");

        VBox leftBox = new VBox(8, filterField, tagCheckCombo, sortByRatingButton, recipeListView, manageRecipesButton);
        leftBox.setPadding(new Insets(10));
        leftBox.setPrefWidth(220);
        leftBox.getStyleClass().add("sidebar");

        // Zentraler Bereich
        portionDisplayLabel.getStyleClass().add("portion-display-label");

        recipeImageView.setPreserveRatio(true);
        recipeImageView.setSmooth(true);
        recipeImageView.setFitHeight(360);

        VBox imageContainer = new VBox(recipeImageView);
        imageContainer.setAlignment(Pos.CENTER);
        VBox.setVgrow(imageContainer, Priority.NEVER);

        portion2Button.getStyleClass().addAll("button", "portion-button");
        portion4Button.getStyleClass().addAll("button", "portion-button");
        portion6Button.getStyleClass().addAll("button", "portion-button");
        portion12Button.getStyleClass().addAll("button", "portion-button");

        HBox portionButtons = new HBox(10, portion2Button, portion4Button, portion6Button, portion12Button);
        portionButtons.setAlignment(Pos.CENTER);

        Label portionLabel = new Label("Portionen:");
        portionLabel.setPadding(new Insets(5, 0, 0, 5));
        VBox portionBox = new VBox(5, portionLabel, portionButtons);
        portionBox.setAlignment(Pos.CENTER);

        ingredientsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Ingredient, String> nameCol = new TableColumn<>("Zutat");
        nameCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));

        TableColumn<Ingredient, String> qtyCol = new TableColumn<>("Menge");
        qtyCol.setCellValueFactory(cell -> {
            double wert = cell.getValue().getQuantity();
            String formatiert = String.format("%.1f", wert);
            return new javafx.beans.property.SimpleStringProperty(formatiert);
        });

        TableColumn<Ingredient, String> unitCol = new TableColumn<>("Einheit");
        unitCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getUnit()));

        ingredientsTable.getColumns().addAll(nameCol, qtyCol, unitCol);
        ingredientsTable.getStyleClass().add("ingredients-table");

        VBox centerBox = new VBox(10, portionDisplayLabel, imageContainer, ingredientsTable, portionBox);
        centerBox.setPadding(new Insets(10));
        centerBox.setPrefWidth(360);
        centerBox.getStyleClass().add("recipe-center");

        recipeImageView.fitWidthProperty().bind(centerBox.widthProperty().subtract(20));

        // Rechter Bereich
        rightRecipeNameLabel.getStyleClass().add("right-recipe-title");
        ratingControl.getStyleClass().add("rating-control");

        tagsFlowPane.setHgap(6);
        tagsFlowPane.setVgap(6);
        tagsFlowPane.getStyleClass().add("tags-flow");

        stepTextArea.setWrapText(true);
        stepTextArea.setEditable(false);
        stepTextArea.getStyleClass().add("step-area");

        prevStepButton.getStyleClass().addAll("button", "step-button");
        nextStepButton.getStyleClass().addAll("button", "step-button");

        lastModifiedLabel.getStyleClass().add("last-modified-label");
        stepTitleLabel.getStyleClass().add("step-title-label");

        printButton.getStyleClass().add("button");

        printButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Druckfunktion ist noch nicht implementiert.");
            alert.setHeaderText(null);
            alert.showAndWait();
        });

        HBox stepButtons = new HBox(10, prevStepButton, nextStepButton);
        stepButtons.setAlignment(Pos.CENTER);

        VBox.setMargin(ratingControl, new Insets(15, 0, 0, 0));
        VBox.setMargin(tagsFlowPane, new Insets(15, 0, 0, 0));
        VBox.setMargin(stepTextArea, new Insets(35, 0, 0, 0));
        VBox.setMargin(stepButtons, new Insets(5, 0, 0, 0));

        VBox bottomBox = new VBox(10, lastModifiedLabel, printButton);
        bottomBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(bottomBox, Priority.ALWAYS);

        VBox rightBox = new VBox(
                10,
                rightRecipeNameLabel,
                ratingControl,
                tagsFlowPane,
                stepTitleLabel,
                stepTextArea,
                stepButtons,
                bottomBox
        );

        rightBox.setPadding(new Insets(10));
        rightBox.setPrefWidth(300);
        rightBox.getStyleClass().add("recipe-details");

        setLeft(leftBox);
        setCenter(centerBox);
        setRight(rightBox);
    }

    // Getter für UI-Elemente

    public TextField getFilterField() { return filterField; }
    public CheckComboBox<Tag> getTagCheckCombo() { return tagCheckCombo; }
    public Button getSortByRatingButton() { return sortByRatingButton; }
    public ListView<Recipe> getRecipeListView() { return recipeListView; }
    public Button getManageRecipesButton() { return manageRecipesButton; }

    public Label getCenterRecipeNameLabel() { return centerRecipeNameLabel; }
    public Label getPortionDisplayLabel() { return portionDisplayLabel; }
    public ImageView getRecipeImageView() { return recipeImageView; }
    public TableView<Ingredient> getIngredientsTable() { return ingredientsTable; }
    public Button getPortion2Button() { return portion2Button; }
    public Button getPortion4Button() { return portion4Button; }
    public Button getPortion6Button() { return portion6Button; }
    public Button getPortion12Button() { return portion12Button; }
    public Label getStepTitleLabel() { return stepTitleLabel; }

    public Label getRightRecipeNameLabel() { return rightRecipeNameLabel; }
    public Rating getRatingControl() { return ratingControl; }
    public FlowPane getTagsFlowPane() { return tagsFlowPane; }
    public TextArea getStepTextArea() { return stepTextArea; }
    public Button getPrevStepButton() { return prevStepButton; }
    public Button getNextStepButton() { return nextStepButton; }
    public Label getLastModifiedLabel() { return lastModifiedLabel; }
    public Button getPrintButton() { return printButton; }
}