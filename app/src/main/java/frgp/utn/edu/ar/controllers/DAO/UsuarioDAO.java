package frgp.utn.edu.ar.controllers.DAO;

import android.content.Context;

import java.util.List;

import frgp.utn.edu.ar.controllers.data.model.Usuario;

public interface UsuarioDAO {
    boolean modificarUsuario(Context context, Usuario modifcar);

    Usuario login(Context context, String username, String password);
    List<Usuario> listarUsuarios(Context context);
    List<Usuario> listarUsuariosPorTipo(Context context, int tipoUsuario);
    List<Usuario> listarUsuariosPorEstado(Context context, int estado);
}
