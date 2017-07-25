/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.enfester;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import static ru.enfester.fx.Start.utils;
import static ru.enfester.fx.controllers.MainController.mainController;

/**
 *
 * @author antiv
 */
public class MessegeWindow {

    Pane white = new Pane();
    Label title = new Label(), messege = new Label();
    Button button = new Button();

    public MessegeWindow(String tit, String mess, String but) {
        GaussianBlur blur = new GaussianBlur();

      //  mainController.pane.setEffect(blur);
        white.setPrefSize(901, 300);
        white.setLayoutY(150);
        white.setStyle("-fx-background-color:white;-fx-border-color: gray;-fx-border-width: 1px;");

        title.setText(tit);
        title.setLayoutY(20);
        title.setPrefWidth(900);
        title.setAlignment(Pos.CENTER);
        title.setFont(new Font(51));
        title.setTextFill(Color.BLACK);

        messege.setText(mess);
        messege.setAlignment(Pos.CENTER);
        messege.setLayoutY(150);
        messege.setPrefWidth(900);

        button.setText(but);
        button.setPrefWidth(200);
        button.setPrefHeight(45);
        button.setLayoutY(220);
        button.setLayoutX(330);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent me) {
                close();
            }
        });

        white.getChildren().add(title);
        white.getChildren().add(messege);
        white.getChildren().add(button);
        mainController.pane.getChildren().add(white);
        utils.sendErr(tit + ":" + mess);
    }

    void close() {
        mainController.pane.setEffect(null);
        mainController.pane.getChildren().remove(white);
    }

}
