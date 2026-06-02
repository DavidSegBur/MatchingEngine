package group20tup.matchingengine.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class VehiculoDisponibleController {

    @FXML private Label vehiculoPatenteCatch;
    @FXML private Label vehiculoPosicionCatch;
    @FXML private Label nombreCalleCath;

    /**
     * Rellena los datos de la ventana con la información del vehículo disponible.
     * @param patente  Patente del vehículo     (atributo String de Vehiculo)
     * @param posicion Nodo actual del vehículo (int de Vehiculo)
     * @param calle    Nombre de la esquina     (atributo String de MetadataNodo)
     */
    public void setDatos(String patente, int posicion, String calle) {
        vehiculoPatenteCatch.setText(patente);
        vehiculoPosicionCatch.setText(String.valueOf(posicion));
        nombreCalleCath.setText(calle);
    }
}