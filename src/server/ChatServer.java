package src.server;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import config.Config;
import src.database.MessageDAO;

public class ChatServer {
    private static List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("[Server] Starting on port " + Integer.parseInt(Config.get("app.port")));
        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(Config.get("app.port")))) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[Server] New client: " + clientSocket);
                ClientHandler handler = new ClientHandler(clientSocket);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.out.println("[Server] Error: " + e.getMessage());
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                System.out.println("[Server] Client init failed: " + e.getMessage());
                closeResources();
            }
        }

        public void run() {
            String msg;
            try {
                while ((msg = in.readLine()) != null) {
                    System.out.println("[Server Received]: " + msg);
                    broadcast(msg);

                    // Nếu là tin nhắn định dạng [Tên]: Nội dung
                    if (msg.startsWith("[") && msg.contains("]:")) {
                        int nameEnd = msg.indexOf("]:");
                        String sender = msg.substring(1, nameEnd);
                        String content = msg.substring(nameEnd + 2).trim();
                        MessageDAO.save(sender, content);
                    }
                }
            } catch (IOException e) {
                System.out.println("[Server] Client disconnected: " + socket);
            } finally {
                closeResources();
                clients.remove(this);
            }
        }

        private void broadcast(String msg) {
            if (msg.equals(Config.get("security.status.account.exist"))) {
                this.out.println("Username does not match mac address");
                return;
            }

            for (ClientHandler c : clients) {
                c.out.println(msg);
            }
        }

        private void closeResources() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) {
                System.out.println("[Server] Failed to close client resources.");
            }
        }
    }
}
