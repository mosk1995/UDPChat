package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by MikAnt on 06.09.2015.
 */
public class ClientController extends Pane {

    public static ArrayList<String> JL_ONLINE = new ArrayList<>();

    public Button disconnectButton;
    public TextField enterMessage;
    public TextArea onlineUsers;
    public Button sndMsgBtn;
    public Button connectButton;
    public TextField fieldPortClient;
    public TextField fieldIP;
    public TextArea conversationArea;
    public TextField fieldNick;

    private String userNick;
    private int port;
    private String host;
    private UDPClientThread udpClientThread;

    public ClientController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("client.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
            conversationArea.setDisable(true);
            disconnectButton.setDisable(true);
            enterMessage.setDisable(true);
            onlineUsers.setDisable(true);
            sndMsgBtn.setDisable(true);
            fieldIP.setText("localhost");
            fieldPortClient.setText("10000");
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }


    //Обработчик кнопки отправки сообщения
    @FXML
    public void handleSendButton(ActionEvent event) {

        String message = enterMessage.getText();
        if (!message.equals("")) {
            message = "000" + message;
            try {
                DatagramSocket datagramSocket = null;
                datagramSocket = new DatagramSocket();
                DatagramPacket outPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, InetAddress.getByName("localhost"), port);
                datagramSocket.send(outPacket);
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        enterMessage.setText("");

    }

    @FXML
    public void handleDisconnectButton(ActionEvent event) throws IOException {
        System.exit(0);
    }

    @FXML
    public void handleConnectButton(ActionEvent event) {

        if (!fieldNick.getText().equals("")) {
            userNick = fieldNick.getText();
            ///ServerController.CurrentUsers.add(userNick);
            try {
                port = Integer.valueOf(fieldPortClient.getText());
                host = fieldIP.getText();

                //Начало  работы UDP-клиента

                String message = "001" + userNick;
                DatagramSocket datagramSocket = new DatagramSocket();
                DatagramPacket outPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, InetAddress.getByName("localhost"), port);
                datagramSocket.send(outPacket);
                byte[] buffer = new byte[512]; //512 позволяет нам гарантировать корректный приём любым хостом см. https://ru.wikipedia.org/wiki/UDP, но нихрена не обнспечивает нормальны размер сообщений

                //Ответ от сервера если 002 то отправляем менять ник, если 003 начинаем чатить
                DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(inPacket);
                String response = new String(inPacket.getData(), 0, inPacket.getLength());
                if (response.equals(UDPServerThread.USER_WAS_CONNECTED)) {
                    System.out.println("Угрожающее всплывающее сообщение");
                    JOptionPane.showMessageDialog(null, "Пиздуй отсюда, школьник!!!");
                    fieldNick.setText("");
                } else if (response.equals(UDPServerThread.USER_CONNECTED_SUCCESSFUL)) {
                    conversationArea.appendText("You connected to: " + host + "\n");
                    connectButton.setDisable(true);
                    conversationArea.setDisable(false);
                    disconnectButton.setDisable(false);
                    enterMessage.setDisable(false);
                    onlineUsers.setDisable(false);
                    sndMsgBtn.setDisable(false);
                    udpClientThread=new UDPClientThread(conversationArea,onlineUsers,datagramSocket);
                    new Thread(udpClientThread).start();

                }
                System.out.println(response);

                //Конец работы UDP-клиента

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Connecting with server failed");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please enter the nick!");
        }
    }
}