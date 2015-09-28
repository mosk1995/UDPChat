package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.BindException;
import java.net.DatagramSocket;
import java.net.SocketException;

public class MainClient extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        if (!isRun()) {
            ClientController clientController = new ClientController();
            stage.setScene(new Scene(clientController));
            stage.setTitle("Chat by Mikant and Xenomorf");
            stage.setWidth(627);
            stage.setHeight(437);
            stage.setResizable(false);
            stage.show();
            stage.setOnCloseRequest((event) -> {
                UDPClientThread.IS_WORK = false;
                ClientBackground.IS_WORK = false;
                System.out.println("Клиент завершил свою работу");
            });
        } else {
            Text text1 = new Text(20, 30, "Приложение уже запущено!!!");
            Text text2 = new Text(10, 70, "Нажмите крестик для завершения работы.");
            text1.setFont(new Font(20));
            text2.setFont(new Font(15));

            Scene scene = new Scene(new Group(text1, text2));

            stage.setTitle("Ошибочка вышла!");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.sizeToScene();
            stage.show();
        }

    }

    private boolean isRun() {
        try {
            DatagramSocket ds = new DatagramSocket(6789);
            ds.close();
            return false;
        } catch (SocketException e) {
            System.out.println("Программа запущена");
            return true;
        }

    }


    public static void main(String[] args) {
        launch(args);
    }
}
