package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Created by MikAnt on 06.09.2015.
 */
public class ServerController extends Pane {

    public ServerController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("server.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
            outputData.setEditable(false);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static volatile Vector<Client> connectedUser = new Vector<>();

    public TextField fieldPort;
    public TextField fieldNick;
    public TextField fieldIP;
    public Button buttonStart;
    public TextArea outputData;
    public Button buttonNick;
    public Button buttonIP;
    public Button buttonGetList;
    public Button buttonConnectedUser;
    private ArrayList<InetAddress> bannedIP = new ArrayList<>();
    private ArrayList<String> bannedName = new ArrayList<>();

    @FXML
    public void handleStartButton(ActionEvent event) throws IOException {
        String port_s = fieldPort.getText();
        int port;
        try {
            if (port_s.equals("")) {
                port = 10000;
            } else {
                port = Integer.parseInt(port_s);
            }
            DatagramSocket testSocket = new DatagramSocket(port, InetAddress.getLocalHost());
            testSocket.close();

            outputData.appendText("Сервер запущен на порте " + port + " !\n");
            new Thread(new UDPServerThread(port, outputData)).start();
            fieldPort.setDisable(true);
            buttonStart.setDisable(true);
        } catch (NumberFormatException e) {
            outputData.appendText("Введите корректное значение порта!!!\n");
        } catch (BindException e) {
            outputData.appendText("Порт занят введите другое значение!!!\n");
        }
    }



    @FXML
    public void getList(ActionEvent event) {
        printBannedUser();
    }

    @FXML
    public void changeStateName() throws FileNotFoundException {
        getBannedUser();
        String name = fieldNick.getText();
        boolean inBannedNames = false;
        for (String bNames : bannedName) {
            if (name.equals(bNames)) {
                inBannedNames = true;
                bannedName.remove(bNames);
                bannedName.trimToSize();
                break;
            }
        }
        if (!inBannedNames) {
            bannedName.add(name);
            for (Client t : connectedUser) {
                if (t.getNick().equals(name)) {
                    connectedUser.remove(t);
                    System.out.println("__________________________________" + connectedUser.size());
                    connectedUser.trimToSize();
                    break;
                }
            }

        }
        PrintWriter writer = new PrintWriter(new File("bannedName"));
        for (int i = 0; i < bannedName.size(); i++) {
            writer.print(bannedName.get(i) + "\n");
        }
        writer.close();
        printBannedUser();
    }

    @FXML
    public void changeStateIP() throws FileNotFoundException, UnknownHostException {
        getBannedUser();
        String ip = fieldIP.getText();
        boolean inBannedNames = false;
        for (InetAddress bIP : bannedIP) {
            if (InetAddress.getByName(ip).equals(bIP)) {
                inBannedNames = true;
                bannedIP.remove(bIP);
                bannedIP.trimToSize();

                break;
            }
        }
        if (!inBannedNames) {
            bannedIP.add(InetAddress.getByName(ip));
            for (Client t : connectedUser) {
                if (t.getIp().equals(InetAddress.getByName(ip))) {
                    connectedUser.remove(t);
                    connectedUser.trimToSize();
                    break;
                }
            }
        }
        PrintWriter writer = new PrintWriter(new File("bannedIP"));
        for (int i = 0; i < bannedIP.size(); i++) {
            //String temp= String.valueOf(bannedIP.get(i)).replace("/","");

            writer.print(String.valueOf(bannedIP.get(i)).replace("/", "") + "\n");
        }
        writer.close();
        printBannedUser();
    }

    @FXML
    public void showConnectedUser(){
        outputData.appendText("Connected users :\n");
        for (Client t : connectedUser) {
            outputData.appendText(t.getNick()+"\n"+t.getIp().toString()+"\n"+t.getPort()+"\n\n");
        }
        outputData.appendText("----------------------------------------------\n");
    }

    private void printBannedUser(){
        File bannedNameFile = new File("bannedName");
        File bannedIPFile = new File("bannedIP");
        outputData.appendText("List of banned Names:\n");
        try (BufferedReader br = new BufferedReader(new FileReader(bannedNameFile))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                outputData.appendText(sCurrentLine + "\n");
                System.out.println(sCurrentLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        outputData.appendText("----------------------------------------------\n");
        outputData.appendText("List of banned IP's:\n");
        try (BufferedReader br = new BufferedReader(new FileReader(bannedIPFile))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                //sCurrentLine = sCurrentLine.replace("/", "");
                outputData.appendText(String.valueOf(InetAddress.getByName(sCurrentLine)) + "\n");
                System.out.println(sCurrentLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        outputData.appendText("----------------------------------------------\n");
    }

    private void getBannedUser() {

        String content = null;
        bannedIP.clear();
        bannedName.clear();
        File bannedNameFile = new File("bannedName");
        File bannedIPFile = new File("bannedIP");

        try (BufferedReader br = new BufferedReader(new FileReader(bannedNameFile))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                bannedName.add(sCurrentLine);
                System.out.println(sCurrentLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(bannedIPFile))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                bannedIP.add(InetAddress.getByName(sCurrentLine));
                System.out.println(sCurrentLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
