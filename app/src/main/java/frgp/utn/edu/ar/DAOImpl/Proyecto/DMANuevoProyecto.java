package frgp.utn.edu.ar.DAOImpl.Proyecto;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import frgp.utn.edu.ar.DAOImpl.Connector.DataDB;
import frgp.utn.edu.ar.entidades.Proyecto;

public class DMANuevoProyecto extends AsyncTask<String, Void, Boolean> {

    private final Context context;
    private Proyecto nuevo;

    public DMANuevoProyecto(Proyecto nuevo, Context ct)
    {
        this.nuevo = nuevo;
        context = ct;
    }

    @Override
    protected Boolean doInBackground(String... urls) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO proyectos (titulo, descripcion, coordenadas, fecha, cupo, id_user, id_tipo, id_estado) VALUES (?,?,?,?,?,?,?,?)");

            preparedStatement.setString(1, nuevo.getTitulo());
            preparedStatement.setString(2, nuevo.getDescripcion());
            preparedStatement.setString(3, nuevo.getLocation().toString());
            preparedStatement.setDate(4, (Date) nuevo.getFecha());
            preparedStatement.setInt(5, nuevo.getCupo());
            preparedStatement.setInt(6, nuevo.getOwner().getId());
            preparedStatement.setInt(7, nuevo.getTipo().getId());
            preparedStatement.setInt(8, nuevo.getEstado().getId());

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
