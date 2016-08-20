/*
 * Copyright 2016 Tyler Snedigar.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details.
 */
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
            VBox root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            controller = loader.getController();
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
