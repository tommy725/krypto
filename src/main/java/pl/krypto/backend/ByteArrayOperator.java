package pl.krypto.backend;

import java.util.ArrayList;
import java.util.List;

public class ByteArrayOperator {
    private final int NUM_OF_BYTES = 256;
    private final int ROW_SIZE = 4;
    private final int BLOCK_SIZE = 16;
    private final byte REDUCTION = 0x1B; //The reduction polynomial for Rijndael x^8+x^4+x^3+x+1
    private final static byte[] mds = {0x02, 0x03, 0x01, 0x01,
            0x01, 0x02, 0x03, 0x01,
            0x01, 0x01, 0x02, 0x03,
            0x03, 0x01, 0x01, 0x02};

    private final static byte[] invmds = {0x0e, 0x0b, 0x0d, 0x09,
            0x09, 0x0e, 0x0b, 0x0d,
            0x0d, 0x09, 0x0e, 0x0b,
            0x0b, 0x0d, 0x09, 0x0e};

    /**
     * Method adds 0xFF and later 0x00 bytes to make block 16 bytes long
     *
     * @param original last block of crypted text
     * @return new block fulfilled to 16 bytes with 0xFF and 0x00
     */
    public byte[] addToLastFor16Bytes(byte[] original) {
        int lastBlockBytesNumber = original.length % BLOCK_SIZE;
        if (lastBlockBytesNumber == 0) {
            return original;
        }
        int bytesToAdd = BLOCK_SIZE - lastBlockBytesNumber;
        byte[] longer = new byte[original.length + bytesToAdd];
        System.arraycopy(original, 0, longer, 0, original.length);
        longer[original.length] = (byte) 0xFF;
        for (int i = original.length + 1; i < original.length + bytesToAdd; i++) {
            longer[i] = 0x00;
        }
        return longer;
    }

    /**
     * Method removes added before 0x00 and 0xFF to come back to original text
     *
     * @param original last block of decrypted text
     * @return new block with removed 0xFF and 0x00
     */
    public byte[] remove00fromEnd(byte[] original) {
        int numOf0 = 0;
        for (int i = original.length - 1; i >= original.length - BLOCK_SIZE; i--) {
            if (original[i] != 0) break;
            numOf0++;
        }
        if (numOf0 != 0 || original[original.length - 1] == (byte) 0xFF) {
            numOf0++;
        }
        byte[] shorter = new byte[original.length - numOf0];
        for (int i = 0; i < original.length - numOf0; i++) {
            shorter[i] = original[i];
        }
        return shorter;
    }

