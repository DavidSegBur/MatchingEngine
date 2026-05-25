package group20tup.matchingengine.model.recursos;

public class MetadataNodo {
    private int indiceInterno;
    private long idOSM;
    private double latitud;
    private double longitud;
    private String nombreEsquina;

    public MetadataNodo(int indiceInterno, long idOSM, double latitud, double longitud, String nombreEsquina) {
        this.indiceInterno = indiceInterno;
        this.idOSM = idOSM;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombreEsquina = nombreEsquina;
    }

    public int getIndiceInterno() {
        return indiceInterno;
    }

    public long getIdOSM() {
        return idOSM;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String getNombreEsquina() {
        return nombreEsquina;
    }
}
