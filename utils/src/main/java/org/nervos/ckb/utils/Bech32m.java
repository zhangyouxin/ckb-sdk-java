package org.nervos.ckb.utils;

/*
 * Copyright 2018 Coinomi Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import org.nervos.ckb.exceptions.AddressFormatException;

/*
 * Copyright 2018 Coinomi Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/core/Bech32.java
 */

public class Bech32m {
  /** The Bech32 character set for encoding. */
  private static final String CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

  private static final int BECH32M_CONST = 0x2bc830a3;

  private static final int[] GENERATOR =
      new int[] {0x3b6a57b2, 0x26508e6d, 0x1ea119fa, 0x3d4233dd, 0x2a1462b3};

  /** The Bech32 character set for decoding. */
  private static final byte[] CHARSET_REV = {
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    15, -1, 10, 17, 21, 20, 26, 30, 7, 5, -1, -1, -1, -1, -1, -1,
    -1, 29, -1, 24, 13, 25, 9, 8, 23, -1, 18, 22, 31, 27, 19, -1,
    1, 0, 3, 16, 11, 28, 12, 14, 6, 4, 2, -1, -1, -1, -1, -1,
    -1, 29, -1, 24, 13, 25, 9, 8, 23, -1, 18, 22, 31, 27, 19, -1,
    1, 0, 3, 16, 11, 28, 12, 14, 6, 4, 2, -1, -1, -1, -1, -1
  };

  public static class Bech32Data {
    public final String hrp;
    public final byte[] data;

    public Bech32Data(final String hrp, final byte[] data) {
      this.hrp = hrp;
      this.data = data;
    }
  }

  /** Find the polynomial with value coefficients mod the generator as 30-bit. */
  private static int polymod(final byte[] values) {
    int chk = 1;
    for (byte v_i : values) {
      int top = (chk >> 25);
      chk = (chk & 0x1ffffff) << 5 ^ v_i;
      for (int i = 0; i < 6; i++) {
        if (((top >> i) & 1) == 0 ? false : true) {
          chk ^= GENERATOR[i];
        } else {
          chk ^= 0;
        }
      }
    }
    return chk;
  }

  /** Expand a HRP for use in checksum computation. */
  private static byte[] expandHrp(final String hrp) {
    int hrpLength = hrp.length() * 2 + 1;
    int index = 0;
    byte[] ret = new byte[hrpLength];

    for (char ch : hrp.toCharArray()) {
      ret[index] = (byte) (ord(ch) >> 5);
      index++;
    }

    ret[index] = (byte) 0;
    index++;

    for (char ch : hrp.toCharArray()) {
      ret[index] = (byte) (ord(ch) & 31);
      index++;
    }

    return ret;
  }

  private static int ord(String s) {
    return s.length() > 0 ? (s.getBytes(StandardCharsets.UTF_8)[0] & 0xff) : 0;
  }

  private static int ord(char c) {
    return c < 0x80 ? c : ord(Character.toString(c));
  }

  /** Verify a checksum. */
  public static boolean verifyChecksum(final String hrp, final byte[] values) {
    byte[] hrpExpanded = expandHrp(hrp);
    byte[] combined = new byte[hrpExpanded.length + values.length];
    System.arraycopy(hrpExpanded, 0, combined, 0, hrpExpanded.length);
    System.arraycopy(values, 0, combined, hrpExpanded.length, values.length);
    return polymod(combined) == BECH32M_CONST;
  }

  /** Create a checksum. */
  private static byte[] createChecksum(final String hrp, final byte[] values) {
    byte[] hrpExpanded = expandHrp(hrp);
    byte[] enc = new byte[hrpExpanded.length + values.length + 6];
    System.arraycopy(hrpExpanded, 0, enc, 0, hrpExpanded.length);
    System.arraycopy(values, 0, enc, hrpExpanded.length, values.length);

    int polymod = polymod(enc) ^ BECH32M_CONST;
    byte[] ret = new byte[6];
    for (int i = 0; i < 6; ++i) {
      ret[i] = (byte) ((polymod >> 5 * (5 - i)) & 31);
    }
    return ret;
  }

  /** Encode a Bech32 string. */
  public static String encode(final Bech32Data bech32) {
    return encode(bech32.hrp, bech32.data);
  }

  /** Encode a Bech32 string. */
  public static String encode(String hrp, byte[] values) {
    if (hrp.length() < 1)
      throw new AddressFormatException.InvalidDataLength(
          "Human-readable part is too short: " + hrp.length());
    if (hrp.length() > 83)
      throw new AddressFormatException.InvalidDataLength(
          "Human-readable part is too long: " + hrp.length());

    hrp = hrp.toLowerCase(Locale.ROOT);

    byte[] checksum = createChecksum(hrp, values);
    byte[] combined = new byte[values.length + checksum.length];

    System.arraycopy(values, 0, combined, 0, values.length);
    System.arraycopy(checksum, 0, combined, values.length, checksum.length);
    StringBuilder sb = new StringBuilder(hrp.length() + 1 + combined.length);

    sb.append(hrp);
    sb.append('1');
    for (byte b : combined) {
      sb.append(CHARSET.charAt(b));
    }
    return sb.toString();
  }

  /** Decode a Bech32 string. */
  public static Bech32Data decode(final String str) throws AddressFormatException {
    boolean lower = false, upper = false;
    if (str.length() < 8)
      throw new AddressFormatException.InvalidDataLength("Input too short: " + str.length());
    for (int i = 0; i < str.length(); ++i) {
      char c = str.charAt(i);
      if (c < 33 || c > 126) throw new AddressFormatException.InvalidCharacter(c, i);
      if (c >= 'a' && c <= 'z') {
        if (upper) throw new AddressFormatException.InvalidCharacter(c, i);
        lower = true;
      }
      if (c >= 'A' && c <= 'Z') {
        if (lower) throw new AddressFormatException.InvalidCharacter(c, i);
        upper = true;
      }
    }
    final int pos = str.lastIndexOf('1');
    if (pos < 1) throw new AddressFormatException.InvalidPrefix("Missing human-readable part");
    final int dataPartLength = str.length() - 1 - pos;
    if (dataPartLength < 6)
      throw new AddressFormatException.InvalidDataLength("Data part too short: " + dataPartLength);
    byte[] values = new byte[dataPartLength];
    for (int i = 0; i < dataPartLength; ++i) {
      char c = str.charAt(i + pos + 1);
      if (CHARSET_REV[c] == -1) throw new AddressFormatException.InvalidCharacter(c, i + pos + 1);
      values[i] = CHARSET_REV[c];
    }
    String hrp = str.substring(0, pos).toLowerCase(Locale.ROOT);
    if (!Bech32m.verifyChecksum(hrp, values)) throw new AddressFormatException.InvalidChecksum();
    return new Bech32Data(hrp, Arrays.copyOfRange(values, 0, values.length - 6));
  }
}
