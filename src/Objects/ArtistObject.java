package Objects;

public class ArtistObject {
    String name;
    String id;
    String uri;
    String type;
    int popularity;

    public ArtistObject(String name, String id, String uri, String type, int popularity) {
        this.name = name;
        this.id = id;
        this.uri = uri;
        this.type = type;
        this.popularity = popularity;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public String getType() {
        return type;
    }

    public int getPopularity() {
        return popularity;
    }
}
