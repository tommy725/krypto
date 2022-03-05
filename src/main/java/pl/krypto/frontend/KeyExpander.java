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

    public void expand(int iteration) {
        //etap 1
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        //System.out.println(hf.formatHex(temp));
        byte[] temp = getLast4Bytes();
        byte pom = temp[0];
        temp[0] = temp[1];
        temp[1] = temp[2];
        temp[2] = temp[3];
        temp[3] = pom;
        temp = changeByteBasedOnSbox(temp);
        int num = 1;
        for (int i = 0; i < iteration; i++) {
            num *= 2;
        }
        temp[0] = (byte) (temp[0] ^ num);
        xorSum(temp);
        //etap 2
        for (int j = 0; j < 3; j++) {
            xorSum(getLast4Bytes());
        }
        //etap 3
        temp = getLast4Bytes();
        temp = changeByteBasedOnSbox(temp);
        xorSum(temp);
        //etap 4
        xorSum(getLast4Bytes());
        if (originalKey.size() != 176) {
            expand(++iteration);
            return;
        }
        System.out.println(originalKey);
    }

    private byte[] changeByteBasedOnSbox(byte[] temp) {
        for (int i = 0; i < 4; i++) {
            int intFromByte = temp[i];
            if (intFromByte < 0) intFromByte += 256;
            temp[i] = (byte) SBox.getBox(intFromByte / 16, intFromByte % 16);
        }
        return temp;
    }

    private byte[] getLast4Bytes() {
        byte[] temp = new byte[4];
        for (int i = 0; i < 4; i++) {
            temp[i] = originalKey.get(originalKey.size() - 4 + i);
        }
        return temp;
    }

    private void xorSum(byte[] temp) {
        byte[] temp2 = new byte[4];
        for (int i = 0; i < 4; i++) {
            temp2[i] = originalKey.get(originalKey.size() - 16 + i);
            temp[i] = (byte) (temp[i] ^ temp2[i]);
        }
        for (byte b : temp) {
            originalKey.add(b);
        }
    }
}
