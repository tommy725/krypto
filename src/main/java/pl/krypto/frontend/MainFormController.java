package pl.krypto.frontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import pl.krypto.backend.Decryptor;
import pl.krypto.backend.Encryptor;
import pl.krypto.backend.KeyExpander;
import pl.krypto.backend.KeyGenerator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

public class MainFormController {

    public static final String MAIN_FORM_RESOURCE = "MainForm.fxml";
    public static final String MAIN_FORM_TITLE = "AES encryption/decryption";

    @FXML
    public TextField key;
    public TextArea plainText;
    public TextArea cryptogram;
    public Button save1;
    public Button save2;
    public Button reset;
    public Button encrypt;
    public Button decrypt;

    private byte[] plainData;
    private byte[] cryptData;

    /**
     * Set generated key to key textField
     */
    public void generateKey() {
        key.setText(KeyGenerator.generateKey());
    }

    /**
     * Method reads plain byte array from given file
     *
     * @param actionEvent eventFromJavaFX
     * @throws InvocationTargetException exception
     * @throws NoSuchMethodException     exception
     * @throws IllegalAccessException    exception
     * @throws IOException               exception
     */
    public void readFromFile(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, IOException {
        String strPath = FileChoose.openChooser("Choose a file to encrypt", false, actionEvent);
        if (!strPath.equals("")) {
            Path p = Paths.get(strPath);
            plainData = Files.readAllBytes(p);
            clearTextFields();
            plainText.setText(byteArrayToString(plainData));
            changeDisableAfterRead();
        }
    }

    /**
     * Method saves plain byte array to file
     *
     * @param actionEvent eventFromJavaFX
     * @throws InvocationTargetException exception
     * @throws NoSuchMethodException     exception
     * @throws IllegalAccessException    exception
     * @throws IOException               exception
     */
    public void writeToFile(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, IOException {
        String strPath = FileChoose.saveChooser("Choose a file to encrypt", false, actionEvent);
        if (!strPath.equals("")) {
            Path p = Paths.get(strPath);
            Files.write(p, plainData);
        }
    }

    /**
     * Method reads encrypted byte array from given file
     *
     * @param actionEvent eventFromJavaFX
     * @throws InvocationTargetException exception
     * @throws NoSuchMethodException     exception
     * @throws IllegalAccessException    exception
     * @throws IOException               exception
     */
    public void readFromFileEncrypted(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, IOException {
        String strPath = FileChoose.openChooser("Choose an encrypted file to decrypt",
                false, actionEvent);
        if (!strPath.equals("")) {
            Path p = Paths.get(strPath);
            cryptData = Files.readAllBytes(p);
            clearTextFields();
            cryptogram.setText(byteArrayToString(cryptData));
            changeDisableAfterRead();
        }
    }

    /**
     * Method saves encrypted byte array to file
     *
     * @param actionEvent eventFromJavaFX
     * @throws InvocationTargetException exception
     * @throws NoSuchMethodException     exception
     * @throws IllegalAccessException    exception
     * @throws IOException               exception
     */
    public void writeToFileEncrypted(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, IOException {
        String strPath = FileChoose.saveChooser("Choose a file to encrypt", false, actionEvent);
        if (!strPath.equals("")) {
            Path p = Paths.get(strPath);
            Files.write(p, cryptData);
        }
    }

    /**
     * Method reads key from given file
     *
     * @param actionEvent eventFromJavaFX
     * @throws InvocationTargetException exception
     * @throws NoSuchMethodException     exception
     * @throws IllegalAccessException    exception
     * @throws IOException               exception
     */
    public void readKeyFromFile(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, IOException {
        String strPath = FileChoose.openChooser("Choose a file to save a key", true, actionEvent);
        if (!strPath.equals("")) {
            Path p = Paths.get(strPath);
            key.setText(new String(Files.readAllBytes(p)));
        }
    }

    /**
     * Method saves key to given file
     *
     * @param actionEvent eventFromJavaFX
     * @throws InvocationTargetException exception
     * @throws NoSuchMethodException     exception
     * @throws IllegalAccessException    exception
     * @throws IOException               exception
     */
    public void writeKeyToFile(ActionEvent actionEvent) throws InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, IOException {
        String strPath = FileChoose.saveChooser("Choose a file with key", true, actionEvent);
        if (!strPath.equals("")) {
            Path p = Paths.get(strPath);
            String passwd = key.getText();
            Files.write(p, passwd.getBytes());
        }
    }

    /**
     * Methods resets file and able to insert text
     */
    public void reset() {
        plainData = null;
        cryptData = null;
        clearTextFields();
        save1.setDisable(true);
        save2.setDisable(true);
        reset.setDisable(true);
        plainText.setDisable(false);
        cryptogram.setDisable(false);
        decrypt.setDisable(false);
        encrypt.setDisable(false);
    }


    /**
     * Method to start encryption from GUI and return result on textField and cryptData
     */
    public void encrypt() {
        decrypt.setDisable(false);
        cryptogram.setText("");
        List<Byte> key = expandKey();
        if (key == null) return;
        Encryptor e = new Encryptor(key);
        byte[] cryptBytes;
        if (plainData != null) {
            cryptBytes = e.encrypt(plainData);
            cryptData = cryptBytes;
            cryptogram.setText(byteArrayToString(cryptBytes));
        } else {
            cryptBytes = e.encrypt(plainText.getText().getBytes());
            pl.krypto.backend.Base64 b64 = new pl.krypto.backend.Base64();
            String s = b64.encode(cryptBytes);
            cryptogram.setText(s);
        }
    }

    /**
     * Method to start decryption from GUI and return result on textField and to plainData
     */
    public void decrypt() {
        encrypt.setDisable(false);
        plainText.setText("");
        List<Byte> key = expandKey();
        if (key == null) return;
        Decryptor d = new Decryptor(key);
        byte[] cryptBytes;
        if (cryptData != null) {
            cryptBytes = d.decrypt(cryptData);
            plainData = cryptBytes;
            plainText.setText(byteArrayToString(cryptBytes));
        } else {
            pl.krypto.backend.Base64 b64 = new pl.krypto.backend.Base64();
            byte[] base64 = b64.decode(cryptogram.getText());
            cryptBytes = d.decrypt(base64);
            String s = new String(cryptBytes, StandardCharsets.UTF_8);
            plainText.setText(s);
        }
    }

    /**
     * Expands key to desired length;
     * @return expanded key
     */
    private List<Byte> expandKey() {
        Validator v = new Validator();
        if (v.validatePassword(key.getText()) != null) {
            return null;
        }
        KeyExpander ke = new KeyExpander(key.getText().getBytes());
        return ke.expand(1);
    }

    /**
     * Method converts byte array to string
     *
     * @param data byteArray
     * @return String
     */
    private String byteArrayToString(byte[] data) {
        StringBuilder buffer = new StringBuilder();
        for (byte b : data) {
            buffer.append((char) b);
        }
        return buffer.toString();
    }

    /**
     * Methods clear textFields
     */
    private void clearTextFields() {
        plainText.setText("");
        cryptogram.setText("");
    }

    /**
     * Method disables components not connected with file
     */
    private void changeDisableAfterRead() {
        save1.setDisable(false);
        save2.setDisable(false);
        reset.setDisable(false);
        plainText.setDisable(true);
        cryptogram.setDisable(true);
        decrypt.setDisable(true);
    }
}