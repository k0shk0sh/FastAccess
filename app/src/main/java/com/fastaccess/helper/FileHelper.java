package com.fastaccess.helper;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by kosh20111 on 10/7/2015. CopyRights @ Innov8tif
 */
public class FileHelper {

    private static String folderName;

    public static void initFolderName(String fName) {
        folderName = Environment.getExternalStorageDirectory() + "/" + fName;
    }

    public static File folderName() {
        File file = new File(folderName);
        if (!file.exists())
            file.mkdir();
        return file;
    }

    private static String getPng(String path) {
        return path + ".png";
    }

    public static String generateFileName(String packageName) {
        return getPng(packageName);
    }

    public static File generateFile(String path) {
        File file = new File(folderName, ".nomedia");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                file.mkdir();
            }
        }
        return new File(folderName(), generateFileName(path));
    }
}
