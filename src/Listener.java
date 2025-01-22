import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class Listener {
    public static void browse() throws IOException {
        String clientId = "20495fa10b8d4f74bfce20d4d8fde4e5"; // Replace with your Spotify client ID
        String redirectUri = "http://localhost:8888/callback";

        String authorizationUrl = "https://accounts.spotify.com/authorize" +
    "?client_id=" + clientId +
    "&response_type=code" +
    "&redirect_uri=" + redirectUri +
    "&scope=playlist-read-private%20playlist-read-collaborative%20user-top-read" +
    "&state=YourState";
        try {
            // Open the Spotify authorization URL in the user's default browser
            URI uri = new URI(authorizationUrl);
            Desktop.getDesktop().browse(uri);
            System.out.println("Redirecting to Spotify for authorization...");
        } catch (URISyntaxException | IOException e) {
            System.err.println("Failed to open URL: " + e.getMessage());
            return; // Exit if unable to open the URL
        }

        // Prompt the user to paste the redirected URL
    }

    // Helper method to extract the 'code' parameter from the URL
    public static String extractCodeFromUrl(String url) {
        try {
            URI uri = new URI(url);
            String query = uri.getQuery(); // Get the query part of the URL
            for (String param : query.split("&")) {
                if (param.startsWith("code=")) {
                    return param.substring(5); // Extract the value of 'code'
                }
            }
        } catch (URISyntaxException e) {
            System.err.println("Invalid URL: " + e.getMessage());
        }
        catch (NullPointerException e) {
            System.err.println("Invalid URL");
        }
        return null; // Return null if 'code' is not found
    }

    
}


