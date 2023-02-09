package com.appops.realmrnd.secure.encryption;

public interface NonReversibleData {
    String getSalt();
    String getEncryptedData();
    void setSalt(String salt);
    void setEncryptedData(String encryptedData);
}