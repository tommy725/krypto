package pl.krypto.backend;

import java.util.HashMap;
import java.util.Map;

public class Base64 {
    private Map<String, String> base64 = new HashMap<>();

    public Base64() {
        base64.put("000000", "A");
        base64.put("000001", "B");
        base64.put("000010", "C");
        base64.put("000011", "D");
        base64.put("000100", "E");
        base64.put("000101", "F");
        base64.put("000110", "G");
        base64.put("000111", "H");
        base64.put("001000", "I");
        base64.put("001001", "J");
        base64.put("001010", "K");
        base64.put("001011", "L");
        base64.put("001100", "M");
        base64.put("001101", "N");
        base64.put("001110", "O");
        base64.put("001111", "P");
        base64.put("010000", "Q");
        base64.put("010001", "R");
        base64.put("010010", "S");
        base64.put("010011", "T");
        base64.put("010100", "U");
        base64.put("010101", "V");
        base64.put("010110", "W");
        base64.put("010111", "X");
        base64.put("011000", "Y");
        base64.put("011001", "Z");
        base64.put("011010", "a");
        base64.put("011011", "b");
        base64.put("011100", "c");
        base64.put("011101", "d");
        base64.put("011110", "e");
        base64.put("011111", "f");
        base64.put("100000", "g");
        base64.put("100001", "h");
        base64.put("100010", "i");
        base64.put("100011", "j");
        base64.put("100100", "k");
        base64.put("100101", "l");
        base64.put("100110", "m");
        base64.put("100111", "n");
        base64.put("101000", "o");
        base64.put("101001", "p");
        base64.put("101010", "q");
        base64.put("101011", "r");
        base64.put("101100", "s");
        base64.put("101101", "t");
        base64.put("101110", "u");
        base64.put("101111", "v");
        base64.put("110000", "w");
        base64.put("110001", "x");
        base64.put("110010", "y");
        base64.put("110011", "z");
        base64.put("110100", "0");
        base64.put("110101", "1");
        base64.put("110110", "2");
        base64.put("110111", "3");
        base64.put("111000", "4");
        base64.put("111001", "5");
        base64.put("111010", "6");
        base64.put("111011", "7");
        base64.put("111100", "8");
        base64.put("111101", "9");
        base64.put("111110", "+");
        base64.put("111111", "/");
    }

    public String encode(byte[] array) {
        String s = getBits(array);
        System.out.println(s);
        int iterations = s.length() / 6;
        if (s.length() % 6 != 0) {
            iterations++;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            if (6 * (i + 1) > s.length()) {
                StringBuilder add0 = new StringBuilder();
                int diff = (6 * (i + 1)) - s.length();
                add0.append(s.substring(6 * i));
                for (int j = 0; j < diff; j++) {
                    add0.append("0");
                }
                result.append(base64.get(add0.toString()));
            } else {
                result.append(base64.get(s.substring(6 * i, 6 * (i + 1))));
            }
        }
        return result.toString();
    }

    public byte[] decode(String s) {
        byte[] result = new byte[s.length() * 6 / 8];
        String key = null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            for (Map.Entry<String, String> entry : base64.entrySet()) {
                if (entry.getValue().equals(s.substring(i, i + 1))) {
                    key = entry.getKey();
                    break;
                }
            }
            sb.append(key);
        }
        for (int i = 0; i < s.length() * 6 / 8; i++) {
            System.out.println(sb.substring(8 * i, 8 * (i + 1)));
            System.out.println(bitsToInt(sb.substring(8 * i, 8 * (i + 1))));
            result[i] = (byte) bitsToInt(sb.substring(8 * i, 8 * (i + 1)));
        }


        return result;
    }

    public String getBits(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            for (int i = 7; i >= 0; i--) {
                sb.append((b >> i) & 1);
            }
        }
        return sb.toString();
    }

    public int bitsToInt(String bitsString) {
        int result = 0;
        String reversed = new StringBuilder(bitsString).reverse().toString();
        for (int i = 0; i < 8; i++) {
            String s = reversed.substring(i, i + 1);
            if (s.equals("1")) {
                int temp = 1;
                for (int j = 0; j < i; j++) {
                    temp *= 2;
                }
                result += temp;
            }
        }
        return result;
    }
}
