/**
 * Copyright (c) 2021 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Feb 18, 2022
 */
package com.peratonlabs.closure.codegen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Utils
{
    public static void copyDir(String srcDir, String dstDir, boolean delete) {
        try {
            if (delete)
                deleteDir(new File(dstDir));
            
            File dir = new File(dstDir);
            if (!dir.exists())
                dir.mkdirs();
            
            Files.walk(Paths.get(srcDir)).forEach(source -> {
                Path destination = Paths.get(dstDir, source.toString().substring(srcDir.length()));
                try {
                    Files.copy(source, destination);
                }
                catch (IOException e) {
//                    e.printStackTrace();
                }
            });
        }
        catch (Exception e) {
            System.err.println("srcDir= " + srcDir + " dstDir=" + dstDir);
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children == null)
                return true;
            for (int i=0; i<children.length; i++) {
                if (children[i] == null)
                    continue;
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
    
    public static byte[] compress(byte[] bytes) {
        ByteArrayOutputStream baos = null;
        Deflater dfl = new Deflater();
        dfl.setLevel(Deflater.BEST_COMPRESSION);
        dfl.setInput(bytes);
        dfl.finish();
        baos = new ByteArrayOutputStream();
        byte[] tmp = new byte[4 * 1024];
        try {
            while (!dfl.finished()) {
                int size = dfl.deflate(tmp);
                baos.write(tmp, 0, size);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (baos != null)
                    baos.close();
            }
            catch (Exception ex) {
            }
        }
        return baos.toByteArray();
    }
    
    public static byte[] decompress(byte[] bytes) {
        ByteArrayOutputStream baos = null;
        Inflater iflr = new Inflater();
        iflr.setInput(bytes);
        baos = new ByteArrayOutputStream();
        byte[] tmp = new byte[4 * 1024];
        try {
            while (!iflr.finished()) {
                int size = iflr.inflate(tmp);
                baos.write(tmp, 0, size);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (baos != null)
                    baos.close();
            }
            catch (Exception ex) {
            }
        }
        return baos.toByteArray();
    }
    
    public static String basename(String path) {
        String filename = path.substring(path.lastIndexOf('/') + 1);

        if (filename == null || filename.equalsIgnoreCase("")) {
            filename = "";
        }
        return filename;
    }
    
    
    private static void populateFilesList(List<String> filesListInDir, File dir) throws IOException {
        
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile())
                filesListInDir.add(file.getAbsolutePath());
            else
                populateFilesList(filesListInDir, file);
        }
    }

    public static void zipDirectory(String dirName, String zipDirName, String hal) {
        try {
            File dir = new File(dirName);
            List<String> filesListInDir = new ArrayList<String>();
            populateFilesList(filesListInDir, dir);
            
            String basename = Utils.basename(dirName);

            // now zip files one by one
            // create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for (String filePath : filesListInDir) {
//                System.out.println("Zipping " + filePath);
                // for ZipEntry we need to keep only relative file path, so we
                // used substring on absolute path
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() - basename.length(), filePath.length()));
                zos.putNextEntry(ze);
                // read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            
            ZipEntry ze = new ZipEntry(hal.substring(dir.getAbsolutePath().length() - basename.length(), hal.length()));
            zos.putNextEntry(ze);
            FileInputStream fis = new FileInputStream(hal);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            fis.close();
            
            zos.close();
            fos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    public static void test(String[] args) {
        byte[] bytes = "This is a test".getBytes();
        byte[] cmp = compress(bytes);
        String str = new String(cmp);
        System.out.println("Complressed Data: "+ str + " len=" + str.length());
  
        byte[] decmp = decompress(cmp);
        str = new String(decmp);
        System.out.println("Uncomplressed Data: "+ str + " len=" + str.length());
    }

}
