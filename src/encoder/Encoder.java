package encoder;

import java.security.Key;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;


public class Encoder 
{
    
    private static final String ALGORITHM = "AES";
    
    /*
    public static String encryptOld(String value, String sKey) throws Exception
    {
        Key key = generateKey(sKey);
        Cipher cipher = Cipher.getInstance(Encoder.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        String encryptedValue64 = new BASE64Encoder().encode(encryptedByteValue);
        return encryptedValue64;
               
    }
    */
    public static String encrypt(String value, String sKey) throws Exception 
    {
        Key key = generateKey(sKey);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        return Base64.getEncoder().encodeToString(encryptedByteValue);
    }

    /*
    public static String decryptOld(String value, String sKey) throws Exception
    {
        Key key = generateKey(sKey);
        Cipher cipher = Cipher.getInstance(Encoder.ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte [] decryptedValue64 = new BASE64Decoder().decodeBuffer(value);
        byte [] decryptedByteValue = cipher.doFinal(decryptedValue64);
        String decryptedValue = new String(decryptedByteValue,"utf-8");
        return decryptedValue;
                
    }
    */
    
    public static String decrypt(String value, String sKey) throws Exception 
    {
        Key key = generateKey(sKey);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue64 = Base64.getDecoder().decode(value);
        byte[] decryptedByteValue = cipher.doFinal(decryptedValue64);
        return new String(decryptedByteValue, "utf-8");
    }
    
    /*
    private static Key generateKeyOld(String sKey) throws Exception 
    {
        //Получаем ключь из инферфейсп
        String str = sKey;
        
        //Генерируме md5
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] bytes = md5.digest(str.getBytes());
        
        //Переводим в строку
        StringBuilder builder = new StringBuilder();
        for(byte b : bytes){
            builder.append(String.format("%02X", b));
        }
        
        //Режем ключ до 16
        String strKey = builder.toString().substring(16);
        
        //Генерируем хеш на основе ключа
        Key key = new SecretKeySpec(strKey.getBytes(),Encoder.ALGORITHM);
        //Key key = new SecretKeySpec(Encoder.KEY.getBytes(),Encoder.ALGORITHM);
        return key;
    }
    */
    private static Key generateKey(String sKey) throws Exception 
    {
        String str = sKey;
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(str.getBytes("utf-8"));
        byte[] truncatedKeyBytes = new byte[16];
        System.arraycopy(keyBytes, 0, truncatedKeyBytes, 0, 16);
        return new SecretKeySpec(truncatedKeyBytes, ALGORITHM);
    }

    /*
    public static String generatePassword() 
    {
        String charArr = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_";
        String randomPass = "";
        int index = 0;

        for (int i = 0; i < 10; i++)
        {
            index = new Random().nextInt(63);
            randomPass += charArr.charAt(index);
        }
        return randomPass;
    }
    */
    
    public static String generatePassword(boolean isCheckBoxSimplePassword) 
    {
        String charArr;
        int charCount;
        
        if(isCheckBoxSimplePassword)
        { 
            charArr = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"; 
            charCount = 62;
        }
        else
        {
            charArr = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_!#$%&'()*+,-./:;<=>?@[]^`{|}~"; 
            charCount = 93;
        }
        
        StringBuilder randomPass = new StringBuilder();
        int index;
        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            index = random.nextInt(charCount);
            randomPass.append(charArr.charAt(index));
        }
        return randomPass.toString();
    }
}
