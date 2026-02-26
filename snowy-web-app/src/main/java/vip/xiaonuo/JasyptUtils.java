package vip.xiaonuo;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;

public class JasyptUtils {
    public static void main(String[] args) {
        // 加密数据库用户名
        String user = encrypt("一个用户名");
        System.out.println("user = " + user);
//         加密数据库密码
        String password = encrypt("一个密码");
        System.out.println("password = " + password);

        // 需要解密的值
//        String user = decrypt("5sRqdMaT9EyC5OZEby5S6RUSWyJTXPAR2");
//        System.out.println("user = " + user);
//        String pass = decrypt("M7D5l5p8VjA0/acNKnSihIilxqA8/3TK");
//        System.out.println("pass = " + pass);

    }

    /*
     * 加密
     *
     * @param plaintext 明文
     * @return
     */
    public static String encrypt(String plaintext) {
        // 生成加密数据
        return JasyptUtils.buildEncryptor().encrypt(plaintext);
    }


    /**
     * 解密
     *
     * @param data 加密后数据
     * @return
     */
    public static String decrypt(String data) {
        // 解密数据
        return JasyptUtils.buildEncryptor().decrypt(data);
    }

    private static StringEncryptor buildEncryptor() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        // 指定算法，和yml配置文件中保持一致
        config.setAlgorithm("PBEWithMD5AndTripleDES");
        // 指定秘钥，和yml配置文件中保持一致
        config.setPassword("encrypt");
        // 指定Iv参数生成器，和yml配置中保持一致
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        encryptor.setConfig(config);
        return encryptor;
    }

}