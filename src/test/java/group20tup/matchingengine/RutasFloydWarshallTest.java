package group20tup.matchingengine;

import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoDirigido;
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
}
