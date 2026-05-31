package group20tup.matchingengine;

import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoDirigido;
import group20tup.matchingengine.model.utilidades.calculadorescaminos.DijkstraRutas;
import group20tup.matchingengine.model.utilidades.calculadorescaminos.FloydWarshallRutas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RutasFloydWarshallTest {

    private static GrafoDirigido grafoSintetico(int n) {
        GrafoDirigido g = new GrafoDirigido(n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                g.getMatrizCosto().actualizar(Double.POSITIVE_INFINITY, i, j);
            }
        }
        for (int i = 0; i < n; i++) {
            g.getMatrizCosto().actualizar(0.0, i, i);
        }
        return g;
    }

    @Test
    @DisplayName("Camino correcto en grafo sintetico de 4 nodos")
    void testFloydWarshallCaminoCorrecto() {
        GrafoDirigido grafo = grafoSintetico(4);
        grafo.getMatrizCosto().actualizar(2.0, 0, 1);
        grafo.getMatrizCosto().actualizar(5.0, 0, 2);
        grafo.getMatrizCosto().actualizar(1.0, 1, 2);
        grafo.getMatrizCosto().actualizar(4.0, 1, 3);
        grafo.getMatrizCosto().actualizar(1.0, 2, 3);

        FloydWarshallRutas floyd = new FloydWarshallRutas(grafo);

        int[] ruta03 = floyd.calcularRuta(0, 3);
        assertArrayEquals(new int[]{0, 1, 2, 3}, ruta03);

        double costo03 = 0;
        for (int i = 0; i < ruta03.length - 1; i++) {
            costo03 += grafo.getMatrizCosto().devolver(ruta03[i], ruta03[i + 1]);
        }
        assertEquals(4.0, costo03, 1e-9);

        int[] ruta02 = floyd.calcularRuta(0, 2);
        assertArrayEquals(new int[]{0, 1, 2}, ruta02);
    }

    @Test
    @DisplayName("Retorna arreglo vacio para nodos desconectados")
    void testFloydWarshallRutaInexistente() {
        GrafoDirigido grafo = grafoSintetico(4);
        grafo.getMatrizCosto().actualizar(2.0, 0, 1);
        grafo.getMatrizCosto().actualizar(1.0, 1, 2);

        FloydWarshallRutas floyd = new FloydWarshallRutas(grafo);

        int[] ruta = floyd.calcularRuta(0, 3);
        assertEquals(0, ruta.length);
    }

    @Test
    @DisplayName("Grafo de un solo nodo retorna [0]")
    void testFloydWarshallNodoUnico() {
        GrafoDirigido grafo = grafoSintetico(1);
        FloydWarshallRutas floyd = new FloydWarshallRutas(grafo);
        int[] ruta = floyd.calcularRuta(0, 0);
        assertArrayEquals(new int[]{0}, ruta);
    }

    @Test
    @DisplayName("Reconstruccion de camino tiene nodos intermedios correctos")
    void testFloydPathReconstruction() {
        GrafoDirigido grafo = grafoSintetico(5);
        // 0 -> 1 -> 2 -> 3 -> 4 (linear chain)
        grafo.getMatrizCosto().actualizar(1.0, 0, 1);
        grafo.getMatrizCosto().actualizar(1.0, 1, 2);
        grafo.getMatrizCosto().actualizar(1.0, 2, 3);
        grafo.getMatrizCosto().actualizar(1.0, 3, 4);

        FloydWarshallRutas floyd = new FloydWarshallRutas(grafo);

        int[] ruta = floyd.calcularRuta(0, 4);
        assertArrayEquals(new int[]{0, 1, 2, 3, 4}, ruta);

        // Shortcut: 0 -> 2 direct (shorter than 0->1->2)
        grafo.getMatrizCosto().actualizar(0.5, 0, 2);
        FloydWarshallRutas floyd2 = new FloydWarshallRutas(grafo);
        int[] ruta2 = floyd2.calcularRuta(0, 4);
        assertArrayEquals(new int[]{0, 2, 3, 4}, ruta2,
                "Con atajo 0->2 de 0.5, la ruta debe ser 0-2-3-4");
    }

    @Test
    @DisplayName("Floyd-Warshall y Dijkstra producen el mismo costo en grafo denso")
    void testFloydVsDijkstraConsistencia() {
        GrafoDirigido grafo = grafoSintetico(6);
        grafo.getMatrizCosto().actualizar(3.0, 0, 1);
        grafo.getMatrizCosto().actualizar(6.0, 0, 2);
        grafo.getMatrizCosto().actualizar(2.0, 1, 2);
        grafo.getMatrizCosto().actualizar(4.0, 1, 3);
        grafo.getMatrizCosto().actualizar(1.0, 2, 3);
        grafo.getMatrizCosto().actualizar(5.0, 2, 4);
        grafo.getMatrizCosto().actualizar(7.0, 3, 4);
        grafo.getMatrizCosto().actualizar(3.0, 3, 5);
        grafo.getMatrizCosto().actualizar(2.0, 4, 5);

        FloydWarshallRutas floyd = new FloydWarshallRutas(grafo);
        DijkstraRutas dijkstra = new DijkstraRutas(grafo);

        int[][] pares = {{0, 5}, {1, 4}, {0, 3}, {2, 5}, {1, 5}};
        for (int[] par : pares) {
            int[] rutaFloyd = floyd.calcularRuta(par[0], par[1]);
            int[] rutaDijkstra = dijkstra.calcularRuta(par[0], par[1]);

            boolean floydAlcanzable = rutaFloyd.length > 0;
            boolean dijkstraAlcanzable = rutaDijkstra.length > 0;
            assertEquals(dijkstraAlcanzable, floydAlcanzable,
                    "Ambos algoritmos deben coincidir en alcanzabilidad para " + par[0] + " -> " + par[1]);

            if (floydAlcanzable) {
                double costeFloyd = 0;
                for (int i = 0; i < rutaFloyd.length - 1; i++) {
                    costeFloyd += grafo.getMatrizCosto().devolver(rutaFloyd[i], rutaFloyd[i + 1]);
                }
                double costeDijkstra = 0;
                for (int i = 0; i < rutaDijkstra.length - 1; i++) {
                    costeDijkstra += grafo.getMatrizCosto().devolver(rutaDijkstra[i], rutaDijkstra[i + 1]);
                }
                assertEquals(costeDijkstra, costeFloyd, 1e-9,
                        "El costo del camino debe coincidir entre Floyd y Dijkstra");
            }
        }
    }

    @Test
    @DisplayName("Reconstruccion de Floyd produce costos correctos contra matriz de costos")
    void testFloydReconstruccionCostosCorrectos() {
        GrafoDirigido grafo = grafoSintetico(5);
        grafo.getMatrizCosto().actualizar(2.0, 0, 1);
        grafo.getMatrizCosto().actualizar(7.0, 0, 2);
        grafo.getMatrizCosto().actualizar(3.0, 1, 2);
        grafo.getMatrizCosto().actualizar(1.0, 1, 3);
        grafo.getMatrizCosto().actualizar(5.0, 2, 4);
        grafo.getMatrizCosto().actualizar(2.0, 3, 4);
        grafo.getMatrizCosto().actualizar(8.0, 3, 0);
        grafo.getMatrizCosto().actualizar(4.0, 4, 2);

        FloydWarshallRutas floyd = new FloydWarshallRutas(grafo);

        int[][] pares = {{0, 4}, {1, 4}, {3, 2}, {0, 0}};
        for (int[] par : pares) {
            int[] ruta = floyd.calcularRuta(par[0], par[1]);
            assertNotNull(ruta, "La ruta no debe ser null");
            if (par[0] == par[1]) {
                assertArrayEquals(new int[]{par[0]}, ruta, "Ruta a si mismo debe ser [nodo]");
            } else {
                assertTrue(ruta.length >= 2,
                        "Debe haber al menos 2 nodos en ruta " + par[0] + " -> " + par[1]);
                assertEquals(par[0], ruta[0],
                        "El primer nodo debe ser el origen");
                assertEquals(par[1], ruta[ruta.length - 1],
                        "El ultimo nodo debe ser el destino");

                double costeAcumulado = 0;
                for (int i = 0; i < ruta.length - 1; i++) {
                    double arista = grafo.getMatrizCosto().devolver(ruta[i], ruta[i + 1]);
                    assertTrue(arista > 0 && arista < Double.POSITIVE_INFINITY,
                            "Arista " + ruta[i] + " -> " + ruta[i + 1] + " debe ser valida");
                    costeAcumulado += arista;
                }
                assertTrue(costeAcumulado > 0,
                        "El costo acumulado debe ser positivo");
            }
        }

        // Par inalcanzable 2 -> 1 debe retornar arreglo vacio
        assertTrue(floyd.calcularRuta(2, 1).length == 0,
                "2 -> 1 debe ser inalcanzable");
    }
}
