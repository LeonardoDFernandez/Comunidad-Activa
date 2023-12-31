package frgp.utn.edu.ar.controllers.data.remote.usuario;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import frgp.utn.edu.ar.controllers.data.model.Usuario;
import frgp.utn.edu.ar.controllers.data.remote.DataDB;

public class DMANuevoUsuario extends AsyncTask<String, Void, Boolean> {

    private final Context context;
    private Usuario nuevo;

    public DMANuevoUsuario(Usuario nuevo, Context ct)
    {
        this.nuevo = nuevo;
        context = ct;
    }

    @Override
    protected Boolean doInBackground(String... urls) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO USUARIOS (username, password, puntuacion, nombre, apellido, telefono, correo, fecha_nac, creacion, id_estado, id_tipo, cod_recuperacion, fecha_bloqueo) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");

            preparedStatement.setString(1, nuevo.getUsername());
            preparedStatement.setString(2, nuevo.getPassword());
            preparedStatement.setInt(3, nuevo.getPuntuacion());
            preparedStatement.setString(4, nuevo.getNombre());
            preparedStatement.setString(5, nuevo.getApellido());
            preparedStatement.setString(6, nuevo.getTelefono());
            preparedStatement.setString(7, nuevo.getCorreo());
            preparedStatement.setDate(8, new java.sql.Date(nuevo.getFecha_nac().getTime()));
            preparedStatement.setDate(9, new java.sql.Date(nuevo.getFecha_alta().getTime()));
            preparedStatement.setInt(10, nuevo.getEstado().getId());
            preparedStatement.setInt(11, nuevo.getTipo().getId());
            preparedStatement.setString(12, nuevo.getCodigo_recuperacion());
            preparedStatement.setDate(13, null);

            int rowsAffected = preparedStatement.executeUpdate();
            preparedStatement.close();
            con.close();
            if (rowsAffected > 0) {
                Log.i("Estado","Agregado");
                return true;
            } else {
                Log.e("Estado","NO Agregado");
                return false;
            }
        }
        catch (MySQLIntegrityConstraintViolationException s){
            Log.e("duplicado","usuario duplicado");
            s.printStackTrace();
            return false;
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
