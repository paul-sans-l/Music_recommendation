
import java.lang.reflect.Type;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import Objects.Track;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;



public class App extends Application {
    private static String accessToken;
    private String clientId = "20495fa10b8d4f74bfce20d4d8fde4e5"; // Replace with your Spotify client ID
    private String redirectUri = "http://localhost:8888/callback";
    private String clientSecret = "60fa5f30ef674a89a353e3cc4eb745a7";

    @Override
    public void start(Stage primaryStage) {
        
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 500);

        Image icon = new Image(getClass().getResource("Assets/Logo_processed.jpg").toExternalForm());
        primaryStage.getIcons().add(icon);

        

        Image image = new Image(getClass().getResource("Assets/Spotify_Full_logo.png").toExternalForm());
        Image loadImage = new Image(getClass().getResource("Assets/Loading-.gif").toExternalForm());
        Image welcome = new Image(getClass().getResource("Assets/Welcome_message-.gif").toExternalForm());
        ImageView imageView = new ImageView(image);
        ImageView loadingImage = new ImageView(loadImage);
        ImageView welcomeImage = new ImageView(welcome);
        imageView.setFitWidth(3432 / 20); // Adjust width as needed
        imageView.setFitHeight(940 / 20); // Adjust height as needed
        loadingImage.setFitWidth(80 * 8); // Adjust width as needed
        loadingImage.setFitHeight(60 * 8); // Adjust height as needed
        welcomeImage.setFitWidth(1600*2/15); // Adjust width as needed
        welcomeImage.setFitHeight(1200*2/15); // Adjust height as needed
       
        primaryStage.setTitle("Spotify custom recommendations");
        scene.getStylesheets().add(getClass().getResource("Assets/Style.css").toExternalForm());
        primaryStage.setScene(scene);
        
        // Layout setup
        
        TextField textField = new TextField();
        textField.setPromptText("Enter the authorization URL here");
        Button submitButton = new Button("Submit");

        

        textField.getStyleClass().add("text-field");
        submitButton.getStyleClass().add("button");
        VBox inputLayout = new VBox(10, textField, submitButton);
        // Display welcome image first
        StackPane welcomePane = new StackPane(welcomeImage);
        root.setCenter(welcomePane);
        javafx.animation.PauseTransition welcomePause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(5.6));
        welcomePause.setOnFinished(e -> {
            root.setCenter(null);
            StackPane imagePane = new StackPane(imageView);
        imagePane.setAlignment(Pos.TOP_RIGHT);
        imagePane.setPadding(new Insets(10, 10, 10, 10));

        root.setTop(imagePane);
       
        // Create text input and submit button
      
        
        inputLayout.setAlignment(Pos.CENTER);
        inputLayout.setPadding(new Insets(10));

        // Add input layout to the center of the root pane
        root.setCenter(inputLayout);
        
        });
        welcomePause.play();
        
        
        submitButton.setOnAction(event -> {
            
            String inputText = textField.getText();

            VBox trackButtonsContainer = new VBox();
            trackButtonsContainer.setSpacing(10);
            trackButtonsContainer.setPadding(new Insets(10));

            
            System.out.println("Submitted text: " + inputText);
            String authorizationCode = Listener.extractCodeFromUrl(inputText);
            textField.setVisible(false);
            submitButton.setVisible(false);
            
            root.setCenter(loadingImage);
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(5));
            pause.setOnFinished(e -> {
                try {
                    
                    accessToken = SpotifyApi.getAccessToken(clientId, clientSecret, authorizationCode, redirectUri);
                    SpotifyApi.getTracks(accessToken, "6RWiSU4cAHKJUPBaMCXSIV");
                    List<Track> tracks = loadTracksFromJson("src/Tracks.json");
                    for (Track track : tracks) {
                        Button trackButton = createTrackButton(track);
                        trackButtonsContainer.getChildren().add(trackButton);
                    }
                    
                    root.setCenter(trackButtonsContainer);


                } catch (Exception e1) {
                System.out.println("Error: " + e1);
                root.setCenter(inputLayout);
                textField.setVisible(true);
                submitButton.setVisible(true);
                Label errorMessage = new Label("The URL is invalid. Please try again. If you lost the URL, please restart the application.");
                errorMessage.setTextFill(Color.RED);
                root.setBottom(errorMessage);
                BorderPane.setAlignment(errorMessage, Pos.CENTER);

                javafx.animation.PauseTransition errorPause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(5));
                errorPause.setOnFinished(ev -> root.setBottom(null));
                errorPause.play();
                return;
                }
                //root.setCenter(null); // Remove the loading image after 5 seconds
            });
            pause.play();
            
          
        });

        // Layout for text input and button
        primaryStage.show();
        try {
            Listener.browse();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//-----------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------


        
    }

    private Button createTrackButton(Track track) {
        // Create the button
        Button button = new Button();
        button.setPrefWidth(380);
        button.setPrefHeight(120);
        button.getStyleClass().add("track-button");

        // Create the HBox layout
        HBox content = new HBox();
        content.setSpacing(10);
        content.getStyleClass().add("track-content");

        // Add an image for the play icon
        ImageView playIcon = new ImageView(new Image(track.getImageUrl())); // Replace with your image path
        playIcon.setFitHeight(24*2);
        playIcon.setFitWidth(24*2);

        // Add track details
        Text titleText = new Text(track.getTitle());
        Text artistText = new Text(track.getArtist());

        titleText.getStyleClass().add("track-title");
        artistText.getStyleClass().add("track-artist");
       

        content.getChildren().addAll(playIcon,titleText, artistText);
        button.setGraphic(content);

        return button;
    }

   public List<Track> loadTracksFromJson(String filename) throws IOException {
        // Create a Gson instance
        Gson gson = new Gson();

        // Define the type for the list of tracks
        Type trackListType = new TypeToken<ArrayList<Track>>() {}.getType();

        // Read the JSON file and parse it into a list of Track objects
        try (FileReader reader = new FileReader(filename)) {
            return gson.fromJson(reader, trackListType);
        }
    }

    

    public static void main(String[] args) {
        launch(args);
    }

    
}
