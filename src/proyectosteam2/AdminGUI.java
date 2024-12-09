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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AdminGUI extends JFrame {
    private AdminManager adminManager;
    private JComboBox<String> comboUsuarios;
    private JTree treeArchivos;
    private DefaultTreeModel modeloArbol;
    private String usuarioActual;

    public AdminGUI(String usuarioActual) {
        this.usuarioActual = usuarioActual;
        adminManager = new AdminManager();

        setTitle("Panel de Administrador - Usuario: " + usuarioActual);
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelSuperior = new JPanel(new FlowLayout());
        JLabel etiquetaUsuarios = new JLabel("Usuarios:");
        comboUsuarios = new JComboBox<>();
        JButton botonCargarArbol = new JButton("Ver Archivos");

        panelSuperior.add(etiquetaUsuarios);
        panelSuperior.add(comboUsuarios);
        panelSuperior.add(botonCargarArbol);
        panelSuperior.setBackground(Color.GRAY);

        add(panelSuperior, BorderLayout.NORTH);

        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Usuarios");
        modeloArbol = new DefaultTreeModel(raiz);
        treeArchivos = new JTree(modeloArbol);
        JScrollPane scrollTree = new JScrollPane(treeArchivos);
        scrollTree.setBackground(Color.LIGHT_GRAY);

        add(scrollTree, BorderLayout.CENTER);

        JButton botonVolver = new JButton("Volver al Menu Principal");
        botonVolver.addActionListener(e -> {
            dispose();
            new MainMenuFrame(usuarioActual).setVisible(true);
        });
        setLocationRelativeTo(null);
        add(botonVolver, BorderLayout.SOUTH);

        botonCargarArbol.addActionListener(this::mostrarArchivosUsuario);

        cargarUsuarios();
    }

    private void cargarUsuarios() {
        comboUsuarios.removeAllItems();
        adminManager.cargarUsuarios(this);
    }

    private void mostrarArchivosUsuario(ActionEvent e) {
    String usuarioSeleccionado = (String) comboUsuarios.getSelectedItem();
    if (usuarioSeleccionado == null || usuarioSeleccionado.isEmpty()) {
        mostrarMensaje("Por favor selecciona un usuario.");
        return;
    }

    DefaultMutableTreeNode raiz = adminManager.cargarArchivosUsuario(usuarioSeleccionado);
    if (raiz == null) {
        mostrarMensaje("No se encontraron archivos para el usuario seleccionado.");
        return;
    }
    modeloArbol.setRoot(raiz); 
    treeArchivos.expandRow(0); 
}

    public void agregarUsuario(String usuario) {
        comboUsuarios.addItem(usuario);
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Mensaje", JOptionPane.INFORMATION_MESSAGE);
    }
}