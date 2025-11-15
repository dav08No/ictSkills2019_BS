package com.example.appdev_nocito_davide;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("TournamentList.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Tournament List");
        stage.setScene(scene);
        stage.show();
    }

    public static void OpenDialog(String fxml, String title, int height, int width, Object data) throws IOException {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOpacity(1);
        stage.setTitle(title);
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = fxmlLoader.load();
        iReceiveData controller = fxmlLoader.getController();
        controller.receiveData(data);
        stage.setScene(new Scene(root, width, height));
        stage.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}