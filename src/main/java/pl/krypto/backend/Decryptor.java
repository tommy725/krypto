package pl.krypto.backend;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;

public class Decryptor {
    private ByteArrayOperator bao = new ByteArrayOperator();

    public Decryptor() {
    }

    public byte[] decrypt(byte[] crypt) {
        KeyExpander ke = new KeyExpander("0123456789ABCDEF0123456789ABCDEF".getBytes());
        List<Byte> key = ke.expand(1);
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        System.out.println("KEY ENDED");
        byte[] block;
        System.out.println("START: " + hf.formatHex(crypt));
        block = decryptEndRound(crypt, key,14);
        System.out.println("INIT ROUND: " + hf.formatHex(block));
        for (int i = 13; i > 0; i--) {
            System.out.println(i);
            block = decryptCenterRound(block, key, i);
        }
        block = decryptInitRound(block, key);
        System.out.println(hf.formatHex(block));
        return block;
    }

    private byte[] decryptInitRound(byte[] data, List<Byte> key) {
        byte[] result;
        result = bao.addRoundKey(data, bao.get16bytesKeyFragment(0, key));
        return result;
    }

    private byte[] decryptCenterRound(byte[] data, List<Byte> key, int iteration) {
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        byte[] temp;
        temp = bao.addRoundKey(data, bao.get16bytesKeyFragment(iteration, key));
        System.out.println("AddRoundKey: I=" + iteration + " " + hf.formatHex(temp));
        temp = bao.invMixColumns(temp);
        System.out.println("InvMixColumns: I=" + iteration + "  " + hf.formatHex(temp));
        temp = bao.shiftRowsRight(temp);
        System.out.println("InvShiftRows: I=" + iteration + "  " + hf.formatHex(temp));
        temp = bao.changeByteBasedOnInvSbox16(temp);
        System.out.println("InvSubBytes: I=" + iteration + "  " + hf.formatHex(temp));
        return temp;
    }

    private byte[] decryptEndRound(byte[] data, List<Byte> key, int iteration) {
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        byte[] temp;
        temp = bao.addRoundKey(data, bao.get16bytesKeyFragment(iteration, key));
        System.out.println("AddRoundKey: I=14 (END ROUND)" + hf.formatHex(temp));
        temp = bao.shiftRowsRight(temp);
        System.out.println("InvShiftRows: I=14 (END ROUND)" + hf.formatHex(temp));
        temp = bao.changeByteBasedOnInvSbox16(temp);
        System.out.println("InvSubBytes: I=14 (END ROUND)" + hf.formatHex(temp));
        return temp;
    }
}
