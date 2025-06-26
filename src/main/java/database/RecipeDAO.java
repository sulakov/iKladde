package database;

import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Dieses DAO verwaltet das Laden, Speichern, Aktualisieren und Löschen von Rezepten
 * sowie deren Zutaten, Schritte und Tags in der SQLite-Datenbank.
 */
public class RecipeDAO {

    /**
     * Gibt alle Rezepte aus der Datenbank zurück.
     *
     * @return Liste aller gespeicherten Rezepte
     */
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT * FROM recipe";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(rs.getInt("id"));
                recipe.setName(rs.getString("name"));
                recipe.setRating(rs.getInt("rating"));
                recipe.setPortions(rs.getInt("portions"));
                recipe.setCreatedDate(rs.getString("createdDate"));
                String photoPath = rs.getString("photoPath");
                if (photoPath != null) {
                    recipe.setPhoto(new Photo(photoPath));
                }
                recipes.add(recipe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipes;
    }

    /**
     * Fügt ein neues Rezept inklusive Zutaten, Schritte und Tags in die Datenbank ein.
     *
     * @param recipe Das neue Rezept
     */
    public void insertRecipe(Recipe recipe) {
        String sql = "INSERT INTO recipe (name, photoPath, rating, portions, createdDate) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getPhoto() != null ? recipe.getPhoto().getFilePath() : null);
            pstmt.setInt(3, recipe.getRating());
            pstmt.setInt(4, recipe.getPortions());
            pstmt.setString(5, recipe.getCreatedDate());
            pstmt.executeUpdate();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid();")) {
                if (rs.next()) {
                    recipe.setId(rs.getInt(1));
                }
            }

            insertIngredients(conn, recipe);
            insertSteps(conn, recipe);
            insertTags(conn, recipe);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Aktualisiert ein bestehendes Rezept und ersetzt alle zugehörigen Daten.
     *
     * @param recipe Rezept mit aktualisierten Daten
     */
    public void updateRecipe(Recipe recipe) {
        String sql = "UPDATE recipe SET name = ?, photoPath = ?, rating = ?, portions = ?, createdDate = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getPhoto() != null ? recipe.getPhoto().getFilePath() : null);
            pstmt.setInt(3, recipe.getRating());
            pstmt.setInt(4, recipe.getPortions());
            pstmt.setString(5, recipe.getCreatedDate());
            pstmt.setInt(6, recipe.getId());
            pstmt.executeUpdate();

            deleteIngredients(conn, recipe.getId());
            insertIngredients(conn, recipe);

            deleteSteps(conn, recipe.getId());
            insertSteps(conn, recipe);

            deleteRecipeTags(conn, recipe.getId());
            insertTags(conn, recipe);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Löscht ein Rezept und alle zugehörigen Daten.
     *
     * @param recipeId ID des zu löschenden Rezepts
     */
    public void deleteRecipe(int recipeId) {
        String sql = "DELETE FROM recipe WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recipeId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertIngredients(Connection conn, Recipe recipe) throws SQLException {
        String sql = "INSERT INTO ingredient (recipe_id, name, quantity, unit) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Ingredient ing : recipe.getIngredients()) {
                pstmt.setInt(1, recipe.getId());
                pstmt.setString(2, ing.getName());
                pstmt.setDouble(3, ing.getQuantity());
                pstmt.setString(4, ing.getUnit());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private void deleteIngredients(Connection conn, int recipeId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM ingredient WHERE recipe_id = ?")) {
            pstmt.setInt(1, recipeId);
            pstmt.executeUpdate();
        }
    }

    private void insertSteps(Connection conn, Recipe recipe) throws SQLException {
        String sql = "INSERT INTO step (recipe_id, number, description) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int number = 1;
            for (Step step : recipe.getSteps()) {
                pstmt.setInt(1, recipe.getId());
                pstmt.setInt(2, number);
                pstmt.setString(3, step.getDescription());
                pstmt.addBatch();
                number++;
            }
            pstmt.executeBatch();
        }
    }

    private void deleteSteps(Connection conn, int recipeId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM step WHERE recipe_id = ?")) {
            pstmt.setInt(1, recipeId);
            pstmt.executeUpdate();
        }
    }

    private void insertTags(Connection conn, Recipe recipe) throws SQLException {
        String insertTag = "INSERT OR IGNORE INTO tag (name) VALUES (?)";
        String insertLink = "INSERT INTO recipe_tag (recipe_id, tag_id) VALUES (?, (SELECT id FROM tag WHERE name = ?))";

        try (PreparedStatement tagStmt = conn.prepareStatement(insertTag);
             PreparedStatement linkStmt = conn.prepareStatement(insertLink)) {

            for (Tag tag : recipe.getTags()) {
                String name = tag.getName().trim();

                tagStmt.setString(1, name);
                tagStmt.executeUpdate();

                linkStmt.setInt(1, recipe.getId());
                linkStmt.setString(2, name);
                try {
                    linkStmt.executeUpdate();
                } catch (SQLException ex) {
                    System.out.println("Fehler beim Verknüpfen mit Tag '" + name + "': " + ex.getMessage());
                }
            }
        }
    }

    private void deleteRecipeTags(Connection conn, int recipeId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM recipe_tag WHERE recipe_id = ?")) {
            pstmt.setInt(1, recipeId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Gibt alle Zutaten für ein bestimmtes Rezept zurück.
     *
     * @param recipeId Rezept-ID
     * @return Liste der Zutaten
     */
    public List<Ingredient> getIngredientsForRecipe(int recipeId) {
        List<Ingredient> list = new ArrayList<>();
        String sql = "SELECT * FROM ingredient WHERE recipe_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recipeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Ingredient(
                        rs.getString("name"),
                        rs.getDouble("quantity"),
                        rs.getString("unit")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Gibt die Kochschritte eines Rezepts in richtiger Reihenfolge zurück.
     *
     * @param recipeId Rezept-ID
     * @return Liste der Schritte
     */
    public List<Step> getStepsForRecipe(int recipeId) {
        List<Step> list = new ArrayList<>();
        String sql = "SELECT * FROM step WHERE recipe_id = ? ORDER BY number ASC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recipeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Step step = new Step(rs.getString("description"));
                list.add(step);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Gibt die Tags für ein bestimmtes Rezept zurück.
     *
     * @param recipeId Rezept-ID
     * @return Liste der Tags
     */
    public List<Tag> getTagsForRecipe(int recipeId) {
        List<Tag> list = new ArrayList<>();
        String sql = "SELECT t.name FROM tag t JOIN recipe_tag rt ON t.id = rt.tag_id WHERE rt.recipe_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recipeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Tag(rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Gibt alle verfügbaren Tags aus der Datenbank zurück.
     *
     * @return Alphabetisch sortierte Liste aller Tags
     */
    public List<Tag> getAllTags() {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT name FROM tag ORDER BY name";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tags.add(new Tag(rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tags;
    }
}