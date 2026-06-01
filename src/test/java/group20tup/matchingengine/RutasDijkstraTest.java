package group20tup.matchingengine;

import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoDirigido;
import group20tup.matchingengine.model.utilidades.calculadorescaminos.DijkstraRutas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RutasDijkstraTest {

    private static GrafoDirigido grafoLineal(int n) {
        GrafoDirigido g = new GrafoDirigido(n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                g.getMatrizCosto().actualizar(Double.POSITIVE_INFINITY, i, j);
            }
            g.getMatrizCosto().actualizar(0.0, i, i);
        }
        for (int i = 0; i < n - 1; i++) {
            g.getMatrizCosto().actualizar((double) i + 1, i, i + 1);
        }
        return g;
    }

    @Test
    @DisplayName("Encuentra camino multi-salto en grafo lineal de 5 nodos")
    void testDijkstraRutaEncuentraCamino() {
        GrafoDirigido grafo = grafoLineal(5);
        DijkstraRutas d = new DijkstraRutas(grafo);
        int[] ruta = d.calcularRuta(0, 4);

        assertNotNull(ruta);
        assertArrayEquals(new int[]{0, 1, 2, 3, 4}, ruta);
    }

    @Test
    @DisplayName("Mismo origen y destino retorna [origen]")
    void testDijkstraMismoOrigenYDestino() {
        GrafoDirigido grafo = grafoLineal(3);
        DijkstraRutas d = new DijkstraRutas(grafo);
        int[] ruta = d.calcularRuta(1, 1);

        assertNotNull(ruta);
        assertEquals(1, ruta.length);
        assertEquals(1, ruta[0]);
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
