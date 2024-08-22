package com.verraki.globalmart.stockmonitoring.service.serviceimpl;

import com.verraki.globalmart.stockmonitoring.service.EncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;


@Service
@Slf4j
public class AesEncryptionService implements EncryptionService {

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;

    private SecretKey secretKey;

    public AesEncryptionService() {
        this.secretKey = generateAESKey();
    }

    private SecretKey generateAESKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(KEY_SIZE);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            log.error("Error generating AES key", e);
            throw new RuntimeException("Failed to generate AES key", e);
        }
    }

    @Override
    public String encrypt(String message) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("Error encrypting message", e);
            throw new RuntimeException("Failed to encrypt message", e);
        }
    }

    @Override
    public String decrypt(String encryptedMessage) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
            return new String(decryptedBytes);
        } catch (Exception e) {
            log.error("Error decrypting message", e);
            throw new RuntimeException("Failed to decrypt message", e);
        }
    }
}