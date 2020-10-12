package backend.main.com.projectcmd.csvprocessor;

import java.util.ArrayList;
import java.util.List;

public class CommandFactor {
    public static String getHexStr(String command, String format, String content) {
        String message = "";
        message = command != null || !command.equals("") ? command : message;
        message = format != null || !format.equals("") ? message + "," + format : message;
        message = content != null || !content.equals("") ? message + "," + content : message;

        char[] arr = message.toCharArray();
        String hexStr = "";
        for(char c: arr) {
            String hexString = Integer.toHexString(c);
            hexStr += hexString;
        }

        return hexStr + "0d";
    }

    public static List<String> getHexStrs(String command, String format, List<String> content, String suffix) {
        List<String> hexStrs = new ArrayList<>();
        content.forEach(cur -> hexStrs.add(CommandFactor.getHexStr(command, format, cur)));
        return hexStrs;
    }
}
