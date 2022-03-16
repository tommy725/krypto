package pl.krypto.backend;

import java.nio.charset.StandardCharsets;

public class KeyGenerator {
    private static final int SIZE_OF_CHAR_IN_STRING = "a".getBytes(StandardCharsets.UTF_8).length * Byte.SIZE;

    /**
     * Returns a random key.
     * @param keySize number of key bits
     * @return String
     */
    public static String generateKey(int keySize) {
        StringBuilder builder = new StringBuilder();
        int keyLength = keySize / SIZE_OF_CHAR_IN_STRING;
        for (int i = 0; i < keyLength; i++) {
            builder.append(getRandom());
        }
        return builder.toString();
    }

    /**
     * Returns random char.
     * @return char
     */
    private static char getRandom() {
        return (new RandomGenerator()).generateRandomChar();
    }
}
