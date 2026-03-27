package com.assessx.assessx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/fxml/login.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(MainApplication.class.getResource("/styles/login.css").toExternalForm());
        stage.setTitle("AssessX");
        stage.setMinWidth(500);
        stage.setMinHeight(500);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
