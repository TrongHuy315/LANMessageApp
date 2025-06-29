package src.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import config.Config;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ChatClientUI extends Application {
    private Pane pane;
    private TextArea messageArea;
    private TextField inputField;
    private Button sendButton;
    private Scene scene;
    private ChatClient chatClient;
    private AuthenticationClientUI authenticationClientUI;
    private ProfileClientUI profileClientUI;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    @Override
    public void start(Stage stage) {
        pane = new Pane();
        pane.setPrefSize(Integer.parseInt(Config.get("pane.width")), Integer.parseInt(Config.get("pane.height")));

        try {
            socket = new Socket(Config.get("app.host"), Integer.parseInt(Config.get("app.port")));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            
            profileClientUI = new ProfileClientUI(pane, stage);
            authenticationClientUI = new AuthenticationClientUI(pane, out, in, profileClientUI);

            chatClient = new ChatClient(socket, out, in, authenticationClientUI);
        } catch (IOException e) {
            System.out.println("[Client] Lỗi khi đóng kết nối: " + e.getMessage());
        }
        
        scene = new Scene(pane);
        setStage(stage);
    }

    private void setMessageArea(Pane pane) {
        messageArea = new TextArea();
        messageArea.setPrefHeight(Integer.parseInt(Config.get("messageArea.height")));
        messageArea.setPrefWidth(Integer.parseInt(Config.get("messageArea.width")));
        messageArea.setLayoutX(Integer.parseInt(Config.get("messageArea.x")));
        messageArea.setLayoutY(Integer.parseInt(Config.get("messageArea.y")));
        messageArea.setEditable(false);
        
        pane.getChildren().add(messageArea);
    }

    private void setInputField(Pane pane) {
        inputField = new TextField();
        inputField.setPrefHeight(Integer.parseInt(Config.get("inputField.height")));
        inputField.setPrefWidth(Integer.parseInt(Config.get("inputField.width")));
        inputField.setLayoutX(Integer.parseInt(Config.get("inputField.x")));
        inputField.setLayoutY(Integer.parseInt(Config.get("inputField.y")));
        inputField.setPromptText("Nhập tin nhắn...");
        inputField.setOnAction(_ -> {
            chatClient.sendMessage(inputField.getText());
            inputField.clear();
        });

        pane.getChildren().add(inputField);
    }

    private void setStage(Stage stage) {
        stage.setScene(scene);
        stage.setTitle("Chat Client");
        stage.show();
        stage.setOnCloseRequest(_ -> {
            stage.close();
            chatClient.disconnect();
            Platform.exit();
            System.exit(0);
        });
    }

    private void setSendButton(Pane pane) {
        sendButton = new Button("➤");
        sendButton.setPrefSize(Integer.parseInt(Config.get("sendButton.width")), Integer.parseInt(Config.get("sendButton.height")));
        sendButton.setLayoutX(Integer.parseInt(Config.get("sendButton.x")));
        sendButton.setLayoutY(Integer.parseInt(Config.get("sendButton.y")));
        sendButton.setStyle("-fx-background-color: #0084ff; -fx-text-fill: white; -fx-font-size: 18px;");
        sendButton.setOnAction(_ -> {
            chatClient.sendMessage(inputField.getText());
            inputField.clear();
        });

        pane.getChildren().add(sendButton);
    }

    public void initialize() {
        pane.getChildren().clear();

        setMessageArea(pane);
        setInputField(pane);
        setSendButton(pane);

        chatClient.startThread(messageArea);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
