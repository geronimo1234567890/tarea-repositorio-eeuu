package org.example.tarea_gasolineras_eu.model;

// Representa el precio de gasolina
public class GasPrice {
    private String date;   // Fecha del dato
    private double price;  // Precio por galón

    public GasPrice(String date, double price) {
        this.date = date;
        this.price = price;
    }

    public GasPrice() {
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}