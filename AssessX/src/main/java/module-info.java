module com.assessx.assessx {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;

    opens com.assessx.assessx to javafx.fxml;
    opens com.assessx.assessx.controller to javafx.fxml;

    exports com.assessx.assessx;
}
