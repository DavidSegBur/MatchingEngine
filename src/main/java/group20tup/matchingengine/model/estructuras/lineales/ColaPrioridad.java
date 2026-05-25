package group20tup.matchingengine.model.estructuras.lineales;


import group20tup.matchingengine.model.recursos.Nodo;

/**
 *
 * @author arc
 */
public abstract class ColaPrioridad extends ColaSLinkedList {
    @Override
    public void meter(Object elemento) {
        if (!estaVacia()) {
            if (esMayor(elemento, this.frenteC.getNodoInfo())) {
                this.frenteC = new Nodo(elemento, this.frenteC);
            } else if (esMenor(elemento, finalC.getNodoInfo()) || sonIguales(elemento, finalC.getNodoInfo())) {
                this.finalC.setNextNodo(new Nodo(elemento));
                this.finalC = this.finalC.getNextNodo();
            } else {
                Nodo aux = frenteC;
                while (aux != null && esMenor(elemento, aux.getNextNodo().getNodoInfo())) {                    
                    aux = aux.getNextNodo();
                }
                aux.setNextNodo(new Nodo(elemento, aux.getNextNodo()));
            }
        } else {
            this.frenteC = this.finalC = new Nodo(elemento);
        }
    }
    
    public abstract boolean esMenor(Object e1, Object e2);
    public abstract boolean esMayor(Object e1, Object e2);
    public abstract boolean sonIguales(Object e1, Object e2);
}
