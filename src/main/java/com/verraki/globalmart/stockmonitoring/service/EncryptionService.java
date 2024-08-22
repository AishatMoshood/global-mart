package com.verraki.globalmart.stockmonitoring.service;

public interface EncryptionService {
    String encrypt(String message);
    String decrypt(String encryptedMessage);
}