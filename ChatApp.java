// Enhanced Java Socket Chat App with Modern UI
// Uses Swing with improved layout, colors, panels, and better UX
package main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatApp {

    // ---------------- SERVER ----------------
    static class ChatServer {
        private ServerSocket serverSocket;
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public ChatServer(int port, JTextArea display) {
            new Thread(() -> {
                try {
                    serverSocket = new ServerSocket(port);
                    display.append("🌐 Server started on port " + port + "...\n");
                    clientSocket = serverSocket.accept();
                    display.append("🟢 Client connected!\n");

                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new PrintWriter(clientSocket.getOutputStream(), true);

                    String msg;
                    while ((msg = in.readLine()) != null) {
                        display.append("👤 Client: " + msg + "\n");
                    }
                } catch (Exception e) {
                    display.append("❌ Error: " + e.getMessage() + "\n");
                }
            }).start();
        }

        public void sendMessage(String msg) {
            if (out != null) out.println(msg);
        }
    }

    // ---------------- CLIENT ----------------
    static class ChatClient {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ChatClient(String host, int port, JTextArea display) {
            new Thread(() -> {
                try {
                    socket = new Socket(host, port);
                    display.append("🟢 Connected to server!\n");

                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);

                    String msg;
                    while ((msg = in.readLine()) != null) {
                        display.append("🌍 Server: " + msg + "\n");
                    }
                } catch (Exception e) {
                    display.append("❌ Error: " + e.getMessage() + "\n");
                }
            }).start();
        }

        public void sendMessage(String msg) {
            if (out != null) out.println(msg);
        }
    }

    // ---------------- UI ----------------
    public static void main(String[] args) {
        JFrame frame = new JFrame("💬 Modern Socket Chat App");
        frame.setSize(550, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Color theme
        Color bg = new Color(204, 255, 229);
        Color accent = new Color(66, 135, 245);
        Color dark = new Color(40, 40, 40);

        // Chat area
        JTextArea chatDisplay = new JTextArea();
        chatDisplay.setEditable(false);
        chatDisplay.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        chatDisplay.setBackground(Color.WHITE);
        chatDisplay.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(chatDisplay);
        scroll.setBorder(BorderFactory.createLineBorder(accent, 2));

        // Input area
        JTextField inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JButton sendBtn = new JButton("Send ➤");
        sendBtn.setBackground(accent);
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendBtn.setFocusPainted(false);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top controls
        String[] modes = {"Server", "Client"};
        JComboBox<String> modeSelect = new JComboBox<>(modes);
        JTextField hostField = new JTextField("localhost");
        JTextField portField = new JTextField("5000");
        JButton connectBtn = new JButton("Start");

        connectBtn.setBackground(new Color(0, 180, 75));
        connectBtn.setForeground(Color.WHITE);
        connectBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        connectBtn.setFocusPainted(false);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 4, 8, 0));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(bg);

        topPanel.add(modeSelect);
        topPanel.add(hostField);
        topPanel.add(portField);
        topPanel.add(connectBtn);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);

        final ChatServer[] server = new ChatServer[1];
        final ChatClient[] client = new ChatClient[1];

        // Connect button action
        connectBtn.addActionListener(e -> {
            String mode = (String) modeSelect.getSelectedItem();
            int port = Integer.parseInt(portField.getText());

            if (mode.equals("Server")) {
                server[0] = new ChatServer(port, chatDisplay);
            } else {
                client[0] = new ChatClient(hostField.getText(), port, chatDisplay);
            }
        });

        // Send button
        sendBtn.addActionListener(e -> {
            String msg = inputField.getText().trim();
            if (msg.isEmpty()) return;

            inputField.setText("");
            chatDisplay.append("🧑 Me: " + msg + "\n");

            if (server[0] != null) server[0].sendMessage(msg);
            if (client[0] != null) client[0].sendMessage(msg);
        });

        frame.getContentPane().setBackground(bg);
        frame.setVisible(true);
    }
}