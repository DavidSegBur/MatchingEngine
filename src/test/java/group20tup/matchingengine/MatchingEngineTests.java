package group20tup.matchingengine;

import group20tup.matchingengine.model.estructuras.lineales.colas.ColaPrioridadMonticulo;
import group20tup.matchingengine.model.estructuras.nolineales.arboles.MonticuloBinario;
import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoDirigido;
import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoMapa;
import group20tup.matchingengine.model.recursos.MetadataNodo;
import group20tup.matchingengine.model.recursos.simulacion.EstadoVehiculo;
import group20tup.matchingengine.model.recursos.simulacion.Usuario;
import group20tup.matchingengine.model.recursos.simulacion.Vehiculo;
import group20tup.matchingengine.model.utilidades.calculadorescaminos.DijkstraRutas;
import group20tup.matchingengine.model.utilidades.calculadorescaminos.FloydWarshallRutas;
import group20tup.matchingengine.model.utilidades.sistema.SistemaViajes;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MatchingEngineTests {

    private static GrafoMapa mapaSalta;
    private static SistemaViajes sistema;
    private static DijkstraRutas dijkstra;

    @BeforeAll
    static void init() {
        mapaSalta = new GrafoMapa();
        mapaSalta.cargarGrafo();
        dijkstra = new DijkstraRutas(mapaSalta);
        sistema = new SistemaViajes(mapaSalta, dijkstra);
    }

    @Test
    @DisplayName("GrafoMapa carga 1665 esquinas con metadatos correctos")
    void testGrafoMapaCargaCorrecta() {
        assertEquals(1665, mapaSalta.getListaEsquinas().tamanio(),
                "Deben cargarse exactamente 1665 esquinas");

        MetadataNodo primerNodo = (MetadataNodo) mapaSalta.getListaEsquinas().devolver(0);
        assertNotNull(primerNodo);
        assertEquals(392350307L, primerNodo.getIdOSM());
        assertEquals("Florentino Ameghino y Paseo Florentino Ameghino",
                primerNodo.getNombreEsquina());

        assertEquals(392350307L, mapaSalta.getMapeoIndicesAIdOSM()[0]);
    }

    @Test
    @DisplayName("Dijkstra encuentra camino entre nodos conectados")
    void testDijkstraRutaEncuentraCamino() {
        int[] ruta = dijkstra.calcularRuta(0, 47);

        assertNotNull(ruta);
        assertTrue(ruta.length > 0, "Debe existir una ruta entre los nodos 0 y 47");
        assertEquals(0, ruta[0], "La ruta debe iniciar en el nodo 0");
        assertEquals(47, ruta[ruta.length - 1], "La ruta debe terminar en el nodo 47");
    }

    @Test
    @DisplayName("Dijkstra con mismo origen y destino retorna [origen]")
    void testDijkstraMismoOrigenYDestino() {
        int[] ruta = dijkstra.calcularRuta(0, 0);

        assertNotNull(ruta);
        assertEquals(1, ruta.length);
        assertEquals(0, ruta[0]);
    }

    @Test
    @DisplayName("Floyd-Warshall calcula caminos correctos en grafo sintetico")
    void testFloydWarshallCaminoCorrecto() {
        GrafoDirigido grafo = new GrafoDirigido(4);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                grafo.getMatrizCosto().actualizar(Double.POSITIVE_INFINITY, i, j);
            }
        }
        for (int i = 0; i < 4; i++) {
            grafo.getMatrizCosto().actualizar(0.0, i, i);
        }
        grafo.getMatrizCosto().actualizar(2.0, 0, 1);
        grafo.getMatrizCosto().actualizar(5.0, 0, 2);
        grafo.getMatrizCosto().actualizar(1.0, 1, 2);
        grafo.getMatrizCosto().actualizar(4.0, 1, 3);
        grafo.getMatrizCosto().actualizar(1.0, 2, 3);

        FloydWarshallRutas floyd = new FloydWarshallRutas(grafo);

        int[] ruta03 = floyd.calcularRuta(0, 3);
        assertArrayEquals(new int[]{0, 1, 2, 3}, ruta03,
                "Ruta 0->3 debe ser 0-1-2-3");

        double costo03 = 0;
        for (int i = 0; i < ruta03.length - 1; i++) {
            costo03 += grafo.getMatrizCosto().devolver(ruta03[i], ruta03[i + 1]);
        }
        assertEquals(4.0, costo03, 1e-9, "Costo 0->3 debe ser 4.0");

        int[] ruta02 = floyd.calcularRuta(0, 2);
        assertArrayEquals(new int[]{0, 1, 2}, ruta02,
                "Ruta 0->2 debe ser 0-1-2 (no directa)");
    }

    @Test
    @DisplayName("SistemaViajes registra vehiculos y usuarios correctamente")
    void testSistemaViajesRegistro() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);

        s.registrarVehiculo(new Vehiculo("TEST01", 0));
        s.registrarVehiculo(new Vehiculo("TEST02", 100));
        s.registrarVehiculo(new Vehiculo("TEST03", 200));
        s.agregarUsuario(new Usuario(1, 47));
        s.agregarUsuario(new Usuario(2, 100));

        assertEquals(3, s.totalVehiculos());
        assertEquals(2, s.totalUsuarios());

        assertNotNull(s.getVehiculo(0));
        assertNotNull(s.getUsuario(0));
        assertEquals("TEST01", s.getVehiculo(0).getPatente());
    }

    @Test
    @DisplayName("SistemaViajes calcula ETA finita entre nodos conectados")
    void testSistemaViajesCalculoETA() {
        double eta = sistema.calcularETA(0, 47);

        assertTrue(Double.isFinite(eta),
                "ETA entre nodos conectados debe ser finito");
        assertTrue(eta > 0,
                "ETA entre nodos distintos debe ser positivo");
    }

    @Test
    @DisplayName("SistemaViajes despacha solo vehiculos disponibles")
    void testSistemaViajesSolicitarViaje() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);

        s.registrarVehiculo(new Vehiculo("DISP01", 0));
        s.registrarVehiculo(new Vehiculo("DISP02", 100));

        Vehiculo ocupado = new Vehiculo("OCUP01", 200);
        ocupado.setEstado(EstadoVehiculo.EN_VIAJE);
        s.registrarVehiculo(ocupado);

        Usuario usuario = new Usuario(99, 47);
        s.agregarUsuario(usuario);

        Vehiculo elegido = s.solicitarViaje(usuario);

        assertNotNull(elegido, "Un vehiculo disponible debe aceptar el viaje");
        assertNotEquals("OCUP01", elegido.getPatente(),
                "El vehiculo ocupado no debe ser despachado");
        assertEquals(EstadoVehiculo.APROXIMANDO, elegido.getEstado(),
                "El vehiculo debe cambiar a APROXIMANDO");
        assertNotNull(elegido.getPasajeroAbordo(),
                "Debe tener un pasajero asignado");
        assertEquals(usuario.getId(), elegido.getPasajeroAbordo().getId());
        assertTrue(elegido.getRutaActiva().length > 0,
                "Debe tener una ruta activa hacia el usuario");
    }

    @Test
    @DisplayName("ColaPrioridadMonticulo extrae en orden ascendente de prioridad")
    void testColaPrioridadMonticulo() {
        ColaPrioridadMonticulo cola = new ColaPrioridadMonticulo(4);

        cola.insertar(3, 10.0);
        cola.insertar(1, 5.0);
        cola.insertar(2, 15.0);
        cola.insertar(0, 1.0);

        assertEquals(0, cola.extraerMin(), "Primero debe salir el de prioridad 1.0");
        assertEquals(1, cola.extraerMin(), "Segundo debe salir el de prioridad 5.0");
        assertEquals(3, cola.extraerMin(), "Tercero debe salir el de prioridad 10.0");
        assertEquals(2, cola.extraerMin(), "Cuarto debe salir el de prioridad 15.0");
        assertTrue(cola.estaVacia(), "La cola debe quedar vacia");
    }

    @Test
    @DisplayName("MonticuloBinario funciona correctamente vacio y con elementos")
    void testMonticuloBinarioBasico() {
        MonticuloBinario heap = new MonticuloBinario(2);

        assertTrue(heap.estaVacia(), "Heap nuevo debe estar vacio");
        assertEquals(0, heap.tamanio());
        assertEquals(-1, heap.extraerMin(), "Extraer de heap vacio retorna -1");

        heap.insertar(5, 3.0);
        heap.insertar(3, 1.0);
        heap.insertar(7, 2.0);

        assertFalse(heap.estaVacia(), "Heap con elementos no debe estar vacio");
        assertEquals(3, heap.tamanio());

        assertEquals(3, heap.extraerMin(), "Prioridad 1.0 primero");
        assertEquals(7, heap.extraerMin(), "Prioridad 2.0 segundo");
        assertEquals(5, heap.extraerMin(), "Prioridad 3.0 tercero");
        assertTrue(heap.estaVacia(), "Heap debe quedar vacio");
    }
}
