package pl.krypto.backend;

import java.util.HexFormat;
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
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        //System.out.println("START: " + hf.formatHex(crypt));
        byte[] result = new byte[crypt.length];
        for (int blockNumber = 0; blockNumber < crypt.length / BLOCK_SIZE; blockNumber++) {
            byte[] block = bao.getDataBlock(blockNumber, crypt);
            block = decryptInitRound(block, key);
            for (int i = ROUNDS - 1; i > 0; i--) {
                block = decryptCenterRound(block, key, i);
            }
            block = decryptEndRound(block, key);
            //System.out.println("===========================================");
            //System.out.println("PLAIN TEXT: " + hf.formatHex(block));
            //System.out.println("===========================================");
            for (int i = 0; i < BLOCK_SIZE; i++) {
                result[BLOCK_SIZE * blockNumber + i] = block[i];
            }
        }
        //System.out.println("DECODED PLAN TEXT LONGER: " + hf.formatHex(result));
        result = bao.remove00fromEnd(result);
        //System.out.println("DECODED PLAN TEXT 0 REMOVED: " + hf.formatHex(result));
        return result;
    }

    /**
     * Decrypt init round (AddRoundKey -> InvShiftRows -> InvSubBytes)
     * @param data crypt block
     * @param key key for decryption
     * @return return decrypted block
     */
    private byte[] decryptInitRound(byte[] data, List<Byte> key) {
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        byte[] temp;
        temp = bao.addRoundKey(data, bao.getKeyBlock(ROUNDS, key));
        //System.out.println("AddRoundKey: I=0 (INIT ROUND) " + hf.formatHex(temp));
        temp = bao.invShiftRows(temp);
        //System.out.println("InvShiftRows: I=0 (INIT ROUND) " + hf.formatHex(temp));
        temp = bao.changeByteBasedOnInvSbox16(temp);
        //System.out.println("InvSubBytes: I=0 (INIT ROUND) " + hf.formatHex(temp));
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
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        byte[] temp;
        temp = bao.addRoundKey(data, bao.getKeyBlock(iteration, key));
        //System.out.println("AddRoundKey: I=" + (ROUNDS - iteration) + " " + hf.formatHex(temp));
        temp = bao.invMixColumns(temp);
        //System.out.println("InvMixColumns: I=" + (ROUNDS - iteration) + " " + hf.formatHex(temp));
        temp = bao.invShiftRows(temp);
        //System.out.println("InvShiftRows: I=" + (ROUNDS - iteration) + " " + hf.formatHex(temp));
        temp = bao.changeByteBasedOnInvSbox16(temp);
        //System.out.println("InvSubBytes: I=" + (ROUNDS - iteration) + " " + hf.formatHex(temp));
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
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        //System.out.println("AddRoundKey I=14 (END ROUND) " + hf.formatHex(result));
        return result;
    }
}
