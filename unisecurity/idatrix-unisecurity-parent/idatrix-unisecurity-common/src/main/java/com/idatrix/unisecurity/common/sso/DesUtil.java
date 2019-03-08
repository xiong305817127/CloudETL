package com.idatrix.unisecurity.common.sso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.security.Key;

public class DesUtil {

    private static Logger log = LoggerFactory.getLogger(DesUtil.class);

    private static String strDefaultKey = "12345678";

    private Cipher encryptCipher = null;

    private Cipher decryptCipher = null;

    /**
     * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[] hexStr2ByteArr(String strIn) 互为可逆的转换过程
     *
     * @param arrB 需要转换的byte数组
     * @return 转换后的字符串
     * @throws Exception 本方法不处理任何异常，所有异常全部抛出
     */

    public static String byteArr2HexStr(byte[] arrB) throws Exception {
        int iLen = arrB.length;
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
        StringBuffer sb = new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++) {
            int intTmp = arrB[i];
            // 把负数转换为正数
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }
            // 小于0F的数需要在前面补0
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16));
        }
        return sb.toString();
    }

    /**
     * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB) 互为可逆的转换过程
     *
     * @param strIn 需要转换的字符串
     * @return 转换后的byte数组
     * @throws Exception 本方法不处理任何异常，所有异常全部抛出
     */

    public static byte[] hexStr2ByteArr(String strIn) throws Exception {

        byte[] arrB = strIn.getBytes();

        int iLen = arrB.length;

        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2

        byte[] arrOut = new byte[iLen / 2];

        for (int i = 0; i < iLen; i = i + 2) {

            String strTmp = new String(arrB, i, 2);

            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);

        }

        return arrOut;

    }

    /**
     * 默认构造方法，使用默认密钥
     *
     * @throws Exception
     */

    public DesUtil() throws Exception {

        this(strDefaultKey);

    }

    /**
     * 指定密钥构造方法
     *
     * @param strKey 指定的密钥
     * @throws Exception
     */

    public DesUtil(String strKey) throws Exception {

        Key key = getKey(strKey.getBytes());

        // NoPadding

        // PKCS5Padding

        encryptCipher = Cipher.getInstance("DES/ECB/NoPadding", "SunJCE");

        encryptCipher.init(Cipher.ENCRYPT_MODE, key);

        decryptCipher = Cipher.getInstance("DES/ECB/NoPadding", "SunJCE");

        decryptCipher.init(Cipher.DECRYPT_MODE, key);

    }

    /**
     * 加密字节数组
     *
     * @param arrB 需加密的字节数组
     * @return 加密后的字节数组
     * @throws Exception
     */

    public byte[] encrypt(byte[] arrB) throws Exception {

        return encryptCipher.doFinal(arrB);

    }

    /**
     * 加密字符串
     *
     * @param strIn 需加密的字符串
     * @return 加
     * <p>
     * 密后的字符串
     * @throws Exception
     */

    public String encrypt(String strIn, String encode) throws Exception {

        return byteArr2HexStr(encrypt(strIn.getBytes(encode)));

    }

    /**
     * 解密字节数组
     *
     * @param arrB 需解密的字节数组
     * @return 解密后的字节数组
     * @throws Exception
     */

    public byte[] decrypt(byte[] arrB) throws Exception {

        return decryptCipher.doFinal(arrB);

    }

    /**
     * 解密字符串
     *
     * @param strIn 需解密的字符串
     * @return 解密后的字符串
     * @throws Exception
     */

    public String decrypt(String strIn, String encode) throws Exception {

        return new String(decrypt(hexStr2ByteArr(strIn)), encode);

    }

    public String decrypt(String str) {

        return decrypt(str, "", "UTF-8");
    }

    /**
     * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
     *
     * @param arrBTmp 构成该字符串的字节数组
     * @return 生成的密钥
     * @throws Exception
     */

    private Key getKey(byte[] arrBTmp) throws Exception {

        // 创建一个空的8位字节数组（默认值为0）

        byte[] arrB = new byte[8];

        // 将原始字节数组转换为8位

        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {

            arrB[i] = arrBTmp[i];

        }

        // 生成密钥

        Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");

        return key;

    }

    /**
     * DES加密
     *
     * @param key
     * @param str
     * @param encode
     * @return
     */

    public static String encrypt(String str, String key, String encode) {

        try {

            if (str == null) {

                return str;
            }
            if (encode == null) {
                encode = "UTF-8";
            }
            byte[] strBytes = str.getBytes(encode);

            byte[] newStrBytes = new byte[strBytes.length + (8 - strBytes.length % 8)];

            for (int i = 0; i < newStrBytes.length; i++) {

                newStrBytes[i] = i < strBytes.length ? strBytes[i] : 32;

            } // for

            return new DesUtil(key).encrypt(new String(newStrBytes, encode), encode);

        } catch (Exception e) {
            e.printStackTrace();
            // throw new MPayException(MPayException.MPAY_DES_ERROR,"加密失败",e);
            log.error("加密失败");
            return str;

        } // try

    }

    /**
     * DES解密
     *
     * @param key
     * @param str
     * @param encode
     * @return
     */

    public static String decrypt(String str, String key, String encode) {

        try {

            return new DesUtil(key).decrypt(str, encode).replaceAll("\\s*$", "");

        } catch (Exception e) {

            // throw new MPayException(MPayException.MPAY_DES_ERROR,"解密失败",e);

            return str;

        } // try

    }

    public static void main(String[] args) throws Exception {

        String admin = encrypt("admin", "12345678", "UTF-8");
        System.out.println("admin:" + admin);
        //d151152f1345914b

        String jmpass = decrypt("d151152f1345914b", "12345678", "UTF-8");
        System.out.println("admin解密：" + jmpass);


        // String str = DesUtil.encrypt("}7$#3@+1~/B\\[D&...","intf", "UTF-8");

        // System.out.println("密文=" + str);

        // System.out.println("明文=#" +
        // DesUtil.decrypt("}7$#3@+1~/B\\[D&...",str, "UTF-8")+"#");

        // System.out.println(DesUtil.decrypt("}7$#3@+1~/B\\[D&...","54c5b564c51868ba",
        // "UTF-8"));//f54c505c18c74894 54c5b564c51868ba

        //

        // System.out.println( DesUtil.decrypt("}7$#3@+1", "d40d315388d74f2d",
        // "GBK") );

        String key = "11111111";

        String message = "D99282C6D9C0A53B3D4FB8E50C34856C32F61E8123ACCAA60FB67889E9793C18E6DEA76D2EF5CD86DFB2C51FCAC2A36BCBE3EFD9EDB8728DC7AC7C1820B12F2EF4EACBAA37BB42A48D90B4744A549E01537FBA065EFD0372D3384701DD10AA1090A0EE4E8242C1642F014DD0A4957C2D4BFBC0118D7E400EF06E5987C10EA1CC0993AA7886A2389193FC3613AF49190C8BCE42E41F52BC637B4FC7A16E359160B37EF4C7E35479A5EEF49843F59351EF7A49F8FF72F441504C4860239490545D5527555E17569E67B75ED36F92CE56E25527555E17569E67C77F71B2725675CF6026517850B91160EA11F1D8D3B550AF137FF3D9C51EDA6DAA46D70799C1DB48654AD8BDFB3BE8D899BDE2E5C9CC457D8061475039EFBAEFD9EA23B0A6FE0BCF0355ECEA0EEEBAAA75B54AE0653DF00D6BD573B838C8431E2B5CA7A0ECAF2FC0C78EA1A396946E5623406E71102A11AEB27107267CC5E1D38D90B4744A549E0127312D59B70D7263";

        System.out.println("解密：" + DesUtil.decrypt(message, key, "GBK"));

        String str = "<?xml version=\"1.0\" encoding=\"GBK\"?><accept_in><accept_id>20131226121215</accept_id><sysfunc_id>101168</sysfunc_id><process_code>MPAY_PPU_NOTICE_RESULT</process_code><request_source>30210100</request_source><request_time>20131226121215</request_time><order_num>1</order_num><order_content><txnsts>S</txnsts><logno>123456</logno></order_content></accept_in>";

        System.out.println("加密：" + DesUtil.encrypt(str, key, "GBK"));
        String test = "test123";
        String encrytest = DesUtil.encrypt(test, key, "UTF-8");
        System.out.println("encrytest:" + encrytest);
        System.out.println("descry:" + DesUtil.decrypt(encrytest, key, "utf-8"));

    }

}