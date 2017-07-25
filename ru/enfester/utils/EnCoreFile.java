package ru.enfester.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Formatter;

public class EnCoreFile {

    private static File workDir = null;

    public File getWorkingDirectory() {

        return workDir;
    }

    public void recursiveDelete(File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            if (file.list().length == 0) {
                file.delete();
            } else {
                String files[] = file.list();

                for (String temp : files) {
                    File fileDelete = new File(file, temp);
                    recursiveDelete(fileDelete);
                }
                if (file.list().length == 0) {
                    file.delete();
                }
            }
        } else {
            file.delete();
        }
    }

    public String getMD5(File f) {
        try {
            return calculateHash(
                    MessageDigest.getInstance("MD5"), f.toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            return "";
        }
    }

    public String calculateHash(MessageDigest algorithm, String fileName)
            throws Exception {
        FileInputStream fis = new FileInputStream(fileName);
        BufferedInputStream bis = new BufferedInputStream(fis);
        DigestInputStream dis = new DigestInputStream(bis, algorithm);

        while (dis.read() != -1)
			;
        byte[] hash = algorithm.digest();
        dis.close();
        return byteArray2Hex(hash);
    }

    public String byteArray2Hex(byte[] hash) {
        @SuppressWarnings("resource")
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        // formatter.close();
        return formatter.toString();
    }

    public void writeString(String text, File file) {
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            fw.write(text);
            fw.close();
        } catch (IOException ignored) {
        }

    }

    public String readFileAsString(String filePath, String defaultVar) {
        try {
            StringBuffer fileData = new StringBuffer(1000);
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            reader.close();
            return fileData.toString();
        } catch (Exception e) {
            return defaultVar;
        }
    }

}
