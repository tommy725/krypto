package pl.krypto.frontend;

import javafx.scene.control.Alert;

public class Validator {
    public Validator() {
    }

    /**
     * Validate password length
     * @param password key textField text
     * @return error alert
     */
    public Alert validatePassword(String password) {
        if (password.length() != 32) {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("ERROR");
            err.setHeaderText("Password must have 32 signs!");
            err.show();
            return err;
        };
        return null;
    }
}
