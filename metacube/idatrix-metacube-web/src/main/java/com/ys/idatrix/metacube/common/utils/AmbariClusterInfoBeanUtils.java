package com.ys.idatrix.metacube.common.utils;

import com.ys.idatrix.metacube.metamanage.beans.AmbariCluesterInfoBean;
import com.ys.idatrix.metacube.metamanage.beans.AmbariCluesterInfoBean.ItemsBean;
import com.ys.idatrix.metacube.metamanage.beans.AmbariCluesterInfoBean.ItemsBean.ConfigurationsBean;
import com.ys.idatrix.metacube.metamanage.beans.AmbariCluesterInfoBean.ItemsBean.ConfigurationsBean.PropertiesBean;
import java.util.List;

public class AmbariClusterInfoBeanUtils {

    public static String getELASTICSEARCH(AmbariCluesterInfoBean bean) {
        return getPropertiesBean(bean).getELASTICSEARCH();
    }

    public static String getHBASE(AmbariCluesterInfoBean bean) {
        return getPropertiesBean(bean).getHBASE();
    }

    public static String getHDFS(AmbariCluesterInfoBean bean) {
        return getPropertiesBean(bean).getHDFS();
    }

    public static String getHIVE(AmbariCluesterInfoBean bean) {
        return getPropertiesBean(bean).getHIVE();
    }

    public static String getCLOUDETL(AmbariCluesterInfoBean bean) {
        return getPropertiesBean(bean).getCLOUDETL();
    }

    private static PropertiesBean getPropertiesBean(AmbariCluesterInfoBean bean) {
        List<ItemsBean> items = bean.getItems();
        ItemsBean itemsBean = items.get(0);
        List<ConfigurationsBean> configurations = itemsBean.getConfigurations();
        PropertiesBean propertiesBean = configurations.get(0).getProperties();
        return propertiesBean;
    }
}
