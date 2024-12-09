/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectosteam2;

/**
 *
 * @author Junior Nuñes
 */
    import javax.swing.*;
    import java.awt.*;
    import java.io.File;
    import java.io.IOException;
    import java.io.RandomAccessFile;
    public class SteamGUI extends JFrame {
        private final JPanel panelGlobal;
        private final JPanel panelBibliotecaPersonal;
        private final SteamManager steamManager;
        private final String usuario;
        private final boolean esAdmin; // Nuevo atributo
        private String juegoSeleccionado = null;

        public SteamGUI(String usuario, boolean esAdmin) { // Modifica el constructor
            this.usuario = usuario;
            this.esAdmin = esAdmin; // Inicializa el nuevo atributo
            this.steamManager = new SteamManager();

            setTitle("Steam - Usuario: " + usuario);
            setSize(1024, 768);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout());
            setLocationRelativeTo(null);

            panelGlobal = new JPanel();
            panelGlobal.setLayout(new BoxLayout(panelGlobal, BoxLayout.Y_AXIS));
            JScrollPane scrollGlobal = new JScrollPane(panelGlobal);
            scrollGlobal.setBorder(BorderFactory.createTitledBorder("Biblioteca Global"));

            panelBibliotecaPersonal = new JPanel();
            panelBibliotecaPersonal.setLayout(new BoxLayout(panelBibliotecaPersonal, BoxLayout.Y_AXIS));
            JScrollPane scrollBiblioteca = new JScrollPane(panelBibliotecaPersonal);
            scrollBiblioteca.setBorder(BorderFactory.createTitledBorder("Mi Biblioteca"));

            JPanel panelCentral = new JPanel(new GridLayout(1, 2));
            panelCentral.add(scrollGlobal);
            panelCentral.add(scrollBiblioteca);
            add(panelCentral, BorderLayout.CENTER);

            // Botones
            JButton botonAgregarGlobal = new JButton("Añadir Juego Global");
            JButton botonDescargar = new JButton("Descargar a Biblioteca");
            JButton botonEliminar = new JButton("Eliminar Juego");
            JButton botonVolver = new JButton("Volver al Menú Principal");

            JPanel panelBotones = new JPanel();
            if (esAdmin) { // Muestra solo si el usuario es administrador
                panelBotones.add(botonAgregarGlobal);
            }
            panelBotones.add(botonDescargar);
            panelBotones.add(botonEliminar);
            panelBotones.add(botonVolver);
            add(panelBotones, BorderLayout.SOUTH);

            // Eventos de botones
            botonAgregarGlobal.addActionListener(e -> añadirJuegoGlobal());
            botonDescargar.addActionListener(e -> descargarJuego());
            botonEliminar.addActionListener(e -> eliminarJuego());
            botonVolver.addActionListener(e -> {
                dispose();
                new MainMenuFrame(usuario).setVisible(true);
            });

            steamManager.cargarBibliotecaSteam(this);
            steamManager.cargarJuegos(usuario, this);
        }


        private void añadirJuegoGlobal() {
    JTextField campoNombre = new JTextField();
    JComboBox<String> comboGenero = new JComboBox<>();
    JTextField campoDesarrollador = new JTextField();
    JTextField campoFecha = new JTextField();
    JFileChooser fileChooserCaratula = new JFileChooser();

    for (GameGenre genre : GameGenre.values()) {
        comboGenero.addItem(genre.name()); 
    }

    Object[] mensaje = {
        "Nombre del Juego:", campoNombre,
        "Género:", comboGenero,
        "Desarrollador:", campoDesarrollador,
        "Fecha de Lanzamiento:", campoFecha,
        "Carátula del Juego:", fileChooserCaratula
    };

    int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Añadir Juego a Biblioteca Global", JOptionPane.OK_CANCEL_OPTION);
    if (opcion == JOptionPane.OK_OPTION) {
        try {
            String nombre = campoNombre.getText();
            String genero = (String) comboGenero.getSelectedItem(); 
            String desarrollador = campoDesarrollador.getText();
            String fecha = campoFecha.getText();
            File caratula = fileChooserCaratula.getSelectedFile();

            if (nombre.isEmpty() || genero.isEmpty() || desarrollador.isEmpty() || fecha.isEmpty() || caratula == null) {
                JOptionPane.showMessageDialog(this, "Por favor completa todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean resultado = steamManager.subirJuegoSteam(nombre, genero, desarrollador, fecha, "", caratula);
            if (resultado) {
                JOptionPane.showMessageDialog(this, "Juego añadido al catálogo global.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                panelGlobal.removeAll();
                steamManager.cargarBibliotecaSteam(this);
            } else {
                JOptionPane.showMessageDialog(this, "Error al añadir el juego.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en los datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

        private void descargarJuego() {
            if (juegoSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un juego del catálogo global.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            File carpetaGlobal = new File(SteamManager.CARPETA_GLOBAL);
            File[] archivos = carpetaGlobal.listFiles();
            if (archivos == null) return;

            for (File archivo : archivos) {
                try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
                    String nombreJuego = raf.readUTF();
                    String genero = raf.readUTF();
                    String desarrollador = raf.readUTF();
                    String fecha = raf.readUTF();
                    String ruta = raf.readUTF();
                    int tamanioCaratula = raf.readInt();
                    byte[] caratulaBytes = new byte[tamanioCaratula];
                    raf.readFully(caratulaBytes);

                    if (nombreJuego.equals(juegoSeleccionado)) {
                        boolean resultado = steamManager.agregarJuego(usuario, nombreJuego, genero, desarrollador, fecha, ruta, caratulaBytes);
                        if (resultado) {
                            JOptionPane.showMessageDialog(this, "Juego añadido a tu biblioteca.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            panelBibliotecaPersonal.removeAll();
                            steamManager.cargarJuegos(usuario, this);
                        } else {
                            JOptionPane.showMessageDialog(this, "Error al descargar el juego.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

       private void eliminarJuego() {
        if (juegoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un juego de tu biblioteca.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean resultado = steamManager.eliminarJuegoDeBiblioteca(usuario, juegoSeleccionado);
        if (resultado) {
            JOptionPane.showMessageDialog(this, "Juego eliminado de tu biblioteca.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            panelBibliotecaPersonal.removeAll();
            panelBibliotecaPersonal.revalidate();
            panelBibliotecaPersonal.repaint();

            steamManager.cargarJuegos(usuario, this);
        } else {

        }
    }

        public void mostrarJuegoEnBibliotecaGlobal(String nombre, String genero, String desarrollador, String fecha, String ruta, ImageIcon caratula) {
        JButton botonJuego = crearBotonJuego(nombre, genero, desarrollador, fecha, ruta, caratula);
        panelGlobal.add(botonJuego);
        panelGlobal.revalidate();
        panelGlobal.repaint();
    }

    public void agregarJuegoBibliotecaPersonal(String nombre, String genero, String desarrollador, String fecha, String ruta, ImageIcon caratula) {
        JButton botonJuego = crearBotonJuego(nombre, genero, desarrollador, fecha, ruta, caratula);
        panelBibliotecaPersonal.add(botonJuego);
        panelBibliotecaPersonal.revalidate();
        panelBibliotecaPersonal.repaint();
    }

        private JButton crearBotonJuego(String nombre, String genero, String desarrollador, String fecha, String ruta, ImageIcon caratula) {

        Image image = caratula.getImage();
        Image scaledImage = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JButton botonJuego = new JButton("<html>" + nombre + "<br>" + genero + "</html>", scaledIcon);
        botonJuego.setHorizontalTextPosition(SwingConstants.CENTER);
        botonJuego.setVerticalTextPosition(SwingConstants.BOTTOM);
        botonJuego.setPreferredSize(new Dimension(200, 200));
        botonJuego.setBackground(Color.LIGHT_GRAY);

        botonJuego.addActionListener(e -> {
            juegoSeleccionado = nombre;
            JOptionPane.showMessageDialog(SteamGUI.this, "Juego seleccionado: " + nombre);
        });

        return botonJuego;
    }

        public void actualizarLista(String mensaje) {
            JOptionPane.showMessageDialog(this, mensaje, "Mensaje", JOptionPane.INFORMATION_MESSAGE);
        }
    }