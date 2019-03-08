package com.idatrix.unisecurity.common.utils;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

public class WriteExcel<T extends Object> {

    public void createExcel(String fileName, String[] Title, List<T> list, HttpServletResponse response) {
        String path = WriteExcel.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = path.substring(0, path.lastIndexOf("/") + 1);

        String filePath = path + fileName + "_" + DateUtil.dateToString(new Date(), DateUtil.DATE_PATTERN) + ".xlsx";
        //String filePath = "F:\\" + fileName + "_" + DateUtil.dateToString(new Date(), DateUtil.DATE_PATTERN) + ".xlsx";
        try {
            OutputStream os = new FileOutputStream(filePath);
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet(fileName);
            if (CollectionUtils.isNotEmpty(list)) {
                XSSFRow row = sheet.createRow(0);
                for (int i = 0; i < Title.length; i++) {
                    row.createCell(i).setCellValue(Title[i]);
                }
                Field[] fields = null;
                for (int i = 0; i < list.size(); i++) {
                    XSSFRow datarow = sheet.createRow(i + 1);
                    Object o = (Object) list.get(i);
                    fields = o.getClass().getDeclaredFields();
                    int j = 0;
                    for (Field v : fields) {
                        v.setAccessible(true);
                        Object va = v.get(o);
                        if (va == null) {
                            va = "";
                        }
                        datarow.createCell(j).setCellValue(va.toString());
                        j++;
                    }
                }
            }
            wb.write(os);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        download(filePath, response);
    }

    public void createExcel(String fileName, String[] Title, String[] keys, List<T> list, HttpServletResponse response) {
        String path = WriteExcel.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = path.substring(0, path.lastIndexOf("/") + 1);
        String filePath = path + fileName + "_" + DateUtil.dateToString(new Date(), DateUtil.DATE_PATTERN) + ".xlsx";
        try {
            OutputStream os = new FileOutputStream(filePath);
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet(fileName);
            if (CollectionUtils.isNotEmpty(list)) {
                XSSFRow row = sheet.createRow(0);
                for (int i = 0; i < Title.length; i++) {
                    row.createCell(i).setCellValue(Title[i]);
                }
                Field[] fields = null;
                for (int i = 0; i < list.size(); i++) {
                    XSSFRow datarow = sheet.createRow(i + 1);
                    Object o = (Object) list.get(i);
                    fields = o.getClass().getDeclaredFields();
                    int j = 0;
                    for (Field v : fields) {
                        v.setAccessible(true);
                        Object va = v.get(o);
                        if (va == null) {
                            va = "";
                        }
                        for (int z = 0; z < keys.length; z++) {
                            if (v.getName().equals(keys[z])) {
                                datarow.createCell(z).setCellValue(va.toString());
                                // j++;
                            }
                        }
                    }
                }
            }
            wb.write(os);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        download(filePath, response);
    }

    private static void download(String path, HttpServletResponse response) {
        try {
            File file = new File(path);
            String filename = file.getName();
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(filename, "UTF-8"));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(
                    response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
          /*  if(file.exists())
                file.delete();*/
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
