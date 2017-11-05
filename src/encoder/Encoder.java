package encoder;

import java.security.Key;
import java.security.MessageDigest;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Класс Encoder работает с шифрованием и дешифровкой информации
 */
public class Encoder {
    
    private static final String ALGORITHM = "AES";
    
    /**
     * Шифрует строку
     * @param String lines <p>Строка для шифрования</p>
     * @return String <p>Зашифрованная строка</p>
     */
    public static String encrypt(String value, String sKey) throws Exception
    {
        Key key = generateKey(sKey);
        Cipher cipher = Cipher.getInstance(Encoder.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        String encryptedValue64 = new BASE64Encoder().encode(encryptedByteValue);
        return encryptedValue64;
               
    }
    
    /**
     * Расшифровывает строку
     * @param String lines <p>Строка для расшифровки</p>
     * @return String <p>Расшифрованная строка</p>
     */
    public static String decrypt(String value, String sKey) throws Exception
    {
        Key key = generateKey(sKey);
        Cipher cipher = Cipher.getInstance(Encoder.ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte [] decryptedValue64 = new BASE64Decoder().decodeBuffer(value);
        byte [] decryptedByteValue = cipher.doFinal(decryptedValue64);
        String decryptedValue = new String(decryptedByteValue,"utf-8");
        return decryptedValue;
                
    }
    
    /**
     * Генерирует ключ для шифрования или дешифровки
     * @return Key <p>ключ</p>
     */
    private static Key generateKey(String sKey) throws Exception 
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
    
     /**
     * Генерирует пароль
     * @return Key <p>ключ</p>
     */
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
    
}
