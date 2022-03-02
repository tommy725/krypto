package pl.krypto.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainFormController {

    public static final String MAIN_FORM_RESOURCE = "MainForm.fxml";
    public static final String MAIN_FORM_TITLE = "Hello cryptography!";

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}