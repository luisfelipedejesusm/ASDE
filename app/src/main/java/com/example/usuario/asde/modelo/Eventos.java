package com.example.usuario.asde.modelo;

/**
 * Created by User-abreu on 15/01/2017.
 */

public class Eventos {
    private String nombre;
    private String detalle;
    private String latitud;
    private String longitud;
    private String direccion;
    private String foto;
    private String pathFoto;
    private String horaevento;
    private String horaeventogmt;
    private String estatus;
    private String Id;

public Eventos(){}
    public Eventos(String nombre, String detalle, String latitud, String longitud, String direccion, String foto, String pathFoto, String horaevento,String horaeventogmt, String estatus) {
        this.nombre = nombre;
        this.detalle = detalle;
        this.latitud = latitud;
        this.longitud = longitud;
        this.direccion = direccion;
        this.foto = foto;
        this.pathFoto = pathFoto;
        this.horaevento = horaevento;
        this.horaeventogmt = horaeventogmt;
        this.estatus = estatus;
    }

    public Eventos(String nombre, String detalle, String latitud, String longitud, String direccion, String pathFoto, String horaevento,String horaeventogmt, String estatus) {
        this.nombre = nombre;

        this.detalle = detalle;
        this.latitud = latitud;
        this.longitud = longitud;
        this.direccion = direccion;
        this.pathFoto = pathFoto;
        this.horaevento = horaevento;
        this.horaeventogmt = horaeventogmt;
        this.estatus = estatus;
    }
















public String getId(){return Id;}
    public void setId(String Id){this.Id=Id;}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }





    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getPathFoto() {
        return pathFoto;
    }

    public void setPathFoto(String pathFoto) {
        this.pathFoto = pathFoto;
    }

    public String getHoraevento() {
        return horaevento;
    }

    public void setHoraevento(String horaevento) {
        this.horaevento = horaevento;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getHoraeventogmt() {
        return horaeventogmt;
    }

    public void setHoraeventogmt(String horaeventogmt) {
        this.horaeventogmt = horaeventogmt;
    }
}
