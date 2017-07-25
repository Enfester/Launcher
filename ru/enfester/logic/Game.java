package ru.enfester.logic;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import ru.enfester.Config;
import ru.enfester.Vars;
import ru.enfester.fx.controllers.MainController;
import ru.enfester.utils.EnCoreNetwork;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javafx.application.Platform;
import ru.enfester.MessegeWindow;
import static ru.enfester.fx.Start.utils;
import static ru.enfester.fx.controllers.MainController.mainController;

public class Game extends MainController {

    String l = "libraries.jar";
    String e = "external.jar";
    String f = "forge.jar";
    String m = "minecraft.jar";
    String s = "scala.jar";

    String cps;
    ArrayList<String> start = new ArrayList<>();

    boolean jsonStart = true;
    int server;

    public Game(final int srv) {
        server = srv;

        if (!Vars.ONLINE) {
            if (!new File(utils.getMcDir(Config.ClentDir).toString() + File.separator + Config.Servers[server][0]).exists()) {
                new MessegeWindow("Ошибка запуска игры!", "Данный клиент не найден на этом компьютере.", "Выбрать другой");
            } else {
                startGame();
            }
        } else if (!EnCoreNetwork.isOnline()) {
            new MessegeWindow("Не удалось запустить игру!", "Нет интернет соеденения для обновления клиента.", "Повторить");
        } else if (Config.Servers[server][4] == "false") {
            startGame();
        } else {
            new Thread(() -> {
                new Update(srv);
                startGame();
            }).start();
        }

    }

    final void startGame() {
        Platform.runLater(() -> {
            mainController.paneLoadClient.setVisible(true);
            mainController.paneLogin.setVisible(false);
            mainController.paneMain.setVisible(false);
            mainController.labelClientDownload.setText("");
            mainController.labelClientDownloadGlobal.setText("");
            mainController.prograssBarClientDownload.setProgress(-1);
            mainController.prograssBarClientDownloadGlobal.setProgress(-1);
            mainController.labelItap.setText("Подожди пока запустится игра - " + Config.Servers[server][0]);
        });

        FileReader fileReader = null;
        try {
            final String dirMinecraft = utils.getMcDir(Config.ClentDir).toString(),
                    dirClientName = dirMinecraft + File.separator + Config.Servers[server][0],
                    dirBin = dirClientName + File.separator + "bin",
                    dirAssets = dirMinecraft + File.separator + "assets";

            utils.send("Running Minecraft");
            if (utils.getPlatform().ordinal() == 2) {
                cps = ";";
                start.add("javaw");
            } else {
                cps = ":";
                start.add("java");
            }
            String mainClass, libs = "";
            JsonParser parser = new JsonParser();
            fileReader = new FileReader(dirBin + File.separator + "minecraft.json");
            JsonElement jsonElement = parser.parse(fileReader);
            JsonObject asJsonObject = jsonElement.getAsJsonObject();
            mainClass = asJsonObject.get("mainClass").toString().replace("\"", "");
            JsonArray libraries = asJsonObject.getAsJsonArray("libraries");
            for (JsonElement lib : libraries) {
                String lsa = lib.getAsJsonObject().get("name").toString().replace("\"", "");
                String thisJson = dirClientName + File.separator + "libraries"
                        + File.separator + lsa.split(":")[0].replace(".", File.separator)
                        + File.separator + lsa.split(":")[1]
                        + File.separator + lsa.split(":")[2]
                        + File.separator
                        + lsa.split(":")[1] + "-" + lsa.split(":")[2] + ".jar";

                if (!new File(thisJson).exists()) {
                    thisJson = dirClientName + File.separator + "libraries"
                            + File.separator + lsa.split(":")[0].replace(".", File.separator)
                            + File.separator + lsa.split(":")[1]
                            + File.separator + lsa.split(":")[2]
                            + File.separator
                            + lsa.split(":")[1] + "-" + lsa.split(":")[2] + "-natives-" + utils.getPlatformString() + ".jar";
                }

                libs = libs + thisJson + cps;
            }
            start.add("-Xmx" + Vars.DEDICATED_MEMORY + "m");
            start.add("-Dfml.ignoreInvalidMinecraftCertificates=true");
            start.add("-Dfml.ignorePatchDiscrepancies=true");
            start.add("-Djava.library.path=" + dirBin + File.separator + "natives");
            start.add("-cp");
            start.add(libs + dirBin + File.separator + m);
            start.add(mainClass);
                
            start.add("--tweakClass");
            start.add("tlauncher.forge.config.FixSplashScreen");
            start.add("--tweakClass");
            start.add("com.mumfrey.liteloader.launch.LiteLoaderTweaker");
            if (Vars.FULL_SVCREEN_GAME) {
                start.add("--fullscreen");
                start.add("true");
            } else {
                start.add("--width");
                start.add("900");
                start.add("--height");
                start.add("536");
            }
            start.add("--username");
            start.add(Vars.LOGIN);
            start.add("--version");
            start.add(Config.Servers[server][3]);
            start.add("--gameDir");
            start.add(dirClientName);
            start.add("--assetsDir");
            start.add(dirAssets);
            start.add("--assetIndex");
            start.add(Config.Servers[server][3]);
            if (EnCoreNetwork.isOnline()) {
                start.add("--server");
                start.add(Config.Servers[server][1]);
                start.add("--port");
                start.add(Config.Servers[server][2]);
            }
            start.add("--tweakClass");
            start.add("cpw.mods.fml.common.launcher.FMLTweaker");
            start.add("--accessToken");
            start.add(Vars.ACCESS_TOKEN);
            start.add("--uuid");
            start.add(Vars.UUID);
            start.add("--userProperties");
            start.add("{}");

            new Thread(() -> {
                try {
                    utils.send("ProcessBuilder Start");
                    ProcessBuilder pb = new ProcessBuilder(start);
                    pb.directory(new File(dirClientName));
                    
                    //pb.start();
                    InputStream errorStream = pb.start().getInputStream();
                    BufferedReader input = new BufferedReader(new InputStreamReader(errorStream));
                    utils.send("ProcessBuilder Close");
                    String ch;
                    while ((ch = input.readLine()) != null) {
                        final String c = ch;
                        
                        Platform.runLater(() -> {
                            mainController.labelStatus.setText(c);
                        });
                        if (c.contains("Setting user")) {
                            System.exit(0);
                        }
                    }
                } catch (IOException ex) {
                    new MessegeWindow("Ошибка запуска игры!", "Вряд ли ты в это виноват."
                        + "Обратись к администратору с этой ошибкой.\n" + ex.getMessage(), "Продолжить");
                }
            }).start();
        } catch (FileNotFoundException ex) {
            new MessegeWindow("Ошибка запуска игры!", "Скорее всего ты изменил файлы "
                    + "игры или обновление прошло неудачно. Обратись к администратору с этой ошибкой.\n" + ex.getMessage(), "Продолжить");
        } finally {
            try {
                fileReader.close();
            } catch (IOException ex) {
                new MessegeWindow("Ошибка запуска игры!", "Вряд ли ты в это виноват."
                        + "Обратись к администратору с этой ошибкой.\n" + ex.getMessage(), "Продолжить");
            }
        }
    }

}
