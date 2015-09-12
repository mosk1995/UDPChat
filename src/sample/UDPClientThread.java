package sample;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Xenomorf on 08.09.2015.
 */
public class UDPClientThread implements Runnable {
    private TextArea conversationArea;
    private TextArea onlineUsers;
    private DatagramSocket datagramSocket;
    private boolean isOn = false;

    public UDPClientThread(TextArea conversationArea, TextArea onlineUsers, DatagramSocket datagramSocket) {
        this.conversationArea = conversationArea;
        this.onlineUsers = onlineUsers;
        this.datagramSocket = datagramSocket;
    }

    @Override
    public void run() {
        isOn = true;
        while (isOn) {
            byte[] buffer = new byte[512];//Данное ограничение позволяет нам гарантировать корректный приём любым хостом см. https://ru.wikipedia.org/wiki/UDP
            DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);

            try {
                datagramSocket.receive(inPacket);
                String code=new String(inPacket.getData(),0,3);
                String message=new String(inPacket.getData(),3,inPacket.getLength()-3);
                System.out.println("Code : "+code +"\nMessage : "+message);
                if (code.equals(UDPServerThread.MESSAGE)){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            conversationArea.appendText(message + "\n");
                        }
                    });

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
