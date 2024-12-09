/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectosteam2;

/**
 *
 * @author Junior Nuñes
 */

import javazoom.jl.player.Player;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class MusicaManager {
    public static final String CARPETA_GLOBAL = "SpotifyGlobal";
    private static final String CARPETA_USUARIO = "usuarios";

    private static MusicaManager instance; // Singleton instance
    private Player player;
    private Thread hiloReproduccion;
    private final AtomicBoolean isPlaying = new AtomicBoolean(false);
    private String rutaCancionActual = null;

    private MusicaManager() {
        
        File carpetaGlobal = new File(CARPETA_GLOBAL);
        if (!carpetaGlobal.exists()) {
            carpetaGlobal.mkdir();
        }
    }

    public static MusicaManager getInstance() {
        if (instance == null) {
            instance = new MusicaManager();
        }
        return instance;
    }

    private File obtenerArchivoUsuario(String nombreUsuario, String tituloCancion) {
        return new File(CARPETA_USUARIO + "/" + nombreUsuario + "/musica/" + tituloCancion + ".bin");
    }

    private File obtenerArchivoGlobal(String tituloCancion) {
        return new File(CARPETA_GLOBAL + "/" + tituloCancion + ".bin");
    }

    public boolean agregarCancionGlobal(String titulo, String artista, String album, int duracion, String rutaMp3, File caratula) {
        File archivoGlobal = obtenerArchivoGlobal(titulo);

        try (RandomAccessFile raf = new RandomAccessFile(archivoGlobal, "rw")) {
            raf.writeUTF(titulo);
            raf.writeUTF(artista);
            raf.writeUTF(album);
            raf.writeInt(duracion);
            raf.writeUTF(rutaMp3);

            byte[] caratulaBytes = leerArchivoComoBytes(caratula);
            raf.writeInt(caratulaBytes.length);
            raf.write(caratulaBytes);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void cargarCancionesGlobales(MusicaGUI gui) {
        File carpetaGlobal = new File(CARPETA_GLOBAL);
        if (!carpetaGlobal.exists()) {
            gui.actualizarLista("No hay canciones en el catálogo global.");
            return;
        }

        File[] archivos = carpetaGlobal.listFiles();
        if (archivos == null) return;

        for (File archivo : archivos) {
            try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
                String titulo = raf.readUTF();
                String artista = raf.readUTF();
                String album = raf.readUTF();
                int duracion = raf.readInt();
                String rutaMp3 = raf.readUTF();

                int tamanioCaratula = raf.readInt();
                byte[] caratulaBytes = new byte[tamanioCaratula];
                raf.readFully(caratulaBytes);

                ImageIcon caratula = new ImageIcon(caratulaBytes);
                gui.agregarCancionGlobal(titulo, artista, album, duracion, rutaMp3, caratula);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

public boolean agregarCancionBiblioteca(String nombreUsuario, String titulo, String artista, String album, int duracion, String rutaMp3, byte[] caratulaBytes) {
    File carpetaUsuario = new File(CARPETA_USUARIO + "/" + nombreUsuario + "/musica");
    if (!carpetaUsuario.exists()) {
        carpetaUsuario.mkdirs(); 
    }

    File archivoUsuario = new File(carpetaUsuario, titulo + ".mp3");

    try {
        
        copiarArchivoMP3(rutaMp3, archivoUsuario.getAbsolutePath());

        File archivoMetadata = new File(carpetaUsuario, titulo + ".bin");
        try (RandomAccessFile raf = new RandomAccessFile(archivoMetadata, "rw")) {
            raf.writeUTF(titulo);
            raf.writeUTF(artista);
            raf.writeUTF(album);
            raf.writeInt(duracion);
            raf.writeUTF(archivoUsuario.getAbsolutePath()); 
            raf.writeInt(caratulaBytes.length);
            raf.write(caratulaBytes);
        }

        return true;
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
}

    private void copiarArchivoMP3(String rutaOrigen, String rutaDestino) throws IOException {
        File archivoOrigen = new File(rutaOrigen);
        File archivoDestino = new File(rutaDestino);

        if (!archivoDestino.getParentFile().exists()) {
            archivoDestino.getParentFile().mkdirs();
        }

        try (FileInputStream fis = new FileInputStream(archivoOrigen); FileOutputStream fos = new FileOutputStream(archivoDestino)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }

    public void cargarBibliotecaPersonal(String nombreUsuario, MusicaGUI gui) {
        File carpetaUsuario = new File(CARPETA_USUARIO + "/" + nombreUsuario + "/musica");
        if (!carpetaUsuario.exists()) {
            gui.actualizarLista("No hay canciones en tu biblioteca.");
            return;
        }

        File[] archivos = carpetaUsuario.listFiles((dir, name) -> name.endsWith(".bin"));
        if (archivos == null) {
            return;
        }

        for (File archivo : archivos) {
            try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
                String titulo = raf.readUTF();
                String artista = raf.readUTF();
                String album = raf.readUTF();
                int duracion = raf.readInt();
                String rutaMp3 = raf.readUTF();
                int tamanioCaratula = raf.readInt();
                byte[] caratulaBytes = new byte[tamanioCaratula];
                raf.readFully(caratulaBytes);

                ImageIcon caratula = new ImageIcon(caratulaBytes);
                gui.agregarCancionBiblioteca(titulo, artista, album, duracion, rutaMp3, caratula);
            } catch (IOException e) {
                System.err.println("Error al leer el archivo " + archivo.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void limpiarArchivosCorruptos(String nombreUsuario) {
        File carpetaUsuario = new File(CARPETA_USUARIO + "/" + nombreUsuario + "/musica");
        if (!carpetaUsuario.exists()) {
            return;
        }

        File[] archivos = carpetaUsuario.listFiles();
        if (archivos == null) {
            return;
        }

        for (File archivo : archivos) {
            try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
                raf.readUTF();
                raf.readUTF();
                raf.readUTF();
                raf.readInt();
                raf.readUTF();
            } catch (IOException e) {
                System.err.println("Archivo corrupto encontrado: " + archivo.getName() + ". Eliminando...");
                archivo.delete();
            }
        }
    }

     private byte[] leerArchivoComoBytes(File archivo) throws IOException {
        try (FileInputStream fis = new FileInputStream(archivo)) {
            return fis.readAllBytes();
        }
    }

    public boolean eliminarCancionBiblioteca(String nombreUsuario, String rutaCancion) {
    File archivoMp3 = new File(rutaCancion); 
    File archivoBin = new File(rutaCancion.replace(".mp3", ".bin"));

    boolean eliminadoMp3 = false;
    boolean eliminadoBin = false;

    if (archivoMp3.exists()) {
        eliminadoMp3 = archivoMp3.delete();
        if (eliminadoMp3) {
            System.out.println("Archivo MP3 eliminado: " + archivoMp3.getAbsolutePath());
        } else {
            System.err.println("No se pudo eliminar el archivo MP3: " + archivoMp3.getAbsolutePath());
        }
    } else {
        System.err.println("El archivo MP3 no existe: " + archivoMp3.getAbsolutePath());
    }

    if (archivoBin.exists()) {
        eliminadoBin = archivoBin.delete();
        if (eliminadoBin) {
            System.out.println("Archivo BIN eliminado: " + archivoBin.getAbsolutePath());
        } else {
            System.err.println("No se pudo eliminar el archivo BIN: " + archivoBin.getAbsolutePath());
        }
    } else {
        System.err.println("El archivo BIN no existe: " + archivoBin.getAbsolutePath());
    }

    return eliminadoMp3 && eliminadoBin;
}

   
 // *** Reproducir canción ***
    public void reproducirCancion(String rutaMp3) {
        File archivo = new File(rutaMp3);
        if (!archivo.exists()) {
            System.err.println("El archivo no existe: " + rutaMp3);
            JOptionPane.showMessageDialog(null, "La canción no se encuentra disponible.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        detenerCancion(); 

        rutaCancionActual = rutaMp3;
        hiloReproduccion = new Thread(() -> {
            try (FileInputStream fis = new FileInputStream(archivo)) {
                player = new Player(fis);
                isPlaying.set(true);
                player.play();
                isPlaying.set(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        hiloReproduccion.start();
    }

    public void detenerCancion() {
        if (player != null) {
            player.close();
            isPlaying.set(false);
        }
        if (hiloReproduccion != null && hiloReproduccion.isAlive()) {
            hiloReproduccion.interrupt();
        }
        rutaCancionActual = null; 
    }
    
    public boolean hayCancionEnReproduccion() {
        return isPlaying.get();
    }

    public String getRutaCancionActual() {
        return rutaCancionActual;
    }
}