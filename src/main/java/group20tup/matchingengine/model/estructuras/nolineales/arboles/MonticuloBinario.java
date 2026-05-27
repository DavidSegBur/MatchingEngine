package group20tup.matchingengine.model.estructuras.nolineales.arboles;

/**
 * Implementacion de monticulo binario minimo usando arreglos.
 * Almacena los indices de los nodos y su valores de prioridad asociados.
 * Sin operacion decreaseKey - usa delegacion perezosa con entradas duplicadas.
 * @author Ivan
 * @version 1.0
 */
public class MonticuloBinario {
    private int[] heap;
    private double[] prioridades;
    private int size;
    private int capacity;

    /**
     * Crea un monticulo binario minimo con la capacidad especificada.
     *
     * @param capacity Numero maximo de elementos
     */
    public MonticuloBinario(int capacity) {
        this.capacity = capacity;
        this.heap = new int[capacity];
        this.prioridades = new double[capacity];
        this.size = 0;
    }

    /**
     * Inserta un nodo con su prioridad en el monticulo.
     * 
     * @param nodo     Indice del nodo
     * @param prioridad Valor de prioridad (mas bajo valor = mas alta prioridad)
     */
    public void insertar(int nodo, double prioridad) {
        if (size >= capacity) {
            // Redimensionado simple - en practica podria ser mas sofisticado
            int newCapacity = capacity * 2;
            int[] newHeap = new int[newCapacity];
            double[] newPrioridades = new double[newCapacity];
            System.arraycopy(heap, 0, newHeap, 0, capacity);
            System.arraycopy(prioridades, 0, newPrioridades, 0, capacity);
            heap = newHeap;
            prioridades = newPrioridades;
            capacity = newCapacity;
        }

        int i = size;
        heap[i] = nodo;
        prioridades[i] = prioridad;
        size++;
        subir(i);
    }

    /**
     * Elimina y retorna el nodo con la prioridad minima.
     * 
     * @return Indice del nodo con prioridad minima, o -1 si el monticulo esta vacio
     */
    public int extraerMin() {
        if (size <= 0) {
            return -1;
        }
        if (size == 1) {
            size--;
            return heap[0];
        }

        int raiz = heap[0];
        heap[0] = heap[size - 1];
        prioridades[0] = prioridades[size - 1];
        size--;
        hundir(0);

        return raiz;
    }

    /**
     * Revisa si el monticulo esta vacio.
     * 
     * @return verdadero si el monticulo esta vacio
     */
    public boolean estaVacia() {
        return size == 0;
    }

    /**
     * Retorna el numero de elementos en el monticulo.
     *
     * @return El tamaño actual
     */
    public int tamanio() {
        return size;
    }

    /**
     * Mueve el elemento en el indice i hacia arriba en el monticulo hasta que
     * la propiedad del monticulo es la correcta.
     * 
     * @param i Indice para subir (bubble up)
     */
    private void subir(int i) {
        while (i > 0 && prioridades[parent(i)] > prioridades[i]) {
            intercambiar(i, parent(i));
            i = parent(i);
        }
    }

    /**
     * Mueve el elemento en el indice i hacia abajo en el monticulo hasta que la
     * propiedad del monticulo sea la correcta.
     * 
     * @param i Indice para hundir (sink down)
     */
    private void hundir(int i) {
        int menor = i;
        int izquierdo = left(i);
        int derecho = right(i);

        if (izquierdo < size && prioridades[izquierdo] < prioridades[menor]) {
            menor = izquierdo;
        }
        if (derecho < size && prioridades[derecho] < prioridades[menor]) {
            menor = derecho;
        }
        if (menor != i) {
            intercambiar(i, menor);
            hundir(menor);
        }
    }

    /**
     * Retorna el indice padre del nodo en el indice i.
     * 
     * @param i Indice hijo
     * @return Indice padre
     */
    private int parent(int i) {
        return (i - 1) / 2;
    }

    /**
     * Retorna el indice del hijo izquierdo del nodo en el indice i.
     * 
     * @param i Indice padre
     * @return Indice del hijo izquierdo
     */
    private int left(int i) {
        return 2 * i + 1;
    }

    /**
     * Retorna el indice del hijo derecho del nodo en el indice i.
     * 
     * @param i Indice padre
     * @return Indice del hijo derecho
     */
    private int right(int i) {
        return 2 * i + 2;
    }

    /**
     * Intercambia dos elementos en el monticulo.
     * 
     * @param i Primer indice
     * @param j Segundo indice
     */
    private void intercambiar(int i, int j) {
        int tempNodo = heap[i];
        heap[i] = heap[j];
        heap[j] = tempNodo;

        double tempPrioridad = prioridades[i];
        prioridades[i] = prioridades[j];
        prioridades[j] = tempPrioridad;
    }
}