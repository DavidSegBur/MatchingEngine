package group20tup.matchingengine.model.recursos;

public class MetadataNodo {
    private final int indiceInterno;
    private final long idOSM;
    private final double latitud;
    private final double longitud;
    private final String calleA;
    private final String calleB;
    private final String nombreEsquina;

    public MetadataNodo(int indiceInterno, long idOSM, double latitud, double longitud, String calleA, String calleB, String nombreEsquina) {
        this.indiceInterno = indiceInterno;
        this.idOSM = idOSM;
        this.latitud = latitud;
        this.longitud = longitud;
        this.calleA = calleA;
        this.calleB = calleB;
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

    public String getCalleA() {
        return calleA;
    }

    public String getCalleB() {
        return calleB;
    }

    public String getNombreEsquina() {
        return nombreEsquina;
    }
}
