import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Listener {
    public static String listen() {
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
            return null; // Exit if unable to open the URL
        }

        // Prompt the user to paste the redirected URL
        System.out.println("After authorizing, copy the redirected URL and paste it below:");
        Scanner scanner = new Scanner(System.in);
        String redirectedUrl = scanner.nextLine();

        // Extract the 'code' parameter from the redirected URL
        String code = extractCodeFromUrl(redirectedUrl);
        if (code != null) {
            System.out.println("Authorization code retrieved: " + code);
            scanner.close();
            return code;
            // Run another program or call another class/method
        } else {
            System.err.println("Failed to extract the authorization code. Please try again.");
            scanner.close();
            return null;
        }
    }

    // Helper method to extract the 'code' parameter from the URL
    private static String extractCodeFromUrl(String url) {
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
        return null; // Return null if 'code' is not found
    }
}


