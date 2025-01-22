package Objects;
public class Track implements Comparable<Track> {
    ImageObject image;
    String title;
   String artist;
    String album;
    String albumType;
    String releaseDate;
    int duration;
    String uri;
    String id;
    int popularity;
    String previewUrl;
    String imageUrl;
    
        public Track(ImageObject image, String title, String artist, String album, String albumType, String releaseDate, int duration, String uri, String id, int popularity, String previewUrl) {
            this.image = image;
            this.imageUrl = image.getUrl();
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.albumType = albumType;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.uri = uri;
        this.id = id;
        this.popularity = popularity;
        this.previewUrl = previewUrl;
    }

    @Override
    public int compareTo(Track o) {
        boolean isIt = this.title.equals(o.title) && this.artist.equals(o.artist) && this.album.equals(o.album);
        return isIt ? 0 : 1;
    }

    public ImageObject getImage() {
        return image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getAlbumType() {
        return albumType;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public int getDuration() {
        return duration;
    }

    public String getUri() {
        return uri;
    }

    public String getId() {
        return id;
    }

    public int getPopularity() {
        return popularity;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }
}
