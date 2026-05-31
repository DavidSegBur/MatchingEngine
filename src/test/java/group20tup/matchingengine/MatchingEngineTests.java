package group20tup.matchingengine;

import group20tup.matchingengine.model.estructuras.lineales.colas.ColaPrioridadMonticulo;
import group20tup.matchingengine.model.estructuras.lineales.listas.ListaDoubleLinkedL;
import group20tup.matchingengine.model.estructuras.lineales.matrices.MatrizArr;
import group20tup.matchingengine.model.estructuras.lineales.matrices.MatrizGrafo;
import group20tup.matchingengine.model.estructuras.nolineales.arboles.MonticuloBinario;
import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoDirigido;
import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoMapa;
import group20tup.matchingengine.model.recursos.MetadataNodo;
import group20tup.matchingengine.model.recursos.simulacion.EstadoVehiculo;
import group20tup.matchingengine.model.recursos.simulacion.Usuario;
import group20tup.matchingengine.model.recursos.simulacion.Vehiculo;
import group20tup.matchingengine.model.utilidades.calculadorescaminos.DijkstraRutas;
import group20tup.matchingengine.model.utilidades.calculadorescaminos.FloydWarshallRutas;
import group20tup.matchingengine.model.utilidades.sistema.GestorSimulacion;
import group20tup.matchingengine.model.utilidades.sistema.SistemaViajes;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

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

    @Test
    @DisplayName("ListaDoubleLinkedL opera correctamente: insercion, eliminacion, busqueda")
    void testListaDoubleLinkedL() {
        ListaDoubleLinkedL lista = new ListaDoubleLinkedL();

        assertTrue(lista.estaVacia(), "Lista nueva debe estar vacia");
        assertEquals(0, lista.tamanio());

        lista.insertar("A", 0);
        lista.insertar("B", 1);
        lista.insertar("C", 2);
        assertEquals(3, lista.tamanio());
        assertEquals("A", lista.devolver(0));
        assertEquals("B", lista.devolver(1));
        assertEquals("C", lista.devolver(2));

        lista.reemplazar("X", 1);
        assertEquals("X", lista.devolver(1));

        assertEquals(0, lista.buscar("A"), "A debe estar en posicion 0");
        assertEquals(1, lista.buscar("X"), "X debe estar en posicion 1");
        assertEquals(-1, lista.buscar("Z"), "Z no debe encontrarse");

        assertTrue(lista.iguales("A", "A"));
        assertFalse(lista.iguales("A", "B"));

        lista.eliminar(1);
        assertEquals(2, lista.tamanio());
        assertEquals("C", lista.devolver(1), "Tras eliminar pos 1, esa posicion debe tener 'C'");

        assertThrows(IndexOutOfBoundsException.class, () -> lista.devolver(99),
                "devolver con indice fuera de rango debe lanzar excepcion");
        assertThrows(IndexOutOfBoundsException.class, () -> lista.insertar("Z", 99),
                "insertar con posicion > tamanio debe lanzar excepcion");
    }

    @Test
    @DisplayName("MatrizArr mantiene dimensiones, actualiza, limpia y valida limites")
    void testMatrizArr() {
        MatrizArr m = new MatrizArr(3, 4);

        assertEquals(3, m.getNroFilas());
        assertEquals(4, m.getNroColumnas());

        m.actualizar(5.0, 1, 2);
        assertEquals(5.0, m.devolver(1, 2), 1e-12);

        m.limpiaMatriz();
        assertEquals(0.0, m.devolver(1, 2), 1e-12, "Luego de limpiar debe ser 0.0");

        assertThrows(IndexOutOfBoundsException.class, () -> m.actualizar(1.0, 5, 0),
                "Fila fuera de rango debe lanzar excepcion");
        assertThrows(IndexOutOfBoundsException.class, () -> m.devolver(-1, 0),
                "Fila negativa debe lanzar excepcion");
        assertThrows(IndexOutOfBoundsException.class, () -> m.devolver(0, 10),
                "Columna fuera de rango debe lanzar excepcion");
    }

    @Test
    @DisplayName("MatrizGrafo.areConnected detecta conexiones y limites")
    void testMatrizGrafo() {
        MatrizGrafo mg = new MatrizGrafo(5);

        mg.actualizar(3.0, 1, 2);
        assertTrue(mg.areConnected(1, 2), "Costo != 0 debe indicar conexion");

        assertFalse(mg.areConnected(1, 3), "Costo == 0 no debe indicar conexion");

        assertFalse(mg.areConnected(-1, 0), "Indice negativo no debe lanzar excepcion, solo false");
        assertFalse(mg.areConnected(0, 99), "Indice fuera de rango no debe lanzar excepcion, solo false");
    }

    @Test
    @DisplayName("Vehiculo transiciona entre estados correctamente")
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
        assertEquals(0, v.getIndiceRuta(), "Indice de ruta debe reiniciarse a 0");

        Usuario user = new Usuario(1, 10);
        v.setPasajeroAbordo(user);
        assertEquals(user, v.getPasajeroAbordo());

        v.setPasajeroAbordo(null);
        assertNull(v.getPasajeroAbordo());
    }

    @Test
    @DisplayName("Usuario se construye con y sin destino, y compara por id")
    void testUsuarioConstructores() {
        Usuario u1 = new Usuario(1, 10);
        assertEquals(1, u1.getId());
        assertEquals(10, u1.getNodoOrigen());
        assertEquals(-1, u1.getNodoDestino(), "Sin destino, debe ser -1");

        Usuario u2 = new Usuario(2, 20, 30);
        assertEquals(30, u2.getNodoDestino());

        u2.setNodoDestino(99);
        assertEquals(99, u2.getNodoDestino());

        Usuario u1c = new Usuario(1, 99);
        assertEquals(u1, u1c, "Mismo id debe ser igual");
        assertNotEquals(u1, u2, "Distinto id no debe ser igual");
    }

    @Test
    @DisplayName("FloydWarshall retorna arreglo vacio para nodos desconectados")
    void testFloydWarshallRutaInexistente() {
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
        grafo.getMatrizCosto().actualizar(1.0, 1, 2);

        FloydWarshallRutas floyd = new FloydWarshallRutas(grafo);

        int[] ruta = floyd.calcularRuta(0, 3);
        assertEquals(0, ruta.length, "Sin camino a nodo 3 debe retornar arreglo vacio");
    }

    @Test
    @DisplayName("Solicitar viaje sin vehiculos disponibles retorna null")
    void testSistemaViajesSinDisponibles() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);

        Vehiculo ocupado = new Vehiculo("OCUP01", 0);
        ocupado.setEstado(EstadoVehiculo.EN_VIAJE);
        s.registrarVehiculo(ocupado);

        Usuario usuario = new Usuario(1, 47);
        s.agregarUsuario(usuario);

        assertNull(s.solicitarViaje(usuario), "Sin disponibles debe retornar null");
    }

    @Test
    @DisplayName("ETA desde un nodo a si mismo es 0")
    void testSistemaViajesETAMismoNodo() {
        double eta = sistema.calcularETA(0, 0);
        assertEquals(0.0, eta, 1e-12, "ETA mismo origen y destino debe ser 0");
    }

    @Test
    @DisplayName("Ciclo completo: DISPONIBLE -> APROXIMANDO -> EN_VIAJE -> DISPONIBLE")
    void testCicloViajeCompleto() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        s.registrarVehiculo(new Vehiculo("V001", 0));
        Usuario u = new Usuario(1, 47);
        s.agregarUsuario(u);

        Vehiculo v = s.solicitarViaje(u);
        assertNotNull(v);
        assertEquals(EstadoVehiculo.APROXIMANDO, v.getEstado());
        assertEquals(u, v.getPasajeroAbordo());
        assertTrue(v.getRutaActiva().length >= 2);

        assertTrue(s.realizarPickup(v));
        assertEquals(EstadoVehiculo.EN_VIAJE, v.getEstado());
        assertFalse(s.getColaOcupados().estaVacia());

        s.completarTransito(v);
        assertTrue(v.isDisponible());
        assertNull(v.getPasajeroAbordo());
    }

    @Test
    @DisplayName("Pickup remueve al usuario del sistema de viajes")
    void testPickupRemueveUsuario() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        s.registrarVehiculo(new Vehiculo("V002", 0));
        Usuario u = new Usuario(2, 47);
        s.agregarUsuario(u);

        Vehiculo v = s.solicitarViaje(u);
        assertNotNull(v);
        assertEquals(1, s.totalUsuarios());

        s.realizarPickup(v);
        assertEquals(0, s.totalUsuarios(), "Usuario debe ser removido del sistema");
    }

    @Test
    @DisplayName("Cola de ocupados se vacia al completar el transito")
    void testCompletarTransitoReconstruyeCola() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        s.registrarVehiculo(new Vehiculo("V003", 0));
        s.registrarVehiculo(new Vehiculo("V004", 100));
        Usuario u = new Usuario(3, 47);
        s.agregarUsuario(u);

        Vehiculo v = s.solicitarViaje(u);
        assertNotNull(v);
        s.realizarPickup(v);

        assertEquals(1, s.getColaOcupados().tamanio());

        s.completarTransito(v);
        assertTrue(s.getColaOcupados().estaVacia(), "Cola de ocupados debe quedar vacia");
    }

    @Test
    @DisplayName("Solo vehiculos DISPONIBLE son considerados en el dispatch")
    void testDispatchExcluyeOcupados() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);

        Vehiculo v1 = new Vehiculo("OCUP", 0);
        v1.setEstado(EstadoVehiculo.EN_VIAJE);
        s.registrarVehiculo(v1);

        Usuario u = new Usuario(4, 47);
        s.agregarUsuario(u);

        assertNull(s.solicitarViaje(u), "Sin disponibles debe retornar null");

        s.registrarVehiculo(new Vehiculo("DISP", 100));
        Vehiculo elegido = s.solicitarViaje(u);
        assertNotNull(elegido, "Con un disponible debe aceptar");
        assertEquals("DISP", elegido.getPatente());
    }

    @Test
    @DisplayName("calcularTarifa produce valores correctos")
    void testCalculoTarifa() {
        assertEquals(12.50, sistema.calcularTarifa(3600.0), 0.001,
                "3600s a 25 km/h = 25 km * $0.50 = $12.50");
        assertEquals(0.0, sistema.calcularTarifa(Double.POSITIVE_INFINITY),
                "ETA infinito debe dar tarifa 0");
        assertEquals(0.0, sistema.calcularTarifa(0.0),
                "ETA 0 debe dar tarifa 0");
    }

    @Test
    @DisplayName("Dispatch con rechazo simulado usando Random con seed fija")
    void testRechazoConRandomSeed() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        s.registrarVehiculo(new Vehiculo("V010", 0));
        s.registrarVehiculo(new Vehiculo("V011", 100));
        s.registrarVehiculo(new Vehiculo("V012", 200));
        Usuario u = new Usuario(5, 47);
        s.agregarUsuario(u);

        Random rnd = new Random(42);
        Vehiculo v = s.solicitarViaje(u, rnd);
        assertNotNull(v, "Con 3 disponibles al menos uno debe aceptar");
        assertEquals(EstadoVehiculo.APROXIMANDO, v.getEstado());
    }

    @Test
    @DisplayName("obtenerTextoColaDespacho retorna cola formateada con patentes y ETAs")
    void testObtenerTextoColaDespachoConVehiculos() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        s.registrarVehiculo(new Vehiculo("TEST_A", 0));
        s.registrarVehiculo(new Vehiculo("TEST_B", 100));
        Usuario u = new Usuario(10, 47);
        s.agregarUsuario(u);

        String texto = s.obtenerTextoColaDespacho(u);
        assertTrue(texto.contains("── Cola de despacho ──"), "Debe contener el header");
        assertTrue(texto.contains("TEST_A"), "Debe incluir la patente TEST_A");
        assertTrue(texto.contains("TEST_B"), "Debe incluir la patente TEST_B");
        assertTrue(texto.contains("s"), "Debe mostrar la unidad de segundos");
        assertTrue(texto.contains("1."), "Debe numerar los candidatos");
    }

    @Test
    @DisplayName("obtenerTextoColaDespacho retorna sin candidatos si no hay vehiculos")
    void testObtenerTextoColaDespachoSinCandidatos() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        Usuario u = new Usuario(20, 47);
        s.agregarUsuario(u);

        assertEquals("(sin candidatos)", s.obtenerTextoColaDespacho(u));
    }

    @Test
    @DisplayName("obtenerTextoColaDespacho excluye vehiculos ocupados")
    void testObtenerTextoColaDespachoExcluyeOcupados() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        s.registrarVehiculo(new Vehiculo("DISP", 0));
        Vehiculo ocupado = new Vehiculo("OCUP", 100);
        ocupado.setEstado(EstadoVehiculo.EN_VIAJE);
        s.registrarVehiculo(ocupado);
        Usuario u = new Usuario(30, 47);
        s.agregarUsuario(u);

        String texto = s.obtenerTextoColaDespacho(u);
        assertTrue(texto.contains("DISP"), "Debe incluir el vehiculo disponible");
        assertFalse(texto.contains("OCUP"), "No debe incluir el vehiculo ocupado");
    }

    @Test
    @DisplayName("iniciarDespacho y procesarSiguienteDespacho completan el ciclo sin rechazo")
    void testIniciarYProcesarDespachoSinRechazo() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        s.registrarVehiculo(new Vehiculo("V01", 0));
        s.registrarVehiculo(new Vehiculo("V02", 100));
        Usuario u = new Usuario(40, 47);
        s.agregarUsuario(u);

        s.iniciarDespacho(u, null);
        assertTrue(s.hayDespachoActivo(), "Despacho debe estar activo tras iniciar");
        assertEquals(0, s.getCandidatosProcesadosDespacho(), "Aun sin procesar");
        assertTrue(s.getTotalCandidatosDespacho() > 0, "Debe haber candidatos");

        Vehiculo aceptado = s.procesarSiguienteDespacho();
        assertNotNull(aceptado, "Sin rechazo debe aceptar el primer candidato");
        assertFalse(s.hayDespachoActivo(), "Despacho debe finalizar tras aceptar");
        assertEquals(EstadoVehiculo.APROXIMANDO, aceptado.getEstado());
        assertNotNull(aceptado.getPasajeroAbordo());
        assertEquals(u.getId(), aceptado.getPasajeroAbordo().getId());
    }

    @Test
    @DisplayName("procesarSiguienteDespacho con rechazo simulado usando Random seed")
    void testProcesarDespachoConRechazoSeed() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        s.registrarVehiculo(new Vehiculo("V01", 0));
        s.registrarVehiculo(new Vehiculo("V02", 100));
        s.registrarVehiculo(new Vehiculo("V03", 200));
        Usuario u = new Usuario(50, 47);
        s.agregarUsuario(u);

        s.iniciarDespacho(u, new Random(42));
        assertTrue(s.hayDespachoActivo());
        assertEquals(3, s.getTotalCandidatosDespacho());

        Vehiculo aceptado = null;
        while (s.hayDespachoActivo()) {
            aceptado = s.procesarSiguienteDespacho();
        }

        assertNotNull(aceptado, "Con 3 candidatos al menos uno debe aceptar");
        assertEquals(EstadoVehiculo.APROXIMANDO, aceptado.getEstado());
        assertTrue(s.getCandidatosProcesadosDespacho() >= 1, "Debe haber procesado al menos un candidato");
    }

    @Test
    @DisplayName("procesarSiguienteDespacho retorna null si la cola esta vacia")
    void testProcesarDespachoColaVacia() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        Vehiculo ocupado = new Vehiculo("OCUP", 0);
        ocupado.setEstado(EstadoVehiculo.EN_VIAJE);
        s.registrarVehiculo(ocupado);
        Usuario u = new Usuario(60, 47);
        s.agregarUsuario(u);

        s.iniciarDespacho(u, null);
        assertEquals(0, s.getTotalCandidatosDespacho(), "No debe haber candidatos");
        assertTrue(s.hayDespachoActivo(), "Despacho inicia aunque este vacio");

        assertNull(s.procesarSiguienteDespacho(), "Debe retornar null con cola vacia");
        assertFalse(s.hayDespachoActivo(), "Despacho debe finalizar");
    }

    @Test
    @DisplayName("cancelarDespacho resetea el estado del despacho")
    void testCancelarDespacho() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        s.registrarVehiculo(new Vehiculo("V01", 0));
        Usuario u = new Usuario(70, 47);
        s.agregarUsuario(u);

        s.iniciarDespacho(u, null);
        assertTrue(s.hayDespachoActivo());

        s.cancelarDespacho();
        assertFalse(s.hayDespachoActivo(), "Cancelar debe desactivar el despacho");
        assertNull(s.procesarSiguienteDespacho(), "Tras cancelar no debe procesar");

        s.iniciarDespacho(u, null);
        assertTrue(s.hayDespachoActivo(), "Debe poder iniciar un nuevo despacho tras cancelar");
        assertNotNull(s.procesarSiguienteDespacho(), "El nuevo despacho debe procesarse correctamente");
    }

    @Test
    @DisplayName("procesarSiguienteDespacho setea destacadoHasta en el vehiculo")
    void testDestacadoHastaEnVehiculo() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        s.registrarVehiculo(new Vehiculo("V01", 0));
        Usuario u = new Usuario(80, 47);
        s.agregarUsuario(u);

        s.iniciarDespacho(u, null);
        Vehiculo v = s.procesarSiguienteDespacho();
        assertNotNull(v);
        assertTrue(v.getDestacadoHasta() > 0, "destacadoHasta debe ser > 0 tras pop");
        assertTrue(v.getDestacadoHasta() > System.nanoTime(), "Debe estar en el futuro");
    }

    @Test
    @DisplayName("iniciarDespacho registra la solicitud en estadisticas")
    void testIniciarDespachoRegistraSolicitud() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        s.registrarVehiculo(new Vehiculo("V01", 0));
        Usuario u = new Usuario(90, 47);
        s.agregarUsuario(u);

        long antes = s.getEstadisticas().getViajesSolicitados();
        s.iniciarDespacho(u, null);
        assertEquals(antes + 1, s.getEstadisticas().getViajesSolicitados(),
                "iniciarDespacho debe incrementar el contador de solicitudes");
    }

    @Test
    @DisplayName("tick() mantiene densidad: 5 usuarios, 10-15 vehiculos")
    void testTickMantieneDensidad() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        GestorSimulacion gestor = new GestorSimulacion(s, null, mapaSalta, dijkstra);
        gestor.inicializarEntidades();

        assertEquals(5, s.totalUsuarios(), "Debe haber 5 usuarios tras inicializar");
        assertTrue(s.totalVehiculos() >= 10 && s.totalVehiculos() <= 15,
                "Vehiculos debe estar entre 10 y 15 tras inicializar");

        gestor.tick();

        assertEquals(5, s.totalUsuarios(), "Debe mantener 5 usuarios tras tick");
        assertTrue(s.totalVehiculos() >= 10 && s.totalVehiculos() <= 15,
                "Debe mantener vehiculos entre 10 y 15 tras tick");
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

        assertEquals(5, s.totalUsuarios(), "Debe mantener 5 usuarios tras 10 ticks");
        assertTrue(s.totalVehiculos() >= 10 && s.totalVehiculos() <= 15,
                "Vehiculos debe estar entre 10 y 15 tras 10 ticks");
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
        GestorSimulacion gestor = new GestorSimulacion(sistema, null, mapaSalta, dijkstra);
        int nodo = 500;
        Vehiculo v = new Vehiculo("RTST", nodo);
        sistema.registrarVehiculo(v);
        assertEquals(0, v.getRutaActiva().length, "Ruta debe estar vacia inicialmente");

        gestor.tick();

        int[] ruta = v.getRutaActiva();
        assertTrue(ruta.length >= 2, "Ruta debe tener al menos 2 nodos despues de tick");
        assertEquals(nodo, ruta[0], "Primer nodo debe ser el nodo actual del vehiculo");
        assertTrue(mapaSalta.getMatrizCosto().areConnected(nodo, ruta[1]),
                "El segundo nodo debe ser un vecino adyacente alcanzable");
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
            assertTrue(mapaSalta.getMatrizCosto().areConnected(desde, hasta),
                    "Cada paso de roaming debe ser entre nodos adyacentes conectados");
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
        assertTrue(ruta.length >= 2, "Incluso sin vecinos debe obtener una ruta de 2 nodos");
        assertEquals(nodo, ruta[0], "Primer nodo debe ser el actual");
    }
}
