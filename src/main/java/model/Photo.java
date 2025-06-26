package model;


/**
 * Repräsentiert ein Foto, das zu einem Rezept gehört (Dateipfad).
 */
public class Photo {

    private String filePath;

    public Photo() {
    }

    public Photo(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return filePath;
    }
}