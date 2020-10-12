package backend.main.com.projectcmd.printerConnector;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

public class TcpClient {
    private final Socket CLIENT;
    private final int TIME_OUT = 10000;


    public TcpClient(String ip, int port) throws IOException {
        CLIENT = new Socket(ip, port);
        CLIENT.setSoTimeout(TIME_OUT);
    }

    public void startClientWithCommand(List<byte[]> commands) throws IOException {
        DataOutputStream out = new DataOutputStream(CLIENT.getOutputStream());

        // message from server
        BufferedReader buf =  new BufferedReader(new InputStreamReader(CLIENT.getInputStream()));

        for (byte[] command : commands) {

            out.write(command);
            out.flush();

            try {
                String response = buf.readLine();
                System.out.println(response);
            } catch(SocketTimeoutException exception) {
                System.out.println("not response from server");
            }
        }

        if(CLIENT != null) {
            CLIENT.close();
        }
    }
}
