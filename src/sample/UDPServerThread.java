package sample;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.*;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * Created by Xenomorf on 08.09.2015.
 */
public class UDPServerThread implements   Runnable{
    private int port;
    private TextArea outputData;
    private ArrayList<Client> connectedUser;
    //Константы внутрисистемных сообщений
    public static   String MESSAGE="000";
    public static   String USER_WAS_CONNECTED="002";
    public static   String USER_CONNECTED_SUCCESSFUL="003";
    public static   boolean SERVER_IS_WORK=true;

    public UDPServerThread(int port, TextArea outputData){
        this.port=port;
        this.outputData=outputData;
        connectedUser=new ArrayList<Client>();
    }

    @Override
    public void run() {

        try {
            DatagramSocket datagramSocket= new DatagramSocket(port,InetAddress.getLocalHost());

            while (SERVER_IS_WORK) {
                byte[] buffer=new byte[512];//Данное ограничение позволяет нам гарантировать корректный приём любым хостом см. https://ru.wikipedia.org/wiki/UDP
                DatagramPacket inPacket=new DatagramPacket(buffer,buffer.length);
                datagramSocket.receive(inPacket);
                Calendar reseivedTime= Calendar.getInstance();
                InetAddress clientAdress=inPacket.getAddress();
                int clientPort=inPacket.getPort();
                String code=new String(inPacket.getData(),0,3);
                String message=new String(inPacket.getData(),3,inPacket.getLength()-3);
                System.out.println("Code : "+code +"\nMessage : "+message);
                if (code.equals("001")) {
                    boolean isOk=true;
                    for( Client user :connectedUser) {
                        if (user.getNick().equals(message)) {
                            DatagramPacket outPacket = new DatagramPacket(USER_WAS_CONNECTED.getBytes(),USER_WAS_CONNECTED.getBytes().length,clientAdress,clientPort);
                            datagramSocket.send(outPacket);
                            isOk=false;
                            break;
                        }
                    }
                    if (isOk) {
                        System.out.println("Client with nick : \"" + message + "\",ip : "+inPacket.getAddress()+" , port : "+inPacket.getPort()+" connected.");
                        connectedUser.add(new Client(inPacket.getAddress(), inPacket.getPort(), message));
                        DatagramPacket outPacket = new DatagramPacket(USER_CONNECTED_SUCCESSFUL.getBytes(),USER_CONNECTED_SUCCESSFUL.getBytes().length,clientAdress,clientPort);
                        datagramSocket.send(outPacket);
                    }

                }

                if (code.equals("000")){

                    String sendersNick="";
                    for( Client user :connectedUser) {
                        System.out.println("Client with nick : \"" + user.getNick() + "\",ip : "+user.getIp()+" , port : "+user.getPort());
                        System.out.println("Message address "+clientAdress+" port "+clientPort);
                        if (user.getIp().equals(clientAdress)&&(clientPort==user.getPort())) {
                            sendersNick=user.getNick();
                        }
                    }
                    SimpleDateFormat sdf= new SimpleDateFormat("dd.mm.yy HH:mm:ss");
                    String sentedMessage=MESSAGE+sdf.format(reseivedTime.getTime())+" "+sendersNick+" :"+message;
                    for( Client user :connectedUser) {
                        DatagramPacket outPacket=new DatagramPacket(sentedMessage.getBytes(),sentedMessage.getBytes().length,user.getIp(),user.getPort());
                        System.out.println(connectedUser.toString());
                        datagramSocket.send(outPacket);
                    }
                }
                //outputData.appendText(message);
            }


            //datagramSocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("Ошибка создания сервера. Выберите другой порт.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
