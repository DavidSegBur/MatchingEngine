package group20tup.matchingengine.model.recursos.simulacion;

/**
 * Estados posibles de un vehiculo en el sistema de simulacion.
 * <p>
 *     DISPONIBLE: vehiculo libre recorriendo la red sin pasajero.
 *     APROXIMANDO: vehiculo en camino a recoger a un usuario.
 *     EN_VIAJE: vehiculo transportando a un pasajero a su destino.
 * </p>
 * @author Ivan
 * @version 1.0
 */
public enum EstadoVehiculo {
    DISPONIBLE,
    APROXIMANDO,
    EN_VIAJE
}
