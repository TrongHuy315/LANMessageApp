package src.client;

import java.io.File;
import java.io.FileInputStream;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import src.database.UserDAO;
import src.security.DeviceUtils;

public class ProfileClientUI {
    private Pane pane;
    private Stage stage;
    private Circle imageLayout;
    private Button imageUploadButton;

    private TextArea usernameArea;
    private ImageView pencilIcon_1;
    private TextField changedUsernameBar;

    private TextArea bioArea;
    private ImageView pencilIcon_2;
    private TextField changedBioBar;

    public ProfileClientUI(Pane pane, Stage stage) {
        this.pane = pane;
        this.stage = stage;
    }

    private void setUsernameArea() {
        String username = UserDAO.getCurrentUsername();

        usernameArea = new TextArea();
        usernameArea.setPrefSize(500, 50);
        usernameArea.setLayoutX(300);
        usernameArea.setLayoutY(100);
        usernameArea.appendText(username);

        usernameArea.setEditable(false);
        usernameArea.setFocusTraversable(false);
        usernameArea.setMouseTransparent(true);

        pane.getChildren().add(usernameArea);
    }

    private void setChangedUsernameArea() {
        String username = UserDAO.getCurrentUsername();
        changedUsernameBar = new TextField(username);

        changedUsernameBar.setPrefWidth(500);
        changedUsernameBar.setPrefHeight(50);
        changedUsernameBar.setLayoutX(300);
        changedUsernameBar.setLayoutY(100);
        changedUsernameBar.setOnAction(_ -> {
            String newUsername = changedUsernameBar.getText(); // Lam hop hien thi thong bao / canh bao
            UserDAO.setUsername(newUsername);

            usernameArea.clear();
            usernameArea.appendText(newUsername);

            pane.getChildren().remove(changedUsernameBar);
            pane.getChildren().addAll(usernameArea);
        });
    }

    private void setPencilIcon_1() {
        Image icon = new Image(getClass().getResourceAsStream("/images/pencil.png"));
        pencilIcon_1 = new ImageView(icon);

        pencilIcon_1.setFitWidth(50);
        pencilIcon_1.setFitHeight(50);
        pencilIcon_1.setStyle("-fx-effect: innershadow(gaussian, black, 100, 0.5, 0, 0);");
        pencilIcon_1.setLayoutX(800);
        pencilIcon_1.setLayoutY(100);

        pencilIcon_1.setOnMouseClicked(_ -> {
            pane.getChildren().removeAll(usernameArea);
            pane.getChildren().add(changedUsernameBar);
        });

        pane.getChildren().add(pencilIcon_1);
    }

    private void setBioArea() {
        String bio = UserDAO.getCurrentBio();

        bioArea = new TextArea();
        bioArea.setPrefSize(500, 100);
        bioArea.setLayoutX(300);
        bioArea.setLayoutY(300);
        bioArea.appendText(bio);

        bioArea.setEditable(false);
        bioArea.setFocusTraversable(false);
        bioArea.setMouseTransparent(true);

        pane.getChildren().add(bioArea);
    }

    private void setChangedBioArea() {
        String bio = UserDAO.getCurrentBio();
        changedBioBar = new TextField(bio);

        changedBioBar.setPrefWidth(500);
        changedBioBar.setPrefHeight(100);
        changedBioBar.setLayoutX(300);
        changedBioBar.setLayoutY(300);
        changedBioBar.setOnAction(_ -> {
            String newBio = changedBioBar.getText();
            UserDAO.setBio(newBio);

            bioArea.clear();
            bioArea.appendText(newBio);

            pane.getChildren().remove(changedBioBar);
            pane.getChildren().add(bioArea);
        });

        // pane.getChildren().add(changedUsernameBar);
    }

    private void setpencilIcon_2() {
        Image icon = new Image(getClass().getResourceAsStream("/images/pencil.png"));
        pencilIcon_2 = new ImageView(icon);

        pencilIcon_2.setFitWidth(50);
        pencilIcon_2.setFitHeight(50);
        pencilIcon_2.setStyle("-fx-effect: innershadow(gaussian, black, 100, 0.5, 0, 0);");
        pencilIcon_2.setLayoutX(800);
        pencilIcon_2.setLayoutY(300);

        pencilIcon_2.setOnMouseClicked(_ -> {
            pane.getChildren().remove(bioArea);
            pane.getChildren().add(changedBioBar);
        });

        pane.getChildren().add(pencilIcon_2);
    }

    private void setImageUploadButton(Stage stage) {
        imageUploadButton = new Button("Avatar Upload");
        imageUploadButton.setPrefSize(100, 20);
        imageUploadButton.setLayoutX(50);
        imageUploadButton.setLayoutY(170);
        imageUploadButton.setOnAction(_ -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Avatar Upload");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image File", "*.png", "*.jpg", "*.jpeg")
            );

            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                UserDAO.setAvatarUrl(DeviceUtils.getMacAddress(), file.getAbsolutePath());
            }
        });

        pane.getChildren().add(imageUploadButton);
    }

    private void showImage(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            Image image = new Image(fileInputStream);

            this.imageLayout = new Circle(100, 100, 50);
            this.imageLayout.setStroke(Color.web("#B0C4DE")); // Màu LightSteelBlue
            this.imageLayout.setStrokeWidth(3);

            imageLayout.setFill(new ImagePattern(image));

            pane.getChildren().add(imageLayout);
        } catch (Exception e) {
            // them hop canh bao o day
            e.printStackTrace();
        }
    }

    public void initialize() {
        pane.getChildren().clear();

        setChangedBioArea();
        setUsernameArea();
        setImageUploadButton(stage);
        setChangedUsernameArea();
        setBioArea();
        setPencilIcon_1();
        setpencilIcon_2();

        String avatarUrl = UserDAO.getAvatarUrl(DeviceUtils.getMacAddress());
        if (avatarUrl == null) return;
        
        File file = new File(avatarUrl);
        showImage(file);
    }
}
