package group20tup.matchingengine;

import group20tup.matchingengine.model.recursos.simulacion.EstadoVehiculo;
import group20tup.matchingengine.model.recursos.simulacion.Usuario;
import group20tup.matchingengine.model.recursos.simulacion.Vehiculo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehiculoTest {

    @Test
    @DisplayName("Transiciona entre estados correctamente")
    void testVehiculoEstadoTransiciones() {
        Vehiculo v = new Vehiculo("ABC123", 5);

        assertEquals(EstadoVehiculo.DISPONIBLE, v.getEstado());
        assertTrue(v.isDisponible());

        v.setEstado(EstadoVehiculo.APROXIMANDO);
        assertFalse(v.isDisponible());
        assertEquals(EstadoVehiculo.APROXIMANDO, v.getEstado());

        v.setEstado(EstadoVehiculo.EN_VIAJE);
        assertEquals(EstadoVehiculo.EN_VIAJE, v.getEstado());

        v.setEstado(EstadoVehiculo.DISPONIBLE);
        assertTrue(v.isDisponible());

        v.setNodoActual(42);
        assertEquals(42, v.getNodoActual());

        v.setRutaActiva(new int[]{5, 6, 7});
        assertArrayEquals(new int[]{5, 6, 7}, v.getRutaActiva());
        assertEquals(0, v.getIndiceRuta());

        Usuario user = new Usuario(1, 10);
        v.setPasajeroAbordo(user);
        assertEquals(user, v.getPasajeroAbordo());

        v.setPasajeroAbordo(null);
        assertNull(v.getPasajeroAbordo());
    }

    @Test
    @DisplayName("setDestacadoHasta almacena correctamente el valor")
    void testVehiculoDestacadoHasta() {
        Vehiculo v = new Vehiculo("EXP01", 0);
        assertEquals(0, v.getDestacadoHasta(), "Valor inicial debe ser 0");
        v.setDestacadoHasta(12345L);
        assertEquals(12345L, v.getDestacadoHasta());
    }

    @Test
    @DisplayName("setRutaActiva con arreglo vacio no causa errores")
    void testVehiculoRutaActivaVacia() {
        Vehiculo v = new Vehiculo("VAC01", 0);
        assertDoesNotThrow(() -> v.setRutaActiva(new int[0]));
        assertEquals(0, v.getRutaActiva().length);
    }
}
