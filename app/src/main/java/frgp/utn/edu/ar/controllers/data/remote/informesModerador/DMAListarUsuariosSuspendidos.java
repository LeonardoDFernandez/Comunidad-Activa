package frgp.utn.edu.ar.controllers.data.remote.informesModerador;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import frgp.utn.edu.ar.controllers.data.remote.DataDB;

public class DMAListarUsuariosSuspendidos extends AsyncTask<String, Void, JSONArray> {

    Date fechaInicio;
    Date fechaFin;

    public DMAListarUsuariosSuspendidos(Date fechaInicio, Date fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
    @Override
    protected JSONArray doInBackground(String... urls) {
        JSONArray response = new JSONArray();
        JSONObject entry = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
            PreparedStatement preparedStatement = con.prepareStatement(
                    "SELECT U.NOMBRE, U.APELLIDO, EU.ESTADO, TU.TIPO, U.CREACION "+
                            "FROM USUARIOS U " +
                            "JOIN ESTADOS_USUARIO EU ON U.ID_ESTADO = EU.ID "+
                            "JOIN TIPOS_USUARIO TU ON U.ID_TIPO = TU.ID "+
                            "WHERE U.ID_ESTADO = 3 "+
                            "AND U.CREACION BETWEEN ? AND ?;");

            preparedStatement.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            preparedStatement.setDate(2, new java.sql.Date(fechaFin.getTime()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                entry = new JSONObject();
                entry.put("NOMBRE", resultSet.getString("NOMBRE"));
                entry.put("APELLIDO", resultSet.getString("APELLIDO"));
                entry.put("ESTADO", resultSet.getString("ESTADO"));
                entry.put("TIPO", resultSet.getString("TIPO"));
                entry.put("CREACION", resultSet.getString("CREACION"));

                response.put(entry);
            }
            preparedStatement.close();
            con.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
