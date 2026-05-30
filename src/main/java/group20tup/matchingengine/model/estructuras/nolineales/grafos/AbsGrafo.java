package group20tup.matchingengine.model.estructuras.nolineales.grafos;

import group20tup.matchingengine.model.estructuras.lineales.listas.ColaSLinkedList;
import group20tup.matchingengine.model.estructuras.lineales.listas.ListaDoubleLinkedL;
import group20tup.matchingengine.model.estructuras.lineales.matrices.MatrizGrafo;
import group20tup.matchingengine.model.recursos.operaciones.OperacionesG;

/**
 * Clase abstracta que representa un grafo dirigido con matriz de adyacencia.
 * <p>
 *     Proporciona la estructura base para la representacion del grafo
 *     mediante una matriz de costos (MatrizGrafo), e implementa los
 *     recorridos en amplitud (BEA) y profundidad (BPF) definidos en
 *     la interfaz OperacionesG. Las subclases concretas deben
 *     implementar el metodo {@code cargarGrafo()} segun su origen
 *     de datos especifico.
 * </p>
 * @author Catedra de AyED e Ivan
 * @version 2.0
 */
public abstract class AbsGrafo implements OperacionesG {
    protected MatrizGrafo matrizCosto;
    protected int ordenGrafo;

    /**
     * Construye un grafo vacio con el orden dado.
     * @param ordenGrafo Cantidad de nodos del grafo
     */
    public AbsGrafo(int ordenGrafo) {
        this.ordenGrafo = ordenGrafo;
        this.matrizCosto = new MatrizGrafo(getOrden());
    }

    /**
     * Establece el orden (cantidad de nodos) del grafo.
     * @param ordenGrafo Nuevo orden del grafo
     */
    public void setOrdenGrafo(int ordenGrafo) {
        this.ordenGrafo = ordenGrafo;
    }

    /**
     * Devuelve el orden (cantidad de nodos) del grafo.
     * @return Cantidad de nodos
     */
    public int getOrden() {
        return this.ordenGrafo;
    }

    /**
     * Valor infinito usado para representar ausencia de conexion.
     */
    protected static double infinito= Double.POSITIVE_INFINITY;

    /**
     * Metodo abstracto para cargar los datos del grafo.
     * Cada subclase debe implementar su propia logica de carga.
     */
    public abstract void cargarGrafo();

    /**
     * Muestra por consola las aristas del grafo con sus costos.
     * Solo muestra las conexiones existentes (costo != infinito).
     */
    public void muestraGrafo() {
        double currCost;
        for (int i=0; i<getOrden();i++) {
            for (int j=0; j<getOrden();j++) {
                if (i!=j) {
                    currCost= this.matrizCosto.devolver(i, j);
                    if (currCost!=infinito) {
                        System.out.println("costo " + i + " a " + j + "->" + currCost);
                    }
                }
            }
        }
    }

    /**
     * Recorrido en profundidad (DFS) desde un nodo dado.
     * @param listaMarca Lista de booleanos que indica nodos visitados
     * @param v Nodo desde el cual iniciar el recorrido
     */
    private void bpf(ListaDoubleLinkedL listaMarca, int v) {
        boolean marcado;
        double currCost;

        listaMarca.reemplazar(true, v);
        System.out.println("vertice "+ v);
        for (int w=0;w<getOrden();w++) {
            marcado=(boolean)listaMarca.devolver(w);
            currCost=this.matrizCosto.devolver(v,w);
            if (currCost!=infinito && !marcado) {
                bpf(listaMarca,w);
            }
        }
    }

    /**
     * Muestra el recorrido en profundidad (DFS) de todo el grafo.
     */
    public void muestraBPF() {
        ListaDoubleLinkedL listaMarca;
        boolean marcado;

        listaMarca = new ListaDoubleLinkedL();
        for (int v=0;v<getOrden();v++) {
            listaMarca.insertar(false, v);
        }

        for (int v=0;v<getOrden();v++) {
            marcado=(boolean)listaMarca.devolver(v);
            if (!marcado) {
                bpf(listaMarca,v);
            }
        }       
    }

    /**
     * Recorrido en amplitud (BFS) desde un nodo dado.
     * @param listaMarca Lista de booleanos que indica nodos visitados
     * @param v Nodo desde el cual iniciar el recorrido
     */
    private void bea(ListaDoubleLinkedL listaMarca, int v) {
        boolean marcado;
        double currCost;
        ColaSLinkedList cola;
        int w;

        listaMarca.reemplazar(true, v);
        System.out.println("vertice "+ v);
        cola = new ColaSLinkedList();
        cola.meter(v);

        while (!cola.estaVacia()) {
            w=(int)cola.sacar();
            for (int z=0;z<getOrden();z++) {
                marcado=(boolean)listaMarca.devolver(z);
                currCost=this.matrizCosto.devolver(w,z);
                if (currCost!=infinito && !marcado) {
                    listaMarca.reemplazar(true, z);
                    cola.meter(z);
                    System.out.println("arista visitada " + w + " - " + z);
                }
            }
        }
    }

    /**
     * Muestra el recorrido en amplitud (BFS) de todo el grafo.
     */
    public void muestraBEA() {
        ListaDoubleLinkedL listaMarca;
        boolean marcado;

        listaMarca = new ListaDoubleLinkedL();
        for (int v=0;v<getOrden();v++) {
            listaMarca.insertar(false, v);
        }

        for (int v=0;v<getOrden();v++) {
            marcado=(boolean)listaMarca.devolver(v);
            if (!marcado) {
                bea(listaMarca,v);
            }
        }       
    }

    /**
     * Devuelve la matriz de costos del grafo.
     * @return MatrizGrafo con los costos (ETAs) entre nodos
     */
    public MatrizGrafo getMatrizCosto() {
        return matrizCosto;
    }
}
