package ru.enfester.utils;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Random;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.swing.JOptionPane;
import java.text.DecimalFormat;
import ru.enfester.Config;
import ru.enfester.fx.Start;
import static ru.enfester.fx.controllers.MainController.mainController;

public class Utils {

   public static long memorySizeTotal = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize() / 1000000;
   public static long memorySizeFree = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getFreePhysicalMemorySize() / 1000000;

    public int logNumber = 0;
    public FileWriter logWriter;

    private EnCoreBasic framework;

    public Utils() throws IOException {
        framework = new EnCoreBasic();

        //  send("Utls load...");
    }

    public String getReadableSize(long bytes) {
        if (bytes <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(bytes
                / Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }

    public String getReadableSpeed(long speed) {
        return getReadableSize(speed) + "/с";
    }

    public String getTimeFormat(int time) {
        int m = time / 60;
        int s = time % 60;
        String str = String.format("%d:%02d", m, s);
        return str;
    }

    public void PlaySound(String sound) {
        new MediaPlayer(new Media(getClass().getResource("/ru/enfester/fx/sounds/" + sound).toString())).play();
    }

    /**
     * Возвращает исходный код страницы по ссылке
     *
     * @param url
     * @throws URISyntaxException
     */
    public void seeURL(String url) throws URISyntaxException {
        Desktop desktop;
        try {
            desktop = Desktop.getDesktop();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex, "Ошибка: Desktop is not supported", JOptionPane.ERROR_MESSAGE);
            sendErr("Desktop is not supported.");
            return;
        }
        if (!desktop.isSupported(Desktop.Action.BROWSE)) {
            sendErr("BROWSE: Operation is not supported..");
            return;
        }
        try {
            desktop.browse(new URL(url).toURI());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex, "Ошибка: Failed to browse", JOptionPane.ERROR_MESSAGE);
            sendErr("Failed to browse. " + ex.getLocalizedMessage());
        }
    }

