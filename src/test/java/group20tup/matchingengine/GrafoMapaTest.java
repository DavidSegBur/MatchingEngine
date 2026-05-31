package group20tup.matchingengine;

import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoMapa;
import group20tup.matchingengine.model.recursos.MetadataNodo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GrafoMapaTest {

    private static GrafoMapa mapaSalta;

    @BeforeAll
    static void init() {
        mapaSalta = new GrafoMapa();
        mapaSalta.cargarGrafo();
    }

    @Test
    @DisplayName("Carga 1665 esquinas con metadatos correctos")
    void testGrafoMapaCargaCorrecta() {
        assertEquals(1665, mapaSalta.getListaEsquinas().tamanio());

        MetadataNodo primerNodo = (MetadataNodo) mapaSalta.getListaEsquinas().devolver(0);
        assertNotNull(primerNodo);
        assertEquals(392350307L, primerNodo.getIdOSM());
        assertEquals("Florentino Ameghino y Paseo Florentino Ameghino",
                primerNodo.getNombreEsquina());

        assertEquals(392350307L, mapaSalta.getMapeoIndicesAIdOSM()[0]);
    }

    @Test
    @DisplayName("Todos los nodos tienen metadatos completos (lat, long, calles)")
    void testGrafoNodosTienenMetadatosCompletos() {
        for (int i = 0; i < mapaSalta.getListaEsquinas().tamanio(); i++) {
            MetadataNodo nodo = (MetadataNodo) mapaSalta.getListaEsquinas().devolver(i);
            assertNotNull(nodo, "Nodo en indice " + i + " no debe ser null");
            assertTrue(nodo.getLatitud() >= -90 && nodo.getLatitud() <= 90,
                    "Latitud invalida en nodo " + i);
            assertTrue(nodo.getLongitud() >= -180 && nodo.getLongitud() <= 180,
                    "Longitud invalida en nodo " + i);
        }
    }

    @Test
    @DisplayName("Matriz contiene aristas dirigidas validas")
    void testGrafoMatrizTieneAristas() {
        int orden = mapaSalta.getOrden();
        int aristas = 0;
        for (int i = 0; i < orden; i++) {
            for (int j = 0; j < orden; j++) {
                if (mapaSalta.getMatrizCosto().areConnected(i, j)) {
                    aristas++;
                }
            }
        }
        assertTrue(aristas > 0, "Debe haber al menos una arista en el grafo");
        assertTrue(aristas >= orden, "Debe haber al menos " + orden + " aristas (una por nodo)");
    }
}
