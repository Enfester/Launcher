package ru.enfester.utils;

import ru.enfester.Config;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import static ru.enfester.fx.Start.utils;

public class EnCoreBasic {

    private final static DateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
    private static FileWriter logWriter;
    private final static String sep = System.getProperty("line.separator");
    private static String currentTime;
    private static Date now;
    private static EnCoreFile filemodule;
    private EnCoreNetwork netmodule;
    private static EnCoreBasic _instance;

    public EnCoreBasic() {
        _instance = this;
        filemodule = new EnCoreFile();
        netmodule = new EnCoreNetwork();
    }

    public static EnCoreBasic getFramework() {
        return _instance;
    }

    public void log(String type, int prefix, String msg) {
        if (logWriter == null) {
            setupLogger(new File(utils.getMcDir(Config.ClentDir) + File.separator + Config.LogName));
        }
        try {
            now = new Date();
            currentTime = df.format(now);

            String format = "[" + prefix + "][" + currentTime + "][" + type + "] " + msg + sep;

            logWriter.write(format);
            System.out.print(format);
            logWriter.flush();
        } catch (Exception e) {
            System.out.print(stack2string(e));
        }

    }

    public void setupLogger(File logFile) {
        try {
            if (!logFile.exists()) {
                //logFile.mkdirs();     
                logFile.createNewFile();
            }
            if (getFileModule().readFileAsString(logFile.toString(), "").split("\n").length >= 1000) {
                logFile.delete();
                logFile.createNewFile();
            }
            logWriter = new FileWriter(logFile, true);
        } catch (Exception e) {
            System.out.println("[enCore] failed to initialize logger...");
            System.out.println(stack2string(e));
        }
    }

    public String stack2string(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "------\r\n" + sw.toString() + "------\r\n";
        } catch (Exception e2) {
            return "bad stack2string";
        }
    }

    public static URL toURL(String url) {
        try {
            return new URL(url.replace(" ", "%20"));
        } catch (Exception e) {
            // log("INFO","failed to transform String to URL.. Sorry..", this.getClass());
            return null;
        }
    }

    public static URI toURI(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            // log("INFO","failed to transform URL to URI.. Sorry..", this.getClass());
            return null;
        }
    }

    public EnCoreNetwork getNetmodule() {
        return netmodule;
    }

    public void setNetmodule(EnCoreNetwork netmodule) {
        this.netmodule = netmodule;
    }

    public static EnCoreFile getFileModule() {
        return filemodule;
    }

    public void setFilemodule(EnCoreFile intmodule) {
        this.filemodule = intmodule;
    }

    public static EnCoreBasic getInstance() {
        return _instance;
    }

    public static void setInstance(EnCoreBasic _instance) {
        EnCoreBasic._instance = _instance;
    }

    public int randomInRange(int min, int max) {
        Random r = new Random(System.nanoTime());
        return r.nextInt(max - min + 1) + min;

    }

    public static String buildRandomSession() {
        String s = "";
        for (int i = 0; i < 22; i++) {
            Random r = new Random(System.currentTimeMillis()
                    + System.nanoTime() * System.currentTimeMillis() + i
                    + Math.round(Math.random()));
            s = s + r.nextInt(9);
            r = null;
        }
        return s;
    }

    public String filterString(String s) {
        return clear(new String[]{"!", "_", ",", ".", "?", ";"}, s.replaceAll("[^\\d.]", ""));
    }

    public String clear(String[] symbols, String toclear) {
        for (String s : symbols) {
            toclear = toclear.replace(s, "");
        }
        if (toclear.equals("")) {
            return "1";
        }
        return toclear;
    }

    public boolean strcont(String name, String[] strings) {
        for (String s : strings) {
            if (name.contains(s)) {
                return true;
            }
        }
        return false;
    }

}
