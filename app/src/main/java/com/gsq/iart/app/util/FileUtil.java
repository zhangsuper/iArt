package com.gsq.iart.app.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.gsq.iart.app.App;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * 文件操作工具包
 */
public class FileUtil {

    /**
     * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
     *
     * @param context
     */
    public static void write(Context context, String fileName, String content) {
        if (content == null)
            content = "";

        try {
            FileOutputStream fos = context.openFileOutput(fileName,
                    Context.MODE_PRIVATE);
            fos.write(content.getBytes());

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从Assets中读取图片
     */
    public static Bitmap getImageFromAssetsFile(String fileName, Context context) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 读取文本文件
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String read(Context context, String fileName) {
        try {
            FileInputStream in = context.openFileInput(fileName);
            return readInStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String read(String path) {
        try {
            FileInputStream in = new FileInputStream(path);
            return readInStream(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readInStream(FileInputStream inStream) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length = -1;
            while ((length = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }

            outStream.close();
            inStream.close();
            return outStream.toString();
        } catch (IOException e) {
            Log.i("FileTest", e.getMessage());
        }
        return null;
    }

    public static byte[] readByteInStream(String path) {
        try {
            FileInputStream inStream = new FileInputStream(path);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length = -1;
            while ((length = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }

            outStream.close();
            inStream.close();
            return buffer;
        } catch (IOException e) {
            Log.i("FileTest", e.getMessage());
        }
        return null;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    public static File createFile(String folderPath, String fileName) {
        File destDir = new File(folderPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return new File(folderPath, fileName + fileName);
    }

    /**
     * 向手机写图片
     *
     * @param buffer
     * @param folder
     * @param fileName
     * @return
     */
    public static boolean writeFile(byte[] buffer, String folder,
                                    String fileName) {
        boolean writeSucc = false;

        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

        String folderPath = "";
        if (sdCardExist) {
            folderPath = Environment.getExternalStorageDirectory()
                    + File.separator + folder + File.separator;
        } else {
            writeSucc = false;
        }

        File fileDir = new File(folderPath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        File file = new File(folderPath + fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(buffer);
            writeSucc = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return writeSucc;
    }

    /**
     * 根据文件绝对路径获取文件名
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return "";
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * 根据文件的绝对路径获取文件名但不包含扩展名
     *
     * @param filePath
     * @return
     */
    public static String getFileNameNoFormat(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        int point = filePath.lastIndexOf('.');
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1,
                point);
    }

    /**
     * 重命名文件
     *
     * @param oldPath 原来的文件地址
     * @param newPath 新的文件地址
     */
    public static void renameFile(String oldPath, String newPath) {
        File oleFile = new File(oldPath);
        File newFile = new File(newPath);
        //执行重命名
        oleFile.renameTo(newFile);
    }


    /**
     * 遍历文件夹下的文件
     *
     * @param file 地址
     */
    public static List<File> getFile(File file) {
        List<File> list = new ArrayList<>();
        File[] fileArray = file.listFiles();
        if (fileArray == null) {
            return null;
        } else {
            for (File f : fileArray) {
                if (f.isFile()) {
                    list.add(0, f);
                } else {
                    getFile(f);
                }
            }
        }
        return list;
    }

    /**
     * 获取目录下所有文件(按时间排序)
     *
     * @param file
     * @return
     */
    public static List<File> listFileSortByModifyTime(File file) {
        List<File> list = getFile(file);
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return 1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
        }
        return list;
    }

    /**
     * 删除文件
     *
     * @param filePath 文件地址
     * @return
     */
    public static boolean deleteFiles(String filePath) {
        List<File> files = getFile(new File(filePath));
        if (files.size() != 0) {
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);

                /**  如果是文件则删除  如果都删除可不必判断  */
                if (file.isFile()) {
                    file.delete();
                }

            }
        }
        return true;
    }


    /**
     * 向文件中添加内容
     *
     * @param strcontent 内容
     * @param filePath   地址
     * @param fileName   文件名
     */
    public static void writeToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写

        File subfile = new File(strFilePath);


        RandomAccessFile raf = null;
        try {
            /**   构造函数 第二个是读写方式    */
            raf = new RandomAccessFile(subfile, "rw");
            /**  将记录指针移动到该文件的最后  */
            raf.seek(subfile.length());
            /** 向文件末尾追加内容  */
            raf.write(strcontent.getBytes());

            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 修改文件内容（覆盖或者添加）
     *
     * @param path    文件地址
     * @param content 覆盖内容
     * @param append  指定了写入的方式，是覆盖写还是追加写(true=追加)(false=覆盖)
     */
    public static void modifyFile(String path, String content, boolean append) {
        try {
            FileWriter fileWriter = new FileWriter(path, append);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.append(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件内容
     *
     * @param filePath 地址
     * @param filename 名称
     * @return 返回内容
     */
    public static String getString(String filePath, String filename) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(filePath + filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    /**
     * 复制文件
     *
     * @param fromFile 要复制的文件目录
     * @param toFile   要粘贴的文件目录
     * @return 是否复制成功
     */
    public static boolean copy(String fromFile, String toFile) {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        //如同判断SD卡是否存在或者文件是否存在
        //如果不存在则 return出去
        if (!root.exists()) {
            return false;
        }
        //如果存在则获取当前目录下的全部文件 填充数组
        currentFiles = root.listFiles();

        //目标目录
        File targetDir = new File(toFile);
        //创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        //遍历要复制该目录下的全部文件
        for (int i = 0; i < currentFiles.length; i++) {
            if (currentFiles[i].isDirectory())//如果当前项为子目录 进行递归
            {
                copy(currentFiles[i].getPath() + "/", toFile + currentFiles[i].getName() + "/");

            } else//如果当前项为文件则进行文件拷贝
            {
                CopySdcardFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());
            }
        }
        return true;
    }


    //文件拷贝
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    public static boolean CopySdcardFile(String fromFile, String toFile) {

        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return true;

        } catch (Exception ex) {
            return false;
        }
    }


    /**
     * 获取文件扩展名
     *
     * @param fileName
     * @return
     */
    public static String getFileFormat(String fileName) {
        if (TextUtils.isEmpty(fileName))
            return "";

        int point = fileName.lastIndexOf('.');
        return fileName.substring(point + 1);
    }

    /**
     * 获取文件大小
     *
     * @param filePath
     * @return
     */
    public static long getFileSize(String filePath) {
        long size = 0;

        File file = new File(filePath);
        if (file != null && file.exists()) {
            size = file.length();
        }
        return size;
    }

    /**
     * 获取文件大小
     *
     * @param size 字节
     * @return
     */
    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";
        java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
        float temp = (float) size / 1024;
        if (temp >= 1024) {
            return df.format(temp / 1024) + "M";
        } else {
            return df.format(temp) + "K";
        }
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 获取目录文件大小
     *
     * @param dir
     * @return
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

    /**
     * 获取目录文件个数
     *
     * @return
     */
    public long getFileList(File dir) {
        long count = 0;
        File[] files = dir.listFiles();
        count = files.length;
        for (File file : files) {
            if (file.isDirectory()) {
                count = count + getFileList(file);// 递归
                count--;
            }
        }
        return count;
    }

    public static byte[] toBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1) {
            out.write(ch);
        }
        byte buffer[] = out.toByteArray();
        out.close();
        return buffer;
    }

    /**
     * 检查文件是否存在
     *
     * @param name
     * @return
     */
    public static boolean checkFileExists(String name) {
        boolean status;
        if (!name.equals("")) {
            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + name);
            status = newPath.exists();
        } else {
            status = false;
        }
        return status;

    }

    public static boolean deleteDir(String path) {
        boolean success = true;
        File file = new File(path);
        if (file.exists()) {
            File[] list = file.listFiles();
            if (list != null) {
                int len = list.length;
                for (int i = 0; i < len; ++i) {
                    if (list[i].isDirectory()) {
                        deleteDir(list[i].getPath());
                    } else {
                        boolean ret = list[i].delete();
                        if (!ret) {
                            success = false;
                        }
                    }
                }
            }
        } else {
            success = false;
        }
        if (success) {
            file.delete();
        }
        return success;
    }


    /**
     * 新建目录
     *
     * @param directoryPath
     * @return
     */
    public static boolean createDirectory(String directoryPath) {
        boolean status;
        if (!directoryPath.equals("")) {
            File newPath = new File(directoryPath);
            status = newPath.mkdir();
            status = true;
        } else
            status = false;
        return status;
    }


    /**
     * 检查是否安装SD卡
     *
     * @return
     */
    public static boolean checkSaveLocationExists() {
        String sDCardStatus = Environment.getExternalStorageState();
        boolean status;
        if (sDCardStatus.equals(Environment.MEDIA_MOUNTED)) {
            status = true;
        } else
            status = false;
        return status;
    }

    /**
     * 删除目录(包括：目录里的所有文件)
     *
     * @param fileName
     * @return
     */
    public static boolean deleteDirectory(String fileName) {
        boolean status;
        SecurityManager checker = new SecurityManager();

        if (!fileName.equals("")) {

            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + fileName);
            checker.checkDelete(newPath.toString());
            if (newPath.isDirectory()) {
                String[] listfile = newPath.list();
                // delete all files within the specified directory and then
                // delete the directory
                try {
                    for (int i = 0; i < listfile.length; i++) {
                        File deletedFile = new File(newPath.toString() + "/"
                                + listfile[i].toString());
                        deletedFile.delete();
                    }
                    newPath.delete();
                    status = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    status = false;
                }

            } else
                status = false;
        } else
            status = false;
        return status;
    }

    /**
     * 删除文件
     *
     * @param fileName
     * @return
     */
    public static boolean deleteFile(String fileName) {
        boolean status;
        SecurityManager checker = new SecurityManager();

        if (!fileName.equals("")) {

            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + fileName);
            checker.checkDelete(newPath.toString());
            if (newPath.isFile()) {
                try {
                    newPath.delete();
                    status = true;
                } catch (SecurityException se) {
                    se.printStackTrace();
                    status = false;
                }
            } else
                status = false;
        } else
            status = false;
        return status;
    }

    /**
     * 解压缩功能. 将zipFile文件解压到folderPath目录下.
     *
     * @throws Exception
     */
    public static int unZipFile(File zipFile, String folderPath)
            throws ZipException, IOException {

        ZipFile zfile = new ZipFile(zipFile);
        Enumeration<? extends ZipEntry> zList = zfile.entries();

        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            File tempFile = new File(folderPath + "/" + ze.getName());
            /*
             * if(ze.isDirectory()){ Log.d("upZipFile",
             * "ze.getName() = "+ze.getName()); String dirstr = folderPath +
             * ze.getName(); //dirstr.trim(); // dirstr = new
             * String(dirstr.getBytes("8859_1"), "GB2312"); Log.d("upZipFile",
             * "str = "+dirstr); File f=new File(dirstr); f.mkdir(); continue; }
             */
            if (ze.isDirectory()) {
                tempFile.mkdirs();
                continue;
            } else if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }

            Log.d("upZipFile", "ze.getName() = " + ze.getName());
            OutputStream os = new BufferedOutputStream(new FileOutputStream(
                    new File(folderPath, ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
        Log.d("upZipFile", "finish");
        return 0;
    }

    public static void downFile(String url, String path, String fileName)
            throws IOException {
        String localfile = fileName;
        if (TextUtils.isEmpty(fileName))
            localfile = url.substring(url.lastIndexOf("/") + 1);

        URL Url = new URL(url);
        URLConnection conn = Url.openConnection();
        conn.connect();
        InputStream is = conn.getInputStream();
        try {
            int fileSize = conn.getContentLength();// 根据响应获取文件大小
            if (fileSize <= 0) {
                // 获取内容长度为0
                throw new RuntimeException("无法获知文件大小 ");
            }
            if (is == null) {

                throw new RuntimeException("无法获取文件");
            }
            FileOutputStream FOS = new FileOutputStream(path + File.separator
                    + localfile);
            // 创建写入文件内存流，通过此流向目标写文件
            byte buf[] = new byte[1024];

            int numread;
            while ((numread = is.read(buf)) != -1) {
                FOS.write(buf, 0, numread);
            }
            FOS.close();
        } finally {
            is.close();
        }

    }

    public static void saveBitmap(Bitmap bitmap, String savePath) {

        String folder = savePath.substring(0, savePath.lastIndexOf("/"));

        File fileDir = new File(folder);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(savePath);
            out.write(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static String getFileNameWithoutExtension(String filePath) {
        String path = getFileName(filePath);
        return path.substring(0, path.lastIndexOf("."));
    }

    /**
     * 删除文件夹
     *
     * @param filePath
     */
    public static boolean delete(String filePath) {
        boolean status = false;
        File file = new File(filePath);
        if (file.exists()) {
            try {
                file.delete();
                status = true;
            } catch (SecurityException se) {
                se.printStackTrace();
                status = false;
            }

        }
        return status;
    }

    public static boolean isExists(String fileUrl) {
        File file = new File(fileUrl);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param oldPath
     * @param newPath
     * @throws Exception
     */
    public static void copyFile(String oldPath, String newPath)
            throws Exception {

        int bytesum = 0;
        int byteread = 0;
        FileInputStream inPutStream = null;
        FileOutputStream outPutStream = null;

        try {

            // oldPath的文件copy到新的路径下，如果在新路径下有同名文件，则覆盖源文件
            inPutStream = new FileInputStream(oldPath);

            outPutStream = new FileOutputStream(newPath);
            byte[] buffer = new byte[4096];

            while ((byteread = inPutStream.read(buffer)) != -1) {

                // byte ファイル
                bytesum += byteread;
                outPutStream.write(buffer, 0, byteread);
            }
        } finally {

            // inPutStreamを关闭
            if (inPutStream != null) {
                inPutStream.close();
                inPutStream = null;
            }

            // inPutStream关闭
            if (outPutStream != null) {
                outPutStream.close();
                outPutStream = null;
            }

        }

    }

    // 递归删除文件及文件夹
    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    /**
     * 获取SD卡下指定文件夹的绝对路径
     *
     * @return 返回SD卡下的指定文件夹的绝对路径
     */
    public static String getSavePath(String folderName) {
        return getSaveFolder(folderName).getAbsolutePath() + "/";
    }

    /**
     * 获取文件夹对象
     *
     * @return 返回SD卡下的指定文件夹对象，若文件夹不存在则创建
     */
    public static File getSaveFolder(String folderName) {
        File file = new File(getSDCardPath() + File.separator + folderName
                + File.separator);
        file.mkdirs();
        return file;
    }


    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

//    public static String getSDPath(Context context) {
//        File sdDir = null;
//        boolean sdCardExist = Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
//        if (sdCardExist) {
//            if (Build.VERSION.SDK_INT >= 29) {
//                //Android10之后
//                sdDir = context.getFilesDir();
//            } else {
//                sdDir = Environment.getExternalStorageDirectory();// 获取SD卡根目录
//            }
////            sdDir = Environment.getExternalStorageDirectory();
//        } else {
//            sdDir = Environment.getRootDirectory();// 获取跟目录
//        }
//        return sdDir.toString();
//    }

    public static String getSDPath(Context context) {
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {
//            File external = context.getExternalFilesDir(null);
//            if (external != null) {
//                return external.getAbsolutePath();
//            }
//        }
//        return context.getFilesDir().getAbsolutePath();

        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
        if (sdCardExist) {
            if (Build.VERSION.SDK_INT>=29){
                //Android10之后
                sdDir = context.getExternalFilesDir(null);
            }else {
                sdDir = Environment.getExternalStorageDirectory();// 获取SD卡根目录
            }
        } else {
            sdDir = Environment.getRootDirectory();// 获取跟目录
        }
        return sdDir.toString();
    }

    public static String getPrivateSavePath(String folderName) {
        return getPrivateSaveFolder(folderName).getAbsolutePath() + "/";
    }

    public static File getPrivateSaveFolder(String folderName) {
        File file = new File(App.instance.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + folderName
                + File.separator);
        file.mkdirs();
        return file;
    }

    public static boolean writeTxtFile(String content, File fileName) {
        boolean flag = false;
        FileOutputStream o = null;
        try {
            File parentpath = fileName.getParentFile();
            if (!parentpath.exists()) {
                parentpath.mkdirs();
            }
            o = new FileOutputStream(fileName);
            o.write(content.getBytes("UTF-8"));
            o.close();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (o != null)
                try {
                    o.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return flag;
    }


    /**
     * 读TXT文件内容
     *
     * @param fileName
     * @return
     */
    public static String readTxtFile(File fileName) {
        String result = null;
        FileInputStream fileInputStream = null;
        BufferedReader bufferedReader = null;
        try {
            fileInputStream = new FileInputStream(fileName);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
            try {
                String read = null;
                while ((read = bufferedReader.readLine()) != null) {
                    result = read;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static File convertBitmapToFile(Bitmap bitmap) throws IOException {
        File f = new File(App.instance.getCacheDir(), "cover");
        f.createNewFile();

//Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return f;
    }


    public static boolean c(File file, File file2) {
        if (file == null || !file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            return e(file, file2);
        }
        return d(file, file2);
    }

    private static boolean e(File file, File file2) {
        int i = 0;
        if (file == null || !file.exists() || !file.isDirectory()) {
            return false;
        }
        if (file2.exists()) {
            file2.delete();
        }
        file2.mkdir();
        File[] listFiles = file.listFiles();
        if (listFiles == null || listFiles.length <= 0) {
            return false;
        }
        int length = listFiles.length;
        while (i < length) {
            File file3 = listFiles[i];
            if (file3.isDirectory()) {
                e(file3, new File(file2, file3.getName()));
            } else {
                d(file3, new File(file2, file3.getName()));
            }
            i++;
        }
        return true;
    }


    private static boolean d(File file, File file2) {
        FileOutputStream fileOutputStream;
        FileOutputStream fileOutputStream2;
        FileChannel channel;
        FileChannel fileChannel;
        FileInputStream fileInputStream = null;

        Throwable e;
        Throwable th;
        if (file == null || !file.exists()) {
            return false;
        }
        if (file2.exists()) {
            file2.delete();
        }
        FileChannel fileChannel2 = null;
        FileInputStream fileInputStream2;
        try {
            fileInputStream2 = new FileInputStream(file);
            try {
                fileOutputStream = new FileOutputStream(file2);
                try {
                    channel = fileInputStream2.getChannel();
                } catch (Exception e2) {
                    fileChannel = null;
                    fileInputStream = fileInputStream2;
                    fileOutputStream2 = fileOutputStream;
                    e = e2;
                    channel = fileChannel;
                    try {
//                        g.b(e);//打印日志
                        if (fileInputStream != null) {
                        }
                        if (channel != null) {
                        }
                        if (fileOutputStream2 != null) {
                        }
                        if (fileChannel != null) {
                        }
                        return false;
                    } catch (Throwable th2) {
                        th = th2;
                        fileOutputStream = fileOutputStream2;
                        fileInputStream2 = fileInputStream;
                        fileChannel2 = channel;
                        if (fileInputStream2 != null) {
                            try {
                                fileInputStream2.close();
                            } catch (IOException unused) {
                            }
                        }
                        if (fileChannel2 != null) {
                            try {
                                fileChannel2.close();
                            } catch (IOException unused2) {
                            }
                        }
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException unused3) {
                            }
                        }
                        if (fileChannel != null) {
                            try {
                                fileChannel.close();
                            } catch (IOException unused4) {
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    fileChannel = null;
                    if (fileInputStream2 != null) {
                    }
                    if (fileChannel2 != null) {
                    }
                    if (fileOutputStream != null) {
                    }
                    if (fileChannel != null) {
                    }
                    throw th;
                }
            } catch (Exception e3) {
                e = e3;
                channel = null;
                fileChannel = channel;
                fileInputStream = fileInputStream2;
                fileOutputStream2 = null;
//                g.b(e);
                if (fileInputStream != null) {
                }
                if (channel != null) {
                }
                if (fileOutputStream2 != null) {
                }
                if (fileChannel != null) {
                }
                return false;
            } catch (Throwable th4) {
                th = th4;
                fileOutputStream = null;
                fileChannel = fileOutputStream.getChannel();
                if (fileInputStream2 != null) {
                }
                if (fileChannel2 != null) {
                }
                if (fileOutputStream != null) {
                }
                if (fileChannel != null) {
                }
                throw th;
            }
            try {
                fileChannel = fileOutputStream.getChannel();
                try {
                    channel.transferTo(0, channel.size(), fileChannel);
                    if (fileInputStream2 != null) {
                        try {
                            fileInputStream2.close();
                        } catch (IOException unused5) {
                        }
                    }
                    if (channel != null) {
                        try {
                            channel.close();
                        } catch (IOException unused6) {
                        }
                    }
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException unused7) {
                        }
                    }
                    if (fileChannel != null) {
                        try {
                            fileChannel.close();
                        } catch (IOException unused8) {
                        }
                    }
                    return true;
                } catch (Exception e4) {
                    FileInputStream fileInputStream3 = fileInputStream2;
                    fileOutputStream2 = fileOutputStream;
                    e = e4;
                    fileInputStream = fileInputStream3;
//                    g.b(e);
                    if (fileInputStream != null) {
                    }
                    if (channel != null) {
                    }
                    if (fileOutputStream2 != null) {
                    }
                    if (fileChannel != null) {
                    }
                    return false;
                } catch (Throwable th5) {
                    th = th5;
                    fileChannel2 = channel;
                    if (fileInputStream2 != null) {
                    }
                    if (fileChannel2 != null) {
                    }
                    if (fileOutputStream != null) {
                    }
                    if (fileChannel != null) {
                    }
                    throw th;
                }
            } catch (Exception e5) {
                fileChannel = null;
                fileInputStream = fileInputStream2;
                fileOutputStream2 = fileOutputStream;
                e = e5;
//                g.b(e);
                if (fileInputStream != null) {
                }
                if (channel != null) {
                }
                if (fileOutputStream2 != null) {
                }
                if (fileChannel != null) {
                }
                return false;
            } catch (Throwable th6) {
                th = th6;
                fileChannel = null;
                fileChannel2 = channel;
                if (fileInputStream2 != null) {
                }
                if (fileChannel2 != null) {
                }
                if (fileOutputStream != null) {
                }
                if (fileChannel != null) {
                }
                throw th;
            }
        } catch (Exception e6) {
            e = e6;
            channel = null;
            fileOutputStream2 = null;
            fileChannel = fileOutputStream2.getChannel();
//            g.b(e);
            if (null != fileInputStream) {
                try {
                    fileInputStream.close();
                } catch (IOException unused9) {
                }
            }
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException unused10) {
                }
            }
            if (fileOutputStream2 != null) {
                try {
                    fileOutputStream2.close();
                } catch (IOException unused11) {
                }
            }
            if (fileChannel != null) {
                try {
                    fileChannel.close();
                } catch (IOException unused12) {
                }
            }
            return false;
        } catch (Throwable th7) {
            th = th7;
            fileOutputStream = null;
            fileInputStream2 = null;
            fileChannel = null;
            if (fileInputStream2 != null) {
            }
            if (fileChannel2 != null) {
            }
            if (fileOutputStream != null) {
            }
            if (fileChannel != null) {
            }
            try {
                throw th;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return true;
    }

}