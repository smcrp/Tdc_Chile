package Levantamiento;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by georgeperez on 5/4/16.
 */
public class Antena {
    private int id;
    private int activo;
    private String creado;
    private String nombre;
    private String direccion;
    private String empresa;
    private String identificador;
    private String latitud;
    private String longitud;

    public Antena(String nombre, String direccion, String empresa, String identificador, String latitud, String longitud, int activo) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.empresa = empresa;
        this.identificador = identificador;
        this.latitud = latitud;
        this.longitud = longitud;
        this.activo = activo;
    }

    public Antena(int id, String nombre, String direccion, String empresa, String identificador, String latitud, String longitud, int activo) {
        this.activo = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.empresa = empresa;
        this.identificador = identificador;
        this.latitud = latitud;
        this.longitud = longitud;
        this.activo = activo;
    }

    public Antena() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }

    public String getCreado() {
        return creado;
    }

    public void setCreado(String creado) {
        this.creado = creado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
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

    /**
     * get datetime
     * */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
