package pl.krypto.frontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class MainFormController {

    public static final String MAIN_FORM_RESOURCE = "MainForm.fxml";
    public static final String MAIN_FORM_TITLE = "AES encryption/decryption";

    @FXML
    public TextField key;
    public TextArea plainText;
    public TextArea cryptogram;

    private byte[] plainData;
    private byte[] cryptData;

    public void generateKey() {

    }

    /**
     * Method reads plain byte array from given file
     * @param actionEvent eventFromJavaFX
     * @throws InvocationTargetException exception
     * @throws NoSuchMethodException exception
     * @throws IllegalAccessException exception
     * @throws IOException exception
     */
    public void readFromFile(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException {
        String strPath = FileChoose.openChooser("Choose a file to encrypt",actionEvent);
        if (!strPath.equals("")) {
            plainText.setText(fileToString(Paths.get(strPath),true));
        }
    }

    /**
     * Method saves plain byte array to file
     * @param actionEvent eventFromJavaFX
     * @throws InvocationTargetException exception
     * @throws NoSuchMethodException exception
     * @throws IllegalAccessException exception
     * @throws IOException exception
     */
    public void writeToFile(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException {
        String strPath = FileChoose.saveChooser("Choose a file to encrypt",actionEvent);
        if (!strPath.equals("")) {
            Path p = Paths.get(strPath);
            Files.write(p,plainData);
        }
    }

    /**
     * Method reads encrypted byte array from given file
     * @param actionEvent eventFromJavaFX
     * @throws InvocationTargetException exception
     * @throws NoSuchMethodException exception
     * @throws IllegalAccessException exception
     * @throws IOException exception
     */
    public void readFromFileEncrypted(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException {
        String strPath = FileChoose.openChooser("Choose an encrypted file to decrypt",actionEvent);
        if (!strPath.equals("")) {
            cryptogram.setText(fileToString(Paths.get(strPath),false));
        }
    }

    /**
     * Method saves encrypted byte array to file
     * @param actionEvent eventFromJavaFX
     * @throws InvocationTargetException exception
     * @throws NoSuchMethodException exception
     * @throws IllegalAccessException exception
     * @throws IOException exception
     */
    public void writeToFileEncrypted(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException {
        String strPath = FileChoose.saveChooser("Choose a file to encrypt",actionEvent);
        if (!strPath.equals("")) {
            Path p = Paths.get(strPath);
            Files.write(p,cryptData);
        }
    }

    public void writeKeyToFile(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String strPath = FileChoose.saveChooser("Choose a file with key",actionEvent);
    }

    public void readKeyFromFile(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String strPath = FileChoose.openChooser("Choose a file to save a key",actionEvent);
    }

    public void encrypt() {
        cryptogram.setText(plainText.getText());
    }

    public void decrypt() {
        plainText.setText(cryptogram.getText());
    }

    /**
     * Method converts file to string
     * @param p path
     * @param isPlain from which textbox
     * @return String
     * @throws IOException exception
     */
    private String fileToString(Path p,Boolean isPlain) throws IOException {
        byte[] data = saveByteArray(p,isPlain);
        StringBuilder buffer = new StringBuilder();
        for (byte b : data) {
            buffer.append((char)b);
        }
        return buffer.toString();
    }

    /**
     * Methods converts file to byte array and saves it in class
     * @param p path
     * @param isPlain from which textbox
     * @return byte[]
     * @throws IOException exception
     */
    private byte[] saveByteArray(Path p,Boolean isPlain) throws IOException {
        if (isPlain) {
            plainData = Files.readAllBytes(p);
            return plainData;
        }
        cryptData = Files.readAllBytes(p);
        return cryptData;
    }
}