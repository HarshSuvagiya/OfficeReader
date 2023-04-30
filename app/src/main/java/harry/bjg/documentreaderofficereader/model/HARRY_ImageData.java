package harry.bjg.documentreaderofficereader.model;

public class HARRY_ImageData {
    String title;
    String imageUrl;
    int width;
    int height;

    public HARRY_ImageData(String title, String imageUrl, int width, int height) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.width = width;
        this.height = height;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
