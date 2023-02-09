package com.appops.realmrnd.secure.key;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * This Class provides secure key
 * The secure key will be stored in android shared preferences with encryption
 *
 * @author julkar nain
 * @since 3/25/19
 */
public class SecureKeyProvider {

    private static final String PREFERENCES_KEY = "secure_preferences";
    private KeystoreKeyProvider encryptionProvider;
    private Context context;

    public SecureKeyProvider(Context context) {
        this.context = context;
        encryptionProvider = new KeystoreKeyProvider(context);
    }

    public SecureKeyProvider(Context context, String alias) {
        this.context = context;
        encryptionProvider = new KeystoreKeyProvider(context, alias);
    }

    /**
     * This method provide secure encryption key
     *
     * @param keySize (bit)
     * @param preferencesKey
     *
     * @return key
     */
    public byte[] getSecureKey(int keySize, String preferencesKey) {
        String key = getSharedPreference().getString(preferencesKey, null);

        if (TextUtils.isEmpty(key)) {
            return createSecureKey(keySize, preferencesKey);
        }

        return encryptionProvider.decrypt(key);
    }

    private void saveSecureKey(byte[] key, String preferencesKey) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putString(preferencesKey, encryptionProvider.encrypt(key));
        editor.apply();
    }

    private byte[] createSecureKey(int keySize, String preferencesKey) {
        byte[] key = new byte[64]; // 64*8 = 512 -> keySize

        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");

            keyGen.init(128);

            SecretKey key1 = keyGen.generateKey();
            SecretKey key2 = keyGen.generateKey();
            SecretKey key3 = keyGen.generateKey();
            SecretKey key4 = keyGen.generateKey();


            System.arraycopy(key1.getEncoded(), 0, key, 0, 16);
            System.arraycopy(key2.getEncoded(), 0, key, 16, 16);
            System.arraycopy(key3.getEncoded(), 0, key, 32, 16);
            System.arraycopy(key4.getEncoded(), 0, key, 48, 16);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        saveSecureKey(key, preferencesKey);
        return key;
    }

    private SharedPreferences getSharedPreference() {
        return context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }
}