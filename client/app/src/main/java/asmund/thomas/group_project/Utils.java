package asmund.thomas.group_project;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

class Utils {
    private static final String host="10.0.2.2";
    private static final String port="8080";
    private static final String BASE_URL="http://"+host+":"+port+"/";
    public static final String LOGIN_URL=BASE_URL+"methodPostRemoteLogin";
    public static final String CLAIMS_REQUEST_URL=BASE_URL+"getMethodMyClaims";


    public static String md5(String input) {
        return new String(Hex.encodeHex(DigestUtils.md5(input)));
    }
}
