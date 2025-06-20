package src.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import config.Config;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class AuthenticationClientUI {
    private Pane pane;
    private PrintWriter out;
    private BufferedReader in;
    private Button createAccountButton;
    private TextField nameField;
    private boolean checkFlag = false;

    private AuthenticationClient authenticationClient;
    private ChatClientUI parent;

    public AuthenticationClientUI(Pane pane, PrintWriter out, BufferedReader in, ChatClientUI parent) {
        this.pane = pane;
        this.out = out;
        this.in = in;
        this.parent = parent;
        
        setCreateButton();
        setNameField();
    }

    private void setCreateButton() {
        createAccountButton = new Button();
        createAccountButton.setPrefSize(Integer.parseInt(Config.get("createAccountButton.width")), Integer.parseInt(Config.get("createAccountButton.height")));
        createAccountButton.setLayoutX(Integer.parseInt(Config.get("createAccountButton.x")));
        createAccountButton.setLayoutY(Integer.parseInt(Config.get("createAccountButton.y")));
        createAccountButton.setOnAction(_ -> {
            authenticationClient = new AuthenticationClient(nameField.getText(), out);
            if (authenticationClient.getUsername() == null) {
                try {
                    String msg = in.readLine();
                    AlertHelper.showWarning("Alert", msg);
                } catch (IOException e) {
                    Platform.runLater(() -> AlertHelper.showWarning("Alert", "Undefined Error"));
                }
            }
            else {
                AlertHelper.showWarning("Notification", "Access Successfully");
                checkFlag = true;
                Platform.runLater(() -> parent.initialize());
            }
        });

        pane.getChildren().add(createAccountButton);
    }

    private void setNameField() {
        nameField = new TextField();
        nameField.setPrefSize(Integer.parseInt(Config.get("nameField.width")), Integer.parseInt(Config.get("nameField.height")));
        nameField.setLayoutX(Integer.parseInt(Config.get("nameField.x")));
        nameField.setLayoutY(Integer.parseInt(Config.get("nameField.y")));
        nameField.setOnAction(_ -> {
            authenticationClient = new AuthenticationClient(nameField.getText(), out);
            if (authenticationClient.getUsername() == null) {
                try {
                    String msg = in.readLine();
                    AlertHelper.showWarning("Alert", msg);
                } catch (IOException e) {
                    Platform.runLater(() -> AlertHelper.showWarning("Alert", "Undefined Error"));
                }
            }
            else {
                AlertHelper.showWarning("Notification", "Access Successfully");
                checkFlag = true;
                Platform.runLater(() -> parent.initialize());
            }
        });

        pane.getChildren().add(nameField);
    }

    public boolean getFlag() {
        return checkFlag;
    }

    public String getUserName() {
        return this.authenticationClient.getUsername();
    }
}
