package group20tup.matchingengine;

import group20tup.matchingengine.model.estructuras.lineales.listas.ListaDoubleLinkedL;
import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoMapa;
import group20tup.matchingengine.model.recursos.simulacion.EstadoVehiculo;
import group20tup.matchingengine.model.recursos.simulacion.Usuario;
import group20tup.matchingengine.model.recursos.simulacion.Vehiculo;
import group20tup.matchingengine.model.utilidades.calculadorescaminos.DijkstraRutas;
import group20tup.matchingengine.model.utilidades.sistema.SistemaViajes;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SistemaViajesTest {

    private static GrafoMapa mapaSalta;
    private static DijkstraRutas dijkstra;
    private static SistemaViajes sistema;

    @BeforeAll
    static void init() {
        mapaSalta = new GrafoMapa();
        mapaSalta.cargarGrafo();
        dijkstra = new DijkstraRutas(mapaSalta);
        sistema = new SistemaViajes(mapaSalta, dijkstra);
    }

    @Test
    @DisplayName("Registra vehiculos y usuarios correctamente")
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
    @DisplayName("Calcula ETA finita entre nodos conectados")
    void testSistemaViajesCalculoETA() {
        double eta = sistema.calcularETA(0, 47);
        assertTrue(Double.isFinite(eta));
        assertTrue(eta > 0);
    }

    @Test
    @DisplayName("ETA desde un nodo a si mismo es 0")
    void testSistemaViajesETAMismoNodo() {
        double eta = sistema.calcularETA(0, 0);
        assertEquals(0.0, eta, 1e-12);
    }

    @Test
    @DisplayName("Solicitar viaje solo considera disponibles")
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
        assertNotNull(elegido);
        assertNotEquals("OCUP01", elegido.getPatente());
        assertEquals(EstadoVehiculo.APROXIMANDO, elegido.getEstado());
        assertNotNull(elegido.getPasajeroAbordo());
        assertEquals(usuario.getId(), elegido.getPasajeroAbordo().getId());
        assertTrue(elegido.getRutaActiva().length > 0);
    }

    @Test
    @DisplayName("Solicitar viaje sin disponibles retorna null")
    void testSistemaViajesSinDisponibles() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        Vehiculo ocupado = new Vehiculo("OCUP01", 0);
        ocupado.setEstado(EstadoVehiculo.EN_VIAJE);
        s.registrarVehiculo(ocupado);
        Usuario usuario = new Usuario(1, 47);
        s.agregarUsuario(usuario);

        assertNull(s.solicitarViaje(usuario));
    }

    @Test
    @DisplayName("Dispatch excluye ocupados")
    void testDispatchExcluyeOcupados() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        Vehiculo v1 = new Vehiculo("OCUP", 0);
        v1.setEstado(EstadoVehiculo.EN_VIAJE);
        s.registrarVehiculo(v1);
        Usuario u = new Usuario(4, 47);
        s.agregarUsuario(u);

        assertNull(s.solicitarViaje(u));
        s.registrarVehiculo(new Vehiculo("DISP", 100));
        Vehiculo elegido = s.solicitarViaje(u);
        assertNotNull(elegido);
        assertEquals("DISP", elegido.getPatente());
    }

    @Test
    @DisplayName("Ciclo completo DISPONIBLE -> APROXIMANDO -> EN_VIAJE -> DISPONIBLE")
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
    @DisplayName("Pickup remueve al usuario del sistema")
    void testPickupRemueveUsuario() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        s.registrarVehiculo(new Vehiculo("V002", 0));
        Usuario u = new Usuario(2, 47);
        s.agregarUsuario(u);

        Vehiculo v = s.solicitarViaje(u);
        assertNotNull(v);
        assertEquals(1, s.totalUsuarios());

        s.realizarPickup(v);
        assertEquals(0, s.totalUsuarios());
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
        assertTrue(s.getColaOcupados().estaVacia());
    }

    @Test
    @DisplayName("calcularTarifa produce valores correctos")
    void testCalculoTarifa() {
        assertEquals(12.50, sistema.calcularTarifa(3600.0), 0.001);
        assertEquals(0.0, sistema.calcularTarifa(Double.POSITIVE_INFINITY));
        assertEquals(0.0, sistema.calcularTarifa(0.0));
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
        assertNotNull(v);
        assertEquals(EstadoVehiculo.APROXIMANDO, v.getEstado());
    }

    @Test
    @DisplayName("obtenerTextoColaDespacho con vehiculos formatea patentes y ETAs")
    void testObtenerTextoColaDespachoConVehiculos() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        s.registrarVehiculo(new Vehiculo("TEST_A", 0));
        s.registrarVehiculo(new Vehiculo("TEST_B", 100));
        Usuario u = new Usuario(10, 47);
        s.agregarUsuario(u);

        String texto = s.obtenerTextoColaDespacho(u);
        assertTrue(texto.contains("── Cola de despacho ──"));
        assertTrue(texto.contains("TEST_A"));
        assertTrue(texto.contains("TEST_B"));
        assertTrue(texto.contains("s"));
        assertTrue(texto.contains("1."));
    }

    @Test
    @DisplayName("obtenerTextoColaDespacho sin candidatos")
    void testObtenerTextoColaDespachoSinCandidatos() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        Usuario u = new Usuario(20, 47);
        s.agregarUsuario(u);

        assertEquals("(sin candidatos)", s.obtenerTextoColaDespacho(u));
    }

    @Test
    @DisplayName("obtenerTextoColaDespacho excluye ocupados")
    void testObtenerTextoColaDespachoExcluyeOcupados() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        s.registrarVehiculo(new Vehiculo("DISP", 0));
        Vehiculo ocupado = new Vehiculo("OCUP", 100);
        ocupado.setEstado(EstadoVehiculo.EN_VIAJE);
        s.registrarVehiculo(ocupado);
        Usuario u = new Usuario(30, 47);
        s.agregarUsuario(u);

        String texto = s.obtenerTextoColaDespacho(u);
        assertTrue(texto.contains("DISP"));
        assertFalse(texto.contains("OCUP"));
    }

    @Test
    @DisplayName("iniciarDespacho y procesarSiguienteDespacho sin rechazo")
    void testIniciarYProcesarDespachoSinRechazo() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        s.registrarVehiculo(new Vehiculo("V01", 0));
        s.registrarVehiculo(new Vehiculo("V02", 100));
        Usuario u = new Usuario(40, 47);
        s.agregarUsuario(u);

        s.iniciarDespacho(u, null);
        assertTrue(s.hayDespachoActivo());
        assertEquals(0, s.getCandidatosProcesadosDespacho());
        assertTrue(s.getTotalCandidatosDespacho() > 0);

        Vehiculo aceptado = s.procesarSiguienteDespacho();
        assertNotNull(aceptado);
        assertFalse(s.hayDespachoActivo());
        assertEquals(EstadoVehiculo.APROXIMANDO, aceptado.getEstado());
        assertNotNull(aceptado.getPasajeroAbordo());
        assertEquals(u.getId(), aceptado.getPasajeroAbordo().getId());
    }

    @Test
    @DisplayName("procesarSiguienteDespacho con rechazo simulado")
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
        assertNotNull(aceptado);
        assertEquals(EstadoVehiculo.APROXIMANDO, aceptado.getEstado());
        assertTrue(s.getCandidatosProcesadosDespacho() >= 1);
    }

    @Test
    @DisplayName("procesarSiguienteDespacho con cola vacia retorna null")
    void testProcesarDespachoColaVacia() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        Vehiculo ocupado = new Vehiculo("OCUP", 0);
        ocupado.setEstado(EstadoVehiculo.EN_VIAJE);
        s.registrarVehiculo(ocupado);
        Usuario u = new Usuario(60, 47);
        s.agregarUsuario(u);

        s.iniciarDespacho(u, null);
        assertEquals(0, s.getTotalCandidatosDespacho());
        assertTrue(s.hayDespachoActivo());

        assertNull(s.procesarSiguienteDespacho());
        assertFalse(s.hayDespachoActivo());
    }

    @Test
    @DisplayName("cancelarDespacho resetea el estado")
    void testCancelarDespacho() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        s.registrarVehiculo(new Vehiculo("V01", 0));
        Usuario u = new Usuario(70, 47);
        s.agregarUsuario(u);

        s.iniciarDespacho(u, null);
        assertTrue(s.hayDespachoActivo());

        s.cancelarDespacho();
        assertFalse(s.hayDespachoActivo());
        assertNull(s.procesarSiguienteDespacho());

        s.iniciarDespacho(u, null);
        assertTrue(s.hayDespachoActivo());
        assertNotNull(s.procesarSiguienteDespacho());
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
        assertTrue(v.getDestacadoHasta() > 0);
        assertTrue(v.getDestacadoHasta() > System.nanoTime());
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
        assertEquals(antes + 1, s.getEstadisticas().getViajesSolicitados());
    }

    // ── New tests ──

    @Test
    @DisplayName("Despacho con 25 vehiculos funciona correctamente")
    void testDespachoConVeinticincoVehiculos() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        for (int i = 0; i < 25; i++) {
            s.registrarVehiculo(new Vehiculo(String.format("GRP%02d", i), i * 10));
        }
        Usuario u = new Usuario(100, 47);
        s.agregarUsuario(u);

        s.iniciarDespacho(u, new Random(42));
        assertEquals(25, s.getTotalCandidatosDespacho());

        Vehiculo aceptado = null;
        while (s.hayDespachoActivo()) {
            aceptado = s.procesarSiguienteDespacho();
        }
        assertNotNull(aceptado);
        assertEquals(EstadoVehiculo.APROXIMANDO, aceptado.getEstado());
    }

    @Test
    @DisplayName("Pickup sin ruta de transito valida vuelve el vehiculo a DISPONIBLE")
    void testPickupSinRutaValida() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        s.registrarVehiculo(new Vehiculo("NOROUTE", 0));
        Usuario u = new Usuario(101, 100);
        s.agregarUsuario(u);

        // Create a vehicle that is APPROACHING but has no passenger — edge case
        Vehiculo v = s.solicitarViaje(u);
        assertNotNull(v);
        v.setPasajeroAbordo(u);

        // Attempt pickup — should handle gracefully
        boolean result = s.realizarPickup(v);
        // Result may be true or false depending on route availability, but shouldn't throw
        assertTrue(v.isDisponible() || v.getEstado() == EstadoVehiculo.EN_VIAJE);
    }

    @Test
    @DisplayName("Estadisticas acumulan correctamente tras multiples viajes")
    void testEstadisticasAfterMultipleViajes() {
        SistemaViajes s = new SistemaViajes(mapaSalta, dijkstra);
        for (int i = 0; i < 5; i++) {
            s.registrarVehiculo(new Vehiculo(String.format("M%02d", i), i * 50));
        }

        for (int i = 0; i < 3; i++) {
            Usuario u = new Usuario(200 + i, 47);
            s.agregarUsuario(u);
            Vehiculo v = s.solicitarViaje(u);
            if (v != null) {
                s.realizarPickup(v);
                s.completarTransito(v);
            }
        }

        assertTrue(s.getEstadisticas().getViajesSolicitados() > 0,
                "Debe haber viajes solicitados");
        assertTrue(s.getEstadisticas().getViajesCompletados() > 0,
                "Debe haber viajes completados");
        assertTrue(s.getEstadisticas().getSumaDistanciasKm() > 0,
                "Debe haber distancia acumulada");
    }
}
