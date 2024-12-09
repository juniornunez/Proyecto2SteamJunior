/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectosteam2;

/**
 *
 * @author Junior Nuñes
 */

import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;

public class AdminManager {
    private static final String CARPETA_USUARIOS = "usuarios";

    public void cargarUsuarios(AdminGUI gui) {
        File carpetaUsuarios = new File(CARPETA_USUARIOS);
        if (!carpetaUsuarios.exists()) {
            gui.mostrarMensaje("No se encontraron usuarios registrados.");
            return;
        }

        File[] usuarios = carpetaUsuarios.listFiles();
        if (usuarios != null) {
            for (File usuario : usuarios) {
                if (usuario.isDirectory()) {
                    gui.agregarUsuario(usuario.getName());
                }
            }
        }
    }

    public DefaultMutableTreeNode cargarArchivosUsuario(String nombreUsuario) {
        File carpetaUsuario = new File(CARPETA_USUARIOS + "/" + nombreUsuario);
        if (!carpetaUsuario.exists()) {
            return null;
        }
        return crearNodoArbol(carpetaUsuario);
    }

   private DefaultMutableTreeNode crearNodoArbol(File archivo) {
    DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(archivo.getName());
    if (archivo.isDirectory()) {
        File[] archivos = archivo.listFiles();
        if (archivos != null) {
            for (File hijo : archivos) {
                nodo.add(crearNodoArbol(hijo));
            }
        }
    }
    return nodo;
}
}