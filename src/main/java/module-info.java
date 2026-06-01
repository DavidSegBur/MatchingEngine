module group20tup.matchingengine {
    requires javafx.controls;
    requires javafx.fxml;

    opens group20tup.matchingengine.controller to javafx.fxml;
    opens group20tup.matchingengine.css to javafx.graphics;
    exports group20tup.matchingengine;
}