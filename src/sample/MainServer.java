package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainServer extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        ServerController serverController=new ServerController();
        stage.setScene(new Scene(serverController));
        stage.setTitle("Sign in");
        stage.setWidth(279);
        stage.setHeight(423);
        stage.setResizable(false);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
