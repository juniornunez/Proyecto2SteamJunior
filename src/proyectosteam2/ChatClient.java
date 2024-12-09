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

public class ChatClient {
    private String usuarioActual;
    private String destinatario;
    private File archivoChat;

    public ChatClient(String usuarioActual, String destinatario) {
        this.usuarioActual = usuarioActual;
        this.destinatario = destinatario;

        File carpetaChats = new File("usuarios/" + usuarioActual + "/chatsuser");
        if (!carpetaChats.exists()) {
            carpetaChats.mkdirs();
        }
        this.archivoChat = new File(carpetaChats, "chatcon" + destinatario + ".bin");
    }

    public void enviarMensaje(String mensaje) {
        try (RandomAccessFile raf = new RandomAccessFile(archivoChat, "rw")) {
            raf.seek(raf.length());
            raf.writeUTF(usuarioActual);
            raf.writeUTF(mensaje);
            raf.writeUTF(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cargarHistorial(ChatGUI gui) {
        if (!archivoChat.exists()) {
            gui.actualizarMensajes("                 ");
            return;
        }

        try (RandomAccessFile raf = new RandomAccessFile(archivoChat, "r")) {
            while (raf.getFilePointer() < raf.length()) {
                String remitente = raf.readUTF();
                String mensaje = raf.readUTF();
                String timestamp = raf.readUTF();
                gui.actualizarMensajes(String.format("[%s] %s: %s\n", timestamp, remitente, mensaje));
            }
        } catch (IOException e) {
            gui.actualizarMensajes("Error al cargar historial de chat.\n");
        }
    }
}