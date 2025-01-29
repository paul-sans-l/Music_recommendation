import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Objects.ImageObject;
import Objects.Track;


public class SpotifyApi {

    // Use client credentials flow for access token
    public static String getAccessToken(String clientId, String clientSecret, String code, String redirectUri) throws IOException {
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
    
        HttpPost post = new HttpPost("https://accounts.spotify.com/api/token");
        post.setHeader("Authorization", "Basic " + encodedAuth);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
    
        StringEntity entity = new StringEntity("grant_type=authorization_code&code=" + code + "&redirect_uri=" + redirectUri);
        post.setEntity(entity);
    
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(post)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
            return json.get("access_token").getAsString();
        }
    }
    


    public static List<Track> getTracks(String accessToken, String playlistId) throws IOException {
        HttpGet get = new HttpGet("https://api.spotify.com/v1/playlists/" + playlistId + "/tracks");
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
    
            // Check if the "items" field is present
            if (!jsonResponse.has("items")) {
                System.err.println("No tracks found in the response.");
                return new ArrayList<>();
            }
            
            JsonArray tracks = jsonResponse.getAsJsonArray("items");
            List<Track> trackList = new ArrayList<>();
            for (int i = 0; i < tracks.size(); i++) {
                JsonObject track = tracks.get(i).getAsJsonObject().getAsJsonObject("track");
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
            //updateTrackFiles(trackList);
            return trackList;
        }
    }

    
}
