module group20tup.matchingengine {
    requires javafx.controls;
    requires javafx.fxml;

    opens group20tup.matchingengine.controller to javafx.fxml;
    exports group20tup.matchingengine;
}