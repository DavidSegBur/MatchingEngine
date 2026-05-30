package group20tup.matchingengine.model.utilidades.sistema;

import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoMapa;
import group20tup.matchingengine.model.recursos.simulacion.EstadoVehiculo;
import group20tup.matchingengine.model.recursos.simulacion.Usuario;
import group20tup.matchingengine.model.recursos.simulacion.Vehiculo;
import group20tup.matchingengine.model.utilidades.CalculadorRutas;
import group20tup.matchingengine.view.MapCanvas;
import javafx.animation.AnimationTimer;
import java.util.Random;

/**
 * Motor de simulacion en tiempo real del sistema de flota de vehiculos.
 * <p>
 *     Gestiona el ciclo principal de la simulacion mediante un
 *     {@code AnimationTimer} que ejecuta pasos discretos a intervalos
 *     regulares. Mantiene una densidad constante de usuarios y vehiculos,
 *     controla el desplazamiento autonomo (roaming) de los vehiculos
 *     disponibles, avanza los vehiculos que siguen rutas activas,
 *     detecta eventos de recogida y finalizacion de viajes, y actualiza
 *     el renderizado del mapa en cada tick.
 * </p>
 * @author Ivan
 * @version 1.0
 */
public class GestorSimulacion {
    private static final long INTERVALO_TICK = 350_000_000;
    private static final double PASO_INTERPOLACION = 0.33;
    private static final int USUARIOS_OBJETIVO = 5;
    private static final int VEHICULOS_MIN = 10;
    private static final int VEHICULOS_MAX = 15;

    private final SistemaViajes sistema;
    private final MapCanvas renderizador;
    private final GrafoMapa grafo;
    private CalculadorRutas ruteador;
    private final Random rnd;
    private final AnimationTimer timer;
    private long ultimoTick;
    private int contadorUsuarios;
    private int contadorVehiculos;

