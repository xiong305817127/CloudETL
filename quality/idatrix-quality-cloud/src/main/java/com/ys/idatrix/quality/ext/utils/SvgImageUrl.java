/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.ext.utils;

import java.awt.image.BufferedImage;

import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.ui.core.ConstUI;

/**
 * SVG image process.
 *   - no need any more since new web site doesn't use SVG.
 * 
 * @author JW
 * @since 05-12-2017
 * 
 */
public class SvgImageUrl {

    //public static final String Size_Small = "small";
    //public static final String Size_Middle = "middle";

    public static String getSmallUrl(PluginInterface plugin) {
        return url(plugin.getImageFile(), ConstUI.SMALL_ICON_SIZE);
    }

    public static String getSmallUrl(String imageFile) {
        return url(imageFile, ConstUI.SMALL_ICON_SIZE);
    }

    public static String getMiddleUrl(String imageFile) {
        return url(imageFile, ConstUI.MEDIUM_ICON_SIZE);
    }

    public static String getMiddleUrl(PluginInterface plugin) {
        return url(plugin.getImageFile(), ConstUI.MEDIUM_ICON_SIZE);
    }

    public static String getUrl(PluginInterface plugin) {
        return url(plugin.getImageFile(), ConstUI.ICON_SIZE);
    }

    public static String getUrl(String imageFile) {
        return url(imageFile, ConstUI.ICON_SIZE);
    }

    public static String url(PluginInterface plugin, int scale) {
        return url(plugin.getImageFile(), scale);
    }

    public static String url(String imageFile, int scale) {
        return imageFile + "?scale=" + scale; 
    }

    public static BufferedImage createImage(int scale) {
        return new BufferedImage(scale, scale, BufferedImage.TYPE_INT_ARGB);
    }
    
}
