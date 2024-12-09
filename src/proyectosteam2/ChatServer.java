/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectosteam2;

/**
 *
 * @author Junior Nuñes
 */
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ChatServer {
    private static final int PORT = 12345;
    private ConcurrentHashMap<String, PrintWriter> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        new ChatServer().startServer();
    }

    public void startServer() {
        System.out.println("Servidor de chat iniciado...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado.");
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private String username;
        private BufferedReader input;
        private PrintWriter output;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);

                username = input.readLine();
                if (username == null) return;

                synchronized (clients) {
                    clients.put(username, output);
                }
                System.out.println("Usuario registrado: " + username);

                String message;
                while ((message = input.readLine()) != null) {
                    String[] parts = message.split("\\|", 2);
                    if (parts.length == 2) {
                        String recipient = parts[0].trim();
                        String msgContent = parts[1].trim();

                        PrintWriter recipientOutput = clients.get(recipient);
                        if (recipientOutput != null) {
                            recipientOutput.println(username + ": " + msgContent);
                        } else {
                            output.println("Usuario " + recipient + " no está disponible.");
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error en cliente " + username + ": " + e.getMessage());
            } finally {
                if (username != null) {
                    clients.remove(username);
                    System.out.println("Usuario desconectado: " + username);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar socket: " + e.getMessage());
                }
            }
        }
    }
}