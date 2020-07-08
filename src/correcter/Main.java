package correcter;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class Main {
    private final static Random random = new Random(LocalDateTime.now().getNano());

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose mode: encode/send/decode/all");
        String mode = scanner.nextLine();
        switch (mode) {
            case "encode":
                System.out.println("Choose algorithm: default[d]/Hamming[h]");
                String algorithm = scanner.nextLine();
                modeEncode(algorithm);
                break;
            case "send":
                modeSend();
                break;
            case "decode":
                System.out.println("Choose algorithm: default[d]/Hamming[h]");
                algorithm = scanner.nextLine();
                modeDecode(algorithm);
                break;
            case "all":
                System.out.println("Choose algorithm: default[d]/Hamming[h]");
                algorithm = scanner.nextLine();
                modeEncode(algorithm);
                modeSend();
                modeDecode(algorithm);
        }
    }

    private static void modeDecode(String algorithm) {
        StringBuilder sb = new StringBuilder();
        try (FileInputStream inputStream = new FileInputStream("received.txt")) {
            int charAsNumber = inputStream.read();
            while(charAsNumber != -1) {
                String binaryChar = intTo8bitsString(charAsNumber);
                sb.append(binaryChar);
                charAsNumber = inputStream.read();
            }
        } catch (IOException e) {
            System.out.println("Something went wrong:");
            e.printStackTrace();
        }
        String decodedBinary;
        if (algorithm.equals("h") || algorithm.equals("H")) {
            decodedBinary = Hamming.decode(sb.toString());
        } else {
            decodedBinary = DefaultCoding.decodeToBinary(sb.toString());
        }
        save("decoded.txt", binaryStringToChars(decodedBinary).getBytes());
    }

    private static void modeSend() {
        StringBuilder sb = new StringBuilder();
        try (FileInputStream inputStream = new FileInputStream("encoded.txt");) {
            int charAsNumber = inputStream.read();
            while(charAsNumber != -1) {
                String binaryChar = intTo8bitsString(charAsNumber);
                String errorBinaryChar = make1biteErrorInByte(binaryChar);
                sb.append(errorBinaryChar);
                charAsNumber = inputStream.read();
            }
        } catch (IOException e) {
            System.out.println("Something went wrong:");
            e.printStackTrace();
        }
        byte[] bytes = binaryStringToByteArray(sb.toString());
        save("received.txt", bytes);
    }

    private static void modeEncode(String algorithm) {
        CharArrayWriter charWriter = new CharArrayWriter();
        try (FileReader reader = new FileReader("send.txt")) {
            int charAsNumber = reader.read();
            while(charAsNumber != -1) {
                charWriter.append((char) charAsNumber);
                charAsNumber = reader.read();
            }
        } catch (IOException e) {
            System.out.println("Something went wrong:");
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        for (char c : charWriter.toCharArray()) {
            String binary = intTo8bitsString(c);
            sb.append(binary);
        }
        String encoded;
        if (algorithm.equals("h") || algorithm.equals("H")) {
            encoded = Hamming.encode(sb.toString());
        } else {
            encoded = DefaultCoding.encode(sb.toString());
        }
        byte[] bytes = binaryStringToByteArray(encoded);
        save("encoded.txt", bytes);
    }

    private static int binaryToDecimal(String binaryNumber1byte) {
        int number = 0;
        int length = binaryNumber1byte.length();
        for (int i = 0; i < length; i++) {
            if (binaryNumber1byte.charAt(i) == '1') {
                number += Math.pow(2, length - 1 - i);
            }
        }
        return number;
    }

    private static String make1biteErrorInByte(String binaryNumber8bits) {
        int index = random.nextInt(8);
        StringBuilder out = new StringBuilder();
        out.append(binaryNumber8bits, 0, index);
        if (binaryNumber8bits.charAt(index) == '1') {
            out.append(0);
        } else {
            out.append('1');
        }
        out.append(binaryNumber8bits, index + 1, 8);
        return out.toString();
    }

    private static String binaryStringToChars(String encodedBinary) {
        StringBuilder sb = new StringBuilder();
        for (int i = 7; i < encodedBinary.length(); i += 8) {
            char c = (char) binaryToDecimal(encodedBinary.substring(i - 7, i + 1));
            sb.append(c);
        }
        return sb.toString();
    }

    private static byte[] binaryStringToByteArray (String encodedBinary) {
        byte[] bytes = new byte[encodedBinary.length() / 8];
        for (int i = 0; i < encodedBinary.length(); i += 8) {
            bytes[i / 8] = (byte) binaryToDecimal(encodedBinary.substring(i, i + 8));
        }
        return bytes;
    }

    private static String intTo8bitsString(int number) {
        StringBuilder sb = new StringBuilder();
        String binaryNumber = Integer.toBinaryString(number);
        sb.append("0".repeat(8 - binaryNumber.length()));
        sb.append(binaryNumber);
        return sb.toString();
    }

    private static void save(String fileName, byte[] data) {
        File file = new File(fileName);
        try (OutputStream outputStream = new FileOutputStream(file, false)) {
            outputStream.write(data);
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
        }
    }
}


