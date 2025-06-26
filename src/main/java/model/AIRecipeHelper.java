package model;

import java.io.File;

/**
 * Diese Klasse dient als Platzhalter für zukünftige KI-Integration.
 * Hier sollen später Bilder analysiert und automatisch Rezeptdaten extrahiert werden.
 */
public class AIRecipeHelper {

    /**
     * Diese Methode soll später ein Rezept aus einem Bild (z. B. Screenshot eines Rezepts)
     * automatisch erkennen und in ein Recipe-Objekt umwandeln.
     *
     * @param imageFile Bilddatei eines Rezepts
     * @return ein neues Recipe-Objekt (oder null als Platzhalter)
     */
    public static Recipe importFromImage(File imageFile) {
        // Beispielhafte Platzhalter-Variablen
        String erkannterName = "Platzhalter-Rezept";
        int erkanntePortionen = 4;

        // Neues leeres Rezept-Objekt als Platzhalter
        Recipe recipe = new Recipe();
        recipe.setName(erkannterName);
        recipe.setPortions(erkanntePortionen);

        return recipe;
    }
}