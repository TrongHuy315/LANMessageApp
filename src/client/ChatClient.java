package src.client;

import java.io.PrintWriter;
import javafx.scene.control.TextArea;

class ChatClient {
    private PrintWriter out;
    private AuthenticationClientUI authenticationClientUI;

    public ChatClient(PrintWriter out, AuthenticationClientUI authenticationClientUI) {
        this.out = out;
        this.authenticationClientUI = authenticationClientUI;
    }

    protected void sendMessage(String msg) {
        if (out != null && !msg.isBlank()) {
            out.println("[" + authenticationClientUI.getUserName() + "]: " + msg);
        }
    }

    protected void receiveMessage(TextArea messageArea, String msg) {
        messageArea.appendText(msg + "\n");
    }
}
