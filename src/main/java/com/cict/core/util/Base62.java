package com.cict.core.util;

import java.math.BigInteger;

/**
 * Encoders and decoders for base-62 formatted data. Uses the alphabet 0..9 a..z
 * A..Z, e.g. '0' => 0, 'a' => 10, 'A' => 35 and 'Z' => 61.
 * <p>
 * I found this code here:
 * http://code.openhub.net/file?fid=kGojiBkxuAfSsCcDqKNg8ptyZ3g&cid=sqwHTynYLxw&s=&fp=103029&mp=&projSelected=true#L0
 */
public class Base62 {

    private static final BigInteger BASE = BigInteger.valueOf(62);

    /**
     * Returns the index of a byte in the alphabet.
     *
     * @param key element to search for
     * @return index of key in alphabet
     */
    private static int valueForByte(byte key) {
        if ('0' <= key && key <= '9' || 'a' <= key && key <= 'z' || 'A' <= key && key <= 'Z') {
            if (Character.isLowerCase(key)) {
                return key - ('a' - 10);
            } else if (Character.isUpperCase(key)) {
                return key - ('A' - 10 - 26);
            }

            return key - '0';
        }

        throw new IndexOutOfBoundsException(Character.toString((char) key));
    }

    /**
     * Convert a base-62 string known to be a number.
     */
    public static BigInteger decodeToBigInteger(String s) {
        return decodeToBigInteger(s.getBytes());
    }

    /**
     * Convert a base-62 byte array known to be a number.
     */
    public static BigInteger decodeToBigInteger(byte[] bytes) {
        BigInteger res = BigInteger.ZERO;
        BigInteger multiplier = BigInteger.ONE;

        for (int i = bytes.length - 1; i >= 0; i--) {
            res = res.add(multiplier.multiply(BigInteger.valueOf(valueForByte(bytes[i]))));
            multiplier = multiplier.multiply(BASE);
        }

        return res;
    }

    /**
     * Encodes the big int as a base-62 string.
     */
    public static String encode(BigInteger i) throws IllegalArgumentException {
        if (i == null) {
            throw new NullPointerException("Argument must be non-null");
        }

        if (BigInteger.ZERO.compareTo(i) > 0) {
            throw new IllegalArgumentException("Argument must be larger than zero");
        }

        if (BigInteger.ZERO.compareTo(i) == 0) {
            return "0";
        }

        StringBuilder stringBuilder = new StringBuilder();
        BigInteger value = i.add(BigInteger.ZERO); // Clone argument

        while (BigInteger.ZERO.compareTo(value) < 0) {
            BigInteger[] divRem = value.divideAndRemainder(BASE);
            int remainder = divRem[1].intValue();

            if (remainder < 10) {
                stringBuilder.insert(0, (char) (remainder + '0'));
            } else if (remainder < 10 + 26) {
                stringBuilder.insert(0, (char) (remainder + 'a' - 10));
            } else {
                stringBuilder.insert(0, (char) (remainder + 'A' - 10 - 26));
            }

            value = divRem[0];
        }

        return stringBuilder.toString();
    }
}