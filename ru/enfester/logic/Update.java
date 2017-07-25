package ru.enfester.logic;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.enfester.Config;
import ru.enfester.utils.EnCoreNetwork;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javafx.application.Platform;
import ru.enfester.MessegeWindow;
import ru.enfester.Vars;
import static ru.enfester.fx.Start.utils;
import static ru.enfester.fx.controllers.MainController.mainController;
import ru.enfester.utils.HttpRequests;

/**
 * Обновление клиента
 *
 * @author Samar
 */
public class Update {

    private final List<File> clientList = new ArrayList<>();
    private final List<File> serverList = new ArrayList<>();
    private final HashMap<File, String> downloadlist = new HashMap<>();

    String minecraftPatch = utils.getMcDir(Config.ClentDir).toString();

    File clientv = new File(minecraftPatch + File.separator + "client.yml");

    /**
     * Запускаем обеновление в отдельном потоке
     */
    int gi, i = 0;

    final int server;

    public Update(int ser) {
        server = ser;

        mainController.paneLoadClient.setVisible(true);
        utils.send("Run Update");

        // Прверка файлов
        try {
// Открываем соеденеие
            HttpRequests request = new HttpRequests();

            // Отправляем запрос на список файлов по данному клиенту
            String json = request.Get(Config.SystemDir + "client/" + Config.Servers[server][0]);
// Получаем JSON ответ

// Создаем билдер для JSON
            Gson gson = new Gson();

            // Парсим полученный ответ по структуре класса Client
            Client client = gson.fromJson(json, Client.class);

            // Запускаем проверку MD5 сумм по клиент-сервер
            CheckFiles(client, Config.Servers[server][0]);

            File assets = new File(minecraftPatch + File.separator + "assets.zip");
            String md5assets = utils.getMD5(assets);

            if (!client.assets.md5.equals(md5assets)) {
                // если файл не соответствует мд5 - качаем
                downloadlist.put(assets, (client.assets.url));
            }

        } catch (FileNotFoundException ex) {
            new MessegeWindow("Ошибка обновления!", "Что то пошло не так."
                    + " Обратись к администратору с этой ошибкой.\n" + ex.getMessage(), "Повторить");
        } catch (Exception ex) {
            Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            uploadStart();
        }

    }

    class Client {

        public int count;
        public ArrayList<file> files;
        public String status;
        public asset assets;

        class file {

            public String url, path, md5;
            public boolean update;
        }

        class asset {

            public String url, md5;
        }

    }

    void uploadStart() {
        for (Entry<File, String> entry : downloadlist.entrySet()) {
            gi++;

            try {
                utils.send("Download: " + entry.getValue());
                EnCoreNetwork.download(new URL(entry.getValue()), entry.getKey(), downloadlist.size());

                Platform.runLater(() -> {
                    mainController.prograssBarClientDownloadGlobal.setProgress((gi * 100 / downloadlist.size()) / 100D);
                    mainController.labelClientDownloadGlobal.setText("Файлов загружено: " + gi + " из " + downloadlist.size());
                });

            } catch (Exception e) {
                new MessegeWindow("Ошибка!", "Вряд ли ты в это виноват."
                        + "Обратись к администратору с этой ошибкой.\n" + e.getMessage(), "Продолжить");
            }

        }

        if (!new File(minecraftPatch + File.separator + "assets").exists()) {
            unzip(minecraftPatch + File.separator + "assets.zip", minecraftPatch + File.separator + "assets");
        } else if (getDirSize(new File(minecraftPatch + File.separator + "assets")) < 110302013) {
            unzip(minecraftPatch + File.separator + "assets.zip", minecraftPatch + File.separator + "assets");
        }

        RemoveExcess(Config.Servers[server][0]);

        utils.send("Close Update");

        //Доавиь дополнительную проверку Assets
        delete(new File(minecraftPatch + File.separator + "assets" + File.separator + "skins"));
    }

    private void FiltrQuality(File dir, String url) {
        if (Vars.QUALITY == 1) { // Если стоит ультра то качаем все файлы
            // ULTRA MAX MID ALL
            downloadlist.put(dir, url);
        } else if (Vars.QUALITY == 2) { // Если стоит максимум то качаем все без ультра
            // MAX MID ALL
            if (url.contains("-ULTRA")) { // Если в моде ултра прописано то удаляем и наче качаем
                delete(dir);
            } else {
                downloadlist.put(dir, url);
            }
        } else if (Vars.QUALITY == 3) { // Если стоит среднее то качаем все кроме ультра и максимума
            // MID ALL
            if (url.contains("-ULTRA") || url.contains("-MAX")) {
                delete(dir);
            } else {
                downloadlist.put(dir, url);
            }
        } else if (Vars.QUALITY == 4) {
            // ALL
            if (url.contains("-ULTRA") || url.contains("-MAX") || url.contains("-MID")) {
                delete(dir);
            } else {
                downloadlist.put(dir, url);
            }
        } else {

        }

    }

