package pl.krypto.backend;

import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

public class ByteArrayOperator {
    static byte[] mds = {0x02, 0x03, 0x01, 0x01,
            0x01, 0x02, 0x03, 0x01,
            0x01, 0x01, 0x02, 0x03,
            0x03, 0x01, 0x01, 0x02};

    public ByteArrayOperator() {
    }

    public byte[] rotate1Left(byte[] original) {
        byte[] rotate = new byte[4];
        rotate[0] = original[1];
        rotate[1] = original[2];
        rotate[2] = original[3];
        rotate[3] = original[0];
        return rotate;
    }

    public byte[] changeByteBasedOnSbox(byte[] temp) {
        for (int i = 0; i < 4; i++) {
            int intFromByte = temp[i];
            if (intFromByte < 0) intFromByte += 256;
            temp[i] = (byte) SBox.getBox(intFromByte / 16, intFromByte % 16);
        }
        return temp;
    }

    public byte[] rconOperation(int iteration) {
        byte[] rcon = new byte[4];
        int num = 1;
        for (int i = 0; i < iteration - 1; i++) {
            num *= 2;
        }
        rcon[0] = (byte) (0 ^ num);
        return rcon;
    }

    public byte[] xor(byte[] a1, byte[] a2) {
        for (int i = 0; i < 4; i++) {
            a1[i] = (byte) (a1[i] ^ a2[i]);
        }
        return a1;
    }

    public byte xorMix(byte[] b1) {
        byte result = (byte) (b1[0] ^ b1[1] ^ b1[2] ^ b1[3]);
        return result;
    }

    public byte[] get4Bytes(int start, List<Byte> list) {
        byte[] temp = new byte[4];
        for (int i = 0; i < 4; i++) {
            temp[i] = list.get(list.size() - start + i);
        }
        return temp;
    }

    public byte[] get4BytesArray(int start, byte[] array) {
        byte[] temp = new byte[4];
        for (int i = 0; i < 4; i++) {
            temp[i] = array[array.length - start + i];
        }
        return temp;
    }

    public byte[] changeByteBasedOnSbox16(byte[] temp) {
        for (int i = 0; i < 16; i++) {
            int intFromByte = temp[i];
            if (intFromByte < 0) intFromByte += 256;
            temp[i] = (byte) SBox.getBox(intFromByte / 16, intFromByte % 16);
        }
        return temp;
    }

    public byte[] shiftRows(byte[] array) {
        byte[] result = new byte[16];
        //row 1
        result[0] = array[0];
        result[4] = array[4];
        result[8] = array[8];
        result[12] = array[12];
        //row 2
        result[1] = array[5];
        result[5] = array[9];
        result[9] = array[13];
        result[13] = array[1];
        //row 3
        result[2] = array[10];
        result[6] = array[14];
        result[10] = array[2];
        result[14] = array[6];
        //row 4
        result[3] = array[15];
        result[7] = array[3];
        result[11] = array[7];
        result[15] = array[11];
        return result;
    }

    public byte[] mixColumns(byte[] array) {
        HexFormat hf = HexFormat.of().withDelimiter(" ");
        byte[] result = new byte[16];
        for (int i = 0; i < 16; i++) {
            int row = i % 4;
            int col = i / 4;
            byte[] temp = new byte[4];
            for (int j = 0; j < 4; j++) {
                int dec = array[col * 4 + j];
                int mul = mds[row * 4 + j];
                if (mul == 3) {
                    mul = 2;
                }
                if (dec < 0) {
                    dec += 256;
                }
                int value = dec * mul;
                if (mds[row * 4 + j] == 2 && value > 255) {
                    value = value ^ 0x1B;
                }
                if (mds[row * 4 + j] == 3) {
                    if (value > 255) {
                        value = value ^ dec ^ 0x1B;
                    } else {
                        value = value ^ dec;
                    }
                }
                temp[j] = (byte) value;
            }
            result[i] = xorMix(temp);
        }
        return result;
    }

    public byte[] addRoundKey(byte[] array,List<Byte> key) {
        byte[] result = new byte[16];
        for (int i = 0; i < 16; i++) {
            result[i] = (byte) (array[i] ^ key.get(i));
        }
        return result;
    }

    public List<Byte> get16bytesKeyFragment(int iteration, List<Byte> key) {
        List<Byte> result = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            result.add(key.get(iteration*16+i));
        }
        return result;
    }
}
