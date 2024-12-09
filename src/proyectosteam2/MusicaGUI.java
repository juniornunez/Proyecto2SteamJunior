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

public class MusicaGUI extends JFrame {
    private final JPanel panelGlobal;
    private final JPanel panelBibliotecaPersonal;
    private final MusicaManager gestorMusica;
    private final String usuario;
    private String rutaCancionSeleccionada = null;
    private final boolean esAdmin;

    public MusicaGUI(String usuario, boolean esAdmin) {
        this.usuario = usuario;
        this.esAdmin = esAdmin;
        this.gestorMusica = MusicaManager.getInstance(); 

        setTitle("Spotify - Usuario: " + usuario);
        setSize(1024, 768);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel panelTitulos = new JPanel(new GridLayout(1, 2));
        panelTitulos.add(new JLabel("Catalogo Global", SwingConstants.CENTER));
        panelTitulos.add(new JLabel("Mi Biblioteca", SwingConstants.CENTER));
        add(panelTitulos, BorderLayout.NORTH);

        panelGlobal = new JPanel();
        panelGlobal.setLayout(new BoxLayout(panelGlobal, BoxLayout.Y_AXIS));
        JScrollPane scrollGlobal = new JScrollPane(panelGlobal);

        panelBibliotecaPersonal = new JPanel();
        panelBibliotecaPersonal.setLayout(new BoxLayout(panelBibliotecaPersonal, BoxLayout.Y_AXIS));
        JScrollPane scrollBiblioteca = new JScrollPane(panelBibliotecaPersonal);

        JPanel panelCentral = new JPanel(new GridLayout(1, 2));
        panelCentral.add(scrollGlobal);
        panelCentral.add(scrollBiblioteca);
        add(panelCentral, BorderLayout.CENTER);

        JButton botonAgregarGlobal = new JButton("Añadir Cancion Global");
        JButton botonAgregarBiblioteca = new JButton("Añadir a Biblioteca");
        JButton botonEliminarBiblioteca = new JButton("Eliminar de Biblioteca");
        JButton botonReproducir = new JButton("Reproducir");
        JButton botonDetener = new JButton("Detener");
        JButton botonVolver = new JButton("Volver al Menu Principal");

        JPanel panelBotones = new JPanel(new FlowLayout());
        if (esAdmin) {
            panelBotones.add(botonAgregarGlobal);
        }
        panelBotones.add(botonAgregarBiblioteca);
        panelBotones.add(botonEliminarBiblioteca);
        panelBotones.add(botonReproducir);
        panelBotones.add(botonDetener);
        panelBotones.add(botonVolver);
        add(panelBotones, BorderLayout.SOUTH);

        // Eventos de los botones
        botonAgregarGlobal.addActionListener(e -> agregarCancionGlobal());
        botonAgregarBiblioteca.addActionListener(e -> agregarAColeccionPersonal());
        botonEliminarBiblioteca.addActionListener(e -> eliminarDeBiblioteca());
        botonReproducir.addActionListener(e -> reproducirCancion());

        botonDetener.addActionListener(e -> {
            if (gestorMusica.hayCancionEnReproduccion()) {
                gestorMusica.detenerCancion();
                JOptionPane.showMessageDialog(this, "Reproduccion detenida.", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No hay ninguna cancion en reproduccion.", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        botonVolver.addActionListener(e -> {
            dispose();
            new MainMenuFrame(usuario).setVisible(true);
        });

        cargarCancionesGlobales();
        cargarBibliotecaPersonal();
        panelGlobal.setBackground(Color.GRAY);
        panelBibliotecaPersonal.setBackground(Color.GRAY);
        panelBotones.setBackground(Color.BLACK);


    }

    @Override
    public void dispose() {
        super.dispose();
    }

    private void cargarCancionesGlobales() {
        panelGlobal.removeAll();
        gestorMusica.cargarCancionesGlobales(this);
        panelGlobal.revalidate();
        panelGlobal.repaint();
    }

    private void cargarBibliotecaPersonal() {
        panelBibliotecaPersonal.removeAll();
        gestorMusica.cargarBibliotecaPersonal(usuario, this);
        panelBibliotecaPersonal.revalidate();
        panelBibliotecaPersonal.repaint();
    }

    private void agregarCancionGlobal() {
    JTextField campoTitulo = new JTextField();
    JTextField campoArtista = new JTextField();
    JTextField campoAlbum = new JTextField();
    JTextField campoDuracion = new JTextField();
    JFileChooser fileChooserMp3 = new JFileChooser();
    JFileChooser fileChooserCaratula = new JFileChooser();

    Object[] mensaje = {
        "Titulo:", campoTitulo,
        "Artista:", campoArtista,
        "Album:", campoAlbum,
        "Duracion (en segundos):", campoDuracion,
        "Archivo MP3:", fileChooserMp3,
        "Caratula (JPG/PNG):", fileChooserCaratula
    };

    int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Agregar Cancion al Catalogo Global", JOptionPane.OK_CANCEL_OPTION);
    if (opcion == JOptionPane.OK_OPTION) {
        try {
            String titulo = campoTitulo.getText();
            String artista = campoArtista.getText();
            String album = campoAlbum.getText();
            int duracion = Integer.parseInt(campoDuracion.getText());
            File archivoMp3 = fileChooserMp3.getSelectedFile();
            File archivoCaratula = fileChooserCaratula.getSelectedFile();

            // Validar campos vacíos
            if (titulo.isEmpty() || artista.isEmpty() || album.isEmpty() || archivoMp3 == null || archivoCaratula == null) {
                JOptionPane.showMessageDialog(this, "Por favor llena todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar extensión del archivo
            if (!archivoMp3.getName().toLowerCase().endsWith(".mp3")) {
                JOptionPane.showMessageDialog(this, "El archivo seleccionado no es un MP3. Por favor selecciona un archivo valido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean resultado = gestorMusica.agregarCancionGlobal(titulo, artista, album, duracion, archivoMp3.getAbsolutePath(), archivoCaratula);
            if (resultado) {
                JOptionPane.showMessageDialog(this, "Cancion añadida al catalogo global.");
                cargarCancionesGlobales();
            } else {
                JOptionPane.showMessageDialog(this, "Error al añadir la cancion.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Duracion invalida. Asegurate de ingresar un numero valido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrio un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}


   private void agregarAColeccionPersonal() {
    if (rutaCancionSeleccionada == null) {
        JOptionPane.showMessageDialog(this, "Selecciona una cancion del catalogo global.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String titulo = null;
    String artista = null;
    String album = null;
    int duracion = 0;
    String rutaMp3 = null;
    byte[] caratulaBytes = null;

    File carpetaGlobal = new File(MusicaManager.CARPETA_GLOBAL);
    File[] archivos = carpetaGlobal.listFiles();
    if (archivos != null) {
        for (File archivo : archivos) {
            try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
                String tituloActual = raf.readUTF();
                String artistaActual = raf.readUTF();
                String albumActual = raf.readUTF();
                int duracionActual = raf.readInt();
                String rutaMp3Actual = raf.readUTF();
                int tamanioCaratula = raf.readInt();
                byte[] caratulaActual = new byte[tamanioCaratula];
                raf.readFully(caratulaActual);

                if (rutaMp3Actual.equals(rutaCancionSeleccionada)) {
                    titulo = tituloActual;
                    artista = artistaActual;
                    album = albumActual;
                    duracion = duracionActual;
                    rutaMp3 = rutaMp3Actual;
                    caratulaBytes = caratulaActual;
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    if (titulo == null || rutaMp3 == null) {
        JOptionPane.showMessageDialog(this, "Esta cancion ya esta en tu biblioteca!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    boolean resultado = gestorMusica.agregarCancionBiblioteca(usuario, titulo, artista, album, duracion, rutaMp3, caratulaBytes);
    if (resultado) {
        JOptionPane.showMessageDialog(this, "Cancion añadida a tu biblioteca.");
        cargarBibliotecaPersonal();
    } else {
        JOptionPane.showMessageDialog(this, "Error al añadir la canción a tu biblioteca.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void eliminarDeBiblioteca() {
    if (rutaCancionSeleccionada == null) {
        return; 
    }

    File carpetaGlobal = new File(MusicaManager.CARPETA_GLOBAL);
    File[] archivosGlobales = carpetaGlobal.listFiles();
    if (archivosGlobales != null) {
        for (File archivo : archivosGlobales) {
            try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
                raf.readUTF(); 
                raf.readUTF(); 
                raf.readUTF(); 
                raf.readInt(); 
                String rutaMp3 = raf.readUTF(); 

                if (rutaCancionSeleccionada.equals(rutaMp3)) {
                    return; 
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    if (gestorMusica.getRutaCancionActual() != null &&
        gestorMusica.getRutaCancionActual().equals(rutaCancionSeleccionada)) {
        gestorMusica.detenerCancion(); 
    }

    boolean resultado = gestorMusica.eliminarCancionBiblioteca(usuario, rutaCancionSeleccionada);
    if (resultado) {
        JOptionPane.showMessageDialog(this, "Cancion eliminada de tu biblioteca.");
        cargarBibliotecaPersonal();
    } else {
        JOptionPane.showMessageDialog(this, "Error al eliminar la cancion de tu biblioteca.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

     private void reproducirCancion() {
        if (rutaCancionSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una cancion para reproducir.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        gestorMusica.reproducirCancion(rutaCancionSeleccionada);
    }

public void agregarCancionGlobal(String titulo, String artista, String album, int duracion, String ruta, ImageIcon caratula) {
   
    Image image = caratula.getImage();
    Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH); 
    ImageIcon scaledIcon = new ImageIcon(scaledImage);

    JButton botonCancion = new JButton(String.format("%s - %s (%s)", titulo, artista, album), scaledIcon);
    botonCancion.setHorizontalTextPosition(SwingConstants.RIGHT);
    botonCancion.setToolTipText(String.format("Duracion: %d seg | Ruta: %s", duracion, ruta));
    botonCancion.addActionListener(e -> {
        rutaCancionSeleccionada = ruta;
        JOptionPane.showMessageDialog(this, String.format("Seleccionada: %s", titulo));
    });
    panelGlobal.add(botonCancion);
}

public void agregarCancionBiblioteca(String titulo, String artista, String album, int duracion, String ruta, ImageIcon caratula) {
    
    Image image = caratula.getImage();
    Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH); 
    ImageIcon scaledIcon = new ImageIcon(scaledImage);

    JButton botonCancion = new JButton(String.format("%s - %s (%s)", titulo, artista, album), scaledIcon);
    botonCancion.setHorizontalTextPosition(SwingConstants.RIGHT);
    botonCancion.setToolTipText(String.format("Duracion: %d seg | Ruta: %s", duracion, ruta));
    botonCancion.addActionListener(e -> {
        rutaCancionSeleccionada = ruta;
        JOptionPane.showMessageDialog(this, String.format("Seleccionada: %s", titulo));
    });
    panelBibliotecaPersonal.add(botonCancion);
}

     public void actualizarLista(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Mensaje", JOptionPane.INFORMATION_MESSAGE);
    }
}