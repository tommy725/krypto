package pl.krypto.backend;

import java.util.List;

public class Encryptor {
    private List<Byte> key;
    private ByteArrayOperator bao = new ByteArrayOperator();
    private final int BLOCK_SIZE = 16;
    private final int ROUNDS = 14;

    public Encryptor(List<Byte> key) {
        this.key = key;
    }

    /**
     * Method encrypts byte array and return List of crypted bytes
     * @param data Byte array to encrypt
     * @return list of encrypted bytes
     */
    public byte[] encrypt(byte[] data) {
        data = bao.addToLastFor16Bytes(data);
        byte[] result = new byte[data.length];
        for (int blockNumber = 0; blockNumber < data.length / BLOCK_SIZE; blockNumber++) {
            byte[] block = bao.getDataBlock(blockNumber, data);
            block = encryptInitRound(block, key);
            for (int i = 0; i < ROUNDS - 1; i++) {
                block = encryptCenterRound(block, key, i + 1);
            }
            byte[] cryptogram = encryptEndRound(block, key);
            for (int i = 0; i < BLOCK_SIZE; i++) {
                result[BLOCK_SIZE * blockNumber + i] = cryptogram[i];
            }
        }
        return result;
    }

    /**
     * Encrypt init round (AddRoundKey)
     * @param data data block
     * @param key key for encryption
     * @return return encrypted block
     */
    private byte[] encryptInitRound(byte[] data, List<Byte> key) {
        byte[] result;
        result = bao.addRoundKey(data, key); //AddRoundKey
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
        byte[] temp;
        temp = bao.changeByteBasedOnSbox16(data); //SubBytes
        temp = bao.shiftRows(temp); //ShiftRows
        temp = bao.mixColumns(temp); //MixColumns
        temp = bao.addRoundKey(temp, bao.getKeyBlock(iteration, key)); //AddRoundKey
        return temp;
    }

    /**
     * Encrypt end round (SubBytes -> ShiftRows -> AddRoundKey)
     * @param data data block
     * @param key key for encryption
     * @return return encrypted block
     */
    private byte[] encryptEndRound(byte[] data, List<Byte> key) {
        byte[] temp;
        temp = bao.changeByteBasedOnSbox16(data); //SubBytes
        temp = bao.shiftRows(temp); //ShiftRows
        temp = bao.addRoundKey(temp, bao.getKeyBlock(14, key)); //AddRoundKey
        return temp;
    }
}
