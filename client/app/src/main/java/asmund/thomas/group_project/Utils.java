package asmund.thomas.group_project;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

class Utils {

    public static String md5(String input) {
        return new String(Hex.encodeHex(DigestUtils.md5(input)));
    }
}
