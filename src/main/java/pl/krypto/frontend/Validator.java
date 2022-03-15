package pl.krypto.frontend;

import javafx.scene.control.Alert;

public class Validator {

    /**
     * Validate password length
     * @param key key textField text
     * @return error alert
     */
    public Alert validateKey(String key) {
        if (key.length() != 32) {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("ERROR");
            err.setHeaderText("Password must have 32 signs!");
            err.show();
            return err;
        };
        return null;
    }
}
