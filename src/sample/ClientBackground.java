package sample;

import com.sun.org.apache.xpath.internal.SourceTree;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.*;

/**
 * Created by mikant on 15.09.2015.
 */
public class ClientBackground implements Runnable {
    private int port;
    private DatagramSocket datagramSocket;
    private Button disconnectButton;
    private TextField enterMessage;
    private TextArea onlineUsers;
    private Button sndMsgBtn;
    private Button connectButton;
    private TextField fieldPortClient;
    private TextField fieldIP;
    private TextArea conversationArea;
    private TextField fieldNick;
    public static boolean IS_WORK = true;
    private DatagramSocket mainSocket;

    ClientBackground(TextArea onlineUsers, Button disconnectButton, Button connectButton, TextField enterMessage, TextField fieldIP,
                     TextField fieldNick, TextField fieldPortClient, Button sndMsgBtn, TextArea conversationArea, DatagramSocket mainSocket) {
        this.port = 6789;
        this.sndMsgBtn = sndMsgBtn;
        this.conversationArea = conversationArea;
        this.disconnectButton = disconnectButton;
        this.connectButton = connectButton;
        this.enterMessage = enterMessage;
        this.fieldIP = fieldIP;
        this.fieldNick = fieldNick;
        this.fieldPortClient = fieldPortClient;
        this.mainSocket = mainSocket;
        try {
            this.datagramSocket = new DatagramSocket(port, InetAddress.getLocalHost());
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("Exception#1 on ClientBackground");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("Exception#2 on ClientBackground");
        }
        this.onlineUsers = onlineUsers;
    }

    @Override
    public void run() {
        try {
            datagramSocket.setSoTimeout(5000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (IS_WORK) {
            System.out.println(IS_WORK);
            byte[] buffer = new byte[512];//Данное ограничение позволяет нам гарантировать корректный приём любым хостом см. https://ru.wikipedia.org/wiki/UDP
            DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
            try {
                try {
                    datagramSocket.receive(inPacket);
                    String code = new String(inPacket.getData(), 0, 3);
                    String message = new String(inPacket.getData(), 3, inPacket.getLength() - 3);
                    Platform.runLater(() -> onlineUsers.setText(message));
                    System.out.println(message);
                    System.out.println("Server ping me!");
                    if (code.equals("012")) {
                        DatagramPacket outPacket = new DatagramPacket("012".getBytes(), "012".getBytes().length, InetAddress.getByName(ClientController.host), ClientController.port + 1);
                        datagramSocket.send(outPacket);
                    }
                } catch (SocketTimeoutException e) {
                    Platform.runLater(() -> {
                        connectButton.setDisable(false);
                        conversationArea.setDisable(true);
                        disconnectButton.setDisable(true);
                        enterMessage.setDisable(true);
                        onlineUsers.setDisable(true);
                        sndMsgBtn.setDisable(true);
                        //mainSocket.close();
                    });
//                    conversationArea.setDisable(true);
//                    disconnectButton.setDisable(true);
//                    enterMessage.setDisable(true);
//                    onlineUsers.setDisable(true);
//                    sndMsgBtn.setDisable(true);
                    //mainSocket.close();
                    Thread.currentThread().stop();
                    System.out.println("SERVER WAS DOWN!");
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
