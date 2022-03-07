package pl.krypto.backend;

import java.util.HexFormat;
import java.util.List;

public class Encryptor {
    private List<Byte> key;
    private ByteArrayOperator bao = new ByteArrayOperator();

    public Encryptor(List<Byte> key) {
        this.key = key;
    }

    /**
     * Method encrypts byte array and return List of crypted bytes
     * @param data Byte array to encrypt
     * @return list of encrypted bytes
     */
    public byte[] encrypt(byte[] data) {
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        System.out.println("START: " + hf.formatHex(data));
        System.out.println("LONGER: " + hf.formatHex(data));
        data = bao.addToLastFor16Bytes(data);
        byte[] result = new byte[data.length];
        for (int blockNumber = 0; blockNumber < data.length / 16; blockNumber++) {
            byte[] block = bao.getDataBlock(blockNumber, data);
            System.out.println("BLOCK: " + hf.formatHex(block));
            block = encryptInitRound(block, key);
            for (int i = 0; i < 13; i++) {
                block = encryptCenterRound(block, key, i + 1);
            }
            byte[] cryptogram = encryptEndRound(block, key);
            System.out.println("===========================================");
            System.out.println("BLOCK CRYPTOGRAM: " + hf.formatHex(cryptogram));
            System.out.println("===========================================");
            for (int i = 0; i < 16; i++) {
                result[16 * blockNumber + i] = cryptogram[i];
            }
        }
        System.out.println("CRYPTOGRAM " + hf.formatHex(result));
        return result;
    }

    /**
     * Encrypt init round (AddRoundKey)
     * @param data data block
     * @param key key for encryption
     * @return return encrypted block
     */
    private byte[] encryptInitRound(byte[] data, List<Byte> key) {
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        byte[] result;
        result = bao.addRoundKey(data, key); //AddRoundKey
        System.out.println("AddRoundKey I=0 (INIT ROUND) " + hf.formatHex(result));
        return result;
    }

    /**
     * Encrypt center round (SubBytes -> ShiftRows -> MixColumns -> AddRoundKey)
     * @param data data block
     * @param key key for encryption
     * @param iteration iteration number
     * @return return encrypted block
     */
    private byte[] encryptCenterRound(byte[] data, List<Byte> key, int iteration) {
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        byte[] temp;
        temp = bao.changeByteBasedOnSbox16(data); //SubBytes
        System.out.println("SubBytes: I=" + iteration + " " + hf.formatHex(temp));
        temp = bao.shiftRows(temp); //ShiftRows
        System.out.println("ShiftRows: I=" + iteration + " " + hf.formatHex(temp));
        temp = bao.mixColumns(temp); //MixColumns
        System.out.println("MixColumns: I=" + iteration + " " + hf.formatHex(temp));
        temp = bao.addRoundKey(temp, bao.getKeyBlock(iteration, key)); //AddRoundKey
        System.out.println("AddRoundKey: I=" + iteration + " " + hf.formatHex(temp));
        return temp;
    }

    /**
     * Encrypt end round (SubBytes -> ShiftRows -> AddRoundKey)
     * @param data data block
     * @param key key for encryption
     * @return return encrypted block
     */
    private byte[] encryptEndRound(byte[] data, List<Byte> key) {
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        byte[] temp;
        temp = bao.changeByteBasedOnSbox16(data); //SubBytes
        System.out.println("SubBytes: I=14 (END ROUND) " + hf.formatHex(temp));
        temp = bao.shiftRows(temp); //ShiftRows
        System.out.println("ShiftRows: I=14 (END ROUND) " + hf.formatHex(temp));
        temp = bao.addRoundKey(temp, bao.getKeyBlock(14, key)); //AddRoundKey
        System.out.println("AddRoundKey: I=14 (END ROUND) " + hf.formatHex(temp));
        return temp;
    }
}
