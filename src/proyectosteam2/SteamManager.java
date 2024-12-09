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
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class SteamManager {
    public static final String CARPETA_GLOBAL = "SteamGlobal";
    private static final String CARPETA_USUARIO = "usuarios";

    public SteamManager() {
       
        File carpetaGlobal = new File(CARPETA_GLOBAL);
        if (!carpetaGlobal.exists()) {
            carpetaGlobal.mkdir();
        }
    }

    private File obtenerArchivoUsuario(String nombreUsuario, String nombreJuego) {
        return new File(CARPETA_USUARIO + "/" + nombreUsuario + "/juegos/" + nombreJuego + ".bin");
    }

    private File obtenerArchivoGlobal(String nombreJuego) {
        return new File(CARPETA_GLOBAL + "/" + nombreJuego + ".bin");
    }

    public boolean subirJuegoSteam(String nombreJuego, String genero, String desarrollador, String fechaLanzamiento, String rutaInstalacion, File caratula) {
        File archivoGlobal = obtenerArchivoGlobal(nombreJuego);

        try (RandomAccessFile raf = new RandomAccessFile(archivoGlobal, "rw")) {
            raf.writeUTF(nombreJuego);
            raf.writeUTF(genero);
            raf.writeUTF(desarrollador);
            raf.writeUTF(fechaLanzamiento);
            raf.writeUTF(rutaInstalacion);

            byte[] caratulaBytes = leerArchivoComoBytes(caratula);
            raf.writeInt(caratulaBytes.length);
            raf.write(caratulaBytes); 
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean agregarJuego(String nombreUsuario, String nombreJuego, String genero, String desarrollador, String fechaLanzamiento, String rutaInstalacion, byte[] caratulaBytes) {
        File carpetaUsuario = new File(CARPETA_USUARIO + "/" + nombreUsuario + "/juegos");
        if (!carpetaUsuario.exists()) {
            carpetaUsuario.mkdirs();
        }

        File archivoUsuario = new File(carpetaUsuario, nombreJuego + ".bin");

        try (RandomAccessFile raf = new RandomAccessFile(archivoUsuario, "rw")) {
            raf.writeUTF(nombreJuego);
            raf.writeUTF(genero);
            raf.writeUTF(desarrollador);
            raf.writeUTF(fechaLanzamiento);
            raf.writeUTF(rutaInstalacion);
            raf.writeInt(caratulaBytes.length);
            raf.write(caratulaBytes);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarJuegoDeBiblioteca(String nombreUsuario, String nombreJuego) {
        File archivoBin = obtenerArchivoUsuario(nombreUsuario, nombreJuego);
        if (archivoBin.exists() && archivoBin.delete()) {
            System.out.println("Archivo eliminado: " + archivoBin.getAbsolutePath());
            return true;
        } else {
            System.out.println("    ");
            return false;
        }
    }

    public void cargarJuegos(String nombreUsuario, SteamGUI gui) {
        File carpetaUsuario = new File(CARPETA_USUARIO + "/" + nombreUsuario + "/juegos");
        if (!carpetaUsuario.exists()) {
            gui.actualizarLista("No hay juegos en tu biblioteca.");
            return;
        }

        File[] archivos = carpetaUsuario.listFiles();
        if (archivos == null) return;

        for (File archivo : archivos) {
            try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
                String nombreJuego = raf.readUTF();
                String genero = raf.readUTF();
                String desarrollador = raf.readUTF();
                String fechaLanzamiento = raf.readUTF();
                String rutaInstalacion = raf.readUTF();
                int tamanioCaratula = raf.readInt();
                byte[] caratulaBytes = new byte[tamanioCaratula];
                raf.readFully(caratulaBytes);

                ImageIcon caratula = new ImageIcon(caratulaBytes);
                gui.agregarJuegoBibliotecaPersonal(nombreJuego, genero, desarrollador, fechaLanzamiento, rutaInstalacion, caratula);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void cargarBibliotecaSteam(SteamGUI gui) {
        File carpetaGlobal = new File(CARPETA_GLOBAL);
        if (!carpetaGlobal.exists()) {
            gui.actualizarLista("No hay juegos en la biblioteca global.");
            return;
        }

        File[] archivos = carpetaGlobal.listFiles();
        if (archivos == null) return;

        for (File archivo : archivos) {
            try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
                String nombreJuego = raf.readUTF();
                String genero = raf.readUTF();
                String desarrollador = raf.readUTF();
                String fechaLanzamiento = raf.readUTF();
                String rutaInstalacion = raf.readUTF();
                int tamanioCaratula = raf.readInt();
                byte[] caratulaBytes = new byte[tamanioCaratula];
                raf.readFully(caratulaBytes);

                ImageIcon caratula = new ImageIcon(caratulaBytes);
                gui.mostrarJuegoEnBibliotecaGlobal(nombreJuego, genero, desarrollador, fechaLanzamiento, rutaInstalacion, caratula);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] leerArchivoComoBytes(File archivo) throws IOException {
        try (FileInputStream fis = new FileInputStream(archivo)) {
            return fis.readAllBytes();
        }
    }
}
