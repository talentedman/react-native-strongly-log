package com.stronglylog.utils;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * zip工具类
 */

public class ZipUtil {

    /**
     * 解压zip
     * @param zipFile       压缩文件路径
     * @param targetDir     解压到的文件夹
     */
    public static boolean unzip(String zipFile, String targetDir) {
        final int BUFFER = 4096; //这里缓冲区我们使用4KB，

        ZipInputStream zis = null;
        try {
            BufferedOutputStream dest = null; //缓冲输出流
            zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
            ZipEntry entry; //每个zip条目的实例

            byte[] data = new byte[BUFFER];

            boolean bHasEntry = false;

            while ((entry = zis.getNextEntry()) != null) {
                bHasEntry = true;

                try {
//                    Log.i("Unzip: ", "=" + entry);
                    if (entry.getName().contains("..")) {
                        continue;
                    }

                    File entryFile = new File(targetDir, entry.getName());
                    File entryDir = entryFile.getParentFile();
                    if (!entryDir.exists()) {
                        entryDir.mkdirs();
                    }

                    int count;
                    dest = new BufferedOutputStream(new FileOutputStream(entryFile), BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    if (null != dest) {
                        try {
                            dest.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dest = null;
                    }
                }
            }

            return bHasEntry;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (null != zis) {
                try {
                    zis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                zis = null;
            }
        }

    }

    /**
     * 压缩zip
     * @param src       源文件或者目录
     * @param dest      压缩文件路径
     * @return  是否成功
     */
    public static boolean zip(final String src, final String dest) {
        //提供了一个数据项压缩成一个ZIP归档输出流
        ZipOutputStream out = null;
        try {

            byte[] buffer = new byte[4096];
            File outFile = new File(dest);//压缩文件路径
            File outFileFolder = outFile.getParentFile();
            if (!outFileFolder.exists()) {
                outFileFolder.mkdirs();
            }
            File fileOrDirectory = new File(src);//源文件或者目录
            out = new ZipOutputStream(new FileOutputStream(outFile));
            //如果此文件是一个文件，否则为false。
            if (fileOrDirectory.isFile()) {
                return zipFileOrDirectory(buffer, out, fileOrDirectory, "");
            } else {
                //返回一个文件或空阵列。
                File[] entries = fileOrDirectory.listFiles();
                for (File entry : entries) {
                    // 递归压缩，更新curPaths
                    if (!zipFileOrDirectory(buffer, out, entry, "")) {
                        return false;
                    }
                }

            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            //关闭输出流
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean zipFileOrDirectory(byte[] buffer, ZipOutputStream out, File fileOrDirectory, String curPath) {
        //从文件中读取字节的输入流
        FileInputStream in = null;
        try {
            //如果此文件是一个目录，否则返回false。
            if (!fileOrDirectory.isDirectory()) {
                // 压缩文件
                int bytes_read;
                in = new FileInputStream(fileOrDirectory);
                //实例代表一个条目内的ZIP归档
                ZipEntry entry = new ZipEntry(curPath
                        + fileOrDirectory.getName());
                //条目的信息写入底层流
                out.putNextEntry(entry);
                while ((bytes_read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytes_read);
                }
                out.closeEntry();
            } else {
                // 压缩目录
                File[] entries = fileOrDirectory.listFiles();
                for (File entry : entries) {
                    // 递归压缩，更新curPaths
                    if (!zipFileOrDirectory(buffer, out, entry, curPath
                            + fileOrDirectory.getName() + "/")) {
                        return false;
                    }
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
