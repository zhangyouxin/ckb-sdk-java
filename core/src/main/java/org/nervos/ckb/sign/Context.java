package org.nervos.ckb.sign;

import com.google.gson.annotations.SerializedName;

public class Context {
  @SerializedName("private_key")
  private String privateKey;

  @SerializedName("payload")
  private Object payload;

  public Context(String privateKey) {
    this(privateKey, null);
  }

  public Context(String privateKey, Object payload) {
    this.privateKey = privateKey;
    this.payload = payload;
  }

  public String getPrivateKey() {
    return privateKey;
  }

  public void setPrivateKey(String ecPrivateKey) {
    this.privateKey = ecPrivateKey;
  }

  public Object getPayload() {
    return payload;
  }

  public void setPayload(Object payload) {
    this.payload = payload;
  }
}