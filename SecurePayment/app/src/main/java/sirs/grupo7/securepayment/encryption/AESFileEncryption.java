package sirs.grupo7.securepayment.encryption;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

// TO SAVE FILES ON ANDROID
// https://developer.android.com/guide/topics/data/data-storage.html#filesInternal

public class AESFileEncryption {

    public byte[] encrypt(String code, byte[] value) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidParameterSpecException, InvalidKeyException, InvalidKeySpecException {
        return encrypt_decrypt(code, value, Cipher.ENCRYPT_MODE);
    }

    public byte[] decrypt(String code, byte[] value) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidParameterSpecException, InvalidKeyException, InvalidKeySpecException {
        return encrypt_decrypt(code, value, Cipher.DECRYPT_MODE);
    }

    private byte[] encrypt_decrypt(String code, byte[] value, int mode) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, BadPaddingException, IllegalBlockSizeException {
        byte[] key = makeHash(code);
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        SecretKey secret = new SecretKeySpec(key, "AES");
        byte [] iv = "1234567812345678".getBytes();
        System.out.println("IV = " + Arrays.toString(iv));
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        try {
            cipher.init(mode, secret, new IvParameterSpec(iv));
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return cipher.doFinal(value);
    }

    private byte[] makeHash(String code) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        ByteArrayOutputStream opBuffer = new ByteArrayOutputStream();
        DataOutputStream message = new DataOutputStream(opBuffer);
        message.writeBytes(code);
        return digest.digest(opBuffer.toByteArray());
    }
}
