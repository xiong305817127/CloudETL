package com.idatrix.unisecurity.core.freemarker.extend;

import com.idatrix.unisecurity.common.utils.SecurityStringUtils;
import com.idatrix.unisecurity.common.utils.UtilPath;
import com.idatrix.unisecurity.core.tags.APITemplateModel;
import freemarker.template.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 开发公司：粤数大数据
 */
@SuppressWarnings({"unchecked"})
public class FreeMarker {
    //HTML输出目录
    protected static final String HTML_PATH = UtilPath.getHTMLPath();

    //FTL输入目录
    protected static String FTL_PATH = UtilPath.getFTLPath();

    private static Configuration cfg = null;

    private static Log logger = LogFactory.getLog(FreeMarker.class);

    static Map<String, Object> initMap;

    @Autowired
    static APITemplateModel api;

    @Autowired
    static FreeMarkerConfigExtend ext;

    static {
        initMap = new LinkedHashMap<String, Object>();
        /**项目静态地址*/
//		String jsPath = WYFConfig.get("js_path");
//		String csspath = WYFConfig.get("css_path");
//		String imgpath = WYFConfig.get("img_path");
//		String path    = WYFConfig.get("path");
//		initMap.put("jspath", jsPath);
//		initMap.put("csspath", csspath);
//		initMap.put("imgpath", imgpath);
//		initMap.put("path", path);
        /**Freemarker Config*/
        //1、创建Cfg
        cfg = new Configuration();
        //2、设置编码
        cfg.setLocale(Locale.getDefault());
        cfg.setEncoding(Locale.getDefault(), "UTF-8");

        /**添加自定义标签*/
        /*APITemplateModel api = SpringContextUtil.getBean("api", APITemplateModel.class);*/
        cfg.setSharedVariable("api", api);

        /*FreeMarkerConfigExtend ext = SpringContextUtil.getBean("freemarkerConfig", FreeMarkerConfigExtend.class);*/

        Configuration vcfg = ext.getConfiguration();
        Set<String> keys = vcfg.getSharedVariableNames();
        for (String key : keys) {
            TemplateModel value = vcfg.getSharedVariable(key);
            cfg.setSharedVariable(key, value);
        }
        try {
            FreeMarkerConfigExtend.putInitShared(cfg);
        } catch (TemplateModelException e) {
            logger.error("添加Freemarker自定义方法失败;", e);
        }
        /**获取配置文件里的 Configuration.settings 设到当前Cfg里
         Properties setting = new Properties();
         Map<String,String> sets = vcfg.getSettings();
         setting.putAll(sets);
         try {
         cfg.setSettings(setting);
         } catch (TemplateException e) {
         logger.error("添加settings error;" ,e);
         }
         */
    }


    /**
     * @param path    模版路径
     * @param inFile  模版文件
     * @param outPath 输出html路径
     * @param outFile 输出html NAME+后缀
     * @throws IOException
     * @throws TemplateException
     * @throws Exception
     */
    public void outHtml(String path, String inFile, String outPath, String outFile, Map<String, Object> outMap) throws IOException, TemplateException {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        try {
            //3、加载模板目录
            File filex = new File(path);
            cfg.setDirectoryForTemplateLoading(filex);
            //4、加载模板文件
            Template temp = cfg.getTemplate(inFile);
            //设置编码
            temp.setEncoding("UTF-8");
            //5、构建一个File对象输出
            File file = new File(outPath + outFile);
            fos = new FileOutputStream(file);
            osw = new OutputStreamWriter(fos, "UTF-8");
            bw = new BufferedWriter(osw);
            //6、准备数据模型
            // 模版方法模式,子类实现
            Map<String, Object> resultMap = doOutMap(outMap);

            resultMap.putAll(initMap);

            //7、调用Template对象的process方法来输出文件
            temp.process(resultMap, bw);
        } finally {
            try {
                if (bw != null) bw.flush();
                if (fos != null) fos.close();
                if (osw != null) osw.close();
                if (bw != null) bw.close();
            } catch (IOException e) {
                logger.error("创建 [" + outFile + "] . io close exception" + e.getMessage());
            }
        }

    }

    /**
     * 交给子类实现
     *
     * @return
     */
    protected Map<String, Object> doOutMap(Map<String, Object> outMap) {
        return new HashMap<String, Object>();
    }

    /**
     * 备份原来HTML
     */
    public static void bakFile(String fileName) {
        if (SecurityStringUtils.isBlank(fileName))
            return;
        File[] files = UtilPath.getFiles(HTML_PATH);
        b:
        for (File file : files) {
            /***备份原来文件**/
            if ((fileName).equals(file.getName())) {
                String newName = fileName + "-" + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()) + ".html";
                String parentPath = file.getParent();
                File xfile = new File(parentPath + "/" + newName);
                if (xfile.exists()) {
                    break b;
                } else {
                    if(!file.renameTo(xfile))
                        logger.error("备份文件失败");
                    break b;
                }
            }
        }
    }

}
