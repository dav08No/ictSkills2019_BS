module com.example.appdev_nocito_davide {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.appdev_nocito_davide to javafx.fxml;
    exports com.example.appdev_nocito_davide;
}
