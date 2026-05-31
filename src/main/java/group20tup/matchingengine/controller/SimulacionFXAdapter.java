package group20tup.matchingengine.controller;

import group20tup.matchingengine.model.utilidades.sistema.MotorSimulacion;
import javafx.animation.AnimationTimer;

/**
 * Adaptador que conecta un {@link MotorSimulacion} con el bucle de animacion JavaFX.
 * <p>
 *     Envuelve un {@code AnimationTimer} que invoca {@code tick()} y
 *     {@code renderizarFrame()} del motor a intervalos regulares de 350ms.
 *     Permite que la logica de simulacion permanezca pura (sin dependencias
 *     de JavaFX) mientras que el bucle de frames se mantiene en la capa
 *     de presentacion.
 * </p>
 * @author Ivan
 * @version 1.0
 */
public class SimulacionFXAdapter {
    private static final long INTERVALO_TICK = 350_000_000L;
    private final MotorSimulacion motor;
    private final AnimationTimer timer;
    private long ultimoTick;

    /**
     * Construye el adaptador para el motor de simulacion dado.
     * @param motor Motor de simulacion que ejecutara la logica de cada tick
     */
    public SimulacionFXAdapter(MotorSimulacion motor) {
        this.motor = motor;
        this.timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - ultimoTick >= INTERVALO_TICK) {
                    motor.tick();
                    motor.renderizarFrame();
                    ultimoTick = now;
                }
            }
        };
    }

    /**
     * Inicia el bucle de animacion.
     * <p>
     *     El primer tick se ejecuta inmediatamente en el siguiente frame
     *     de JavaFX. Llama a este metodo despues de que las entidades
     *     iniciales hayan sido creadas.
     * </p>
     */
    public void iniciar() {
        ultimoTick = 0;
        timer.start();
    }

    /**
     * Detiene el bucle de animacion.
     * <p>
     *     Los ticks de simulacion dejan de ejecutarse. El motor conserva
     *     su estado interno y puede reanudarse llamando a {@code iniciar()}
     *     nuevamente.
     * </p>
     */
    public void detener() {
        timer.stop();
    }
}
