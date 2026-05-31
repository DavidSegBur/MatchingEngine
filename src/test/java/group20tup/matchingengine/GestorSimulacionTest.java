package group20tup.matchingengine;

import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoMapa;
import group20tup.matchingengine.model.recursos.simulacion.Vehiculo;
import group20tup.matchingengine.model.utilidades.calculadorescaminos.DijkstraRutas;
import group20tup.matchingengine.model.utilidades.sistema.GestorSimulacion;
import group20tup.matchingengine.model.utilidades.sistema.SistemaViajes;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GestorSimulacionTest {

    private static GrafoMapa mapaSalta;
    private static DijkstraRutas dijkstra;

    @BeforeAll
    static void init() {
        mapaSalta = new GrafoMapa();
        mapaSalta.cargarGrafo();
        dijkstra = new DijkstraRutas(mapaSalta);
    }

    @Test
    @DisplayName("tick() mantiene densidad: 5 usuarios, 10-15 vehiculos")
    void testTickMantieneDensidad() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        GestorSimulacion gestor = new GestorSimulacion(s, null, mapaSalta, dijkstra);
        gestor.inicializarEntidades();

        assertEquals(5, s.totalUsuarios());
        assertTrue(s.totalVehiculos() >= 10 && s.totalVehiculos() <= 15);

        gestor.tick();

        assertEquals(5, s.totalUsuarios());
        assertTrue(s.totalVehiculos() >= 10 && s.totalVehiculos() <= 15);
    }

    @Test
    @DisplayName("10 ticks seguidos sin excepcion, densidad constante")
    void testTickMultipleMantieneDensidad() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        GestorSimulacion gestor = new GestorSimulacion(s, null, mapaSalta, dijkstra);
        gestor.inicializarEntidades();

        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> gestor.tick());
        }

        assertEquals(5, s.totalUsuarios());
        assertTrue(s.totalVehiculos() >= 10 && s.totalVehiculos() <= 15);
    }

    @Test
    @DisplayName("tick() funciona con renderizador null sin NPE")
    void testTickSinJavaFX() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        GestorSimulacion gestor = new GestorSimulacion(s, null, mapaSalta, dijkstra);
        gestor.inicializarEntidades();

        assertDoesNotThrow(() -> gestor.tick());
        assertDoesNotThrow(() -> gestor.tick());
        assertDoesNotThrow(() -> gestor.tick());
        assertDoesNotThrow(() -> gestor.renderizarFrame());
    }

    @Test
    @DisplayName("Vehiculo disponible sin ruta recibe ruta de 2 nodos adyacentes tras tick")
    void testRoamingVecinoAleatorio() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        GestorSimulacion gestor = new GestorSimulacion(s, null, mapaSalta, dijkstra);
        int nodo = 500;
        Vehiculo v = new Vehiculo("RTST", nodo);
        s.registrarVehiculo(v);
        assertEquals(0, v.getRutaActiva().length);

        gestor.tick();

        int[] ruta = v.getRutaActiva();
        assertTrue(ruta.length >= 2);
        assertEquals(nodo, ruta[0]);
        assertTrue(mapaSalta.getMatrizCosto().areConnected(nodo, ruta[1]));
    }

    @Test
    @DisplayName("Roaming multiple: vehiculo se mueve a traves de varios nodos adyacentes")
    void testRoamingMultiplePasos() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        GestorSimulacion gestor = new GestorSimulacion(s, null, mapaSalta, dijkstra);
        int nodo = 500;
        Vehiculo v = new Vehiculo("RMLT", nodo);
        s.registrarVehiculo(v);

        for (int i = 0; i < 20; i++) {
            gestor.tick();
        }

        assertTrue(v.getNodoActual() != 500 || v.getIndiceRuta() > 0,
                "El vehiculo debe haberse movido del nodo inicial tras 20 ticks");
        int[] ruta = v.getRutaActiva();
        if (ruta.length >= 2 && v.getIndiceRuta() < ruta.length - 1) {
            int desde = ruta[v.getIndiceRuta()];
            int hasta = ruta[v.getIndiceRuta() + 1];
            assertTrue(mapaSalta.getMatrizCosto().areConnected(desde, hasta));
        }
    }

    @Test
    @DisplayName("Vehiculo en nodo sin salidas usa ruta de 2 nodos a destino aleatorio")
    void testRoamingSinSalidasUsaDestinoAleatorio() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        GestorSimulacion gestor = new GestorSimulacion(s, null, mapaSalta, dijkstra);
        int nodo = 0;
        Vehiculo v = new Vehiculo("SNSL", nodo);
        s.registrarVehiculo(v);

        gestor.tick();

        int[] ruta = v.getRutaActiva();
        assertTrue(ruta.length >= 2);
        assertEquals(nodo, ruta[0]);
    }

    @Test
    @DisplayName("Roaming no rompe las invariantes de densidad")
    void testRoamingMantieneDensidad() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        GestorSimulacion gestor = new GestorSimulacion(s, null, mapaSalta, dijkstra);
        gestor.inicializarEntidades();

        for (int i = 0; i < 50; i++) {
            gestor.tick();
        }

        assertEquals(5, s.totalUsuarios(), "Debe mantener 5 usuarios tras roaming");
        assertTrue(s.totalVehiculos() >= 10 && s.totalVehiculos() <= 15,
                "Debe mantener vehiculos entre 10 y 15 tras roaming");
    }

    @Test
    @DisplayName("Vehiculo cambia de nodo tras multiples ticks de roaming")
    void testVehiculoSeMueveEntreNodosConsecutivos() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        GestorSimulacion gestor = new GestorSimulacion(s, null, mapaSalta, dijkstra);
        int nodo = 500;
        Vehiculo v = new Vehiculo("MOVE01", nodo);
        s.registrarVehiculo(v);

        int nodoAnterior = v.getNodoActual();
        boolean seMovio = false;
        for (int i = 0; i < 30; i++) {
            gestor.tick();
            if (v.getNodoActual() != nodoAnterior) {
                seMovio = true;
                break;
            }
        }
        assertTrue(seMovio, "El vehiculo debe cambiar de nodo tras varios ticks de roaming");
    }
}
