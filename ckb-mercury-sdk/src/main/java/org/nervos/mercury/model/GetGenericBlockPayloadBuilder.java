package org.nervos.mercury.model;

import java.math.BigInteger;
import org.nervos.mercury.model.req.GetGenericBlockPayload;

/** @author zjh @Created Date: 2021/7/20 @Description: @Modify by: */
public class GetGenericBlockPayloadBuilder extends GetGenericBlockPayload {

  public void blockNumber(BigInteger blockNumber) {
    this.blockNumber = blockNumber;
  }

  public void blockHash(String blockHash) {
    this.blockHash = blockHash;
  }

  public GetGenericBlockPayload build() {
    return this;
  }
}
