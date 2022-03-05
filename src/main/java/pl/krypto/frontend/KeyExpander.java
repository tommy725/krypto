package pl.krypto.frontend;

import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

public class KeyExpander {
    private List<Byte> originalKey = new ArrayList<>();

    public KeyExpander(byte[] originalKey) {
        for (byte b : originalKey) {
            this.originalKey.add(b);
        }
    }

    public void expand(int iteration) {
        //etap 1
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        //System.out.println(hf.formatHex(temp));
        byte[] temp = new byte[4];
        for (int i = 0; i < 4; i++) {
            temp[i] = originalKey.get(originalKey.size() - 4 + i);
        }
        byte pom = temp[0];
        temp[0] = temp[1];
        temp[1] = temp[2];
        temp[2] = temp[3];
        temp[3] = pom;
        for (int i = 0; i < 4; i++) {
            int intFromByte = temp[i];
            if (intFromByte<0) intFromByte += 256;
            temp[i] = (byte) SBox.getBox((intFromByte)/16, (intFromByte)%16);
        }
        int num = 1;
        for (int i = 0; i < iteration; i++) {
            num *= 2;
        }
        temp[0] = (byte) (temp[0] ^ num);
        byte[] temp2 = new byte[4];
        for (int i = 0; i < 4; i++) {
            temp2[i] = originalKey.get(originalKey.size() - 16 + i);
            temp[i] = (byte) (temp[i] ^ temp2[i]);
        }
        for (byte b : temp) {
            originalKey.add(b);
        }
        //etap 2
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 4; i++) {
                temp[i] = originalKey.get(originalKey.size() - 4 + i);
                temp2[i] = originalKey.get(originalKey.size() - 16 + i);
                temp[i] = (byte) (temp[i] ^ temp2[i]);
            }
            for (byte b : temp) {
                originalKey.add(b);
            }
        }
        //etap 3
        for (int i = 0; i < 4; i++) {
            temp[i] = originalKey.get(originalKey.size() - 4 + i);
            int intFromByte = temp[i];
            if (intFromByte<0) intFromByte += 256;
            temp[i] = (byte) SBox.getBox((intFromByte)/16, (intFromByte)%16);
            temp2[i] = originalKey.get(originalKey.size() - 16 + i);
            temp[i] = (byte) (temp[i] ^ temp2[i]);
        }
        for (byte b : temp) {
            originalKey.add(b);
        }
        //etap 4
        for (int i = 0; i < 4; i++) {
            temp[i] = originalKey.get(originalKey.size() - 4 + i);
            temp2[i] = originalKey.get(originalKey.size() - 16 + i);
            temp[i] = (byte) (temp[i] ^ temp2[i]);
        }
        for (byte b : temp) {
            originalKey.add(b);
        }
        if (originalKey.size() != 176) {
            expand(iteration + 1);
            return;
        }
        System.out.println(originalKey);
    }
}
