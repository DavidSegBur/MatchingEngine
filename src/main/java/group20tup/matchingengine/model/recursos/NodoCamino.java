package group20tup.matchingengine.model.recursos;

public class NodoCamino {
    private int idFilaArray;
    private double etaAcumulado;

    public int getIdFilaArray() {
        return idFilaArray;
    }

    public double getEtaAcumulado() {
        return etaAcumulado;
    }

    public NodoCamino(int idFilaArray, double etaAcumulado) {
        this.idFilaArray = idFilaArray;
        this.etaAcumulado = etaAcumulado;
    }
}
