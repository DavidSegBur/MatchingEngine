package group20tup.matchingengine.model.utilidades.sistema;

import group20tup.matchingengine.model.estructuras.lineales.listas.ListaDoubleLinkedL;
import group20tup.matchingengine.model.estructuras.lineales.colas.ColaPrioridadMonticulo;
import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoDirigido;
import group20tup.matchingengine.model.recursos.simulacion.EstadoVehiculo;
import group20tup.matchingengine.model.recursos.simulacion.Usuario;
import group20tup.matchingengine.model.recursos.simulacion.Vehiculo;
import group20tup.matchingengine.model.utilidades.CalculadorRutas;

/**
 * Sistema central de despacho y matching de viajes.
 * <p>
 *     Gestiona el ciclo de vida completo de los viajes: registro de
 *     vehiculos y usuarios, solicitud de viaje, cola de prioridad
 *     de despacho (vehiculos disponibles ordenados por ETA),
 *     y cola de vehiculos ocupados ordenados por tiempo restante.
 *     Utiliza exclusivamente las estructuras de datos custom del
 *     proyecto (ListaDoubleLinkedL, ColaPrioridadMonticulo) y los
 *     algoritmos de ruteo (CalculadorRutas).
 * </p>
 * @author Ivan
 * @version 1.0
 */
public class SistemaViajes {
    private static final double INFINITO = Double.POSITIVE_INFINITY;

    private final GrafoDirigido grafo;
    private final CalculadorRutas ruteador;
    private final ListaDoubleLinkedL vehiculos;
    private final ListaDoubleLinkedL usuarios;

    /**
     * Construye el sistema de viajes con el grafo y el ruteador dados.
     * @param grafo Grafo dirigido con la red vial
     * @param ruteador Algoritmo de calculo de rutas (Dijkstra o Floyd-Warshall)
     */
    public SistemaViajes(GrafoDirigido grafo, CalculadorRutas ruteador) {
        this.grafo = grafo;
        this.ruteador = ruteador;
        this.vehiculos = new ListaDoubleLinkedL();
        this.usuarios = new ListaDoubleLinkedL();
    }

    /**
     * Registra un vehiculo en el sistema.
     * @param v Vehiculo a registrar
     */
    public void registrarVehiculo(Vehiculo v) {
        vehiculos.insertar(v, vehiculos.tamanio());
    }

    /**
     * Agrega un usuario al sistema.
     * @param u Usuario a agregar
     */
    public void agregarUsuario(Usuario u) {
        usuarios.insertar(u, usuarios.tamanio());
    }

    /**
     * Devuelve la cantidad de vehiculos registrados.
     * @return Total de vehiculos
     */
    public int totalVehiculos() {
        return vehiculos.tamanio();
    }

    /**
     * Devuelve la cantidad de usuarios registrados.
     * @return Total de usuarios
     */
    public int totalUsuarios() {
        return usuarios.tamanio();
    }

    /**
     * Devuelve el vehiculo en la posicion indicada.
     * @param indice Indice del vehiculo (0-based)
     * @return Vehiculo en la posicion
     */
    public Vehiculo getVehiculo(int indice) {
        return (Vehiculo) vehiculos.devolver(indice);
    }

    /**
     * Devuelve el usuario en la posicion indicada.
     * @param indice Indice del usuario (0-based)
     * @return Usuario en la posicion
     */
    public Usuario getUsuario(int indice) {
        return (Usuario) usuarios.devolver(indice);
    }

    /**
     * Procesa una solicitud de viaje de un usuario.
     * <p>
     *     Coloca todos los vehiculos disponibles en la cola de despacho
     *     ordenados por su ETA al nodo del usuario (mas cercano primero).
     *     Simula la evaluacion secuencial hasta que un vehiculo acepta
     *     el viaje. Cuando acepta, la cola de despacho se destruye.
     * </p>
     * @param usuario Usuario que solicita el viaje
     * @return El vehiculo que acepto el viaje, o null si ninguno acepto
     */
    public Vehiculo solicitarViaje(Usuario usuario) {
        ColaPrioridadMonticulo colaDespacho = new ColaPrioridadMonticulo(vehiculos.tamanio());

        // Poner todos los vehiculos disponibles en la cola de despacho
        for (int i = 0; i < vehiculos.tamanio(); i++) {
            Vehiculo v = (Vehiculo) vehiculos.devolver(i);
            if (v.isDisponible()) {
                double eta = calcularETA(v.getNodoActual(), usuario.getNodoOrigen());
                colaDespacho.insertar(i, eta);
            }
        }

        // Procesar la cola secuencialmente
        while (!colaDespacho.estaVacia()) {
            int idxVehiculo = colaDespacho.extraerMin();
            Vehiculo candidato = (Vehiculo) vehiculos.devolver(idxVehiculo);

            if (candidato.isDisponible()) {
                // El vehiculo acepta el viaje
                aceptarViaje(candidato, usuario);
                return candidato;
            }
            // Si ya no esta disponible (ej. otro viaje lo tomo), sigue con el siguiente
        }

        return null;
    }

    /**
     * Calcula el ETA (tiempo estimado de llegada) entre dos nodos usando el ruteador.
     * @param origen Nodo de origen
     * @param destino Nodo de destino
     * @return Tiempo estimado en segundos, o infinito si no hay ruta
     */
    public double calcularETA(int origen, int destino) {
        int[] ruta = ruteador.calcularRuta(origen, destino);
        if (ruta.length == 0) {
            return INFINITO;
        }

        double eta = 0.0;
        for (int i = 0; i < ruta.length - 1; i++) {
            eta += grafo.getMatrizCosto().devolver(ruta[i], ruta[i + 1]);
        }
        return eta;
    }

    /**
     * Acepta un viaje: asigna el pasajero, calcula la ruta y cambia el estado del vehiculo.
     * @param vehiculo Vehiculo que acepta el viaje
     * @param usuario Usuario a recoger
     */
    private void aceptarViaje(Vehiculo vehiculo, Usuario usuario) {
        vehiculo.setEstado(EstadoVehiculo.APROXIMANDO);
        vehiculo.setPasajeroAbordo(usuario);

        int[] ruta = ruteador.calcularRuta(vehiculo.getNodoActual(), usuario.getNodoOrigen());
        vehiculo.setRutaActiva(ruta);
    }
}
