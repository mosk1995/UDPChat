package sample;

import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by MikAnt on 06.09.2015.
 */
public class ServerThread implements Runnable {

    private String port;
    private TextArea outputData;

    public ServerThread(String port, TextArea outputData){
        this.port=port;
        this.outputData=outputData;
    }

    @Override
    public void run() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(Integer.valueOf(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            Socket sock = null;
            try {
                sock = server.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ServerController.ConnectionArray.add(sock);
            try {
                outputData.appendText("Client connected from: " + sock.getLocalAddress().getHostName()+"\n");
                ServerController.AddUserName(sock);
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(new ServerService(sock, outputData)).start();
        }
    }
}
