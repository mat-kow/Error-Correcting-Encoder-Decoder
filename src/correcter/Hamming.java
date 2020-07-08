package correcter;

public class Hamming {
    public static String encode(String input) {
        StringBuilder sb = new StringBuilder();
        input += "0".repeat(input.length() % 4 == 0 ? 0 : 4 - (input.length() % 4));
        for (int i = 0; i < input.length(); i += 4) {
            int d3 = char0or1toInt(input.charAt(i));
            int d5 = char0or1toInt(input.charAt(i + 1));
            int d6 = char0or1toInt(input.charAt(i + 2));
            int d7 = char0or1toInt(input.charAt(i + 3));
            int p1 = (d3 + d5 + d7) % 2;
            int p2 = (d3 + d6 + d7) % 2;
            int p4 = (d5 + d6 + d7) % 2;
            sb.append(p1).append(p2).append(d3).append(p4).append(d5).append(d6).append(d7).append(0);
        }
        return sb.toString();
    }

    public static String decode(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i += 8) {
            char[] byteChars = input.substring(i, i + 8).toCharArray();
            int p1 = char0or1toInt(input.charAt(i));
            int p2 = char0or1toInt(input.charAt(i + 1));
            int d3 = char0or1toInt(input.charAt(i + 2));
            int p4 = char0or1toInt(input.charAt(i + 3));
            int d5 = char0or1toInt(input.charAt(i + 4));
            int d6 = char0or1toInt(input.charAt(i + 5));
            int d7 = char0or1toInt(input.charAt(i + 6));
            int check1 = (p1 + d3 + d5 + d7) % 2;
            int check2 = (p2 + d3 + d6 + d7) % 2;
            int check4 = (p4 + d5 + d6 + d7) % 2;
            int badBitIndex = check1 + 2 * check2 + 4 * check4 - 1;
            if (badBitIndex >= 0) {
                byteChars[badBitIndex] = (char) ((char0or1toInt(byteChars[badBitIndex]) + 1) % 2 + 48); //48 - ASCII '0'
            }
            sb.append(byteChars[2]).append(byteChars[4]).append(byteChars[5]).append(byteChars[6]);
        }
        return sb.toString();
    }

    private static int char0or1toInt (char c){
        if (c == '1') return 1;
        else if (c == '0') return 0;
        else throw new IllegalArgumentException();
    }
}
