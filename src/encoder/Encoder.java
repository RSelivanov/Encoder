package encoder;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

public class Encoder {

    private static final String LEGACY_ALGORITHM = "AES";
    private static final String MODERN_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int IV_LENGTH = 16;
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 128;
    private static final int ITERATIONS = 65536;

    // ======================
    // MODERN (CBC + PBKDF2)
    // ======================

    public static String encryptModern(String value, String password) throws Exception {
        SecureRandom random = new SecureRandom();

        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_LENGTH];
        random.nextBytes(salt);
        random.nextBytes(iv);

        SecretKey key = deriveKeyPBKDF2(password, salt);
        Cipher cipher = Cipher.getInstance(MODERN_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

        byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

        // Сохраняем: salt:iv:encrypted (в base64)
        return Base64.getEncoder().encodeToString(salt) + ":" +
               Base64.getEncoder().encodeToString(iv) + ":" +
               Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decryptModern(String data, String password) throws Exception {
        String[] parts = data.split(":");
        if (parts.length != 3) throw new IllegalArgumentException("Invalid AES2 format");

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] iv = Base64.getDecoder().decode(parts[1]);
        byte[] encrypted = Base64.getDecoder().decode(parts[2]);

        SecretKey key = deriveKeyPBKDF2(password, salt);
        Cipher cipher = Cipher.getInstance(MODERN_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private static SecretKey deriveKeyPBKDF2(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), LEGACY_ALGORITHM);
    }

    // ======================
    // LEGACY (ECB)
    // ======================

    public static String encryptLegacy(String value, String sKey) throws Exception {
        Key key = generateLegacyKey(sKey);
        Cipher cipher = Cipher.getInstance(LEGACY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decryptLegacy(String value, String sKey) throws Exception {
        Key key = generateLegacyKey(sKey);
        Cipher cipher = Cipher.getInstance(LEGACY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(value));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private static Key generateLegacyKey(String sKey) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(sKey.getBytes(StandardCharsets.UTF_8));
        byte[] truncated = new byte[16];
        System.arraycopy(keyBytes, 0, truncated, 0, 16);
        return new SecretKeySpec(truncated, LEGACY_ALGORITHM);
    }

    public static void migrateLegacyToAES2(String key) {
        ArrayList<String> oldLines = Data.readToFile();
        ArrayList<String> newLines = new ArrayList<>();

        for (String line : oldLines) {
            try {
                String[] parts = line.split("<::>");
                if (parts.length != 3) continue;

                String name = parts[0];
                String login = parts[1];
                String encPassword = parts[2];

                // Если пароль уже имеет вид salt:iv:encrypted (новый формат) — пропускаем
                if (encPassword.split(":").length == 3) {
                    newLines.add(line); // уже новый формат
                    continue;
                }

                // Расшифровка по старому алгоритму (ECB)
                String decrypted = decryptLegacy(encPassword, key);

                // Шифруем по новому алгоритму (CBC + PBKDF2)
                String encrypted = encryptModern(decrypted, key);

                // Формируем новую строку
                newLines.add(name + "<::>" + login + "<::>" + encrypted);

            } catch (Exception e) {
                System.err.println("❌ Ошибка при миграции строки:\n" + line);
                e.printStackTrace();
            }
        }

        if (!newLines.isEmpty()) {
            Data.writeToFile(newLines);
            System.out.println("✅ Миграция завершена. Обновлено строк: " + newLines.size());
        } else {
            System.out.println("⚠️ Файл пуст или миграция не требуется.");
        }
    }

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
            charArr = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_!#$%&'()*+,-./:;<=>?@[]^{|}~"; 
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
