package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainClient extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        ClientController clientController=new ClientController();
        stage.setScene(new Scene(clientController));
        stage.setTitle("Chat by Mikant and Xenomorf");
        stage.setWidth(627);
        stage.setHeight(437);
        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.println("Клиент завершил свою работу");
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
