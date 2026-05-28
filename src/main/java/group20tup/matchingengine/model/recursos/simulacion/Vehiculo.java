package group20tup.matchingengine.model.recursos.simulacion;

import java.util.Objects;

/**
 * Representa un vehiculo de la flota en el sistema de simulacion.
 * <p>
 *     Cada vehiculo tiene una patente unica, una posicion actual
 *     (nodo del grafo), un estado operativo y, si esta realizando
 *     un viaje, una referencia al usuario a bordo y la ruta activa
 *     a seguir.
 * </p>
 * @author Ivan
 * @version 1.0
 */
public class Vehiculo {
    private final String patente;
    private int nodoActual;
    private EstadoVehiculo estado;
    private Usuario pasajeroAbordo;
    private int[] rutaActiva;
    private int indiceRuta;

    /**
     * Construye un vehiculo con los datos basicos.
     * @param patente Identificador unico del vehiculo (patente/licencia)
     * @param nodoInicial Nodo del grafo donde se ubica inicialmente
     */
    public Vehiculo(String patente, int nodoInicial) {
        this.patente = patente;
        this.nodoActual = nodoInicial;
        this.estado = EstadoVehiculo.DISPONIBLE;
        this.pasajeroAbordo = null;
        this.rutaActiva = new int[0];
        this.indiceRuta = 0;
    }

    /**
     * Devuelve la patente del vehiculo.
     * @return Patente unica
     */
    public String getPatente() {
        return patente;
    }

    /**
     * Devuelve el nodo actual en el que se encuentra el vehiculo.
     * @return Indice del nodo en el grafo
     */
    public int getNodoActual() {
        return nodoActual;
    }

    /**
     * Establece la posicion actual del vehiculo.
     * @param nodoActual Nuevo nodo
     */
    public void setNodoActual(int nodoActual) {
        this.nodoActual = nodoActual;
    }

    /**
     * Devuelve el estado operativo actual del vehiculo.
     * @return Estado actual (DISPONIBLE, APROXIMANDO, EN_VIAJE)
     */
    public EstadoVehiculo getEstado() {
        return estado;
    }

    /**
     * Establece el estado operativo del vehiculo.
     * @param estado Nuevo estado
     */
    public void setEstado(EstadoVehiculo estado) {
        this.estado = estado;
    }

    /**
     * Devuelve el usuario que viaja a bordo, o null si esta disponible.
     * @return Pasajero actual, o null
     */
    public Usuario getPasajeroAbordo() {
        return pasajeroAbordo;
    }

    /**
     * Establece el usuario a bordo del vehiculo.
     * @param pasajeroAbordo Pasajero, o null si no hay
     */
    public void setPasajeroAbordo(Usuario pasajeroAbordo) {
        this.pasajeroAbordo = pasajeroAbordo;
    }

    /**
     * Devuelve la ruta activa que el vehiculo esta siguiendo.
     * @return Arreglo de indices de nodos que forman la ruta
     */
    public int[] getRutaActiva() {
        return rutaActiva;
    }

    /**
     * Establece la ruta activa del vehiculo.
     * @param rutaActiva Arreglo de indices de nodos
     */
    public void setRutaActiva(int[] rutaActiva) {
        this.rutaActiva = rutaActiva;
        this.indiceRuta = 0;
    }

    /**
     * Devuelve el indice actual dentro de la ruta activa.
     * @return Posicion en el arreglo de ruta
     */
    public int getIndiceRuta() {
        return indiceRuta;
    }

    /**
     * Establece el indice actual dentro de la ruta activa.
     * @param indiceRuta Nueva posicion en la ruta
     */
    public void setIndiceRuta(int indiceRuta) {
        this.indiceRuta = indiceRuta;
    }

    /**
     * Indica si el vehiculo esta disponible para aceptar viajes.
     * @return true si el estado es DISPONIBLE
     */
    public boolean isDisponible() {
        return estado == EstadoVehiculo.DISPONIBLE;
    }

    /**
     * Compara este vehiculo con otro por su patente.
     * @param o Objeto a comparar
     * @return true si tienen la misma patente
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehiculo vehiculo = (Vehiculo) o;
        return Objects.equals(patente, vehiculo.patente);
    }

    /**
     * Calcula el hash code basado en la patente.
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(patente);
    }
}
