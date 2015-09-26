package sample;

import com.sun.org.apache.xpath.internal.SourceTree;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.*;

/**
 * Created by mikant on 15.09.2015.
 */
public class ClientBackground implements Runnable {
    private int port;
    private DatagramSocket datagramSocket;
    private TextArea onlineUsers;

    ClientBackground(TextArea onlineUsers) {
        this.port = 6789;
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
        while (true) {
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
                    System.out.println("SERVER WAS DOWN!");
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
