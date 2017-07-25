package ru.enfester.fx;

import ru.enfester.utils.Utils;
import javafx.application.Application;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.JOptionPane;
import ru.enfester.Config;
import ru.enfester.MessegeWindow;

/**
 * Запуск лаунчера
 *
 * @author Антон
 */
public class Start extends Application {

    public static Stage stage;
    public static MediaPlayer player;
    public static Utils utils;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Start.stage = stage;
        utils = new Utils();
        if (System.getProperty("java.version").startsWith("1.8")) {
            utils.send("java version ok; " + System.getProperty("java.version"));
        } else {
            JOptionPane.showMessageDialog(null, "Требуется java 8 версии. Скачай на официальном сайте свежую версию https://www.java.com/ru/", "Java version error", JOptionPane.ERROR_MESSAGE);
            utils.openURL("https://www.java.com/ru/");
            System.exit(0);
        }

        utils.send("Startig Enfester Launcher v" + Config.Version + " created by Samar");

        new Main();
        setTrayIcon();
    }

    private static void setTrayIcon() {
        if (!SystemTray.isSupported()) {
            return;
        }

        PopupMenu trayMenu = new PopupMenu();
        MenuItem item = new MenuItem("Выход");
        item.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });
        trayMenu.add(item);
        URL imageURL = Start.class.getResource("/ru/enfester/fx/images/logo128.png");

        Image icon = Toolkit.getDefaultToolkit().getImage(imageURL);
        TrayIcon trayIcon = new TrayIcon(icon, "Enfester Лаунчер", trayMenu);
        trayIcon.setImageAutoSize(true);

        SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            new MessegeWindow("Ошибка trayIcon!", "Вряд ли ты в это виноват."
                    + "Обратись к администратору с этой ошибкой.\n" + e.getMessage(), "Продолжить");

        }

        //trayIcon.displayMessage("Enfester Лаунчер", "Запущен", TrayIcon.MessageType.INFO);
    }
}
