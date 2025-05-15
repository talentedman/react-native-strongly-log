package com.stronglylog.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private static final String TAG = "FileUtil";

    private final static String FILE_EXTENSION_SEPARATOR = ".";


    /**
     * 写文件
     * @param f         文件路径
     * @param data      字节数据
     * @param append    是否尾追加
     * @return   是否成功
     */
    public static boolean writeFile(File f, byte[] data, boolean append) {
        if (f == null) {
            return false;
        }
        if (!f.exists()) {
            if (!makeDirs(f)) {
                Log.e(TAG, "make dirs fail : " + f.getAbsolutePath());
                return false;
            }
            try {
                if (!f.createNewFile()){
                    return false;
                }
            } catch (Exception e) {
                Log.e(TAG,"create file error : " + f.getAbsolutePath() + "  exception : " + e);
                return false;
            }
            append = false;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f, append);
            fos.write(data);
            fos.flush();
            return true;
        } catch (Exception e) {
            Log.e(TAG,"write file error : " + f.getAbsolutePath() + "  exception : " + e);
        } finally {
            //关闭文件流
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    Log.e(TAG,"close fos error : " + f.getAbsolutePath() + "  exception : " + e);
                }
            }
        }

        return false;
    }

    /**
     * 写文件
     * @param file      文件对象
     * @param stream    输入流
     * @param append    是否尾追加
     * @return  是否成功
     */
    public static boolean writeFile(File file, InputStream stream, boolean append) {
        if (!file.exists()) {
            if (!makeDirs(file)) {
                Log.e(TAG, "make dirs fail : " + file.getAbsolutePath());
                return false;
            }
            try {
                if (!file.createNewFile()){
                    return false;
                }
            } catch (Exception e) {
                Log.e(TAG,"create file error : " + file.getAbsolutePath() + "  exception : " + e);
                return false;
            }
            append = false;
        }

        OutputStream fos = null;
        try {
            fos = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length;
            while ((length = stream.read(data)) != -1) {
                fos.write(data, 0, length);
            }
            fos.flush();
            return true;
        } catch (Exception e) {
            Log.e(TAG,"write file error : " + file.getAbsolutePath() + "  exception : " + e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    Log.e(TAG,"close fos error : " + file.getAbsolutePath() + "  exception : " + e);
                }
            }
        }
        return false;
    }

    /**
     * 写文件
     * @param file          文件路径
     * @param s             保存的字符串
     * @param charsetName   字符集
     * @param append        是否尾追加
     * @return  是否成功
     */
    public static boolean writeFile(File file, String s, String charsetName, boolean append) {
        if (file == null) {
            return false;
        }
        if (TextUtils.isEmpty(s)) {
            return false;
        }
        byte[] bytes;
        try {
            bytes = s.getBytes(charsetName);
        } catch (Exception e) {
            Log.e(TAG,"transform bytes error : " + e);
            return false;
        }

        return writeFile(file, bytes, append);
    }

    /**
     * 写文件
     * @param file      文件路径
     * @param s         字符串
     * @param append    是否尾追加
     * @return  是否成功
     */
    public static boolean writeFileUTF8(File file, String s, boolean append) {
        return writeFile(file, s, "UTF-8", append);
    }

    /**
     * 根据字符集读取文件字符串
     * @param filePath      文件路径
     * @param charsetName   字符集
     * @return  字符串对象
     */
    public static String readFile(File filePath, String charsetName) {
        byte[] bytes = readFile(filePath);
        if (bytes == null) {
            return null;
        }
        try {
            return new String(bytes, charsetName);
        } catch (Exception e) {
            Log.e(TAG,"new String error : " + e);
        }
        return null;
    }

    /**
     * 根据字符集读取文件字符串
     * @param filePath      文件路径
     * @return  字符串对象
     */
    public static String readFileUTF8(File filePath) {
        return readFile(filePath, "UTF-8");
    }

    /**
     * 读取文件
     * @param filePath  文件对象
     * @return  字节数据
     */
    public static byte[] readFile(File filePath) {
        if (filePath == null) {
            return null;
        }
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(filePath);
            return readFile(stream);
        } catch (Exception e) {
            Log.e(TAG,"read file error : " + filePath.getAbsolutePath() + "  exception : " + e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                    Log.e(TAG,"close fos error : " + filePath.getAbsolutePath() + "  exception : " + e);
                }
            }
        }
        return null;
    }

    /**
     * 读取 包里面的 assets 文件
     * @param  context
     * @param filePath  文件
     * @return  字节数据
     */
    public static byte[] readAssetsFile(Context context, String filePath) {
        if (filePath == null) {
            return null;
        }
        InputStream stream = null;
        try {
            stream = context.getAssets().open(filePath);
            return readFile(stream);
        } catch (Exception e) {
            Log.e(TAG,"read file error : " + filePath + "  exception : " + e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                    Log.e(TAG,"close fos error : " + filePath + "  exception : " + e);
                }
            }
        }
        return null;
    }

    /**
     * 根据字符集读取文件字符串
     * @param context
     * @param filePath      文件路径
     * @param charsetName   字符集
     * @return  字符串对象
     */
    public static String readAssetsFile(Context context, String filePath, String charsetName) {
        byte[] bytes = readAssetsFile(context, filePath);
        if (bytes == null) {
            return null;
        }
        try {
            return new String(bytes, charsetName);
        } catch (Exception e) {
            Log.e(TAG,"new String error : " + e);
        }
        return null;
    }

    /**
     * 根据字符集读取文件字符串
     * @param context
     * @param filePath      文件路径
     * @return  字符串对象
     */
    public static String readAssetsFileUTF8(Context context, String filePath) {
        return readAssetsFile(context, filePath, "UTF-8");
    }

    /**
     * 读取文件
     * @param stream  输入流   由调用方关闭
     * @return  字节数据
     */
    public static byte[] readFile(InputStream stream) {
        if (stream == null) {
            return null;
        }
        try {
            int destBvLen = stream.available();
            byte[] bytes = new byte[destBvLen];
            stream.read(bytes);
            return bytes;
        } catch (Exception e) {
            Log.e(TAG,"read file error" + e);
        }
        return null;
    }


    /**
     * 移动文件
     * @param sourceFilePath    源路径
     * @param destFilePath      目标路径
     * @return 是否成功
     */
    public static boolean moveFile(String sourceFilePath, String destFilePath) {
        if (TextUtils.isEmpty(sourceFilePath) || TextUtils.isEmpty(destFilePath)) {
            return false;
        }
        return moveFile(new File(sourceFilePath), new File(destFilePath));
    }

    /**
     * 移动文件
     * @param srcFile   源路径
     * @param destFile  目标路径
     * @return  是否成功
     */
    public static boolean moveFile(File srcFile, File destFile) {
        if (srcFile == null || destFile == null) {
            return false;
        }
        if (srcFile.renameTo(destFile)) {
            return true;
        }

        if (copyFile(srcFile, destFile)) {
            deleteFile(srcFile.getAbsolutePath());
            return true;
        }
        return false;
    }

    /**
     * 从资源文件夹拷贝文件
     * @param sourceFilePath    源路径(资源文件夹)
     * @param destFilePath      目标路径
     * @return  是否成功
     */
    private static boolean doCopyAssetFile(Context context, String sourceFilePath, File destFilePath) {
        InputStream stream = null;
        try {
            stream = context.getAssets().open(sourceFilePath);
            return writeFile(destFilePath, stream, false);
        } catch (Exception e) {
            Log.e(TAG,"read file error : " + sourceFilePath + "  exception : " + e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                    Log.e(TAG,"close fos error : " + sourceFilePath + "  exception : " + e);
                }
            }
        }
        return false;
    }

    /**
     * 从资源文件夹拷贝文件
     * @param sourceFilePath    源路径(资源文件夹)
     * @param destFilePath      目标路径
     * @return  是否成功
     */
    public static boolean copyAssetFile(Context context, String sourceFilePath, File destFilePath) {
        if (sourceFilePath == null || destFilePath == null) {
            return false;
        }
        return doCopyAssetFile(context, sourceFilePath, destFilePath);
    }

    /**
     * 从资源文件夹拷贝文件
     * @param assetPath         源路径(资源文件夹)
     * @param destFilePath      目标路径
     * @return  是否成功
     */
    private static boolean doCopyAssets(Context context, String assetPath, File destFilePath) {
        String[] fileNames = null;
        try {
            fileNames = context.getAssets().list(assetPath);
        } catch (Exception e) {
            Log.e(TAG, "list asset path error : " + assetPath + "  exception : " + e);
            return false;
        }

        if (fileNames == null || fileNames.length == 0) {
            return doCopyAssetFile(context, assetPath, destFilePath);
        }

        boolean r = true;
        for (String fileName : fileNames) {
            if (!doCopyAssets(context,assetPath + File.separator + fileName, new File(destFilePath, fileName))) {
                r = false;
            }
        }
        return r;
    }

    /**
     * 从资源文件夹拷贝文件
     * @param assetPath         源路径(资源文件夹)
     * @param destFilePath      目标路径
     * @return  是否成功
     */
    public static boolean copyAssets(Context context, String assetPath, File destFilePath) {
        if (destFilePath == null) {
            return false;
        }
        if (assetPath == null) {
            assetPath = "";
        }

        return doCopyAssets(context, assetPath, destFilePath);
    }

    /**
     * 拷贝文件
     * @param sourceFilePath    源路径
     * @param destFilePath      目标路径
     * @return  是否成功
     */
    public static boolean copyFile(File sourceFilePath, File destFilePath) {
        if (sourceFilePath == null || destFilePath == null) {
            return false;
        }
        if (sourceFilePath.equals(destFilePath)) {
            return true;
        }
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(sourceFilePath);
            return writeFile(destFilePath, inputStream, false);
        } catch (Exception e) {
            Log.e(TAG,"copy file error : " + sourceFilePath + "  exception : " + e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    Log.e(TAG,"close inputStream error : " + sourceFilePath + "  exception : " + e);
                }
            }
        }
        return false;
    }

    /**
     * 读取文件每行
     * @param filePath      文件路径
     * @param charsetName   字符集
     * @return  返回行链表
     */
    public static List<String> readFileLines(File filePath, String charsetName) {
        if (filePath == null || !filePath.isFile()) {
            return null;
        }
        List<String> fileContent = new ArrayList<String>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(filePath), charsetName
                    )
            );
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
            return fileContent;
        } catch (Exception e) {
            Log.e(TAG,"read file error : " + filePath + "  exception : " + e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    Log.e(TAG,"close fos error : " + filePath + "  exception : " + e);
                }
            }
        }

        return null;
    }

    /**
     * 获得文件名不包含扩展名
     * @param filePath      文件路径 或者 文件名
     * @return  不包含扩展名的文件名
     */
    public static String getFileNameWithoutExtension(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (filePosi == -1) {
            return (extenPosi == -1 ? filePath : filePath.substring(0, extenPosi));
        }
        if (extenPosi == -1) {
            return filePath.substring(filePosi + 1);
        }
        return (filePosi < extenPosi ? filePath.substring(filePosi + 1, extenPosi) : filePath.substring(filePosi + 1));
    }

    /**
     * 获得文件路径的文件名
     * @param filePath  文件路径
     * @return  文件名
     */
    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        File f = new File(filePath);
        return f.getName();
    }

    /**
     * 获得文件所在的文件夹
     * @param filePath      文件路径
     * @return  文件夹
     */
    public static String getFolderName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        File f = new File(filePath);
        return f.getParent();
    }

    /**
     * 获得文件的扩展名
     * @param filePath  文件路径 或者 文件名
     * @return  扩展名
     */
    public static String getFileExtension(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (extenPosi == -1) {
            return "";
        }
        return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
    }

    /**
     * 创建文件路径的父文件夹
     * @param filePath  文件路径
     * @return  是否成功
     */
    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (TextUtils.isEmpty(folderName)) {
            return false;
        }

        return makeFolder(new File(folderName));
    }
    /**
     * 创建文件路径的父文件夹
     * @param filePath  文件路径
     * @return  是否成功
     */
    public static boolean makeDirs(File filePath) {
        if (filePath == null) {
            return false;
        }

        return makeFolder(filePath.getParentFile());
    }

    /**
     * 创建文件夹路径
     * @param folder    文件路径
     * @return  是否成功
     */
    public static boolean makeFolder(File folder) {
        if (folder == null) return false;
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }

    /**
     * 文件是否存在
     * @param filePath  文件路径
     * @return  是否存在
     */
    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        return isFileExist(new File(filePath));
    }
    /**
     * 文件是否存在
     * @param filePath
     * @return  是否存在
     */
    public static boolean isFileExist(File filePath) {
        if (filePath == null) {
            return false;
        }

        return (filePath.exists() && filePath.isFile());
    }

    /**
     * 文件夹是否存在
     * @param directoryPath 文件夹路径
     * @return  是否存在
     */
    public static boolean isFolderExist(String directoryPath) {
        if (TextUtils.isEmpty(directoryPath)) {
            return false;
        }

        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }

    /**
     * 资源文件夹下的文件是否存在
     * @param context
     * @param filePath  文件路径
     * @return  是否存在
     */
    public static boolean isAssetsFileExist(Context context, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        InputStream stream = null;
        try {
            stream = context.getAssets().open(filePath);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除 文件 或者 文件夹
     * @param path  路径
     * @return  是否成功
     */
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }

        return deleteFile(new File(path));
    }

    /**
     * 删除 文件 或者 文件夹
     * @param file  路径
     * @return  是否成功
     */
    public static boolean deleteFile(File file) {
        if (file == null) {
            return true;
        }

        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        return file.delete();
    }

    /**
     * 获得文件大小
     * @param path  文件路径
     * @return  文件大小，单位：字节
     */
    public static long getFileSize(String path) {
        if (TextUtils.isEmpty(path)) {
            return -1;
        }

        File file = new File(path);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }



    /**
     * 获得json对象
     * @param bytes 字节数据
     * @return  json
     */
    private static JSONObject readFileToJSONObject(final byte[] bytes) {
        if (bytes == null) return null;

        try {
            return new JSONObject(new String(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 读取文件，序列化为json字符串
     * @param file  文件路径
     * @param key   某个键值
     * @return  json字符串
     */
    public static String readJsonFileData(final File file, final String key) {
        byte[] bytes = readFile(file);
        if (bytes == null) return null;

        JSONObject obj = readFileToJSONObject(bytes);
        if (obj == null) return null;

        try {
            return obj.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得json对象
     * @param file 文件路径
     * @return  json
     */
    public static JSONObject readJsonFileData2(final File file) {
        byte[] bytes = readFile(file);
        if (bytes == null) return null;

        return readFileToJSONObject(bytes);
    }

    /**
     * 获得json对象
     * @param file 文件路径
     * @return  json
     */
    public static JSONObject readAssetsJsonFileData2(Context context, final String file) {
        byte[] bytes = readAssetsFile(context, file);
        if (bytes == null) return null;

        return readFileToJSONObject(bytes);
    }

    /**
     * 读取json字符串
     * @param stream    输入流
     * @param key       键值
     * @return  json字符串
     */
    public static String readJsonFileData(final InputStream stream, final String key) {
        byte[] bytes = readFile(stream);
        if (bytes == null) return null;

        JSONObject obj = readFileToJSONObject(bytes);
        if (obj == null) return null;

        try {
            return obj.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    public static File getFileByPath(String filePath) {
        if (isSpace(filePath)) {
            return null;
        } else {
            File file = new File(filePath);
            if (file.getParentFile().exists()) {
                return file;
            } else {
                file.getParentFile().mkdirs();
                return file;
            }
        }
    }

    private static boolean isSpace(String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static File getExtStorageDir(Context context){
       /* File externalStorageDirectory;
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) && !Environment.isExternalStorageLegacy())
        {
            externalStorageDirectory= context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        }else{
            externalStorageDirectory = Environment.getExternalStorageDirectory();
        }*/

        return getExtPublishStorageDir(context);
    }

    public static File getExtPublishStorageDir(Context context){
//        File externalStorageDirectory;
//        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) && !Environment.isExternalStorageLegacy())
//        {
//            externalStorageDirectory= context.getExternalFilesDir(filePath);
//        }else{
//            externalStorageDirectory = Environment.getExternalStorageDirectory();
//        }
//
//        return externalStorageDirectory;
        return context.getFilesDir();
    }
}
