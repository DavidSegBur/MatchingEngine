package group20tup.matchingengine.model.estructuras.lineales;

/**
 * Binary min-heap implementation using raw arrays.
 * Stores node indices and their associated priority values.
 * No decreaseKey operation - uses lazy deletion with duplicate entries.
 */
public class MonticuloBinario {
    private int[] heap;
    private double[] prioridades;
    private int size;
    private int capacity;

    /**
     * Creates a binary min-heap with the specified capacity.
     * 
     * @param capacity Maximum number of elements
     */
    public MonticuloBinario(int capacity) {
        this.capacity = capacity;
        this.heap = new int[capacity];
        this.prioridades = new double[capacity];
        this.size = 0;
    }

    /**
     * Inserts a node with its priority into the heap.
     * 
     * @param nodo     Node index
     * @param prioridad Priority value (lower = higher priority)
     */
    public void insertar(int nodo, double prioridad) {
        if (size >= capacity) {
            // Simple resize - in practice could be more sophisticated
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
     * Removes and returns the node with minimum priority.
     * 
     * @return Node index with minimum priority, or -1 if heap is empty
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
     * Checks if the heap is empty.
     * 
     * @return true if heap is empty
     */
    public boolean estaVacia() {
        return size == 0;
    }

    /**
     * Returns the number of elements in the heap.
     * 
     * @return Current size
     */
    public int tamanio() {
        return size;
    }

    /**
     * Moves element at index i up the heap until heap property is satisfied.
     * 
     * @param i Index to bubble up
     */
    private void subir(int i) {
        while (i > 0 && prioridades[parent(i)] > prioridades[i]) {
            intercambiar(i, parent(i));
            i = parent(i);
        }
    }

    /**
     * Moves element at index i down the heap until heap property is satisfied.
     * 
     * @param i Index to sink down
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
     * Returns parent index of node at index i.
     * 
     * @param i Child index
     * @return Parent index
     */
    private int parent(int i) {
        return (i - 1) / 2;
    }

    /**
     * Returns left child index of node at index i.
     * 
     * @param i Parent index
     * @return Left child index
     */
    private int left(int i) {
        return 2 * i + 1;
    }

    /**
     * Returns right child index of node at index i.
     * 
     * @param i Parent index
     * @return Right child index
     */
    private int right(int i) {
        return 2 * i + 2;
    }

    /**
     * Swaps two elements in the heap.
     * 
     * @param i First index
     * @param j Second index
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