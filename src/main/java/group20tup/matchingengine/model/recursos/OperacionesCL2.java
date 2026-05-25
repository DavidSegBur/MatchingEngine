package group20tup.matchingengine.model.recursos;

public interface OperacionesCL2 {
	
	int buscar(Object elemento);
	Object devolver(int posicion);
	void eliminar(int posicion);
	void limpiar();
	boolean estaVacia();
	int tamanio();
	
}
