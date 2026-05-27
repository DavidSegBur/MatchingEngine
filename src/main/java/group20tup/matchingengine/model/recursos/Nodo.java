package group20tup.matchingengine.model.recursos;

/**
 * Nodo de una lista simplemente enlazada.
 * <p>
 *     Almacena un elemento generico ({@code Object}) y una referencia
 *     al siguiente nodo en la secuencia. Se utiliza como componente
 *     basico en la estructura {@code ColaSLinkedList}.
 * </p>
 * @author Catedra de AyED
 * @version 1.0
 */
public class Nodo {
    private Object nodoInfo;
    private Nodo nextNodo;

    /**
     * Construye un nodo con el elemento dado y sin siguiente nodo.
     * @param nodoInfo Elemento a almacenar en el nodo
     */
    public Nodo(Object nodoInfo){
        this(nodoInfo,null);
    }

    /**
     * Construye un nodo con el elemento dado y una referencia al siguiente nodo.
     * @param nodoInfo Elemento a almacenar en el nodo
     * @param nextNodo Siguiente nodo en la secuencia
     */
    public Nodo(Object nodoInfo,Nodo nextNodo){
        this.nodoInfo = nodoInfo;
        this.nextNodo = nextNodo;
    }

    /**
     * Establece el elemento almacenado en el nodo.
     * @param nodoInfo Nuevo elemento
     */
    public void setNodoInfo(Object nodoInfo){
        this.nodoInfo = nodoInfo;
    }

    /**
     * Establece la referencia al siguiente nodo.
     * @param nextNodo Siguiente nodo en la secuencia
     */
    public void setNextNodo(Nodo nextNodo){
        this.nextNodo = nextNodo;
    }

    /**
     * Devuelve el elemento almacenado en el nodo.
     * @return Elemento del nodo
     */
    public Object getNodoInfo(){
        return this.nodoInfo;
    }

    /**
     * Devuelve la referencia al siguiente nodo.
     * @return Siguiente nodo, o null si es el ultimo
     */
    public Nodo getNextNodo(){
        return this.nextNodo;
    }
}
