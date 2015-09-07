package sample;

import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by MikAnt on 06.09.2015.
 */
public class ServerService implements Runnable {

    private Socket sock;
    private Scanner inpt;
    private PrintWriter ot;
    private String MESSAGE = "";
    private TextArea outputData;

    public ServerService(Socket sock, TextArea outputData) {
        this.sock = sock;
        this.outputData = outputData;
    }

    public void CheckConnection() throws IOException {
        if (!sock.isConnected()) {
            for (int i = 1; i <= ServerController.ConnectionArray.size(); i++) {
                if (ServerController.ConnectionArray.get(i-1) == sock) {
                    ServerController.ConnectionArray.remove(i);
                }
            }
            for (int i = 1; i <= ServerController.ConnectionArray.size(); i++) {
                Socket tempSock = ServerController.ConnectionArray.get(i - 1);
                PrintWriter tempOt = new PrintWriter(tempSock.getOutputStream());
                tempOt.println(tempSock.getLocalAddress().getHostName() + " disconnected!");
            }

        }
    }

    public void run() {
        try {
            try {
                inpt = new Scanner(sock.getInputStream());
                ot = new PrintWriter(sock.getOutputStream());

                while (true) {
                    CheckConnection();

                    if (!inpt.hasNext()) {
                        return;
                    }

                    MESSAGE = inpt.nextLine();

                    for (int i = 1; i <= ServerController.ConnectionArray.size(); i++) {
                        Socket tempSock = ServerController.ConnectionArray.get(i - 1);
                        PrintWriter tempOt = new PrintWriter(tempSock.getOutputStream());
                        tempOt.println(MESSAGE);
                        tempOt.flush();
                        outputData.appendText("Sent to: " + tempSock.getLocalAddress().getHostName() + "\n");
                    }
                }
            } finally {
                sock.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
