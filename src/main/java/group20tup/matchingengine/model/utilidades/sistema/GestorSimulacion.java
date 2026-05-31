package group20tup.matchingengine.model.utilidades.sistema;

import group20tup.matchingengine.model.estructuras.lineales.matrices.MatrizGrafo;
import group20tup.matchingengine.model.estructuras.nolineales.grafos.GrafoMapa;
import group20tup.matchingengine.model.recursos.simulacion.EstadoVehiculo;
import group20tup.matchingengine.model.recursos.simulacion.Usuario;
import group20tup.matchingengine.model.recursos.simulacion.Vehiculo;
import group20tup.matchingengine.model.utilidades.CalculadorRutas;
import group20tup.matchingengine.view.MapCanvas;
import java.util.Random;

/**
 * Motor de simulacion en tiempo real del sistema de flota de vehiculos.
 * <p>
 *     Gestiona el ciclo principal de la simulacion. Mantiene una densidad
 *     constante de usuarios y vehiculos, controla el desplazamiento autonomo
 *     (roaming) de los vehiculos disponibles, avanza los vehiculos que siguen
 *     rutas activas, detecta eventos de recogida y finalizacion de viajes, y
 *     actualiza el renderizado del mapa en cada tick. No depende de JavaFX
 *     directamente; el bucle de animacion es gestionado externamente por
 *     un adaptador.
 * </p>
 * @author Ivan
 * @version 2.0
 */
public class GestorSimulacion implements MotorSimulacion {
    private static final double PASO_INTERPOLACION = 0.33;
    private static final int USUARIOS_OBJETIVO = 5;
    private static final int VEHICULOS_MIN = 10;
    private static final int VEHICULOS_MAX = 15;

    private final SistemaViajes sistema;
    private final MapCanvas renderizador;
    private final GrafoMapa grafo;
    private CalculadorRutas ruteador;
    private final Random rnd;
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
        this.contadorUsuarios = 0;
        this.contadorVehiculos = 0;
    }

    /**
     * Cambia el algoritmo de ruteo usado por el gestor de simulacion.
     * @param ruteador Nueva instancia del algoritmo de ruteo
     */
    public void setRuteador(CalculadorRutas ruteador) {
        this.ruteador = ruteador;
    }

    /**
     * Crea las entidades iniciales de la simulacion y renderiza el frame inicial.
     * <p>
     *     Crea 5 usuarios y 10 vehiculos ubicados aleatoriamente en el grafo
     *     y renderiza el mapa inicial. No inicia el bucle de animacion;
     *     eso debe hacerlo el adaptador externo.
     * </p>
     */
    public void inicializarEntidades() {
        for (int i = 0; i < USUARIOS_OBJETIVO; i++) {
            crearUsuario();
        }
        for (int i = 0; i < VEHICULOS_MIN; i++) {
            crearVehiculo();
        }
        renderizarFrame();
    }

    /**
     * Renderiza el frame completo del mapa incluyendo rutas activas, vehiculos y usuarios.
     * <p>
     *     Metodo publico utilizable desde el controlador para forzar un re-render
     *     completo (por ejemplo al redimensionar la ventana).
     * </p>
     */
    @Override
    public void renderizarFrame() {
        if (renderizador == null) return;
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
     *     finalizacion de viaje) y mantiene la densidad objetivo de entidades.
     * </p>
     */
    @Override
    public void tick() {
        for (int i = 0; i < sistema.totalVehiculos(); i++) {
            Vehiculo v = sistema.getVehiculo(i);
            int[] ruta = v.getRutaActiva();

            if (v.isDisponible() && (ruta.length == 0 || estaEnDestino(v))) {
                int vecino = obtenerVecinoAleatorio(v.getNodoActual());
                if (vecino != -1) {
                    v.setRutaActiva(new int[]{v.getNodoActual(), vecino});
                } else {
                    int nodo;
                    int intentos = 0;
                    do {
                        nodo = rnd.nextInt(grafo.getOrden());
                        intentos++;
                    } while (nodo == v.getNodoActual() && intentos < 10);
                    if (nodo != v.getNodoActual()) {
                        v.setRutaActiva(new int[]{v.getNodoActual(), nodo});
                    }
                }
            }

            avanzarProgreso(v);

            if (v.getEstado() != EstadoVehiculo.DISPONIBLE) {
                double etaRestante = 0;
                int[] rutaV = v.getRutaActiva();
                for (int j = v.getIndiceRuta(); j < rutaV.length - 1; j++) {
                    etaRestante += grafo.getMatrizCosto().devolver(rutaV[j], rutaV[j + 1]);
                }
                sistema.actualizarPrioridadOcupado(i, etaRestante);
            }
        }

        procesarArribos();
        mantenerDensidad();
    }

    /**
     * Obtiene un vecino aleatorio alcanzable desde el nodo dado,
     * respetando las restricciones de sentido unico del grafo dirigido.
     * @param nodo Nodo de origen
     * @return Indice de un nodo vecino valido, o -1 si no existe ninguna arista saliente
     */
    private int obtenerVecinoAleatorio(int nodo) {
        int orden = grafo.getOrden();
        MatrizGrafo matriz = grafo.getMatrizCosto();
        int count = 0;
        for (int j = 0; j < orden; j++) {
            if (j != nodo && matriz.areConnected(nodo, j)) {
                count++;
            }
        }
        if (count == 0) return -1;
        int target = rnd.nextInt(count);
        for (int j = 0; j < orden; j++) {
            if (j != nodo && matriz.areConnected(nodo, j)) {
                if (target == 0) return j;
                target--;
            }
        }
        return -1;
    }

    /**
     * Verifica si el vehiculo ha llegado al nodo destino de su ruta actual.
     * @param v Vehiculo a verificar
     * @return true si el vehiculo esta en el ultimo nodo de su ruta
     */
    private boolean estaEnDestino(Vehiculo v) {
        return v.getIndiceRuta() >= v.getRutaActiva().length - 1 && v.getProgreso() >= 1.0;
    }

    /**
     * Avanza una posicion del vehiculo en su ruta hacia el nodo siguiente.
     * Si la ruta esta vacia o ya llego al destino, se detiene sin avanzar.
     * @param v Vehiculo cuyo progreso se avanza
     */
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
        for (int i = sistema.totalVehiculos() - 1; i >= 0; i--) {
            Vehiculo v = sistema.getVehiculo(i);
            int[] ruta = v.getRutaActiva();
            if (ruta.length == 0) continue;

            if (v.getIndiceRuta() >= ruta.length - 1 && v.getProgreso() >= 1.0) {
                if (v.getEstado() == EstadoVehiculo.APROXIMANDO) {
                    if (!sistema.realizarPickup(v)) {
                        sistema.removerVehiculo(v);
                        sistema.reconstruirColaOcupados();
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

