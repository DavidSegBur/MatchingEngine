package group20tup.matchingengine;

import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoDirigido;
import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoMapa;
import group20tup.matchingengine.model.utilidades.calculadorescaminos.DijkstraRutas;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RutasDijkstraTest {

    private static GrafoMapa mapaSalta;
    private static DijkstraRutas dijkstra;

    @BeforeAll
    static void init() {
        mapaSalta = new GrafoMapa();
        mapaSalta.cargarGrafo();
        dijkstra = new DijkstraRutas(mapaSalta);
    }

    @Test
    @DisplayName("Encuentra camino entre nodos conectados")
    void testDijkstraRutaEncuentraCamino() {
        int[] ruta = dijkstra.calcularRuta(0, 47);

        assertNotNull(ruta);
        assertTrue(ruta.length > 0, "Debe existir una ruta entre los nodos 0 y 47");
        assertEquals(0, ruta[0], "La ruta debe iniciar en el nodo 0");
        assertEquals(47, ruta[ruta.length - 1], "La ruta debe terminar en el nodo 47");
    }

    @Test
    @DisplayName("Mismo origen y destino retorna [origen]")
    void testDijkstraMismoOrigenYDestino() {
        int[] ruta = dijkstra.calcularRuta(0, 0);

        assertNotNull(ruta);
        assertEquals(1, ruta.length);
        assertEquals(0, ruta[0]);
    }

    @Test
    @DisplayName("Ruta directa entre nodos adyacentes retorna 2 elementos")
    void testDijkstraRutaDirecta() {
        GrafoDirigido grafo = new GrafoDirigido(3);
        grafo.getMatrizCosto().actualizar(1.0, 0, 1);
        DijkstraRutas d = new DijkstraRutas(grafo);
        int[] ruta = d.calcularRuta(0, 1);
        assertNotNull(ruta);
        assertEquals(2, ruta.length);
        assertEquals(0, ruta[0]);
        assertEquals(1, ruta[1]);
    }

    @Test
    @DisplayName("Ruta inalcanzable retorna arreglo vacio")
    void testDijkstraRutaInalcanzable() {
        GrafoDirigido grafoSintetico = new GrafoDirigido(2);
        DijkstraRutas d = new DijkstraRutas(grafoSintetico);
        int[] ruta = d.calcularRuta(0, 1);
        assertNotNull(ruta);
        assertEquals(0, ruta.length);
    }
}
