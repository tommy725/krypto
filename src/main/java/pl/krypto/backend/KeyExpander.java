package pl.krypto.backend;

import java.util.ArrayList;
import java.util.List;

public class KeyExpander {
    private final List<Byte> originalKey = new ArrayList<>();
    private ByteArrayOperator bao = new ByteArrayOperator();
    private final int EXTEND_NUM_OF_BYTES = 240;

    public KeyExpander(byte[] originalKey) {
        for (byte b : originalKey) {
            this.originalKey.add(b);
        }
    }

    /**
     * Expand key from 32 bytes to 240
     * @param iteration iteration number
     * @return expanded key
     */
    public List<Byte> expand(int iteration) {
        //etap 1 (adding 4 bytes)
        byte[] temp = bao.get4Bytes(4, originalKey); //1.1 move to temp vector
        temp = bao.rotate1Left(temp); //1.2 rotate one left
        temp = bao.changeByteBasedOnSbox(temp); //1.3 change 1.2 with sbox
        byte[] rcon = bao.rconOperation(iteration); //1.4.1 rcon operation
        temp = bao.xor(temp, rcon); //1.4.2 xor of 1.3 and 1.4.1
        //1.5 adding xor of 4 bytes starting 32 bytes from end and 1.4.2
        addToKey(bao.xor(temp, bao.get4Bytes(32, originalKey)));
        //etap 2 (adding 12 bytes)
        for (int j = 0; j < 3; j++) {
            //2.1 and 2.2 (xor of last 4 and 4 bytes starting 32 bytes from end)
            addToKey(bao.xor(bao.get4Bytes(4, originalKey), bao.get4Bytes(32, originalKey)));
        }
        //etap 3 (adding 4 bytes)
        temp = bao.get4Bytes(4, originalKey); //3.1 get last 4 bytes of key
        temp = bao.changeByteBasedOnSbox(temp); //3.2 change 3.1 with sbox
        //3.3 xor of 3.2 and 4 bytes starting 32 bytes from end and adding to key
        addToKey(bao.xor(temp, bao.get4Bytes(32, originalKey)));
        //etap 4 (adding 12 bytes)
        for (int i = 0; i < 3; i++) {
            temp = bao.get4Bytes(4, originalKey); //4.1 get last 4 bytes of key
            //4.2.1 xor of 4.1 and 4 bytes starting 32 bytes from end and adding to key
            addToKey(bao.xor(temp, bao.get4Bytes(32, originalKey)));
        }
        if (originalKey.size() < EXTEND_NUM_OF_BYTES) {
            return expand(++iteration); //If not enought bytes repeat
        }
        return originalKey;
    }

    /**
     * Adding new array to key
     * @param add byte array
     */
    private void addToKey(byte[] add) {
        for (byte b : add) {
            originalKey.add(b);
        }
    }
}
