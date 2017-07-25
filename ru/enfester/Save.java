package ru.enfester;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static ru.enfester.Config.ClentDir;
import ru.enfester.utils.Hash;
import static ru.enfester.Vars.*;
import static ru.enfester.fx.Start.utils;

/**
 * Класс сохранения данных в файл YAML
 *
 * @author Антон
 */
public class Save {

    /**
     * Функция сохранения данных в файл YAML
     */
    public Save() {
        utils.send("Login & password saved...");

        try (FileWriter writer = new FileWriter(utils.getMcDir(ClentDir) + File.separator + "launcher.ini", false)) {
            String save = (CHECK_SAVE ? LOGIN : "") + ","
                    + (CHECK_SAVE ? PASSWORD : "") + ","
                    + DEDICATED_MEMORY + ","
                    + FULL_SVCREEN_GAME + ","
                    + CHECK_SAVE + ","
                    + CHECK_MUSIC + ","
                    + QUALITY;

            writer.write(Hash.toHash(save));
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
