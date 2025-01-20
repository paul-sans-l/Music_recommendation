package Objects;

public class Oneofs {
    ArtistObject artist;
    Track track;

    public Oneofs(ArtistObject artist, Track track) {
        this.artist = artist;
        this.track = track;
    }

    public ArtistObject getArtist() {
        return artist;
    }

    public Track getTrack() {
        return track;
    }
}
