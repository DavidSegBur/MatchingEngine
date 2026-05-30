package group20tup.matchingengine.model.estructuras.lineales.listas;

import java.util.NoSuchElementException;

import group20tup.matchingengine.model.recursos.nodos.Nodo;
import group20tup.matchingengine.model.recursos.operaciones.OperacionesCL1;

/**
 * Estructura de datos de Cola con lista simplemente enlazada que implementa OperacionesCL1
 * <p>
 *     Implementa una cola FIFO (First-In, First-Out) utilizando nodos
 *     simplemente enlazados. Mantiene referencias al frente y al final
 *     de la cola para operaciones eficientes.
 * </p>
 * @author Ivan
 * @version 1.1
 */
public class ColaSLinkedList implements OperacionesCL1 {
    protected Nodo frenteC,finalC;

    /**
     * Construye una cola vacia.
     */
    public ColaSLinkedList() {
        limpiar();
    }

    /**
     * Inserta un elemento al final de la cola.
     * @param elemento Elemento a insertar
     */
    @Override
    public void meter(Object elemento) {
        if(!estaVacia()) {
            this.finalC.setNextNodo(new Nodo(elemento));
            this.finalC = this.finalC.getNextNodo();
        }
        else
            this.frenteC = this.finalC = new Nodo(elemento);
    }

    /**
     * Extrae y devuelve el elemento del frente de la cola.
     * @return Elemento extraido del frente
     * @throws NoSuchElementException si la cola esta vacia
     */
    @Override
    public Object sacar() throws NoSuchElementException {
        Object elemento = null;
        if(!estaVacia()) {
            elemento = this.frenteC.getNodoInfo();
            this.frenteC = this.frenteC.getNextNodo();
            if(estaVacia())
                this.finalC = null;
        } else {
            throw new NoSuchElementException("La cola esta vacia");
        }
        return elemento;
    }

    /**
     * Verifica si la cola esta vacia.
     * @return true si la cola no contiene elementos, false en caso contrario
     */
    @Override
    public boolean estaVacia() {
        return this.frenteC==null;
    }

    /**
     * Elimina todos los elementos de la cola, dejandola vacia.
     */
    @Override
    public void limpiar() {
        this.frenteC = this.finalC = null;
    }

    /**
     * Devuelve el nodo del frente de la cola.
     * @return Nodo frontal
     */
    public Nodo getFrenteC() {
        return frenteC;
    }

    /**
     * Establece el nodo del frente de la cola.
     * @param frenteC Nuevo nodo frontal
     */
    public void setFrenteC(Nodo frenteC) {
        this.frenteC = frenteC;
    }

    /**
     * Devuelve el nodo del final de la cola.
     * @return Nodo final
     */
    public Nodo getFinalC() {
        return finalC;
    }

    /**
     * Establece el nodo del final de la cola.
     * @param finalC Nuevo nodo final
     */
    public void setFinalC(Nodo finalC) {
        this.finalC = finalC;
    }
}
