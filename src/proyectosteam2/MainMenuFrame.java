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
import java.awt.event.ActionListener;

public class MainMenuFrame extends JFrame {
    public MainMenuFrame(String nombreUsuario) {
        setTitle("Menu Principal - Bienvenido " + nombreUsuario);
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelOpciones = new JPanel(new GridLayout(2, 2, 10, 10));

        ImageIcon steamIcon = new ImageIcon(new ImageIcon("src/fotos/logoSteam.png").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        ImageIcon spotifyIcon = new ImageIcon(new ImageIcon("src/fotos/logospotify.png").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        ImageIcon chatIcon = new ImageIcon(new ImageIcon("src/fotos/Logochat.png").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        ImageIcon AdminIcon = new ImageIcon(new ImageIcon("src/fotos/logoadmin.png").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));

        JButton botonSteam = new JButton("Steam", steamIcon);
        JButton botonSpotify = new JButton("Spotify", spotifyIcon);
        JButton botonChat = new JButton("Chat", chatIcon);
        JButton botonAdmin = new JButton("Administrador", AdminIcon);
        botonSteam.setBackground(Color.LIGHT_GRAY);
        botonSpotify.setBackground(Color.LIGHT_GRAY);
        botonChat.setBackground(Color.LIGHT_GRAY);
        botonAdmin.setBackground(Color.LIGHT_GRAY);

        panelOpciones.add(botonSteam);
        panelOpciones.add(botonSpotify);
        panelOpciones.add(botonChat);
        panelOpciones.add(botonAdmin);
        panelOpciones.setBackground(Color.BLACK);
        

        JPanel panelInferior = new JPanel(new FlowLayout());
        JButton botonLogout = new JButton("Cerrar Sesion");
        panelInferior.add(botonLogout);

        add(panelOpciones, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        botonLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        botonSpotify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean esAdmin = new UserManager().esAdmin(nombreUsuario);
                new MusicaGUI(nombreUsuario, esAdmin).setVisible(true); 
                dispose();
            }
        });

        botonChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ChatUserListGUI(nombreUsuario).setVisible(true);
                dispose();
            }
        });
        
        botonSteam.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserManager userManager = new UserManager();
                boolean esAdmin = userManager.esAdmin(nombreUsuario); 
                new SteamGUI(nombreUsuario, esAdmin).setVisible(true); 
                dispose();
            }
        });

        botonAdmin.addActionListener(e -> {
            UserManager userManager = new UserManager();
            if (userManager.esAdmin(nombreUsuario)) {
                dispose();
                new AdminGUI(nombreUsuario).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "No tienes permisos para acceder al panel de administrador.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
            }
        });

        setLocationRelativeTo(null);
    }


}