/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectosteam2;

/**
 *
 * @author Junior Nuñez
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class ChatGUI extends JFrame {
    private UserManager userManager;
    private String usuarioActual;
    private String destinatario;

    private JTextArea areaMensajes;
    private JTextField campoMensaje;

    public ChatGUI(String usuarioActual, String destinatario) {
        this.usuarioActual = usuarioActual;
        this.destinatario = destinatario;
        this.userManager = new UserManager();

        setTitle("Chat de " + usuarioActual + " con " + destinatario);
        setSize(500, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
        setLayout(new BorderLayout());

        areaMensajes = new JTextArea();
        areaMensajes.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaMensajes);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout());
        campoMensaje = new JTextField();
        panelInferior.add(campoMensaje, BorderLayout.CENTER);

        JButton botonEnviar = new JButton("Enviar");
        botonEnviar.addActionListener(this::enviarMensaje);
        panelInferior.add(botonEnviar, BorderLayout.EAST);

        add(panelInferior, BorderLayout.SOUTH);

        cargarHistorial();
        setLocationRelativeTo(null);
        iniciarHiloActualizacion();
    }

    private void cargarHistorial() {
        userManager.cargarHistorialChat(usuarioActual, destinatario, this);
    }

    private void enviarMensaje(ActionEvent e) {
        String mensaje = campoMensaje.getText().trim();
        if (!mensaje.isEmpty()) {
           
            userManager.enviarMensaje(usuarioActual, destinatario, mensaje);

            areaMensajes.append(String.format("[%s] %s: %s\n",
                    new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()),
                    usuarioActual, mensaje));
            campoMensaje.setText("");
        }
    }

    private void iniciarHiloActualizacion() {
        new Thread(() -> {
            long lastModified = 0;
            File archivoChat = userManager.obtenerArchivoChat(usuarioActual, destinatario);

            while (true) {
                if (archivoChat.exists() && archivoChat.lastModified() > lastModified) {
                    lastModified = archivoChat.lastModified();

                    SwingUtilities.invokeLater(() -> {
                        areaMensajes.setText(""); 
                        cargarHistorial(); 
                    });
                }

                try {
                    Thread.sleep(1000); 
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public void actualizarMensajes(String mensaje) {
        areaMensajes.append(mensaje);
    }
}