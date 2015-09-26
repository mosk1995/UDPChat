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
    public static boolean isWork = true;

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
        while (isWork) {
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
                // if (ServerController.connectedUser.size() != 0) {
                for (Client user : ServerController.connectedUser) {
                    System.out.println("foreach " + ServerController.connectedUser.size());
                    StringBuilder listUsers = new StringBuilder();
                    for (Client u : ServerController.connectedUser) {
                        listUsers.append(u.getNick());
                        listUsers.append("\n");
                    }
                    String dataUsers = String.valueOf(listUsers);
                    //System.out.println(str);
                    DatagramPacket outPacket = new DatagramPacket((USER_PING + dataUsers).getBytes(), (USER_PING + dataUsers).getBytes().length, user.getIp(), 6789);
                    byte[] buffer = new byte[512];
                    DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                    datagramSocket.send(outPacket);
                    System.out.println("send ++");
                    try {
                        datagramSocket.receive(inPacket);
                        System.out.println("receive " + ServerController.connectedUser.size());
                        code = new String(inPacket.getData(), 0, 3);
                        if (code.equals(USER_PING)) {
                            System.out.println(user.getNick() + " response on ping!");
                        }
                    } catch (SocketTimeoutException e) {
                        System.out.println(user.getNick() + "not response on ping!");
                        ServerController.connectedUser.remove(user);
                        break;
                    }
                    System.out.println("END FOREACH");
                }
                // }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

