package com.idatrix.unisecurity.common.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * @ClassName ValidatedCodeUtils
 * @Description TODO
 * @Author ouyang
 * @Date 2018/9/5 11:23
 * @Version 1.0
 **/
public class ValidatedCodeUtils {

    private final static int LEN = 4;

    private final static String CODES = "0123456789";

    /*
	 * 4位随机数字字符串
	 * @return
	 */
    public static String randomCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LEN; ++i) {
            sb.append(CODES.charAt(random.nextInt(CODES.length())));
        }
        return sb.toString();
    }

    /*
     * 绘制PNG图片
     * @return
     */
    public static byte[] generateImg(String code) throws IOException {
        final int width = 75;
        final int height = 30;

        BufferedImage bimg = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bimg.createGraphics();

        // 背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.GRAY);
        g.setFont(new Font("黑体", Font.BOLD, 25));

        // 干扰线
        Random random = new Random();
        for (int i = 0; i < 10; ++i) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);

            g.drawLine(x1, y1, x2, y2);
        }
        for (int i = 0; i < LEN; ++i) {
            g.drawString(String.valueOf(code.charAt(i)), 5 + 16 * i, 25);
        }
        g.dispose();
        // 输出
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bimg, "png", baos);
        baos.close();
        return baos.toByteArray();
    }

    public static void main(String[] args) {
        String code = randomCode();
        String codeStr = null;
        try {
            codeStr = Base64.getEncoder().encodeToString(generateImg(code));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(code);
        System.out.println(codeStr);
    }
}
