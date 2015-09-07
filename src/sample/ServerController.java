package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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

    public static ArrayList<Socket> ConnectionArray = new ArrayList<>();
    public static ArrayList<String> CurrentUsers = new ArrayList<>();
    public static Map<Socket,String> testMap=new HashMap<>();

    public TextField fieldPort;
    public Button buttonStart;
    public TextArea outputData;

    @FXML
    public void handleStartButton(ActionEvent event) throws IOException {
        fieldPort.setDisable(true);
        String port = fieldPort.getText();
        if (port.equals("")) {
            port = "3331";
            outputData.appendText("Server started with port 3331!\n");
        }
        else outputData.appendText("Server started!\n");
        new Thread(new ServerThread(port, outputData)).start();
    }

    public static void AddUserName(Socket sck) throws IOException {
        Scanner inpt = new Scanner(sck.getInputStream());
        String userNick = inpt.nextLine();
        testMap.put(sck,userNick);
        CurrentUsers.add(userNick);
        for (int i = 1; i <= ConnectionArray.size(); i++) {
            Socket temp = ConnectionArray.get(i - 1);
            PrintWriter ot = new PrintWriter(temp.getOutputStream());
            ot.println("#?!" + CurrentUsers);
            System.out.println(" " + CurrentUsers);
            ot.flush();
        }
    }
}
