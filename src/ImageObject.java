public class ImageObject {
    int height;
    int width;
    String url;

    public ImageObject(int height, int width, String url) {
        this.height = height;
        this.width = width;
        this.url = url;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getUrl() {
        return url;
    }
}