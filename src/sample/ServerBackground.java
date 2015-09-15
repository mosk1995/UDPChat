package sample;

import java.io.IOException;
import java.net.*;

/**
 * Created by mikant on 15.09.2015.
 */
public class ServerBackground implements Runnable {

    private int port;
    private DatagramSocket datagramSocket;
    private String USER_PING = "012";

    ServerBackground(int port) {
        this.port = port + 1;
        try {
            this.datagramSocket = new DatagramSocket(this.port, InetAddress.getLocalHost());
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("Exception#1 on ServerBackground");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("Exception#2 on ServerBackground");
        }
    }

    @Override
    public void run() {
        try {
            datagramSocket.setSoTimeout(500);
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("Exception#3 on ServerBackground");
        }
        while (true) {
            try {
                String code;
//                byte[] buffer = new byte[512];
//                DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
//                datagramSocket.receive(inPacket);
//                InetAddress clientAdress = inPacket.getAddress();
//                int clientPort = inPacket.getPort();
//                String code = new String(inPacket.getData(), 0, 3);
//                String message = new String(inPacket.getData(), 3, inPacket.getLength() - 3);
//                System.out.println("Code : " + code + "\nMessage : " + message);
                //System.out.println("ServiceThread"+ServerController.connectedUser.size());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (ServerController.connectedUser.size() != 0) {
                    for (Client user : ServerController.connectedUser) {
                        System.out.println("foreach " + ServerController.connectedUser.size());
                        DatagramPacket outPacket = new DatagramPacket(USER_PING.getBytes(), USER_PING.getBytes().length, user.getIp(), 6789);
                        byte[] buffer = new byte[512];
                        DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                        datagramSocket.send(outPacket);
                        try {
                            datagramSocket.receive(inPacket);
                        }
                        catch (SocketTimeoutException e){
                            ServerController.connectedUser.remove(user);
                            break;
                        }
                        System.out.println("receive " + ServerController.connectedUser.size());
                        code = new String(inPacket.getData(), 0, 3);
                        if (code.equals(USER_PING)) {
                            System.out.println(user.getNick() + " response on ping!");
                        }
                        System.out.println("END OF FOR");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

