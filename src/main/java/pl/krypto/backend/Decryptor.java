package pl.krypto.backend;

import java.util.List;

public class Decryptor {
    private ByteArrayOperator bao = new ByteArrayOperator();
    private List<Byte> key;
    private final int ROUNDS = 14;
    private final int BLOCK_SIZE = 16;

    public Decryptor(List<Byte> key) {
        this.key = key;
    }

    /**
     * Method decrypts byte array and return List of decrypted bytes
     * @param crypt Byte array to decrypt
     * @return list of decrypted bytes
     */
    public byte[] decrypt(byte[] crypt) {
        byte[] result = new byte[crypt.length];
        for (int blockNumber = 0; blockNumber < crypt.length / BLOCK_SIZE; blockNumber++) {
            byte[] block = bao.getDataBlock(blockNumber, crypt);
            block = decryptInitRound(block, key);
            for (int i = ROUNDS - 1; i > 0; i--) {
                block = decryptCenterRound(block, key, i);
            }
            block = decryptEndRound(block, key);
            for (int i = 0; i < BLOCK_SIZE; i++) {
                result[BLOCK_SIZE * blockNumber + i] = block[i];
            }
        }
        result = bao.remove00fromEnd(result);
        return result;
    }

    /**
     * Decrypt init round (AddRoundKey -> InvShiftRows -> InvSubBytes)
     * @param data crypt block
     * @param key key for decryption
     * @return return decrypted block
     */
    private byte[] decryptInitRound(byte[] data, List<Byte> key) {
        byte[] temp;
        temp = bao.addRoundKey(data, bao.getKeyBlock(ROUNDS, key));
        temp = bao.invShiftRows(temp);
        temp = bao.changeByteBasedOnInvSbox16(temp);
        return temp;
    }

    /**
     * Decrypt center round (AddRoundKey -> InvMixColumns -> InvShiftRows -> InvSubBytes)
     * @param data data block
     * @param key key for decryption
     * @param iteration iteration number
     * @return return decrypted block
     */
    private byte[] decryptCenterRound(byte[] data, List<Byte> key, int iteration) {
        byte[] temp;
        temp = bao.addRoundKey(data, bao.getKeyBlock(iteration, key));
        temp = bao.invMixColumns(temp);
        temp = bao.invShiftRows(temp);
        temp = bao.changeByteBasedOnInvSbox16(temp);
        return temp;
    }

    /**
     * Encrypt end round (AddRoundKey)
     * @param data crypt block
     * @param key key for decryption
     * @return return decrypted block
     */
    private byte[] decryptEndRound(byte[] data, List<Byte> key) {
        byte[] result;
        result = bao.addRoundKey(data, bao.getKeyBlock(0, key));
        return result;
    }
}
