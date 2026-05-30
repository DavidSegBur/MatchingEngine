package group20tup.matchingengine.model.estructuras.lineales.matrices;

/**
 * Matriz utilizada en la estructura de datos Grafo (clase AbsGrafo)
 * <p>
 *     Se utiliza en la implementacion del grafo con matriz de adyacencia.
 *     Hereda de la clase MatrizArr y agrega la operacion para verificar
 *     si dos nodos (elementos de la matriz) estan conectados (son vecinos).
 *     Las validaciones de posicion son heredadas de MatrizArr.
 * </p>
 * @author Catedra de AyED
 * @version 1.0
 */
public class MatrizGrafo extends MatrizArr{
	/**
	 * Construye una matriz cuadrada para el grafo del orden dado.
	 * @param ordenGrafo Cantidad de nodos del grafo (crea una matriz ordenGrafo x ordenGrafo)
	 */
	public MatrizGrafo(int ordenGrafo){
		super(ordenGrafo, ordenGrafo);
	}

	/**
	 * Verifica si dos nodos del grafo estan conectados por una arista.
	 * @param i, posicion del primer nodo
	 * @param j, posicion del segundo nodo
	 * @return verdadero si existe una conexion (costo != 0), falso en caso contrario o si las posiciones son invalidas
	 */
	public boolean areConnected(int i, int j){
		boolean response=false;
		if (i>=0 && i<getNroFilas() && j>=0 && j<getNroColumnas()){
			if (this.matriz[i][j] > 0.0 && this.matriz[i][j] < Double.POSITIVE_INFINITY){
				response=true;
			}
		}				
		return response;
	}
	
}
