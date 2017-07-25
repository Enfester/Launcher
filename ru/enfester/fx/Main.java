package ru.enfester.fx;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;
import ru.enfester.Config;
import ru.enfester.Vars;
import static ru.enfester.fx.Start.player;

public class Main {

    /**
     * Открытие главного фрейма
     *
     */
    public Main() {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
            root.getStylesheets().add("/ru/enfester/fx/style.css");

            Font.loadFont(getClass().getResource("/ru/enfester/fx/Lato-Bold.ttf").toExternalForm(), 10);
            Font.loadFont(getClass().getResource("/ru/enfester/fx/Lato-Thin.ttf").toExternalForm(), 10);

            Start.stage.setScene(new Scene(root));
            Start.stage.initStyle(StageStyle.UNDECORATED);

            Start.stage.setResizable(false);
            Start.stage.setTitle(Config.Title);

            Start.stage.getIcons().add(new Image(getClass().getResourceAsStream("/ru/enfester/fx/images/logo128.png")));
            Start.stage.show();

            player = new MediaPlayer(new Media(getClass().getResource("/ru/enfester/fx/sounds/music.mp3").toString()));
           
            if (Vars.CHECK_MUSIC) {
                player.play();
            }

            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            Start.stage.setX(dim.width / 2 - 900 / 2);
            Start.stage.setY(dim.height / 2 - 600 / 2);
// stage.initStyle(StageStyle.DECORATED);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
