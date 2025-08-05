package org.example.tarea_gasolineras_eu.model;

public class SeriesIdBuilder {

    private String product = "EPMR";     // Producto: Gasolina regular
    private String area = "NUS";         // Área: Estados Unidos
    private String frequency = "W";      // Frecuencia: Semanal

    public SeriesIdBuilder setProduct(String product) {
        this.product = product;
        return this;
    }

    public SeriesIdBuilder setArea(String area) {
        this.area = area;
        return this;
    }

    public SeriesIdBuilder setFrequency(String frequency) {
        this.frequency = frequency;
        return this;
    }

    public String build() {
        // Categoría: Petróleo
        String category = "PET";
        // Encuesta mensual
        String survey = "EMM";
        // Proceso: Ventas minoristas
        String process = "PTE";
        // Unidad: Dólares por galón
        String unit = "DPG";
        return String.format("%s.%s_%s_%s_%s_%s.%s",
                category, survey, product, process, area, unit, frequency);
    }
}