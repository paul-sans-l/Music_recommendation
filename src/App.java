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

    @Override
    public void start(Stage primaryStage) {
        

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
        // Create text input and submit button
        TextField textField = new TextField();
        textField.setPromptText("Enter text here");
        Button submitButton = new Button("Submit");

        textField.getStyleClass().add("text-field");
        submitButton.getStyleClass().add("button");
        
        submitButton.setOnAction(event -> {
            String inputText = textField.getText();
            System.out.println("Submitted text: " + inputText);
        });

        // Layout for text input and button
        VBox inputLayout = new VBox(10, textField, submitButton);
        inputLayout.setAlignment(Pos.CENTER);
        inputLayout.setPadding(new Insets(10));

        // Add input layout to the center of the root pane
        root.setCenter(inputLayout);
        primaryStage.setTitle("Spotify custom recommendations");
        scene.getStylesheets().add(getClass().getResource("Assets/Style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println("Spotify Access Token: " + accessToken);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
