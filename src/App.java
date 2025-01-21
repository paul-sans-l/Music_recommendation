import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

import Objects.Authorizer;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


public class App extends Application {
    private static String accessToken;
    private String clientId = "20495fa10b8d4f74bfce20d4d8fde4e5"; // Replace with your Spotify client ID
    private String redirectUri = "http://localhost:8888/callback";
    private String clientSecret = "60fa5f30ef674a89a353e3cc4eb745a7";

    @Override
    public void start(Stage primaryStage) {
        

        Image image = new Image(getClass().getResource("Assets/Spotify_Full_logo.png").toExternalForm());
        Image loadImage = new Image(getClass().getResource("Assets/Loading.gif").toExternalForm());
        ImageView imageView = new ImageView(image);
        ImageView loadingImage = new ImageView(loadImage);
        imageView.setFitWidth(3432 / 20); // Adjust width as needed
        imageView.setFitHeight(940 / 20); // Adjust height as needed
        loadingImage.setFitWidth(80 * 8); // Adjust width as needed
        loadingImage.setFitHeight(60 * 8); // Adjust height as needed

        // Layout setup
        BorderPane root = new BorderPane();
        StackPane imagePane = new StackPane(imageView);
        imagePane.setAlignment(Pos.TOP_RIGHT);
        imagePane.setPadding(new Insets(10, 10, 10, 10));

        root.setTop(imagePane);
        Scene scene = new Scene(root, 800, 500);
        // Create text input and submit button
        TextField textField = new TextField();
        textField.setPromptText("Enter the authorization URL here");
        Button submitButton = new Button("Submit");

        textField.getStyleClass().add("text-field");
        submitButton.getStyleClass().add("button");
        VBox inputLayout = new VBox(10, textField, submitButton);
        inputLayout.setAlignment(Pos.CENTER);
        inputLayout.setPadding(new Insets(10));

        // Add input layout to the center of the root pane
        root.setCenter(inputLayout);
        primaryStage.setTitle("Spotify custom recommendations");
        scene.getStylesheets().add(getClass().getResource("Assets/Style.css").toExternalForm());
        primaryStage.setScene(scene);
        
        submitButton.setOnAction(event -> {
            String inputText = textField.getText();
            System.out.println("Submitted text: " + inputText);
            String authorizationCode = Listener.extractCodeFromUrl(inputText);
            try {
                textField.setVisible(false);
                submitButton.setVisible(false);
                accessToken = SpotifyApi.getAccessToken(clientId, clientSecret, authorizationCode, redirectUri);
                root.setCenter(loadingImage);
                SpotifyApi.getTracks(accessToken, "6OfVAQ5lGMxCWbzLIziE8P"); // Load then display the tracks using thread
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Layout for text input and button
        primaryStage.show();
        try {
            Listener.browse();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
