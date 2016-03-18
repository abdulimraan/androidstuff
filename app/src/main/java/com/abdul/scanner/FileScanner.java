package com.abdul.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Abdul Aleem Mohammed on 3/17/2016.
 */
public class FileScanner {

    private String fileName;
    private long fileSize;
    private String fileType;

    private static ArrayList<FileScanner> fileList = new ArrayList<FileScanner>();

    FileScanner(String mFileName, long mFileSize, String mFileType){
        this.fileName = mFileName;
        this.fileSize = mFileSize;
        this.fileType = mFileType;
    }

    public String getFileName() {
        return fileName;
    }
    public long getFileSize() {
        return fileSize;
    }

    public static void add(FileScanner file){
        if(typeOfFiles(file.getFileName())){
            fileList.add(file);
        }

    }

    public static ArrayList<FileScanner> getTopTenList(){
        ArrayList<FileScanner> topList = new ArrayList<FileScanner>();
        Collections.sort(fileList, new Comparator<FileScanner>() {
            @Override
            public int compare(FileScanner lhs, FileScanner rhs) {

                if(lhs.getFileSize() > rhs.getFileSize()){
                    return -1;
                }else if(lhs.getFileSize() < rhs.getFileSize()){
                    return 1;
                }else{
                    return 0;
                }
            }
        });
        for(int i=0;i < 10;i++){
            topList.add(fileList.get(i));
        }

        return topList;
    }

    public static long getAverageFileSize(){
        long sum=0;
        for(int i=0; i < fileList.size(); i++){
            sum = sum+fileList.get(i).getFileSize();
        }
        return sum/fileList.size();
    }

//    @Override
//    public int compareTo(FileScanner another) {
//
//    }

    public static ArrayList<FileScanner> getAllFiles(){
        return fileList;
    }

    public static void clear(){
        fileList.clear();
    }

    @Override
    public String toString() {
        return this.fileName+ " "+" Size: "+this.fileSize+"(KB)";
    }

    public static boolean typeOfFiles(String fileName){
        if(fileName.endsWith(".mp3") || fileName.endsWith(".pdf") || fileName.endsWith(".jpeg")){
            return true;
        }
        return false;
    }
}
