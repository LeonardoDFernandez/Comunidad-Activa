package frgp.utn.edu.ar.controllers.data.model;

import java.io.Serializable;

public class EstadoUsuario implements Serializable {
    private int id;
    private String estado;

    public EstadoUsuario(int id, String estado) {
        this.id = id;
        this.estado = estado;
    }

    public EstadoUsuario() {
    }

    @Override
    public String toString() {
        return id + " - " + estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
