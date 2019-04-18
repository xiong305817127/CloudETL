package com.ys.idatrix.quality.web.utils;

import java.io.*;
import java.util.List;

/**
 * @ClassName CSVUtils
 * @Description TODO
 * @Author ouyang
 * @Date 2018/9/29 15:11
 * @Version 1.0
 **/
public class CSVUtils {

	public static boolean exportCsv(File file, List<String> dataList) {
        boolean isSucess = false;
        FileOutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out);
            bw = new BufferedWriter(osw);
            if (dataList != null && !dataList.isEmpty()) {
                for (String data : dataList) {
                    bw.append(data).append("\r\n");
                }
            }
            isSucess = true;
        } catch (Exception e) {
            isSucess = false;
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                    bw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                    osw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSucess;
    }

    /*public static void main(String[] args) {
        String path = CSVUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = path.substring(0, path.lastIndexOf("/") + 1);
        String fileName = TimeUtils.getTimeString();
        //fileName = path + fileName + ".csv";
        String[] str = {"aa","sss","ddd"};
        System.out.println("--------" + path + "-------" + fileName);
        createCSVFile(null, Arrays.asList(Arrays.asList(str)), path, fileName + ".csv");
    }*/
    
    public static void main(String[] args) {
      File f = null;
            
      try{
         // creates temporary file
         f = File.createTempFile("tmp", ".txt");
         
         // prints absolute path
         System.out.println("File path: "+f.getAbsolutePath());
         
         // creates temporary file
         f = File.createTempFile("tmp", null);
         
         // prints absolute path
         System.out.print("File path: "+f.getAbsolutePath());
         
      }catch(Exception e){
         // if any error occurs
         e.printStackTrace();
      }
   }


    /**
     * CSV文件生成方法
     * @param head
     * @param dataList
     * @param outPutPath
     * @param filename
     * @return
     */
    public static File createCSVFile(List<String> head, List<List<String>> dataList,
                                     String outPutPath, String filename) throws Exception{
        File csvFile = null;
        BufferedWriter csvWtriter = null;
        try {
            csvFile = new File(outPutPath + filename);
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();

            // GB2312使正确读取分隔符","
            csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    csvFile), "GB2312"), 1024);
            // 写入文件头部
            writeRow(head, csvWtriter);

            // 写入文件内容
            for (List<String> row : dataList) {
                writeRow(row, csvWtriter);
            }
            csvWtriter.flush();
        } finally {
            try {
                csvWtriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFile;
    }

    /**
     * 写一行数据方法
     * @param row
     * @param csvWriter
     * @throws IOException
     */
    private static void writeRow(List<String> row, BufferedWriter csvWriter) throws IOException {
        if(row == null || row.size() == 0){
            return;
        }
        // 写入文件头部
        for (int i=0; i<row.size(); i++) {
        	Object data = row.get(i);
        	StringBuffer sb = new StringBuffer();
        	String rowStr = null;
        	if(i != row.size()-1) {
        		rowStr = sb.append(data).append(",").toString();
        	} else {
        		rowStr = sb.append(data).toString();
        	}
            csvWriter.write(rowStr);
        }
        csvWriter.newLine();
    }
}
