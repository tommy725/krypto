package pl.krypto.frontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.lang.reflect.InvocationTargetException;

public class MainFormController {

    public static final String MAIN_FORM_RESOURCE = "MainForm.fxml";
    public static final String MAIN_FORM_TITLE = "AES encryption/decryption";

    @FXML
    public TextField key;
    public TextArea plainText;
    public TextArea cryptogram;

    public void generateKey() {

    }

    public void readFromFile(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        FileChoose.openChooser("Choose a file to encrypt",actionEvent);
    }

    public void writeToFile(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        FileChoose.saveChooser("Choose a file to encrypt",actionEvent);
    }

    public void writeKeyToFile(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        FileChoose.saveChooser("Choose a file with key",actionEvent);
    }

    public void readKeyFromFile(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        FileChoose.openChooser("Choose a file to save a key",actionEvent);
    }

    public void encrypt() {
        cryptogram.setText(plainText.getText());
    }

    public void decrypt() {
        plainText.setText(cryptogram.getText());
    }
}