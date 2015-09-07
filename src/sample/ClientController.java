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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by MikAnt on 06.09.2015.
 */
public class ClientController extends Pane {

    public static ArrayList<String> JL_ONLINE = new ArrayList<>();

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
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

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
    private Socket sock;
    private String host;
    private PrintWriter ot;

    @FXML
    public void handleSendButton(ActionEvent event)
    {
        try {
            ot = new PrintWriter(sock.getOutputStream());
        ot.println(userNick + ": " + enterMessage.getText());
        ot.flush();
        enterMessage.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleDisconnectButton(ActionEvent event) throws IOException
    {
        ot.println(userNick + " has been disconnected!");
        JL_ONLINE.remove(userNick);
        onlineUsers.setText("");
        for(int i=0;i<JL_ONLINE.size();i++){
            onlineUsers.appendText(ClientController.JL_ONLINE.get(i)+"\n");
        }
        ot.flush();
        sock.close();
        JOptionPane.showMessageDialog(null, "You disconnected");
        System.exit(0);
    }
    @FXML
    public void handleConnectButton(ActionEvent event) {

        if(!fieldNick.getText().equals(""))
        {
            userNick = fieldNick.getText();
            ///ServerController.CurrentUsers.add(userNick);
            try {
                port = Integer.valueOf(fieldPortClient.getText());
                host = fieldIP.getText();
                sock = new Socket(host, port);
                conversationArea.appendText("You connected to: " + host + "\n");
                connectButton.setDisable(true);
                conversationArea.setDisable(false);
                disconnectButton.setDisable(false);
                enterMessage.setDisable(false);
                onlineUsers.setDisable(false);
                sndMsgBtn.setDisable(false);
                ot = new PrintWriter(sock.getOutputStream());
                ot.println(userNick);
                ot.flush();
                new Thread(new ClientThread(sock,conversationArea,onlineUsers)).start();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Connecting with server failed");
            }
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Please enter the nick!");
        }
    }
}