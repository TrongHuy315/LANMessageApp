package src.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private AuthenticationClientUI authenticationClientUI;

    private Thread listeningThread;

    public ChatClient(Socket socket, PrintWriter out, BufferedReader in, AuthenticationClientUI authenticationClientUI) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.authenticationClientUI = authenticationClientUI;
    }

    protected void startThread(TextArea messageArea) {
        listeningThread = new Thread(() -> {
            try {
                out.println("[JOIN] " + authenticationClientUI.getUserName() + " đã tham gia");

                String msg;
                while ((msg = in.readLine()) != null) {
                    String finalMsg = msg;
                    Platform.runLater(() -> receiveMessage(messageArea, finalMsg));
                }
            } catch (IOException e) {
                Platform.runLater(() -> receiveMessage(messageArea, "[Lỗi kết nối] " + e.getMessage()));
            }
        });
        listeningThread.start();
    }

    protected void disconnect() {
        try {
            if (out != null) out.println("[LEAVE] User đã rời khỏi phòng chat.");
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.out.println("[Client] Lỗi khi đóng kết nối: " + e.getMessage());
        }
        if (listeningThread != null && listeningThread.isAlive()) {
            listeningThread.interrupt();
        }
    }

    protected void sendMessage(String msg) {
        if (out != null && !msg.isBlank()) {
            out.println("[" + authenticationClientUI.getUserName() + "]: " + msg);
        }
    }

    private void receiveMessage(TextArea messageArea, String msg) {
        messageArea.appendText(msg + "\n");
    }
}
