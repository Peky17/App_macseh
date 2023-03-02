package com.example.macseh;

public class Mis_Enchufes_Premium_Modelo
{
    String nombreDispositivo;
    String serieDispositivo;
    String Etiqueta;

    public Mis_Enchufes_Premium_Modelo(String nombreDispositivo, String serieDispositivo, String Etiqueta)
    {
        this.nombreDispositivo = nombreDispositivo;
        this.serieDispositivo = serieDispositivo;
        this.Etiqueta = Etiqueta;
    }

    public String getNombreDispositivo() {
        return nombreDispositivo;
    }

    public void setNombreDispositivo(String nombreDispositivo) {
        this.nombreDispositivo = nombreDispositivo;
    }

    public String getSerieDispositivo() {
        return serieDispositivo;
    }

    public void setSerieDispositivo(String serieDispositivo) {
        this.serieDispositivo = serieDispositivo;
    }

    public String getEtiqueta() {
        return Etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.Etiqueta = etiqueta;
    }
}

