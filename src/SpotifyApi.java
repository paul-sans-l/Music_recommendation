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
    

    // Fetch user playlists (after authorization flow)
    public static List<String> getPlaylists(String accessToken) throws IOException {
        
        HttpGet get = new HttpGet("https://api.spotify.com/v1/me/playlists");
        get.setHeader("Authorization", "Bearer " + accessToken);
    
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(get)) {
    
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                System.err.println("Failed to fetch playlists. HTTP Status Code: " + statusCode);
                String responseBody = EntityUtils.toString(response.getEntity());
                System.err.println("Response Body: " + responseBody);
                return new ArrayList<>();
            }
    
            String responseBody = EntityUtils.toString(response.getEntity());
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
    
            // Check if the "items" field is present
            if (!jsonResponse.has("items")) {
                System.err.println("No playlists found in the response.");
                return new ArrayList<>();
            }
    
            JsonArray playlists = jsonResponse.getAsJsonArray("items");
            List<String> playlistNames = new ArrayList<>();
            for (int i = 0; i < playlists.size(); i++) {
                JsonObject playlist = playlists.get(i).getAsJsonObject();
                String name = playlist.get("name").getAsString();
                playlistNames.add(name);
                System.out.println("Playlist: " + name);
            }
            return playlistNames;
        }
    }
    
    
}
