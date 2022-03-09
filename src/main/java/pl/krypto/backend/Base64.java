package pl.krypto.backend;

import java.util.HashMap;
import java.util.Map;

public class Base64 {
    private Map<String, String> base64 = new HashMap<>();

    public Base64() {
        int offset = 65; //A=65
        for (int i = 0; i < 62; i++) {
            if (i == 26) {
                offset = 71; // a = 97 = 26 + 71
            }
            if (i == 52) {
                offset = -4; // 0 = 48 = 52 - 4
            }
            StringBuilder binaryString = new StringBuilder(Integer.toBinaryString(i));
            for (int j = binaryString.length(); j < 6; j++) {
                binaryString.insert(0, "0");
            }
            base64.put(binaryString.toString(), String.valueOf(((char) (i + offset))));
        }
        base64.put("111110", "+");
        base64.put("111111", "/");
    }

    public String encode(byte[] array) {
        String s = getBits(array);
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
