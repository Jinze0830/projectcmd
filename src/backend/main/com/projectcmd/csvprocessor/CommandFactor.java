package backend.main.com.projectcmd.csvprocessor;

import java.util.ArrayList;
import java.util.List;

public class CommandFactor {
    public static byte[] getHexStr(String command, String format, String content) {
        String message = "";
        message = command == null || command.equals("") ? message : command;
        message = format == null || format.equals("") ? message : message + "," + format;
        message = content == null || content.equals("") ? message : message + "," + content;

        char[] arr = message.toCharArray();
        String hexStr = "";
        for(char c: arr) {
            String hexString = Integer.toHexString(c);
            hexStr = hexString;
        }

        hexStr = hexStr + "0d";


        return hexStringToByteArray(hexStr);
    }

    public static List<byte[]> getHexStrs(String command, String format, List<String> content) {
        List<byte[]> hexStrs = new ArrayList<>();
        content.forEach(cur -> hexStrs.add(CommandFactor.getHexStr(command, format, cur)));
        return hexStrs;
    }

    public static byte[] hexStringToByteArray(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    public static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }
}
