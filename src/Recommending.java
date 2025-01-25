import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Objects.ArtistObject;
import Objects.ImageObject;
import Objects.Track;
import Objects.RecomandationSeedObject;
import Objects.Oneofs;

public class Recommending {

    public static List<Track> getRecomTracks(String accessToken) throws IOException {

        HttpGet get = new HttpGet("https://api.spotify.com/v1/tracks?ids=2CGNAOSuO1MEFCbBRgUzjd,6jTQijAuYxOd8DjQ8D6UkL,6plp1nJtm4Y3m87qmDCy61");
        get.setHeader("Authorization", "Bearer " + accessToken);
    
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(get)) {
    
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                System.err.println("Failed to fetch tracks. HTTP Status Code: " + statusCode);
                String responseBody = EntityUtils.toString(response.getEntity());
                System.err.println("Response Body: " + responseBody);
                return new ArrayList<>();
            }
    
            String responseBody = EntityUtils.toString(response.getEntity());
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
    
            // Check if the "tracks" field is present
            if (!jsonResponse.has("tracks")) {
                System.err.println("No tracks found in the response.");
                return new ArrayList<>();
            }
            JsonArray tracks = jsonResponse.getAsJsonArray("tracks");
            List<Track> trackList = new ArrayList<>();
            for (int i = 0; i < tracks.size(); i++) {
                JsonObject track = tracks.get(i).getAsJsonObject();
                JsonObject album = track.getAsJsonObject("album");
    
                // Access image
                JsonArray images = album.getAsJsonArray("images");
                String imageUrl = images.size() > 0 ? images.get(0).getAsJsonObject().get("url").getAsString() : "";
                ImageObject image = new ImageObject(50, 50, imageUrl);
    
                // Access title
                String title = track.get("name").getAsString();
    
                // Access artist
                JsonArray artists = track.getAsJsonArray("artists");
                String artist = artists.size() > 0 ? artists.get(0).getAsJsonObject().get("name").getAsString() : "";
    
                // Access album name
                String albumName = album.get("name").getAsString();
    
                // Access album type
                String albumType = album.get("album_type").getAsString();
    
                // Access release date
                String releaseDate = album.get("release_date").getAsString();
    
                // Access duration
                int duration = track.get("duration_ms").getAsInt();
    
                // Access URI
                String uri = track.get("uri").getAsString();
    
                // Access ID
                String id = track.get("id").getAsString();
    
                // Access popularity
                int popularity = track.get("popularity").getAsInt();
    
                // Access preview URL
                String previewUrl = track.has("preview_url") && !track.get("preview_url").isJsonNull() ? track.get("preview_url").getAsString() : "";
    
                // Create Track object
                Track newTrack = new Track(image, title, artist, albumName, albumType, releaseDate, duration, uri, id, popularity, previewUrl);
    
                trackList.add(newTrack);
                System.out.println("Track: " + title + " by " + artist);
            }
            System.out.println("Tracks: " + trackList);
            updateTrackFiles(trackList);
            return trackList;
        }
    }
    

    private static void updateTrackFiles(List<Track> tracks) throws IOException {
        Set<String> existingTrackIds = new HashSet<>();
        JsonArray existingTracksArray = new JsonArray();
    
        // Read existing tracks from Tracks.json
        try (FileReader reader = new FileReader("Tracks.json")) {
            existingTracksArray = JsonParser.parseReader(reader).getAsJsonArray();
            for (int i = 0; i < existingTracksArray.size(); i++) {
                JsonObject track = existingTracksArray.get(i).getAsJsonObject();
                existingTrackIds.add(track.get("id").getAsString());
            }
        } catch (IOException e) {
            System.err.println("Tracks.json file not found. A new file will be created.");
        }
    
        // Append new tracks if they are not already in Tracks.json
        for (Track track : tracks) {
            if (!existingTrackIds.contains(track.getId())) {
                JsonObject trackJson = new JsonObject();
                trackJson.addProperty("imageUrl", track.getImage().getUrl());
                trackJson.addProperty("title", track.getTitle());
                trackJson.addProperty("artist", track.getArtist());
                trackJson.addProperty("album", track.getAlbum());
                trackJson.addProperty("albumType", track.getAlbumType());
                trackJson.addProperty("releaseDate", track.getReleaseDate());
                trackJson.addProperty("duration", track.getDuration());
                trackJson.addProperty("uri", track.getUri());
                trackJson.addProperty("id", track.getId());
                trackJson.addProperty("popularity", track.getPopularity());
                trackJson.addProperty("previewUrl", track.getPreviewUrl());
                existingTracksArray.add(trackJson);
            }
        }
    
        // Write the updated tracks to Tracks.json
        try (FileWriter writer = new FileWriter("src/Tracks.json")) {
            writer.write(existingTracksArray.toString());
        }
    }
    
    
    private static List<ArtistObject> getTopArtists(String accessToken) throws IOException {
        HttpGet get = new HttpGet("https://api.spotify.com/v1/me/top/artists?limit=2");
        get.setHeader("Authorization", "Bearer " + accessToken);
    
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(get)) {
    
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                System.err.println("Failed to fetch top artists. HTTP Status Code: " + statusCode);
                String responseBody = EntityUtils.toString(response.getEntity());
                System.err.println("Response Body: " + responseBody);
                return new ArrayList<>();
            }
    
            String responseBody = EntityUtils.toString(response.getEntity());
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
    
            // Check if the "items" field is present
            if (!jsonResponse.has("items")) {
                System.err.println("No artists found in the response.");
                return new ArrayList<>();
            }
    
            JsonArray artists = jsonResponse.getAsJsonArray("items");
            List<ArtistObject> artistList = new ArrayList<>();
            for (int i = 0; i < artists.size(); i++) {
                JsonObject artist = artists.get(i).getAsJsonObject();
                String name = artist.get("name").getAsString();
                String id = artist.get("id").getAsString();
                int popularity = artist.get("popularity").getAsInt();
                String uri = artist.get("uri").getAsString();
                String type = artist.get("type").getAsString();
                artistList.add(new ArtistObject(name, id, uri, type, popularity));
                System.out.println("Artist: " + name);
            }
            return artistList;
        }
    }

    private static List<Track> getTopTracks(String accessToken) throws IOException {
        HttpGet get = new HttpGet("https://api.spotify.com/v1/me/top/tracks?limit=3");
        get.setHeader("Authorization", "Bearer " + accessToken);
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(get)) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            System.err.println("Failed to fetch top artists. HTTP Status Code: " + statusCode);
            String responseBody = EntityUtils.toString(response.getEntity());
            System.err.println("Response Body: " + responseBody);
            return new ArrayList<>();
        }

        String responseBody = EntityUtils.toString(response.getEntity());
        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

        // Check if the "items" field is present
        if (!jsonResponse.has("items")) {
            System.err.println("No artists found in the response.");
            return new ArrayList<>();
        }

        JsonArray tracks = jsonResponse.getAsJsonArray("items");
        List<Track> trackList = new ArrayList<>();
        for (int i = 0; i < tracks.size(); i++) {
            JsonObject track = tracks.get(i).getAsJsonObject();
            JsonObject album = track.getAsJsonObject("album");
            JsonArray images = album.getAsJsonArray("images");
            String imageUrl = images.size() > 0 ? images.get(0).getAsJsonObject().get("url").getAsString() : "";
            ImageObject image = new ImageObject(50,50,imageUrl);
            String title = track.get("name").getAsString();
            String artist = track.getAsJsonArray("artists").get(0).getAsJsonObject().get("name").getAsString();
            String albumName = album.get("name").getAsString();
            String albumType = album.get("album_type").getAsString();
            String releaseDate = album.get("release_date").getAsString();
            int duration = track.get("duration_ms").getAsInt();
            String uri = track.get("uri").getAsString();
            String id = track.get("id").getAsString();
            int popularity = track.get("popularity").getAsInt();
            String previewUrl = track.has("preview_url") && !track.get("preview_url").isJsonNull() ? track.get("preview_url").getAsString() : "";

            Track newTrack = new Track(image, title, artist, albumName, albumType, releaseDate, duration, uri, id, popularity, previewUrl);
            trackList.add(newTrack);
            System.out.println("Track: " + title + " by " + artist);
        }
        return trackList;
    }
}

}
