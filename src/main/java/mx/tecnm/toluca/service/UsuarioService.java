/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.tecnm.toluca.service;

/**
 *
 * @author guble
 */
import mx.tecnm.toluca.dao.UsuarioDAO;
import mx.tecnm.toluca.model.Usuario;

public class UsuarioService {
    private UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public boolean autenticar(String username, String password) {
        Usuario usuario = usuarioDAO.buscarPorUsername(username);
        if (usuario != null && usuario.getPassword().equals(password)) {
            return true;
        }
        return false;
    }
}
