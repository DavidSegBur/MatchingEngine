package group20tup.matchingengine.model.recursos;

/**
 * Interfaz que define las operaciones basicas de una cola (estructura FIFO)
 * implementada con lista simplemente enlazada.
 * <p>
 *     Las operaciones incluyen insercion al final ({@code meter}),
 *     extraccion del frente ({@code sacar}), y estado de la estructura.
 * </p>
 * @author Catedra de AyED
 * @version 1.0
 */
public interface OperacionesCL1 {

    /**
     * Inserta un elemento al final de la cola.
     * @param elemento Elemento a insertar
     */
    void meter(Object elemento);

    /**
     * Extrae y devuelve el elemento del frente de la cola.
     * @return Elemento extraido, o null si la cola esta vacia
     */
    Object sacar();

    /**
     * Elimina todos los elementos de la cola, dejandola vacia.
     */
    void limpiar();

    /**
     * Verifica si la cola esta vacia.
     * @return true si la cola no contiene elementos, false en caso contrario
     */
    boolean estaVacia();
}
