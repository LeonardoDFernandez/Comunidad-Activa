package frgp.utn.edu.ar.controllers.DAOImpl.Usuario;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.Objects;

import frgp.utn.edu.ar.controllers.DAO.UsuarioDAO;
import frgp.utn.edu.ar.controllers.data.model.Usuario;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public boolean modificarUsuario(Context context, Usuario modificar) {
        DMAUpdateUsuario DMAUU = new DMAUpdateUsuario(modificar, context);
        DMAUU.execute();
        try {
            return DMAUU.get();
        } catch (Exception e) {
            Log.d("Error", Objects.requireNonNull(e.getMessage()));
            return false;
        }
    }

    @Override
    public Usuario login(Context context, String username, String password) {
        DMALoginUsuario DMALU = new DMALoginUsuario(username, password, context);
        DMALU.execute();
        try {
            return DMALU.get();
        } catch (Exception e) {
            Log.d("Error", Objects.requireNonNull(e.getMessage()));
            return null;
        }
    }

    @Override
    public List<Usuario> listarUsuarios(Context context) {
        DMAListarUsuarios DMALU = new DMAListarUsuarios(context);
        DMALU.execute();
        try {
            return DMALU.get();
        } catch (Exception e) {
            Log.d("Error", Objects.requireNonNull(e.getMessage()));
            return null;
        }
    }

    @Override
    public List<Usuario> listarUsuariosPorTipo(Context context, int tipoUsuario) {
        DMAListarUsuariosPorTipo DMALUPT = new DMAListarUsuariosPorTipo(context, tipoUsuario);
        DMALUPT.execute();
        try {
            return DMALUPT.get();
        } catch (Exception e) {
            Log.d("Error", Objects.requireNonNull(e.getMessage()));
            return null;
        }
    }

    @Override
    public List<Usuario> listarUsuariosPorEstado(Context context, int estado) {
        DMAListarUsuariosPorEstado DMALUPE = new DMAListarUsuariosPorEstado(context, estado);
        DMALUPE.execute();
        try {
            return DMALUPE.get();
        } catch (Exception e) {
            Log.d("Error", Objects.requireNonNull(e.getMessage()));
            return null;
        }
    }
}
