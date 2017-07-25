package ru.enfester;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;
import static ru.enfester.Config.ClentDir;
import static ru.enfester.fx.Start.utils;
import static ru.enfester.fx.controllers.MainController.mainController;
import static ru.enfester.fx.controllers.MainController.sliderValue;
import ru.enfester.logic.Update;
import ru.enfester.utils.Hash;
import ru.enfester.utils.Utils;

/**
 * Класс загрузки данных из файла YAML
 *
 * @author Антон
 */
public class Load {

    public Load() {

        try (FileReader reader = new FileReader(utils.getMcDir(ClentDir) + File.separator + "launcher.ini")) {
            // читаем посимвольно
            String load = "";
            int c;
            while ((c = reader.read()) != -1) {
                load = load + (char) c;
            }

            utils.send("Login & password loading...");

            try {
                load = Hash.deHash(load);
                Vars.LOGIN = load.split(",")[0];
                Vars.PASSWORD = load.split(",")[1];
                Vars.DEDICATED_MEMORY = utils.toInt(load.split(",")[2]);
                Vars.FULL_SVCREEN_GAME = utils.toBoolean(load.split(",")[3]);
                Vars.CHECK_SAVE = utils.toBoolean(load.split(",")[4]);
                Vars.CHECK_MUSIC = utils.toBoolean(load.split(",")[5]);
                Vars.QUALITY = utils.toInt(load.split(",")[6]);
            } catch (Exception e) {
                Update.delete(new File(utils.getMcDir(ClentDir) + File.separator + "launcher.ini"));
                new Save();
            }

            mainController.login.setText(Vars.LOGIN);
            mainController.password.setText(Vars.PASSWORD);
            mainController.checksave.setSelected(Vars.CHECK_SAVE);

            if (Vars.CHECK_MUSIC) {
                mainController.sound.setImage(new Image(getClass().getResourceAsStream("/ru/enfester/fx/images/suOn.png")));
            } else {
                mainController.sound.setImage(new Image(getClass().getResourceAsStream("/ru/enfester/fx/images/suOff.png")));
            }

            switch (Vars.QUALITY) {
                case 1:
                    mainController.radioButtonUltra.setSelected(true);
                    break;
                case 2:
                    mainController.radioButtonMax.setSelected(true);
                    break;
                case 3:
                    mainController.radioButtonMid.setSelected(true);
                    break;
                case 4:
                    mainController.radioButtonMin.setSelected(true);
                    break;
                default:
                    break;
            }

            mainController.checkBoxFullScreen.setSelected(Vars.FULL_SVCREEN_GAME);

            mainController.ramLabel.setText(Vars.DEDICATED_MEMORY + "MB");

            if (System.getProperty("sun.arch.data.model").equals("64")) {
                mainController.slider.setMax(Utils.memorySizeTotal);
            }
            if (!System.getProperty("sun.arch.data.model").toLowerCase().equals("64")) {
                mainController.slider.setMax(1536);
            }
            sliderValue = new SimpleIntegerProperty(Vars.DEDICATED_MEMORY);

            mainController.slider.valueProperty().bindBidirectional(sliderValue);
            mainController.ramLabel.textProperty().bind(sliderValue.asString());
        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }

    }
}
