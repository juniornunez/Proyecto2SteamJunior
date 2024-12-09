/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectosteam2;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

public final class UserManager {
    private static final String FILE_PATH = "users.bin";
    private static final String CARPETA_USUARIOS = "usuarios";
    

    public UserManager() {
      
        File directorioUsuarios = new File(CARPETA_USUARIOS);
        if (!directorioUsuarios.exists()) {
            directorioUsuarios.mkdir();
        }

       
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   public boolean registerUser(String username, String password, boolean isAdmin) {
    try (RandomAccessFile raf = new RandomAccessFile(FILE_PATH, "rw")) {
       
        if (verificarUsuarioRecursivamente(raf, username, 0)) {
            return false; 
        }

        raf.seek(raf.length());
        raf.writeUTF(username);
        raf.writeUTF(password);
        raf.writeBoolean(isAdmin);

        crearCarpetaUsuario(username);

        return true;
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
}

private boolean verificarUsuarioRecursivamente(RandomAccessFile raf, String username, int index) throws IOException {
   
    if (raf.getFilePointer() >= raf.length()) {
        return false; 
    }

    String existingUsername = raf.readUTF();
    raf.readUTF(); 
    raf.readBoolean(); 

    if (existingUsername.equals(username)) {
        return true;
    }
    
    return verificarUsuarioRecursivamente(raf, username, index + 1);
}

    public boolean esAdmin(String username) {
    try (RandomAccessFile raf = new RandomAccessFile(FILE_PATH, "r")) {
        while (raf.getFilePointer() < raf.length()) {
            String existingUsername = raf.readUTF();
            raf.readUTF(); 
            boolean isAdmin = raf.readBoolean();

            if (existingUsername.equals(username)) {
                return isAdmin;
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return false; 
}

    public boolean loginUser(String username, String password) {
        try (RandomAccessFile raf = new RandomAccessFile(FILE_PATH, "r")) {
            while (raf.getFilePointer() < raf.length()) {
                String existingUsername = raf.readUTF();
                String existingPassword = raf.readUTF();
                raf.readBoolean(); 
                if (existingUsername.equals(username) && existingPassword.equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void crearCarpetaUsuario(String nombreUsuario) {
    File carpetaUsuario = new File(CARPETA_USUARIOS + "/" + nombreUsuario);
    if (!carpetaUsuario.exists() && !carpetaUsuario.mkdir()) {
        throw new RuntimeException("No se pudo crear la carpeta del usuario: " + nombreUsuario);
    }

    File carpetaMusica = new File(carpetaUsuario, "musica");
    if (!carpetaMusica.exists() && !carpetaMusica.mkdir()) {
        throw new RuntimeException("No se pudo crear la carpeta de música para el usuario: " + nombreUsuario);
    }

    File carpetaJuegos = new File(carpetaUsuario, "juegos");
    if (!carpetaJuegos.exists() && !carpetaJuegos.mkdir()) {
        throw new RuntimeException("No se pudo crear la carpeta de juegos para el usuario: " + nombreUsuario);
    }

    File carpetaChats = new File(carpetaUsuario, "chatsuser");
    if (!carpetaChats.exists() && !carpetaChats.mkdir()) {
        throw new RuntimeException("No se pudo crear la carpeta de chats para el usuario: " + nombreUsuario);
    }
}


    public File obtenerArchivoChat(String usuarioActual, String destinatario) {
        
        File carpetaChats = new File(CARPETA_USUARIOS + "/" + usuarioActual + "/chatsuser");
        if (!carpetaChats.exists()) {
            carpetaChats.mkdirs();
        }
        return new File(carpetaChats, "chatcon" + destinatario + ".bin");
    }

    public void enviarMensaje(String remitente, String destinatario, String mensaje) {
        
        File archivoChatRemitente = obtenerArchivoChat(remitente, destinatario);

        File archivoChatDestinatario = obtenerArchivoChat(destinatario, remitente);

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        try (RandomAccessFile raf = new RandomAccessFile(archivoChatRemitente, "rw")) {
            raf.seek(raf.length());
            raf.writeUTF(remitente);
            raf.writeUTF(mensaje);
            raf.writeUTF(timestamp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (RandomAccessFile raf = new RandomAccessFile(archivoChatDestinatario, "rw")) {
            raf.seek(raf.length());
            raf.writeUTF(remitente);
            raf.writeUTF(mensaje);
            raf.writeUTF(timestamp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cargarHistorialChat(String usuarioActual, String destinatario, ChatGUI gui) {
        File archivoChat = obtenerArchivoChat(usuarioActual, destinatario);
        if (!archivoChat.exists()) {
            gui.actualizarMensajes("No hay mensajes previos.\n");
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

    public void obtenerUsuariosDisponibles(String usuarioActual, ChatUserListGUI gui) {
        File filePath = new File("users.bin");
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
            while (raf.getFilePointer() < raf.length()) {
                String username = raf.readUTF();
                raf.readUTF();
                raf.readBoolean(); 
                if (!username.equals(usuarioActual)) {
                    gui.agregarUsuario(username);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
