package com.ys.idatrix.metacube.common.utils;

import com.google.common.io.Closeables;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: CloneUtils
 * @Description:
 * @Author: ZhouJian
 * @Date: 2018/10/11
 */
public class CloneUtils {

    /**
     * 深克隆
     *
     * @param src
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> ArrayList<T> deepCopy(List<T> src) {
        ByteArrayOutputStream byteOut = null;
        ObjectOutputStream out = null;
        ByteArrayInputStream byteIn = null;
        ObjectInputStream in = null;
        ArrayList<T> dest = null;
        try {
            byteOut = new ByteArrayOutputStream();
            out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            in = new ObjectInputStream(byteIn);
            dest = (ArrayList<T>) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Closeables.close(byteOut, true);
                Closeables.close(out, true);
                Closeables.close(byteIn, true);
                Closeables.close(in, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dest;
    }

    /**
     * 深克隆
     *
     * @param src
     * @return
     */
    public static <T> T deepCopy(T src) {
        ByteArrayOutputStream byteOut = null;
        ObjectOutputStream out = null;
        ByteArrayInputStream byteIn = null;
        ObjectInputStream in = null;
        T dest = null;
        try {
            byteOut = new ByteArrayOutputStream();
            out = new ObjectOutputStream(byteOut);
            out.writeObject(src);
            byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            in = new ObjectInputStream(byteIn);
            dest = (T) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Closeables.close(byteOut, true);
                Closeables.close(out, true);
                Closeables.close(byteIn, true);
                Closeables.close(in, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dest;
    }
}
