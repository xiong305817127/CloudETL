package com.idatrix.resource.common.utils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 *  Title : 文件操作 主要是在 tmp目录下创建删除文件
 *
 *  @author Robin
 *  @version v1.0
 *
 */
public class FileUtils {

    /*
    *   在java.io.tmpdir路径下 idatrix-resource 创建路径
    */
    public static File createTempDir() {
        return createTempDir(new File(System.getProperty("java.io.tmpdir")));
    }


    public static File createTempDir(String dirName){
        final File temp = new File(createTempDir(), dirName);
        temp.delete();
        temp.mkdir();
        temp.deleteOnExit();
        return temp;
    }

    public static File createUpdateTempDir(String dirName){

        final File temp = new File(createTempDir("update"), dirName);
        temp.delete();
        temp.mkdir();
        temp.deleteOnExit();
        return temp;
    }

    public static File createUpdateDirByInfo(){
        String dateStr = DateTools.formatDate(new Date(),"yyyyMMdd-HHmmss");
        String uuid = CommonUtils.generateUUID();
        String fileName = dateStr+"-"+uuid;
        return createUpdateTempDir(fileName);
    }

    public static File createTempDir(final File parent) {
        final File temp =
                new File(parent, "idatrix-resource");
        temp.delete();
        temp.mkdir();
        temp.deleteOnExit();
        return temp;
    }

    public static boolean deletefile(String delpath) throws Exception {
        File file = new File(delpath);
        if (file.isDirectory()) {
            String[] filelist = file.list();
            for (String delFile : filelist) {
                File delfile = new File(delpath + File.separator + delFile);
                if (delfile.isDirectory()) {
                    deletefile(delpath + File.separator + delFile);
                } else {
                    delfile.delete();
                }
            }
            file.delete();
        }else {
            file.delete();
        }
        return true;
    }

    public static Boolean validFileSize(int maxSizeMB, CommonsMultipartFile file){
        Boolean flag = true;
        //校验文件大小
        int actualFileSize = CommonUtils.calculateFileSizeByMB(file.getSize());
        if (actualFileSize > maxSizeMB){
            flag = false;
        }
        return flag;
    }


    public static String getFileDirByType(String fileType){
        final File tempDir = createTempDir(fileType); //Utils.createTempDir();
        return tempDir.getPath();
    }

    /**
     *  excel路径为 java.io.tmpdir 下面创建一个 idatrix-resource
     * @param    fileType 表示文件类型，会在 idatrix-resource 下创建这个类型的文件
     * @return  返回文件路径和名称
     */
    public static String createFile(String fileType, CommonsMultipartFile multiPartFile) {
        final File tempDir = createTempDir(fileType); //Utils.createTempDir();
        final FileItem item = multiPartFile.getFileItem();
        String dateStr = DateTools.formatDate(new Date(),"yyyyMMdd-HHmmss");
        String uuid = CommonUtils.generateUUID();
        String fileName = dateStr+"-"+uuid+"-"+item.getName();
        OutputStream out = null;
        File archiveFile = null;
        try {
            archiveFile = new File(tempDir, fileName);
            out = new BufferedOutputStream(new FileOutputStream(archiveFile));
            IOUtils.copy(item.getInputStream(), out);
            out.close();
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return archiveFile.getName();
//        return archiveFile.getPath();
    }
}
