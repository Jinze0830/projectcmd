package backend.main.com.projectcmd.csvprocessor;

import java.io.UnsupportedEncodingException;

public class CommandFactor {
    public static byte[] getByteArr(String command, String format, String content) throws UnsupportedEncodingException {
        String message = "";
        message = command == null || command.equals("") ? message : command;
        message = format == null || format.equals("") ? message : message + "," + format;
        message = content == null || content.equals("") ? message : message + "," + content;

        message = message + '\r';

        return message.getBytes("UTF8");
    }

//    public static List<byte[]> getHexStrs(String command, String format, List<String> content) {
//        List<byte[]> hexStrs = new ArrayList<>();
//        content.forEach(cur -> {
//            try {
//                hexStrs.add(CommandFactor.getHexStr(command, format, cur));
//            } catch (UnsupportedEncodingException e) {
//                throw new UnsupportedOperationException();
//            }
//        });
//
//        return hexStrs;
//    }
}
