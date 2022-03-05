module pl.krypto {
    requires javafx.controls;
    requires javafx.fxml;

    opens pl.krypto.frontend to javafx.fxml;
    exports pl.krypto.frontend;
    exports pl.krypto.backend;
    opens pl.krypto.backend to javafx.fxml;
}