package group20tup.matchingengine.model.estructuras.lineales;

import group20tup.matchingengine.model.recursos.NodoCamino;

public class ColaNodosCamino extends ColaPrioridad{
    @Override
    public boolean esMenor(Object e1, Object e2) {
        NodoCamino nodo1 = (NodoCamino) e1;
        NodoCamino nodo2 = (NodoCamino) e2;
        return nodo1.getEtaAcumulado() < nodo2.getEtaAcumulado();
    }

    @Override
    public boolean esMayor(Object e1, Object e2) {
        NodoCamino nodo1 = (NodoCamino) e1;
        NodoCamino nodo2 = (NodoCamino) e2;
        return nodo1.getEtaAcumulado() > nodo2.getEtaAcumulado();
    }

    @Override
    public boolean sonIguales(Object e1, Object e2) {
        NodoCamino nodo1 = (NodoCamino) e1;
        NodoCamino nodo2 = (NodoCamino) e2;
        return nodo1.getEtaAcumulado() == nodo2.getEtaAcumulado();
    }
}
