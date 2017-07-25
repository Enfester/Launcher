package ru.enfester.utils;

import com.google.gson.Gson;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javax.swing.JOptionPane;
import ru.enfester.MessegeWindow;
import ru.enfester.Session;
import ru.enfester.Vars;
import static ru.enfester.fx.Start.utils;
import static ru.enfester.fx.controllers.MainController.mainController;
import ru.enfester.logic.Game;

/**
 *
 * @author Антон
 */
public class EnCoreNetwork {

    /**
     * Загрузка файла
     *
     * @param url
     * @param f
     * @throws java.io.IOException
     */
    public static int Time = 0;
    public static int i = 0;
    public static int fCount = 0, tCount = 0;

    public static void download(URL url, final File fName, int fCount) throws IOException {
        if (EnCoreNetwork.fCount == 0) {
            EnCoreNetwork.fCount = fCount;
        }

        Time = 0;
        i = 0;
        fName.mkdirs();
        fName.delete();
        fName.createNewFile();
        URLConnection connection = url.openConnection();
        final double down = connection.getContentLength();

        double downm = fName.length();
        if (downm != down) {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            try (FileOutputStream fw = new FileOutputStream(fName)) {
                byte[] b = new byte[1024];
                int count = 0;
                javax.swing.Timer timer = new javax.swing.Timer(1000, new ActionListener() {
                    int j = 0;
                    int speed;

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        Time++;
                        speed = i - j;

                        final int currentProgress = (int) ((((double) i) / ((double) down)) * 100D);

                        // EnCoreNetwork.gCount / EnCoreNetwork.tCount;
                        Platform.runLater(() -> {
                            mainController.prograssBarClientDownload.setProgress(currentProgress / 100D);
                            mainController.labelClientDownload.setText("Загрузка файла: " + currentProgress + "%");

                            mainController.labelSizeDownload.setText("Загружено: " + utils.getReadableSize(i));
                            mainController.labelSpeed.setText("Скорость: " + utils.getReadableSpeed(speed));
                            mainController.labelSizeFile.setText("Размер файла: " + utils.getReadableSize((long) down));
                            mainController.labelTimeDownoad.setText("Времени прошло: " + utils.getTimeFormat(Time));
                            mainController.labelTimeUn.setText("Времени до конца загрузки: " + utils.getTimeFormat(EnCoreNetwork.fCount));
                        });
                        j = i;
                    }
                });

                timer.start();
                while ((count = bis.read(b)) != -1) {
                    fw.write(b, 0, count);
                    i += count;

                }
                timer.stop();
                tCount++;
                EnCoreNetwork.fCount--;

            }
        }
    }

    public static String getMacAddres() {
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            return sb.toString();
        } catch (UnknownHostException e) {
            return "";
        } catch (SocketException e) {
            return "";
        }
    }

    /**
     * Проверка подключения к интернету
     *
     * @return
     */
    public static boolean isOnline() {
        URL url;
        URLConnection urlconn;

        try {
            url = new URL("http://www.google.com");
        } catch (MalformedURLException e) {
            return false;
        }

        try {
            urlconn = url.openConnection();
            urlconn.connect();
            return true;
        } catch (IOException e1) {
            return false;
        }

    }

    /**
     * Запрос GET
     *
     * @param URL
     * @param param
     * @return
     */
    public static String excuteGET(String URL, String param) {
        try {
            URL localURL;
            localURL = new URL(URL + "?" + param);

            BufferedReader localBufferedReader = new BufferedReader(
                    new InputStreamReader(localURL.openStream()));
            StringBuilder sb = new StringBuilder();
            String result;
            while ((result = localBufferedReader.readLine()) != null) {
                sb.append(result + "\n");
            }

            return sb.toString();
        } catch (Exception e) {
            new Error(e);
            Logger.getLogger(Game.class.getName()).log(Level.WARNING, null, e);
            utils.sendErr(e + "");
            //   JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), "Ошибка: excuteGET", JOptionPane.ERROR_MESSAGE);
            return null;
        }

    }

    public static String ApiGet(String URL) {
        try {
            URL localURL;
            localURL = new URL(URL);

            BufferedReader localBufferedReader = new BufferedReader(
                    new InputStreamReader(localURL.openStream()));
            StringBuilder sb = new StringBuilder();
            String result;
            while ((result = localBufferedReader.readLine()) != null) {
                sb.append(result + "\n");
            }

            return sb.toString();
        } catch (Exception e) {
            new Error(e);
            Logger.getLogger(Game.class.getName()).log(Level.WARNING, null, e);
            utils.sendErr(e + "");
            //   JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), "Ошибка: excuteGET", JOptionPane.ERROR_MESSAGE);
            return null;
        }

    }
    
    public static void excutePost(String surl, String name, File binaryFile) {

        String CrLf = "\r\n";
        URLConnection conn = null;
        OutputStream os = null;
        InputStream is = null;

        try {
            URL url = new URL(surl);
            System.out.println("url:" + url);
            conn = url.openConnection();
            conn.setDoOutput(true);

            FileInputStream imgIs = new FileInputStream(binaryFile);

            byte[] imgData = new byte[imgIs.available()];
            imgIs.read(imgData);

            String message1 = "";
            message1 += "-----------------------------4664151417711" + CrLf;
            message1 += "Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + Vars.LOGIN + ".png\""
                    + CrLf;

            message1 += "Content-Type: image/png" + CrLf;
            message1 += CrLf;
            // the image is sent between the messages in the multipart message.
            String message2 = "";
            message2 += CrLf + "-----------------------------4664151417711--" + CrLf;

            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=---------------------------4664151417711");
            // might not need to specify the content-length when sending chunked
            // data.
            conn.setRequestProperty("Content-Length", String.valueOf((message1
                    .length() + message2.length() + imgData.length)));
           
            os = conn.getOutputStream();

            System.out.println(message1);
            os.write(message1.getBytes());

            // SEND THE IMAGE
            int index = 0;
            int size = 1024;
            do {
                System.out.println("write:" + index);
                if ((index + size) > imgData.length) {
                    size = imgData.length - index;
                }
                os.write(imgData, index, size);
                index += size;

            } while (index < imgData.length);
            System.out.println("written:" + index);

            System.out.println(message2);
            os.write(message2.getBytes());
            os.flush();

            System.out.println("open is");
            is = conn.getInputStream();
            String ret = "";
            char buff = 512;
            int len;
            byte[] data = new byte[buff];
            do {
                System.out.println("READ");
                len = is.read(data);

                if (len > 0) {
                    ret += new String(data, 0, len);
                    System.out.println(ret);
                }
            } while (len > 0);

            new MessegeWindow("Файл загружен", ret, "Продолжить");
        } catch (Exception e) {
            new MessegeWindow("Ошибка!", "Лаунчер не смог загрузить " + name + ".\nОшибка: " + e.getMessage(), "Повторить");
        } finally {
            System.out.println("Close connection");
            try {
                os.close();
            } catch (Exception e) {
            }
            try {
                is.close();
            } catch (Exception e) {
            }

        }

    }

    public static String excutePost(String postUrl, Session sess) {

        Gson gson = new Gson();

        String urlParameters = gson.toJson(sess);

        HttpURLConnection connection = null;
        try {
            URL url = new URL(postUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.connect();
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(urlParameters);
                wr.flush();
            }

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            return response.toString();

        } catch (Exception e) {
            new MessegeWindow("Ошибка runPOST", "Лаунчер не смог отправить запрос серверу!\nОшибка: " + e.getMessage(), "Повторить");

            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    /**
     * Запрос POST
     *
     * @param targetURL
     * @param urlParameters
     * @return
     */
    public static String excutePost(String targetURL, String urlParameters) {

        HttpURLConnection connection = null;
        try {
            URL url = new URL(targetURL);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.connect();
            //Certificate[] certs = connection.getServerCertificates();
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(urlParameters);
                wr.flush();
            }

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            String str1 = response.toString();
            return str1;
        } catch (Exception e) {
            new MessegeWindow("Ошибка runPOST", "Лаунчер не смог отправить запрос серверу!\nОшибка: " + e.getMessage(), "Повторить");
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}
