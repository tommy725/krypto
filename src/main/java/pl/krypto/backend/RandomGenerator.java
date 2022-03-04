package pl.krypto.backend;

public class RandomGenerator {
    /**
     * Generating random number from interval.
     * @param min minimum value
     * @param max maximum value
     * @return int
     */
    public int generateRandomInt(int min, int max) {
        return this.hashCode() % (max + 1 - min) + min;
    }

    /**
     * Generate random char from readable for human.
     * @return char
     */
    public char generateRandomChar() {
        return (char) generateRandomInt(33, 126);
    }
}
