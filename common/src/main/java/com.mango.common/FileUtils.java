package com.mango.common;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 文件读写操作工具
 * @author shaowen
 */
public class FileUtils {

    /**
     * 读取文件信息
     * @param dir
     * @param path
     * @return
     * @throws IOException
     */
    public static String getDatafromFile(String dir,String path) throws Exception {
        mkdirIfNotExits(dir);
        createFileIfNotExits(path);
        BufferedReader reader = null;
        StringBuilder laststr = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(dir + path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr.append(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr.toString();
    }

    /**
     * 将信息写入文件
     * @param dir
     * @param path
     * @param data
     * @throws Exception
     */
    public static void saveDataToFile(String dir,String path,String data) throws Exception{
        BufferedWriter writer = null;
        mkdirIfNotExits(dir);
        createFileIfNotExits(path);
        File file = new File(dir + path);
        //写入
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,false), StandardCharsets.UTF_8));
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e);
        }finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 创建目录
     * @param filePath
     */
    private static void mkdirIfNotExits(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 创建文件
     * @param filePath
     * @throws IOException
     */
    private static void createFileIfNotExits(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
    }
}
