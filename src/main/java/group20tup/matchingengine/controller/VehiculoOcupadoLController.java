package group20tup.matchingengine.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class VehiculoOcupadoLController {

    @FXML private Label patenteCatch;
    @FXML private Label estadoCatch;
    @FXML private Label posicionCatch;
    @FXML private Label ubicacionCatch;

    public void setDatos(String patente, String estado, int nodo, String ubicacion) {
        patenteCatch.setText(patente);
        estadoCatch.setText(estado);
        posicionCatch.setText("nodo " + nodo);
        ubicacionCatch.setText(ubicacion);
    }
}
