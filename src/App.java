import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.awt.Desktop;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class App extends Application {
    private static String accessToken;

    @Override
    public void start(Stage primaryStage) {
        // Display access token (for testing purposes)
        System.out.println("Spotify Access Token: " + accessToken);

        // Load image for UI (Spotify logo)
        Image image = new Image(getClass().getResource("Assets/Spotify_Full_logo.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(3432 / 20); // Adjust width as needed
        imageView.setFitHeight(940 / 20); // Adjust height as needed

        // Layout setup
        BorderPane root = new BorderPane();
        StackPane imagePane = new StackPane(imageView);
        imagePane.setAlignment(Pos.TOP_RIGHT);
        imagePane.setPadding(new Insets(10, 10, 10, 10));

        root.setTop(imagePane);
        Scene scene = new Scene(root, 800, 500);
        primaryStage.setTitle("Customizable Desktop App");
        scene.getStylesheets().add(getClass().getResource("Assets/Style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        //System.out.println();
        String clientId = "20495fa10b8d4f74bfce20d4d8fde4e5"; // Replace with your Spotify client ID
        String clientSecret = "60fa5f30ef674a89a353e3cc4eb745a7"; // Replace with your Spotify client secret
        String code = Listener.listen();
        String redirectUri = "http://localhost:8888/callback";


        try {
            // Get access token using client credentials flow (change to use OAuth for user data)
            accessToken = SpotifyApi.getAccessToken(clientId, clientSecret, code, redirectUri);
            // Fetch and print playlists (run in background thread to not block UI)
            new Thread(() -> {
                try {
                    SpotifyApi.getPlaylists(accessToken);
                } catch (IOException e) {
                    System.err.println("Failed to fetch playlists: " + e.getMessage());
                }
            }).start();

        } catch (IOException e) {
            System.err.println("Failed to retrieve Spotify access token: " + e.getMessage());
        }

        // Launch JavaFX application
        launch(args);
    }
}
