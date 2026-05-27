package group20tup.matchingengine.model.recursos;

/**
 * Nodo de una lista doblemente enlazada.
 * <p>
 *     Almacena un elemento generico ({@code Object}) y referencias
 *     al nodo anterior y siguiente en la secuencia. Se utiliza como
 *     componente basico en las listas doblemente enlazadas
 *     ({@code Lista0DLinkedL}, {@code Lista1DLinkedL}, {@code ListaDoubleLinkedL}).
 * </p>
 * @author Catedra de AyED
 * @version 1.0
 */
public class NodoDoble {

	private Object nodoInfo;
	private NodoDoble prevNodo, nextNodo;
	
	/**
	 * Construye un nodo con el elemento dado, sin nodos adyacentes.
	 * @param nodoInfo Elemento a almacenar en el nodo
	 */
	public NodoDoble(Object nodoInfo){
		this(nodoInfo,null,null);
	}

	/**
	 * Construye un nodo con el elemento dado y referencia al siguiente nodo.
	 * @param nodoInfo Elemento a almacenar en el nodo
	 * @param nextNodo Siguiente nodo en la secuencia
	 */
	public NodoDoble(Object nodoInfo, NodoDoble nextNodo){
		this(nodoInfo,null,nextNodo);
	}

	/**
	 * Construye un nodo con el elemento dado y referencias al anterior y siguiente nodo.
	 * @param nodoInfo Elemento a almacenar en el nodo
	 * @param prevNodo Nodo anterior en la secuencia
	 * @param nextNodo Siguiente nodo en la secuencia
	 */
	public NodoDoble(Object nodoInfo, NodoDoble prevNodo, NodoDoble nextNodo){
		this.nodoInfo=nodoInfo;
		this.prevNodo=prevNodo; this.nextNodo=nextNodo;
	}
	
	/**
	 * Establece la referencia al nodo anterior.
	 * @param prevNodo Nodo anterior en la secuencia
	 */
	public void setPrevNodo(NodoDoble prevNodo){
		this.prevNodo=prevNodo;
	}
	
	/**
	 * Devuelve la referencia al nodo anterior.
	 * @return Nodo anterior, o null si es el primero
	 */
	public NodoDoble getPrevNodo(){
		return this.prevNodo;
	}
	
	/**
	 * Establece la referencia al siguiente nodo.
	 * @param nextNodo Siguiente nodo en la secuencia
	 */
	public void setNextNodo(NodoDoble nextNodo){
		this.nextNodo=nextNodo;
	}
	
	/**
	 * Devuelve la referencia al siguiente nodo.
	 * @return Siguiente nodo, o null si es el ultimo
	 */
	public NodoDoble getNextNodo(){
		return this.nextNodo;
	}

	/**
	 * Establece el elemento almacenado en el nodo.
	 * @param nodoInfo Nuevo elemento
	 */
	public void setNodoInfo(Object nodoInfo){
		this.nodoInfo=nodoInfo;
	}

	/**
	 * Devuelve el elemento almacenado en el nodo.
	 * @return Elemento del nodo
	 */
	public Object getNodoInfo(){
		return this.nodoInfo;
	}

}
