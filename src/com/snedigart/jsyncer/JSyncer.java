package com.snedigart.jsyncer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class JSyncer extends Application {

    private JSyncerController controller;

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout.fxml"));
            VBox root = (VBox) loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            controller = loader.<JSyncerController> getController();
            controller.initialize();

            primaryStage.setMinWidth(root.getMinWidth());
            primaryStage.setMinHeight(root.getMinHeight());

            primaryStage.setScene(scene);
            primaryStage.setTitle("JSyncer");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (controller != null) {
            controller.onExit();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