    /**
     * Осущетвляет переход по ссылке в браузер
     *
     * @param url
     */
    public void openURL(String url) {
        try {
            seeURL(url);
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(null, e, "Ошибка: Desktop is not supported", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Записать в логи информацию
     *
     * @param msg
     */
    public void send(String msg) {
        write(msg, "INFO");
    }

    /**
     * Записать в логи ошибку
     *
     * @param msg
     */
    public void sendErr(String msg) {
        write(msg, "ERROR");
        //JOptionPane.showMessageDialog(null, msg, "Ошибка: excuteGET", JOptionPane.ERROR_MESSAGE);
    }

    private void write(String msg, String type) {
  
        framework.log(type, logNumber, msg);

        logNumber++;
    }

    public boolean toBoolean(String str) {
       return str.equals("true");
    }

    public int toInt(String str) {
        return Integer.parseInt(str);
    }

    /**
     * Шифровка пароля
     *
     * @param mode
     * @param passbox
     * @return
     * @throws Exception
     */
    public Cipher getCipher(int mode, String passbox) throws Exception {
        Random random = new Random(43287234L);
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 5);

        SecretKey pbeKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(passbox.toCharArray()));
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        cipher.init(mode, pbeKey, pbeParamSpec);
        return cipher;
    }

    /**
     * MD5 файла File
     *
     * @param f
     * @return
     */
    public String getMD5(File f) {
        try {
            return calculateHash(MessageDigest.getInstance("MD5"), f.toString());
        } catch (Exception e) {

            return "";
        }
    }

    /**
     * MD5 Строки String
     *
     * @param f
     * @return
     */
    public String getMD5(String f) {
          MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(f.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            // тут можно обработать ошибку
            // возникает она если в передаваемый алгоритм в getInstance(,,,) не существует
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);

        while (md5Hex.length() < 32) {
            md5Hex = "0" + md5Hex;
        }

        return md5Hex;
    }

    public int[] getOnline(String ip, String port) throws Exception {
        Socket var2 = null;
        DataInputStream var3 = null;
        DataOutputStream var4 = null;
        try {
            var2 = new Socket();
            var2.setSoTimeout(3000);
            var2.setTcpNoDelay(true);
            var2.setTrafficClass(18);
            var2.connect(new InetSocketAddress(ip, Integer.parseInt(port)), 3000);
            var3 = new DataInputStream(var2.getInputStream());
            var4 = new DataOutputStream(var2.getOutputStream());
            var4.write(254);
            var4.write(1);
            if (var3.read() != 255) {
                throw new IOException("Bad message");
            }
            String var5 = readString(var3, 256);
            char[] var6 = var5.toCharArray();
            var5 = new String(var6);
            String[] var26;
            if (var5.startsWith("\u00a7") && var5.length() > 1) {
                var26 = var5.substring(1).split("\u0000");
                //System.out.println(var26[0]);
                // System.out.println("MOTD: " + var26[3]);
                //System.out.println("PROTOCOL: " + var26[1]);
                //System.out.println("Game version: " + var26[2]);
                //var8 = Integer.parseInt(var26[4]);
                //var9 = Integer.parseInt(var26[5]);
                //System.out.println(var26[4] + "/" + var26[5]);
                return new int[]{Integer.parseInt(var26[4]), Integer.parseInt(var26[5])};
            } else {
                var26 = var5.split("\u00a7");
                var5 = var26[0];
                try {
                    return new int[]{Integer.parseInt(var26[1]), Integer.parseInt(var26[2])};
                } catch (Exception e) {
                    return new int[]{0, 0};
                }
            }
        } finally {
            try {
                if (var3 != null) {
                    var3.close();
                }
            } catch (Throwable e) {
            }
            try {
                if (var4 != null) {
                    var4.close();
                }
            } catch (Throwable e) {
            }
            try {
                if (var2 != null) {
                    var2.close();
                }
            } catch (Throwable e) {
            }
        }
    }

    /**
     * Вроде используется, для онлайна на сервере
     *
     * @param ip
     * @param port
     * @return
     * @throws Exception
     */
    public String readString(DataInputStream par0DataInputStream, int par1) throws IOException {
        short var2 = par0DataInputStream.readShort();

        if (var2 > par1) {
            throw new IOException(
                    "Received string length longer than maximum allowed ("
                    + var2 + " > " + par1 + ")");
        } else if (var2 < 0) {
            throw new IOException(
                    "Received string length is less than zero! Weird string!");
        } else {
            StringBuilder var3 = new StringBuilder();

            for (int var4 = 0; var4 < var2; ++var4) {
                var3.append(par0DataInputStream.readChar());
            }

            return var3.toString();
        }
    }

    /**
     * Не используется (Разобрать)
     *
     * @param str
     * @return
     */
    public String getHash(String str) {
        MessageDigest m;
        try {
            m = MessageDigest.getInstance("MD5");
            m.reset();
            try {
                m.update(str.getBytes("utf-8"));
                String s2 = new BigInteger(1, m.digest()).toString(16);
                while (s2.length() < 32) {
                    s2 = "0" + s2;
                }
                return s2;
            } catch (UnsupportedEncodingException e) {
            }
        } catch (NoSuchAlgorithmException e) {
            JOptionPane.showMessageDialog(null, e, "Ошибка: getHash", JOptionPane.ERROR_MESSAGE);
        }
        return "";
    }

    public String calculateHash(MessageDigest algorithm, String fileName)
            throws Exception {
        FileInputStream fis = new FileInputStream(fileName);
        BufferedInputStream bis = new BufferedInputStream(fis);
        byte[] hash;
        try (DigestInputStream dis = new DigestInputStream(bis, algorithm)) {
            while (dis.read() != -1)
                ;
            hash = algorithm.digest();
        }
        return byteArray2Hex(hash);
    }

    public String byteArray2Hex(byte[] hash) {
        @SuppressWarnings("resource")
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    /**
     * Возвращает поуть до appData Windows, HomeDir Linux
     *
     * @param applicationName
     * @return
     */
    public File getMcDir(String applicationName) {
        String userHome = System.getProperty("user.home", ".");
        File workingDirectory;
        switch (getPlatform().ordinal()) {
            case 0:
            case 1:
                workingDirectory = new File(userHome, applicationName + "/");
                break;
            case 2:
                String applicationData = System.getenv("APPDATA");
                if (applicationData != null) {
                    workingDirectory = new File(applicationData,
                            applicationName + "/");
                } else {
                    workingDirectory = new File(userHome, applicationName + "/");
                }
                break;
            case 3:
                workingDirectory = new File(userHome,
                        "Library" + File.separator + "Application Support" + File.separator + applicationName);
                break;
            default:
                workingDirectory = new File(userHome, applicationName + "/");
        }
        workingDirectory.mkdirs();
        if ((!workingDirectory.exists()) && (!workingDirectory.mkdirs())) {
            throw new RuntimeException(
                    "The working directory could not be created: "
                    + workingDirectory);
        }
        return workingDirectory;

        /*
         String home = System.getProperty("user.home", "");
         String path = Config.pathconst;
         String appData = System.getenv(Config.basedir);
         switch (getPlatform()) {
         case 1:
         return new File(System.getProperty("user.home", ""), path);
         case 2:
         return new File(appData, path);
         case 3:
         return new File(home, "Library/Application Support/" + path);
         default:
         return new File(appData, path);
         }*/
    }

    public boolean fileUploadToServer(String name) {
        try {
  
            send("Select file skin");
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выбери изображение скина");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PNG", "*.png")
            );
            
            File binaryFile = fileChooser.showOpenDialog(Start.stage);
            
            if (binaryFile.isFile()) {
                EnCoreNetwork.excutePost(Config.SystemDir + "index.php", name, binaryFile);         
                return true;                
            } else {           
                return false;
            }
        } catch (Exception e) {
               return false;
        }

    }

