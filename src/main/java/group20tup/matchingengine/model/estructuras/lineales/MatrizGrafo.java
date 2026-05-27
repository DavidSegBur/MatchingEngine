package group20tup.matchingengine.model.estructuras.lineales;

/**
 * Matriz utilizada en la estructura de datos Grafo
 * (clase AbsGrafo)
 * <p>
 *     Se utiliza en la implementacion del grafo con
 *     matriz de adyacencia. Hereda de la clase MatrizArr
 *     y agrega la operacion para verificar si dos nodos
 *     (elementos de la matriz) estan conectados (son vecinos)
 * </p>
 * @author Catedra de AyED
 * @version 1.0
 */
public class MatrizGrafo extends MatrizArr{
	public MatrizGrafo(int ordenGrafo){
		super(ordenGrafo, ordenGrafo);
	}

	/**
	 * Este metodo retorna verdadero cuando dos nodos del grafo estan conectados, sino retorna falso
	 * @param i, posicion del primer nodo
	 * @param j, posicion del segundo nodo
	 * @return verdadero si estan conectados, sino falso
	 */
	public boolean areConnected(int i, int j){
		boolean response=false;
		if (i>=0 && i<getNroFilas() && j>=0 && j<getNroColumnas()){
			if (this.matriz[i][j] != 0){
				response=true;
			}
		}				
		return response;
	}
	
}
