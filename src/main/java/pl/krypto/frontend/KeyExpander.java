package pl.krypto.frontend;

import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

public class KeyExpander {
    private final List<Byte> originalKey = new ArrayList<>();

    public KeyExpander(byte[] originalKey) {
        for (byte b : originalKey) {
            this.originalKey.add(b);
        }
    }

    public List<Byte> expand(int iteration) {
        //etap 1 (adding 4 bytes)
        byte[] temp = get4Bytes(4); //1.1 move to temp vector
        temp = rotate1Left(temp); //1.2 rotate one left
        temp = changeByteBasedOnSbox(temp); //1.3 change 1.2 with sbox
        byte[] rcon = rconOperation(iteration); //1.4.1 rcon operation
        temp = xor(temp, rcon); //1.4.2 xor of 1.3 and 1.4.1
        addToKey(xor(temp, get4Bytes(32))); //1.5 adding xor of 4 bytes starting from -32 and 1.4.2
        //etap 2 (adding 12 bytes)
        for (int j = 0; j < 3; j++) {
            addToKey(xor(get4Bytes(4),get4Bytes(32))); //2.1 and 2.2 (xor of last 4 and 4 bytes starting from -32)
        }
        //etap 3 (adding 4 bytes)
        temp = get4Bytes(4); //3.1 get last 4 bytes of key
        temp = changeByteBasedOnSbox(temp); //3.2 change 3.1 with sbox
        addToKey(xor(temp,get4Bytes(32))); //3.3 xor of 3.2 and 4 bytes starting from -32 and adding to key
        //etap 4 (adding 12 bytes)
        for (int i = 0; i < 3; i++) {
            temp = get4Bytes(4); //4.1 get last 4 bytes of key
            addToKey(xor(temp,get4Bytes(32))); //4.2.1 xor of 4.1 and 4 bytes starting from -32 and adding to key
        }
        System.out.println("===========");
        if (originalKey.size() < 240) {
            return expand(++iteration);
        }
        return originalKey;
    }

    private byte[] rotate1Left(byte[] original) {
        byte[] rotate = new byte[4];
        rotate[0] = original[1];
        rotate[1] = original[2];
        rotate[2] = original[3];
        rotate[3] = original[0];
        return rotate;
    }

    private byte[] changeByteBasedOnSbox(byte[] temp) {
        for (int i = 0; i < 4; i++) {
            int intFromByte = temp[i];
            if (intFromByte < 0) intFromByte += 256;
            temp[i] = (byte) SBox.getBox(intFromByte / 16, intFromByte % 16);
        }
        return temp;
    }

    private byte[] rconOperation(int iteration) {
        byte[] rcon = new byte[4];
        int num = 1;
        for (int i = 0; i < iteration - 1; i++) {
            num *= 2;
        }
        rcon[0] = (byte) (0 ^ num);
        return rcon;
    }

    private byte[] xor(byte[] a1, byte[] a2) {
        byte[] xor = new byte[4];
        for (int i = 0; i < 4; i++) {
            xor[i] = (byte) (a1[i] ^ a2[i]);
        }
        return xor;
    }

    private byte[] get4Bytes(int start) {
        byte[] temp = new byte[4];
        for (int i = 0; i < 4; i++) {
            temp[i] = originalKey.get(originalKey.size() - start + i);
        }
        return temp;
    }

    private void addToKey(byte[] add) {
        for (byte b : add) {
            originalKey.add(b);
        }
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        System.out.println(hf.formatHex(add));
    }
}