    /**
     * Method returns block (16 bytes) fragment of the data
     *
     * @param number which 16 bytes
     * @param data   full data array
     * @return key fragment
     */
    public byte[] getDataBlock(int number, byte[] data) {
        byte[] temp = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            temp[i] = data[number * BLOCK_SIZE + i];
        }
        return temp;
    }

    /**
     * Method returns 16 bytes fragment of the key
     *
     * @param iteration which 16 bytes
     * @param key       full key
     * @return key fragment
     */
    public List<Byte> getKeyBlock(int iteration, List<Byte> key) {
        List<Byte> result = new ArrayList<>();
        for (int i = 0; i < BLOCK_SIZE; i++) {
            result.add(key.get(iteration * BLOCK_SIZE + i));
        }
        return result;
    }

    /**
     * Get 4 bytes of the list and return byte[]
     *
     * @param start which 4 bytes
     * @param list  list to get 4 bytes fragment from
     * @return byte array
     */
    public byte[] get4Bytes(int start, List<Byte> list) {
        byte[] temp = new byte[ROW_SIZE];
        for (int i = 0; i < ROW_SIZE; i++) {
            temp[i] = list.get(list.size() - start + i);
        }
        return temp;
    }

    /**
     * Change 4 rows to block
     *
     * @param row1 row 1
     * @param row2 row 2
     * @param row3 row 3
     * @param row4 row 4
     * @return block
     */
    private byte[] move4RowsToBlock(byte[] row1, byte[] row2, byte[] row3, byte[] row4) {
        byte[] result = new byte[BLOCK_SIZE];
        for (int i = 0; i < ROW_SIZE; i++) {
            result[i * ROW_SIZE] = row1[i];
            result[i * ROW_SIZE + 1] = row2[i];
            result[i * ROW_SIZE + 2] = row3[i];
            result[i * ROW_SIZE + 3] = row4[i];
        }
        return result;
    }

    /**
     * Move given row 1 left
     *
     * @param original row to be moved left
     * @return new row
     */
    public byte[] rotate1Left(byte[] original) {
        byte[] rotate = new byte[ROW_SIZE];
        rotate[0] = original[1];
        rotate[1] = original[2];
        rotate[2] = original[3];
        rotate[3] = original[0];
        return rotate;
    }

    /**
     * Move given row 1 right
     *
     * @param original row to be moved right
     * @return new row
     */
    public byte[] rotate1Right(byte[] original) {
        byte[] rotate = new byte[ROW_SIZE];
        rotate[0] = original[3];
        rotate[1] = original[0];
        rotate[2] = original[1];
        rotate[3] = original[2];
        return rotate;
    }

    /**
     * Rcon operation - XOR of 2^iteration and 0;
     *
     * @param iteration num of iteration
     * @return rcon operation result
     */
    public byte[] rconOperation(int iteration) {
        byte[] rcon = new byte[ROW_SIZE];
        int num = 1;
        for (int i = 0; i < iteration - 1; i++) {
            num *= 2;
        }
        rcon[0] = (byte) (0 ^ num);
        return rcon;
    }

    /**
     * XOR operation for two rows
     *
     * @param a1 row 1
     * @param a2 row 2
     * @return XOR summed row
     */
    public byte[] xor(byte[] a1, byte[] a2) {
        for (int i = 0; i < ROW_SIZE; i++) {
            a1[i] = (byte) (a1[i] ^ a2[i]);
        }
        return a1;
    }

    /**
     * XOR sum of all row elements and return one byte
     *
     * @param b1 row
     * @return byte result
     */
    public byte xorMix(byte[] b1) {
        byte result = (byte) (b1[0] ^ b1[1] ^ b1[2] ^ b1[3]);
        return result;
    }

    /**
     * Change row (4 bytes) with sboxes
     *
     * @param temp row to be changed
     * @return row with sbox changed bytes
     */
    public byte[] changeByteBasedOnSbox(byte[] temp) {
        for (int i = 0; i < ROW_SIZE; i++) {
            int intFromByte = temp[i];
            intFromByte = removeMinusFromByte(intFromByte);
            temp[i] = (byte) SBox.getBox(intFromByte / BLOCK_SIZE, intFromByte % BLOCK_SIZE);
        }
        return temp;
    }

    /**
     * Change block (16 bytes) with sboxes
     *
     * @param temp block to be changed
     * @return block with sbox changed bytes
     */
    public byte[] changeByteBasedOnSbox16(byte[] temp) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            int intFromByte = temp[i];
            intFromByte = removeMinusFromByte(intFromByte);
            temp[i] = (byte) SBox.getBox(intFromByte / BLOCK_SIZE, intFromByte % BLOCK_SIZE);
        }
        return temp;
    }

    /**
     * Change block (16 bytes) with inverse sboxes
     *
     * @param temp block to be changed
     * @return block with inverse sbox changed bytes
     */
    public byte[] changeByteBasedOnInvSbox16(byte[] temp) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            int intFromByte = temp[i];
            intFromByte = removeMinusFromByte(intFromByte);
            temp[i] = (byte) SBox.getInvBox(intFromByte / BLOCK_SIZE, intFromByte % BLOCK_SIZE);
        }
        return temp;
    }

    /**
     * ShiftRows - Move every row by (numOfRow-1) left;
     *
     * @param array - 4x4 array to be moved
     * @return array with rows shifted
     */
    public byte[] shiftRows(byte[] array) {
        byte[] row1 = {array[0], array[4], array[8], array[12]};
        byte[] row2 = {array[1], array[5], array[9], array[13]};
        byte[] row3 = {array[2], array[6], array[10], array[14]};
        byte[] row4 = {array[3], array[7], array[11], array[15]};
        row2 = rotate1Left(row2);
        for (int i = 0; i < 2; i++) {
            row3 = rotate1Left(row3);
        }
        for (int i = 0; i < 3; i++) {
            row4 = rotate1Left(row4);
        }
        return move4RowsToBlock(row1, row2, row3, row4);
    }

    /**
     * InvShiftRows - Move every row by (numOfRow-1) right;
     *
     * @param array 4x4 array to be moved
     * @return array with rows inverse shifted
     */
    public byte[] invShiftRows(byte[] array) {
        byte[] row1 = {array[0], array[4], array[8], array[12]};
        byte[] row2 = {array[1], array[5], array[9], array[13]};
        byte[] row3 = {array[2], array[6], array[10], array[14]};
        byte[] row4 = {array[3], array[7], array[11], array[15]};
        row2 = rotate1Right(row2);
        for (int i = 0; i < 2; i++) {
            row3 = rotate1Right(row3);
        }
        for (int i = 0; i < 3; i++) {
            row4 = rotate1Right(row4);
        }
        return move4RowsToBlock(row1, row2, row3, row4);
    }

    /**
     * Mix columns operation
     * @param array block to do mix columns
     * @return return block with columns mixed
     */
    public byte[] mixColumns(byte[] array) {
        byte[] result = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            int row = i % ROW_SIZE;
            int column = i / ROW_SIZE;
            byte[] temp = new byte[ROW_SIZE];
            for (int j = 0; j < ROW_SIZE; j++) {
                int dec = array[column * ROW_SIZE + j];
                int mul = mds[row * ROW_SIZE + j];
                if (mul == 3) {
                    mul = 2;
                }
                dec = removeMinusFromByte(dec);
                int value = dec * mul;
                if (mds[row * ROW_SIZE + j] == 2 && value > 255) {
                    value = value ^ REDUCTION;
                }
                if (mds[row * ROW_SIZE + j] == 3) {
                    value = value ^ dec;
                    if (value > 255) {
                        value = value ^ 0x1B;
                    }
                }
                temp[j] = (byte) value;
            }
            result[i] = xorMix(temp);
        }
        return result;
    }

    /**
     * Inverse mix columns operation
     * @param array block to do inverse mix columns
     * @return return block with columns mixed
     */
    public byte[] invMixColumns(byte[] array) {
        byte[] result = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            int row = i % ROW_SIZE;
            int column = i / ROW_SIZE;
            byte[] temp = new byte[ROW_SIZE];
            for (int j = 0; j < ROW_SIZE; j++) {
                int arrayDecimalValue = array[column * ROW_SIZE + j];
                int multiplyValue = invmds[row * ROW_SIZE + j];
                arrayDecimalValue = removeMinusFromByte(arrayDecimalValue);
                int value = arrayDecimalValue;
                switch (multiplyValue) {
                    case 9 -> {
                        for (int k = 0; k < 3; k++) {
                            value = multiply2AndRemoveOverflow(value);
                        }
                        value = value ^ arrayDecimalValue;
                    }
                    case 11 -> {
                        for (int k = 0; k < 2; k++) {
                            value = multiply2AndRemoveOverflow(value);
                        }
                        value = value ^ arrayDecimalValue;
                        value = multiply2AndRemoveOverflow(value);
                        value = value ^ arrayDecimalValue;
                    }
                    case 13 -> {
                        value = multiply2AndRemoveOverflow(value);
                        value = value ^ arrayDecimalValue;
                        for (int k = 0; k < 2; k++) {
                            value = multiply2AndRemoveOverflow(value);
                        }
                        value = value ^ arrayDecimalValue;
                    }
                    case 14 -> {
                        for (int k = 0; k < 2; k++) {
                            value = multiply2AndRemoveOverflow(value);
                            value = value ^ arrayDecimalValue;
                        }
                        value = multiply2AndRemoveOverflow(value);
                    }
                }
                temp[j] = (byte) value;
            }
            result[i] = xorMix(temp);
        }
        return result;
    }

    /**
     * AddRoundKey - XOR sum of byte block and key bytes
     *
     * @param array 16 byte block of encoded/decoded text
     * @param key   key 16 byte fragment
     * @return XOR sum
     */
    public byte[] addRoundKey(byte[] array, List<Byte> key) {
        byte[] result = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            result[i] = (byte) (array[i] ^ key.get(i));
        }
        return result;
    }

    /**
     * Method multiplies value by two and removes overflow.
     *
     * @param value value to be multiplied;
     * @return multipied value with removed overflow
     */
    private int multiply2AndRemoveOverflow(int value) {
        value = value * 2;
        if (value > 255) {
            value = value ^ REDUCTION;
            value = value - NUM_OF_BYTES;
        }
        return value;
    }

    /**
     * If value if below 0 add 256 to make it positive
     *
     * @param value value
     * @return not minus value
     */
    private int removeMinusFromByte(int value) {
        if (value < 0) {
            value += NUM_OF_BYTES;
        }
        return value;
    }
}
