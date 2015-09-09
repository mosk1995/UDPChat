package sample;

import javafx.scene.control.TextArea;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Xenomorf on 08.09.2015.
 */
public class UDPClientThread implements Runnable{
    private Socket sock;
    private Scanner inpt;
    private Scanner send = new Scanner(System.in);
    private PrintWriter ot;
    private TextArea conversationArea;
    private TextArea onlineUsers;

    public UDPClientThread(Socket sock, TextArea conversationArea, TextArea onlineUsers) {
        this.sock = sock;
        this.conversationArea = conversationArea;
        this.onlineUsers = onlineUsers;
    }

    @Override
    public void run() {
        try {
            try {
                inpt = new Scanner(sock.getInputStream());
                ot = new PrintWriter(sock.getOutputStream());
                ot.flush();
                while (true) {
                    if (inpt.hasNext()) {
                        String MESSAGE = inpt.nextLine();

                        if (MESSAGE.contains("#?!")) {
                            String TEMP1 = MESSAGE.substring(3);
                            TEMP1 = TEMP1.replace("[", "");
                            TEMP1 = TEMP1.replace("]", "");
                            String[] CurrentUsers = TEMP1.split(", ");

                            for (String s : CurrentUsers) {
                                ClientController.JL_ONLINE.add(s);
                            }
                            Set set = new TreeSet<>(ClientController.JL_ONLINE);
                            ClientController.JL_ONLINE = new ArrayList<>(set);
                            onlineUsers.setText("");
                            for (int i = 0; i < ClientController.JL_ONLINE.size(); i++) {
                                onlineUsers.appendText(ClientController.JL_ONLINE.get(i) + "\n");
                            }

                        } else {
                            conversationArea.appendText(MESSAGE + "\n");
                        }
                    }
                }
            } finally {
                sock.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