    /**
     * Construye el gestor de simulacion con las dependencias necesarias.
     * @param sistema Sistema de viajes que gestiona el matching y las rutas
     * @param renderizador Renderizador del mapa para actualizar la vista
     * @param grafo Grafo vial de la ciudad para consultas de conectividad
     */
    public GestorSimulacion(SistemaViajes sistema, MapCanvas renderizador, GrafoMapa grafo, CalculadorRutas ruteador) {
        this.sistema = sistema;
        this.renderizador = renderizador;
        this.grafo = grafo;
        this.ruteador = ruteador;
        this.rnd = new Random();
        this.ultimoTick = 0;
        this.contadorUsuarios = 0;
        this.contadorVehiculos = 0;
        this.timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - ultimoTick >= INTERVALO_TICK) {
                    tick();
                    ultimoTick = now;
                }
            }
        };
    }

    /**
     * Cambia el algoritmo de ruteo usado por el gestor de simulacion.
     * @param ruteador Nueva instancia del algoritmo de ruteo
     */
    public void setRuteador(CalculadorRutas ruteador) {
        this.ruteador = ruteador;
    }

    /**
     * Inicia la simulacion creando las entidades iniciales y arrancando el timer.
     * <p>
     *     Crea 5 usuarios y 10 vehiculos ubicados aleatoriamente en el grafo,
     *     renderiza el mapa inicial y comienza el bucle de simulacion.
     * </p>
     */
    public void iniciar() {
        for (int i = 0; i < USUARIOS_OBJETIVO; i++) {
            crearUsuario();
        }
        for (int i = 0; i < VEHICULOS_MIN; i++) {
            crearVehiculo();
        }
        renderizarFrame();
        timer.start();
    }

    /**
     * Renderiza el frame completo del mapa incluyendo rutas activas, vehiculos y usuarios.
     * <p>
     *     Metodo publico utilizable desde el controlador para forzar un re-render
     *     completo (por ejemplo al redimensionar la ventana).
     * </p>
     */
    public void renderizarFrame() {
        renderizador.redibujar();

        for (int i = 0; i < sistema.totalVehiculos(); i++) {
            Vehiculo v = sistema.getVehiculo(i);
            if (v.getRutaActiva().length >= 2) {
                renderizador.renderRutaVehiculo(v);
            }
        }

        renderizador.renderVehiculos(sistema.getListaVehiculos());
        renderizador.renderUsuarios(sistema.getListaUsuarios());
    }

    /**
     * Ejecuta un paso de la simulacion.
     * <p>
     *     En cada tick: desplaza los vehiculos disponibles (roaming) y los que
     *     siguen rutas activas, procesa los eventos de llegada (pickup y
     *     finalizacion de viaje), mantiene la densidad objetivo de entidades
     *     y actualiza el renderizado del mapa.
     * </p>
     */
    private void tick() {
        for (int i = 0; i < sistema.totalVehiculos(); i++) {
            Vehiculo v = sistema.getVehiculo(i);
            int[] ruta = v.getRutaActiva();

            if (v.isDisponible() && (ruta.length == 0 || estaEnDestino(v))) {
                int destino = rnd.nextInt(grafo.getOrden());
                if (destino != v.getNodoActual()) {
                    int[] nuevaRuta = ruteador.calcularRuta(v.getNodoActual(), destino);
                    if (nuevaRuta.length >= 2) {
                        v.setRutaActiva(nuevaRuta);
                    }
                }
            }

            avanzarProgreso(v);
        }

        procesarArribos();
        mantenerDensidad();
        renderizarFrame();
    }

    private boolean estaEnDestino(Vehiculo v) {
        return v.getIndiceRuta() >= v.getRutaActiva().length - 1 && v.getProgreso() >= 1.0;
    }

    private void avanzarProgreso(Vehiculo v) {
        int[] ruta = v.getRutaActiva();
        if (ruta.length < 2) return;

        double p = v.getProgreso() + PASO_INTERPOLACION;
        if (p >= 1.0) {
            int idx = v.getIndiceRuta() + 1;
            if (idx < ruta.length - 1) {
                v.setNodoAnterior(ruta[idx]);
                v.setNodoActual(ruta[idx + 1]);
                v.setIndiceRuta(idx);
                v.setProgreso(0.0);
            } else {
                v.setIndiceRuta(ruta.length - 1);
                v.setNodoAnterior(ruta[ruta.length - 1]);
                v.setNodoActual(ruta[ruta.length - 1]);
                v.setProgreso(1.0);
            }
        } else {
            v.setProgreso(p);
        }
    }

    /**
     * Procesa los eventos de llegada de vehiculos a sus destinos.
     * <p>
     *     Si un vehiculo en estado APROXIMANDO llego al nodo del usuario,
     *     ejecuta la recogida. Si un vehiculo en estado EN_VIAJE llego a
     *     su destino aleatorio, finaliza el viaje y lo vuelve a DISPONIBLE.
     * </p>
     */
    private void procesarArribos() {
        for (int i = 0; i < sistema.totalVehiculos(); i++) {
            Vehiculo v = sistema.getVehiculo(i);
            int[] ruta = v.getRutaActiva();
            if (ruta.length == 0) continue;

            if (v.getIndiceRuta() >= ruta.length - 1 && v.getProgreso() >= 1.0) {
                if (v.getEstado() == EstadoVehiculo.APROXIMANDO) {
                    if (!sistema.realizarPickup(v)) {
                        sistema.removerVehiculo(v);
                        System.out.println("[Pickup] Vehiculo " + v.getPatente()
                                + " no encontro destino alcanzable. Reemplazado.");
                    } else {
                        System.out.println("[Pickup] Vehiculo " + v.getPatente()
                                + " recolecto usuario. Dirigiendose a destino aleatorio.");
                    }
                } else if (v.getEstado() == EstadoVehiculo.EN_VIAJE) {
                    sistema.completarTransito(v);
                    System.out.println("[Completado] Vehiculo " + v.getPatente()
                            + " finalizo viaje. Vuelve a DISPONIBLE.");
                }
            }
        }
    }

    /**
     * Mantiene la densidad objetivo de usuarios y vehiculos en la simulacion.
     * <p>
     *     Si hay menos de 5 usuarios, crea nuevos. Si hay menos de 10 vehiculos,
     *     crea nuevos. No supera el maximo de 15 vehiculos.
     * </p>
     */
    private void mantenerDensidad() {
        while (sistema.totalUsuarios() < USUARIOS_OBJETIVO) {
            crearUsuario();
        }
        while (sistema.totalVehiculos() < VEHICULOS_MIN && sistema.totalVehiculos() < VEHICULOS_MAX) {
            crearVehiculo();
        }
    }

    /**
     * Crea un nuevo usuario en una ubicacion aleatoria del grafo.
     */
    private void crearUsuario() {
        int nodo = rnd.nextInt(grafo.getOrden());
        Usuario u = new Usuario(contadorUsuarios++, nodo);
        sistema.agregarUsuario(u);
    }

    /**
     * Crea un nuevo vehiculo en una ubicacion aleatoria del grafo.
     * <p>
     *     La patente se genera automaticamente con formato V###.
     * </p>
     */
    private void crearVehiculo() {
        int nodo = rnd.nextInt(grafo.getOrden());
        String patente = String.format("V%03d", contadorVehiculos++);
        Vehiculo v = new Vehiculo(patente, nodo);
        sistema.registrarVehiculo(v);
    }
}

