package com.cieep.a05_ejercicio_lista_compra.modelos;

import java.io.Serializable;

public class Producto implements Serializable {

  private String nombre;
  private int cantidad;
  private float importe;
  private float total;

    public Producto() {
    }

    public Producto(String nombre, int cantidad, float importe) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.importe = importe;
        this.total = this.cantidad * this.importe;
    }

    public void actualizaTotal () {
        this.total = this.cantidad * this.importe;
    }

    public float getImporte() {
        return importe;
    }

    public void setImporte(float importe) {
        this.importe = importe;
    }

    public float getTotal() {
        return total;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
