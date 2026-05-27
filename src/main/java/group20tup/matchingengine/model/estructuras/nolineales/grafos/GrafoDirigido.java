package group20tup.matchingengine.model.estructuras.nolineales.grafos;

import java.util.Scanner;

/**
 * Implementacion concreta de un grafo dirigido con matriz de adyacencia.
 * <p>
 *     Extiende la clase abstracta {@code AbsGrafo} y proporciona una
 *     implementacion del metodo {@code cargarGrafo()} que permite la
 *     carga manual de costos mediante entrada estandar (consola).
 *     Para uso en produccion, los datos se cargan a traves de
 *     {@code CargadorDatos.cargarMatrizVial()}.
 * </p>
 * @author Ivan
 * @version 1.0
 */
public class GrafoDirigido extends AbsGrafo{

	/**
	 * Construye un grafo dirigido con el orden dado.
	 * @param ordenGrafo Cantidad de nodos del grafo
	 */
	public GrafoDirigido(int ordenGrafo){
		super(ordenGrafo);
	}
	
	/**
	 * Carga manual interactiva del grafo mediante entrada por consola.
	 * <p>
	 *     Solicita al usuario el costo de cada arista entre pares de nodos.
	 *     Un valor de -1 indica ausencia de conexion.
	 * </p>
	 * @deprecated Metodo interactivo solo para propositos academicos.
	 *             En produccion utilizar {@code CargadorDatos.cargarMatrizVial()}.
	 */
	@Override
	@Deprecated
	public void cargarGrafo(){
		double currCost;		
		Scanner scanner = new Scanner(System.in);
		
		for (int i=0; i<getOrden();i++){
			for (int j=0;j<getOrden();j++){
				if (i!=j){
					System.out.println("Ingrese costo[" + i + "," + j + "] (sino -1)");
					currCost=scanner.nextDouble();
					if (currCost!=-1){
						this.matrizCosto.actualizar(currCost, i, j);	
					}else{
						this.matrizCosto.actualizar(infinito, i, j);
					}					
				}else{
					this.matrizCosto.actualizar(infinito, i, j);
				}
			}
		} 	
	}
}