    /**
     * Сверка файлов с сервером
     */
    private void CheckFiles(Client server, String client) throws FileNotFoundException {
        utils.send("Check files " + client);

        // Разбиваем массив файлов
        for (Client.file file : server.files) {

            i++;
            Platform.runLater(() -> {
                mainController.prograssBarClientDownload.setProgress((i * 100 / server.count) / 100D);
                mainController.labelClientDownload.setText((i * 100 / server.count) + "%");
                mainController.prograssBarClientDownloadGlobal.setProgress(-1);
                mainController.labelClientDownloadGlobal.setText("Проверка файлов..");
            });
            // Получаем переменные сервера
            //String dirServerFile = split.split(":")[0];

            //String isUpdate = split.split(":")[2];
            // Идем к файлу который указал сервер
            File dirClientFile = new File((minecraftPatch + File.separator + file.path).replace("/", File.separator));
            serverList.add(dirClientFile);

            if (!dirClientFile.exists()) { // Если файла нет то качаем его

                FiltrQuality(dirClientFile, (file.url).replace(" ", "%20"));

            } else if (dirClientFile.exists() && file.update) { // Если файл есть и в параментра стоит обновляемый то сравниваем хеш
                if (!file.md5.equals(utils.getMD5(dirClientFile))) {
                    FiltrQuality(dirClientFile, (file.url).replace(" ", "%20"));
                }
            }
        }
    }

    /**
     * Удаление файлов не соответствующих оригинальному клиенту
     */
    private void RemoveExcess(String client) {
        utils.send("Remove Excess");

        // Рекурсивно выбераем папки в которых нужно удалить лишние файлы
        // В папках есть папки исключения, заполняются вторым атрибутом
        searchFile(new File(minecraftPatch + File.separator + client + File.separator + "mods"), "");
        searchFile(new File(minecraftPatch + File.separator + client + File.separator + "bin"), "");
        searchFile(new File(minecraftPatch + File.separator + client + File.separator + "libraries"), "");

        clientList.removeAll(serverList);
        for (File str : clientList) {
            delete(str);
        }

    }

    /**
     * Поиск файлов в клиенте
     *
     * @param folder дирректории которые нужно просканировать
     */
    private void searchFile(File folder, String recurs) {

        if (folder.getName() != recurs) {
            for (File file : folder.listFiles()) {
                if (file.isDirectory()) {
                    searchFile(file, recurs);
                } else if (file.getName().endsWith("")) {
                    //if ("jar".equals(getFileExtention(file))) {
                    clientList.add(file);
                    //}
                }
            }
        } else {
            for (File file : folder.listFiles()) {
                if (!file.isDirectory()) {
                    clientList.add(file);
                }
            }
        }

    }

    long getDirSize(File dir) {
        long size = 0;
        if (dir.isFile()) {
            size = dir.length();
        } else {
            File[] subFiles = dir.listFiles();
            for (File file : subFiles) {
                if (file.isFile()) {
                    size += file.length();
                } else {
                    size += getDirSize(file);
                }
            }
        }
        return size;
    }

    /**
     * Распаковка файлов
     *
     * @param f
     * @param file
     * @param dir
     */
    private void unzip(String file, String dir) {

        try {
            Platform.runLater(() -> {
                mainController.labelClientDownloadGlobal.setText("Распаковка " + new File(file).getName());

                mainController.prograssBarClientDownloadGlobal.setProgress(-1);
                mainController.labelItap.setText("Дождись распаковки файлов");
            });

            ZipFile zipFile = new ZipFile(new File(file));

            File jiniHomeParentDir = new File(dir);

            delete(jiniHomeParentDir);

            Enumeration files = zipFile.entries();
            File f = null;
            FileOutputStream fos = null;

            long nread = 0L;
            long length = new File(file).length();

            while (files.hasMoreElements()) {

                try {
                    ZipEntry entry = (ZipEntry) files.nextElement();
                    InputStream eis = zipFile.getInputStream(entry);
                    byte[] buffer = new byte[1024];
                    int bytesRead = 0;

                    f = new File(jiniHomeParentDir.getAbsolutePath() + File.separator + entry.getName());

                    if (entry.isDirectory()) {
                        f.mkdirs();
                        continue;
                    } else {
                        f.getParentFile().mkdirs();
                        f.createNewFile();
                    }

                    fos = new FileOutputStream(f);

                    while ((bytesRead = eis.read(buffer)) != -1) {
                        nread += bytesRead;
                        final long currentProgress = nread;

                        Platform.runLater(() -> {

                            mainController.labelClientDownload.setText(currentProgress * 100 / length + "%");
                            mainController.prograssBarClientDownload.setProgress(((int) ((double) currentProgress) / ((double) length)));
                        });

                        fos.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    new MessegeWindow("Ошибка распаковки файла!", "Обратись к администратору с этой ошибкой.\n" + e.getMessage(), "Продолжить");
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            // ignore
                        }
                    }
                }
            }

            /*   UnZipper uz = new UnZipper();
            uz.recursiveUnzip(new File(file), new File(dir));*/
        } catch (Exception e) {
            new MessegeWindow("Ошибка распаковки файла!", "Обратись к администратору с этой ошибкой.\n" + e.getMessage(), "Продолжить");
        }
        // delete(new File(file));

        // uz.removeAllZipFiles(new File(MainDir));
    }

    /**
     * Удаление файлов
     *
     * @param file
     */
    public static void delete(File file) {
        utils.sendErr("Delete file: " + file);
        try {
            if (!file.exists()) {
                return;
            }
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    delete(f);
                }
                file.delete();
            } else {
                file.delete();
            }
        } catch (Exception e) {
            new MessegeWindow("Ошибка удаления файла!", "Вряд ли ты в это виноват."
                    + "Обратись к администратору с этой ошибкой.\n" + e.getMessage(), "Продолжить");
        }
    }

}
