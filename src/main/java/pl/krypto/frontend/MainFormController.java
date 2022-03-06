package pl.krypto.frontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import pl.krypto.backend.Decryptor;
import pl.krypto.backend.Encryptor;
import pl.krypto.backend.KeyExpander;
import pl.krypto.backend.Validator;
import pl.krypto.backend.KeyGenerator;
import pl.krypto.backend.RandomGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

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
        key.setText(KeyGenerator.generateKey());
    }

    /**
     * Method reads plain byte array from given file
     * @param actionEvent eventFromJavaFX
     * @throws InvocationTargetException exception
     * @throws NoSuchMethodException exception
     * @throws IllegalAccessException exception
     * @throws IOException exception
     */
    public void readFromFile(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, IOException {
        String strPath = FileChoose.openChooser("Choose a file to encrypt",false,actionEvent);
        if (!strPath.equals("")) {
            Path p = Paths.get(strPath);
            plainData = Files.readAllBytes(p);
            plainText.setText(byteArrayToString(plainData));
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
    public void writeToFile(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, IOException {
        String strPath = FileChoose.saveChooser("Choose a file to encrypt",false,actionEvent);
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
    public void readFromFileEncrypted(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, IOException {
        String strPath = FileChoose.openChooser("Choose an encrypted file to decrypt",
                false,actionEvent);
        if (!strPath.equals("")) {
            Path p = Paths.get(strPath);
            cryptData = Files.readAllBytes(p);
            byte[] base64 = Base64.getEncoder().encode(cryptData);
            String s = new String(base64);
            cryptogram.setText(s);
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
    public void writeToFileEncrypted(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, IOException {
        String strPath = FileChoose.saveChooser("Choose a file to encrypt",false,actionEvent);
        if (!strPath.equals("")) {
            Path p = Paths.get(strPath);
            Files.write(p,cryptData);
        }
    }

    /**
     * Method reads key from given file
     * @param actionEvent eventFromJavaFX
     * @throws InvocationTargetException exception
     * @throws NoSuchMethodException exception
     * @throws IllegalAccessException exception
     * @throws IOException exception
     */
    public void readKeyFromFile(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, IOException {
        String strPath = FileChoose.openChooser("Choose a file to save a key",true,actionEvent);
        if (!strPath.equals("")) {
            Path p = Paths.get(strPath);
            key.setText(new String(Files.readAllBytes(p)));
        }
    }

    /**
     * Method saves key to given file
     * @param actionEvent eventFromJavaFX
     * @throws InvocationTargetException exception
     * @throws NoSuchMethodException exception
     * @throws IllegalAccessException exception
     * @throws IOException exception
     */
    public void writeKeyToFile(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, IOException {
        String strPath = FileChoose.saveChooser("Choose a file with key",true,actionEvent);
        if (!strPath.equals("")) {
            Path p = Paths.get(strPath);
            String passwd = key.getText();
            Files.write(p,passwd.getBytes());
        }
    }

    public void encrypt() {
        cryptogram.setText("");
        Validator v = new Validator();
        if(v.validatePassword(key.getText()) != null) {
            return;
        }
        KeyExpander ke = new KeyExpander(key.getText().getBytes());
        List<Byte> key = ke.expand(1);
        Encryptor e = new Encryptor(key);
        byte[] cryptBytes = null;
        if (plainData != null) {
            cryptBytes = e.encrypt(plainData);
        }
        byte[] cryptBytesText = e.encrypt(plainText.getText().getBytes());
        byte[] base64 = Base64.getEncoder().encode(cryptBytesText);
        String s = new String(base64);
        cryptData = cryptBytes;
        cryptogram.setText(s);
    }

    public void decrypt() {
        plainText.setText("");
        Validator v = new Validator();
        if(v.validatePassword(key.getText()) != null) {
            return;
        }
        KeyExpander ke = new KeyExpander(key.getText().getBytes());
        List<Byte> key = ke.expand(1);
        Decryptor d = new Decryptor(key);
        byte[] base64 = Base64.getDecoder().decode(cryptogram.getText().getBytes());
        byte[] decryptbase64 = d.decrypt(base64);
        byte[] cryptBytes = null;
        if (cryptData != null) {
            cryptBytes = d.decrypt(cryptData);
        }
        String s = new String(decryptbase64);
        plainData = cryptBytes;
        plainText.setText(s);
    }

    /**
     * Method converts byte array to string
     * @param data byteArray
     * @return String
     */
    private String byteArrayToString(byte[] data) {
        StringBuilder buffer = new StringBuilder();
        for (byte b : data) {
            buffer.append((char)b);
        }
        return buffer.toString();
    }
}