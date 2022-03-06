package pl.krypto.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;

public class Encryptor {
    private List<Byte> result = new ArrayList<>();
    private ByteArrayOperator bao = new ByteArrayOperator();
    private List<Byte> key;

    public Encryptor(List<Byte> key) {
        this.key = key;
    }

    public byte[] encrypt(byte[] data) {
        KeyExpander ke = new KeyExpander("0123456789ABCDEF0123456789ABCDEF".getBytes());
        List<Byte> key = ke.expand(1);
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        System.out.println("START: " + hf.formatHex(data));
        byte[] block = encryptInitRound(data, key);
        for (int i = 0; i < 13; i++) {
            block = encryptCenterRound(block, key, i + 1);
            for (int j = 0; j < 16; j++) {
                result.add(block[j]);
            }
        }
        byte[] cryptogram = encryptEndRound(block, key);
        System.out.println("===========================================");
        System.out.println("CRYPTOGRAM: " + hf.formatHex(cryptogram));
        System.out.println("===========================================");
        return cryptogram;
    }

    private byte[] encryptInitRound(byte[] data, List<Byte> key) {
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        byte[] result;
        result = bao.addRoundKey(data, key);
        System.out.println("AddRoundKey I=0 (INIT ROUND) " + hf.formatHex(result));
        return result;
    }

    private byte[] encryptCenterRound(byte[] data, List<Byte> key, int iteration) {
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        byte[] temp;
        temp = bao.changeByteBasedOnSbox16(data); //3.1 SubBytes
        System.out.println("SubBytes: I=" + iteration + " " + hf.formatHex(temp));
        temp = bao.shiftRows(temp);
        System.out.println("ShiftRows: I=" + iteration + " " + hf.formatHex(temp));
        temp = bao.mixColumns(temp);
        System.out.println("MixColumns: I=" + iteration + " " + hf.formatHex(temp));
        temp = bao.addRoundKey(temp, bao.get16bytesKeyFragment(iteration, key));
        System.out.println("AddRoundKey: I=" + iteration + " " + hf.formatHex(temp));
        return temp;
    }

    private byte[] encryptEndRound(byte[] data, List<Byte> key) {
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        byte[] temp;
        temp = bao.changeByteBasedOnSbox16(data); //3.1 SubBytes
        System.out.println("SubBytes: I=14 (END ROUND) " + hf.formatHex(temp));
        temp = bao.shiftRows(temp);
        System.out.println("ShiftRows: I=14 (END ROUND) " + hf.formatHex(temp));
        temp = bao.addRoundKey(temp, bao.get16bytesKeyFragment(14, key));
        System.out.println("AddRoundKey: I=14 (END ROUND) " + hf.formatHex(temp));
        return temp;
    }

    private void addToResult(byte[] add) {
        for (byte b : add) {
            result.add(b);
        }
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        System.out.println(hf.formatHex(add));
    }
}
