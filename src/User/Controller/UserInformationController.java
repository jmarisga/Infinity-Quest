package User.Controller;

import Objects.Content;
import Objects.Director;
import Database.DatabaseHandler;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UserInformationController implements Initializable{

    @FXML
    private Button backButton;

    // ============================== INFORMATION ================================

    @FXML
    private ImageView contentImageView;

    @FXML
    private Hyperlink trailerHyperlink;

    @FXML
    private Label titleLabel;

    @FXML
    private Label directorLabel;

    @FXML
    private Label releaseDateLabel;

    @FXML
    private Label runtimeLabel;

    @FXML
    private Label ageRatingLabel;

    @FXML
    private Label seasonLabel;

    @FXML
    private Label episodeLabel;

    @FXML
    private Label synopsisLabel;
    
    private Content content;
    private Stage stage;
    private Scene scene;
    private Parent root;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        backButton.setOnAction(this::backButtonHandler);
    }
    
    public void setContent(Content content) {
        this.content = content;
        displayContentDetails();
    }
    
    private void displayContentDetails() {
        if (content == null) {
            return;
        }
        
        titleLabel.setText(content.getContentTitle());
        
        try {
            ResultSet rs = DatabaseHandler.getDirectorById(content.getDirectorID());
            if (rs.next()) {
                directorLabel.setText(rs.getString("directorName"));
            } else {
                directorLabel.setText("Unknown Director");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            directorLabel.setText("Unknown Director");
        }
        
        if (content.getContentReleaseDate() != null) {
            releaseDateLabel.setText(content.getContentReleaseDate().toString());
        } else {
            releaseDateLabel.setText("Unknown Release Date");
        }
        
        runtimeLabel.setText(content.getContentRuntime());
        ageRatingLabel.setText(content.getContentAgeRating());
        synopsisLabel.setText(content.getContentSynopsis());
        
        if (content.getContentSeason() != null) {
            seasonLabel.setText("Season: " + content.getContentSeason());
            seasonLabel.setVisible(true);
        } else {
            seasonLabel.setVisible(false);
        }
        
        if (content.getContentEpisode() != null) {
            episodeLabel.setText("Episode: " + content.getContentEpisode());
            episodeLabel.setVisible(true);
        } else {
            episodeLabel.setVisible(false);
        }
        
        String trailerURL = content.getContentTrailer();
        if (trailerURL != null && !trailerURL.isEmpty()) {
            trailerHyperlink.setOnAction(event -> {
                try {
                    Desktop.getDesktop().browse(new URI(trailerURL));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Could not open trailer URL: " + trailerURL);
                }
            });
            trailerHyperlink.setVisible(true);
        } else {
            trailerHyperlink.setVisible(false);
        }
        
        try {
            String posterPath = content.getContentPoster();
            if (posterPath != null && !posterPath.isEmpty()) {
                File file = new File(posterPath);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    contentImageView.setImage(image);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading poster image: " + e.getMessage());
        }
    }
    
    @FXML
    private void backButtonHandler(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/FXML/UserHome.fxml"));
            root = loader.load();
            
            UserHomeController controller = loader.getController();
            controller.setUsername(((Node) event.getSource()).getScene().getWindow().getUserData().toString());
            controller.initializeUserHome();
            
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Could not load home screen");
        }
    }
    
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Infinity Quest");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
