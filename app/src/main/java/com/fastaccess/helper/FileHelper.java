package com.fastaccess.helper;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kosh20111 on 10/7/2015. CopyRights @ Innov8tif
 */
public class FileHelper {

    private static String folderName;
    private String TAG = this.getClass().getSimpleName();

    public static void initFolderName(String fName) {
        folderName = Environment.getExternalStorageDirectory() + "/" + fName;
    }

    public static File folderName() {
        File file = new File(folderName);
        if (!file.exists())
            file.mkdir();
        return file;
    }

    public static String getBaseFolderName() {
        return folderName;
    }

    public static File getFile(String path) {
        return new File(path);
    }

    private static String getPng(String path) {
        return path + ".png";
    }

    public static boolean deleteFile(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                return file.delete();
            } else {
                file = new File(folderName(), path);
                if (file.exists()) {
                    return file.delete();
                }
            }
        }
        return false;
    }

    public static void deleteFile(List<String> paths) {
        for (String path : paths) {
            if (path != null) {
                File file = new File(folderName(), path);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
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

    private static void generateDefaultFile() {
        File file = new File(folderName);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static File generateZipFile(String name) {
        File file = new File(folderName, ".nomedia");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                file.mkdir();
            }
        }
        return new File(folderName(), name + ".zip");
    }

    public static File generateFolder(String name) {
        File file = new File(folderName);
        if (!file.exists()) {
            file.mkdir();
        }
        File folderName = new File(file, name);
        if (!folderName.exists()) {
            folderName.mkdirs();
        }
        return folderName;
    }

    public static String getCacheFile(Context context, String packageName) {
        return context.getCacheDir().getPath() + "/" + generateFileName(packageName);
    }

    public static boolean exists(String path) {
        return getFile(path).exists();
    }

    public static List<File> getFiles(File dir) {
        List<File> files = new ArrayList<>();
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (File aListFile : listFile) {
                if (aListFile.isDirectory()) {
                    getFiles(aListFile);
                } else {
                    if (aListFile.getName().endsWith(".png") || aListFile.getName().endsWith(".jpg")
                            || aListFile.getName().endsWith(".jpeg") || aListFile.getName().endsWith(".gif")) {
                        files.add(aListFile);
                    }
                }

            }
        }
        return files;
    }

    public static String getInternalDirectoryPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getSDcardDirectoryPath() {
        return System.getenv("SECONDARY_STORAGE");
    }

    public static String getMimeType(String file) {
        return MimeTypeMap.getFileExtensionFromUrl(file);
    }

    public static String extension(String file) {
        return MimeTypeMap.getFileExtensionFromUrl(file);
    }

}
