package group20tup.matchingengine.model.estructuras.lineales;

/**
 * Matriz base para usar en estructuras no lineales (grafos, arboles)
 * <p>
 *     Esta version es una modificada de la version generica
 *     que la catedra de AyED proporciona. Esto se hizo para evitar
 *     el overhead del casteo de tipos de datos y el Unboxing/boxing
 *     de primitivos
 * </p>
 * @author Ivan
 * @version 1.1
 */
public class MatrizArr {
	protected double[][] matriz;
	protected int nroFilas, nroColumnas;

	/**
	 * Constructor
	 * @param nroFilas, numero filas de la matriz
	 * @param nroColumnas, numero columnas de la matriz
	 */
	public MatrizArr(int nroFilas, int nroColumnas){
		this.nroFilas=nroFilas;
		this.nroColumnas=nroColumnas;
		this.matriz=new double[this.nroFilas][this.nroColumnas];
	}

	/**
	 * Getter de filas de la matriz
	 * @return numero de filas de la matriz
	 */
	public int getNroFilas(){ return this.nroFilas;}

	/**
	 * Getter de columnas de la matriz
	 * @return numero de columnas de la matriz
	 */
	public int getNroColumnas(){ return this.nroColumnas;}

	/**
	 * Limpia la matriz volviendo todos sus valores a 0
	 */
	public void limpiaMatriz(){
		for (int i=0;i<getNroFilas();i++){
			for (int j=0;j<getNroColumnas();j++){
				this.matriz[i][j]=0.0;
			}
		}
	}

	/**
	 * Actualiza el valor de un elemento de la matriz al valor de elemento en la posicion
	 * indicada por posicionFila y posicionColumna
	 * @param elemento, nuevo valor
	 * @param posicionFila, fila en la que se ubicara el nuevo valor
	 * @param posicionColumna, columna en la que se ubicara el nuevo valor
	 */
	public void actualizar(double elemento, int posicionFila, int posicionColumna){
		if (posicionFila>=getNroFilas() || posicionFila<0){
				System.out.println("Error actualiza. Posicion fila inexistente ");
			}else{
				if (posicionColumna>=getNroColumnas() || posicionColumna<0){
					System.out.println("Error actualiza. Posicion columna inexistente ");
				}else{
					this.matriz[posicionFila][posicionColumna]=elemento;
				}				
			}
		}


	/**
	 * Devuelve el valor de la matriz en la fila y columna indicadas
 	 * @param posicionFila, fila en la que se encuentra el valor
	 * @param posicionColumna, columna en la que se encuentra el valor
	 * @return el valor de la matriz en las posiciones indicadas
	 */
	public double devolver(int posicionFila, int posicionColumna){
		double elemento = 0.0;

		if (posicionFila>=getNroFilas() || posicionFila<0){
			System.out.println("Error devuelve. Posicion fila inexistente ");
		}else{
			if (posicionColumna>=getNroColumnas() || posicionColumna<0){
				System.out.println("Error devuelve. Posicion columna inexistente ");
			}else{
				elemento = this.matriz[posicionFila][posicionColumna];
			}				
		}		
		return elemento;
	}	
}
