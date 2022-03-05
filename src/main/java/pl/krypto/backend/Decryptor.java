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
        block = bao.changeByteBasedOnInvSbox16(crypt);
        System.out.println("ISubBytes: " + hf.formatHex(block));
        block = bao.shiftRowsRight(block);
        System.out.println("IShiftRows: " + hf.formatHex(block));
        return null;
    }
}
