package pl.krypto.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MainFormController {

    public static final String MAIN_FORM_RESOURCE = "MainForm.fxml";
    public static final String MAIN_FORM_TITLE = "AES encryption/decryption";

    @FXML
    public TextField key;
    public TextArea plainText;
    public TextArea cryptogram;

    public void generateKey() {

    }

    public void readFromFile() {

    }

    public void writeToFile() {

    }

    public void writeKeyToFile() {

    }

    public void readKeyFromFile() {

    }

    public void encrypt() {
        cryptogram.setText(plainText.getText());
    }

    public void decrypt() {
        plainText.setText(cryptogram.getText());
    }
}