    /**
     * Возвращает использумую платформу: windows, macos, solaris, linux
     *
     * @return
     */
    public OS getPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return OS.windows;
        }
        if (osName.contains("mac")) {
            return OS.macos;
        }
        if (osName.contains("solaris")) {
            return OS.solaris;
        }
        if (osName.contains("sunos")) {
            return OS.solaris;
        }
        if (osName.contains("linux")) {
            return OS.linux;
        }
        if (osName.contains("unix")) {
            return OS.linux;
        }
        return OS.unknown;
    }

    public String getPlatformString() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return "windows";
        }
        if (osName.contains("mac")) {
            return "osx";
        }
        return "linux";
    }

    public String getSerialNumber() {
        switch (getPlatform().ordinal()) {
            case 0:
                return SnMashine.getSerialNumberLinux();
            case 1:
                return SnMashine.getSerialNumberLinux();
            case 2:
                return SnMashine.getSerialNumberWin();
            default:
                return SnMashine.getSerialNumberMac();
        }
    }

    public enum OS {

        linux, solaris, windows, macos, unknown;
    }

    public File urltofile(URL url) {
        try {
            return new File(url.toURI());
        } catch (URISyntaxException var2) {
            return new File(url.getPath().replace("file:/", "").replace("file:", ""));
        }
    }

    public String hash(URL url) {
        if (url == null) {
            return "h";
        } else if (urltofile(url).isDirectory()) {
            return "d";
        } else {
            InputStream IS = null;
            DigestInputStream DI = null;
            BufferedInputStream BS = null;
            Formatter F = null;

            try {
                MessageDigest MD = MessageDigest.getInstance("MD5");
                IS = url.openStream();
                BS = new BufferedInputStream(IS);
                DI = new DigestInputStream(BS, MD);

                while (DI.read() != -1) {
                }

                byte[] Md = MD.digest();
                F = new Formatter();
                byte[] Mi = Md;
                int I = Md.length;

                for (int i = 0; i < I; ++i) {
                    byte Bi = Mi[i];
                    F.format("%02x", new Object[]{Byte.valueOf(Bi)});
                }

                String str = F.toString();
                return str;
            } catch (Exception e) {
            } finally {
                try {
                    IS.close();
                    IS = null;
                } catch (Exception e) {
                }

                try {
                    DI.close();
                    DI = null;
                } catch (Exception e) {
                }

                try {
                    BS.close();
                    BS = null;
                } catch (Exception e) {
                }

                try {
                    F.close();
                    F = null;
                } catch (Exception e) {
                }

            }

            return "h";
        }
    }
}
