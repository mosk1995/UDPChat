package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainServer extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        ServerController serverController = new ServerController();
        stage.setScene(new Scene(serverController));
        stage.setTitle("Sign in");
        stage.setWidth(440);
        stage.setHeight(440);
        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(event -> {
            UDPServerThread.SERVER_IS_WORK = false;
            ServerBackground.IS_WORK = false;
            System.out.println("Сервер завершил свою работу");
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}
