package backend.main.com.projectcmd.printerConnector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
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

    public void startClientWithCommand(List<String> commands) throws IOException {
        PrintStream out = new PrintStream(CLIENT.getOutputStream());

        // message from server
        BufferedReader buf =  new BufferedReader(new InputStreamReader(CLIENT.getInputStream()));

        for (String command : commands) {
            out.println(command);
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
