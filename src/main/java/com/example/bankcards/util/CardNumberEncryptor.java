package com.example.bankcards.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class CardNumberEncryptor {

    private final SecretKeySpec key;

    public CardNumberEncryptor(@Value("${app.encryption.secret}") String secret) {
        this.key = new SecretKeySpec(secret.getBytes(), "AES");
    }

    public String encrypt(String string) {
        try{
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(string.getBytes()));
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
    }


    public String decrypt(String string) {
        try{
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(string)));
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
