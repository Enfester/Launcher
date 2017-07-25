/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.enfester;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import static ru.enfester.fx.Start.utils;
import static ru.enfester.fx.controllers.MainController.mainController;
import ru.enfester.utils.EnCoreNetwork;
import ru.enfester.utils.HttpRequests;

/**
 *
 * @author antiv
 */
public class Auth {

    public Auth() {
        mainController.enter.setDisable(true);// Блокирум кнопку
        mainController.authload.setVisible(true);// Показываем круглый прогрессбар 
        // Заносим данные из полей логина и пароля в глобальные переменные
        Vars.LOGIN = mainController.login.getText();
        Vars.PASSWORD = mainController.password.getText();

        new Thread(()
                -> //Этот метод будет выполняться в побочном потоке
                {
                    if (!EnCoreNetwork.isOnline()) {
                        Platform.runLater(() -> {
                            new MessegeWindow("Нет подключения к интернету!", "Лаунчер не смог подключиться к удаленному серверу.", "Продолжить");
                            mainController.authload.setVisible(false);
                            mainController.enter.setDisable(false);
                        });
                    } else if (check()) {
                        Vars.CHECK_SAVE = mainController.checksave.isSelected();
                        new Save();
                        Platform.runLater(() -> {
                            //  EnCoreNetwork.excuteGET(Config.SystemDir + "auth", Vars.LOGIN);

                            mainController.paneLogin.setVisible(false);
                            mainController.paneMain.setVisible(true);
                            mainController.labelName.setText(Vars.LOGIN);
                            mainController.listing();
                            mainController.setSkin();
                        });
                    } else {
                        Platform.runLater(() -> {
                            mainController.authload.setVisible(false);
                            mainController.enter.setDisable(false);
                            new MessegeWindow("Вход не выполнен!", "Неверный логин или пароль.", "Повторить");
                        });
                    }
                }).start();
    }

    public boolean check() {
// if (Vars.LOGIN != null && Vars.PASSWORD != null) {
//
//            String post = EnCoreNetwork.excutePost(Config.AuthFileDir, "method=auth&user=" + Vars.LOGIN + "&password=" + Vars.PASSWORD);
//            if (!post.contains(":")) {
//                return false;
//            } else {
//                Vars.UUID = post.split(":")[2];
//                Vars.ACCESS_TOKEN = post.split(":")[3];
//
//                return true;
//            }
//
//        } else {
//            return false;
//
//        }
        if (Vars.LOGIN != null && Vars.PASSWORD != null) {

//            Session sess = new Session();
//            sess.username = Vars.LOGIN;
//            sess.password = Vars.PASSWORD;
//            sess.clientToken = 
            HttpRequests http = new HttpRequests();

            String token = utils.getMD5(new Random(new Date().getTime()) + "token");
            String post = null;
            try {
                post = http.Post(Config.SystemDir + "auth",
                        "login=" + Vars.LOGIN
                        + "&password=" + Vars.PASSWORD
                        + "&token=" + token);
            } catch (Exception ex) {
                Logger.getLogger(Auth.class.getName()).log(Level.SEVERE, null, ex);
            }

            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(post);
            JsonObject asJsonObject = jsonElement.getAsJsonObject();

            if ("ok".equals(jsonClear(asJsonObject.get("status")))) {
                Vars.LOGIN = jsonClear(asJsonObject.get("login"));
                Vars.UUID = jsonClear(asJsonObject.get("uuid"));
                Vars.ACCESS_TOKEN = jsonClear(asJsonObject.get("accessToken"));

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private String jsonClear(JsonElement str) {
        return str.toString().replace("\"", "").replace("\n", "").replace("\t", "").replace(" ", "");
    }

}
