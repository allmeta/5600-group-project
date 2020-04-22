package asmund.thomas.group_project;

import android.content.Context;
import static android.content.Context.MODE_PRIVATE;
import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

class Utils {
    private static final String host="10.0.2.2";
    private static final String port="8080";
    private static final String BASE_URL="http://"+host+":"+port+"/";
    public static final String LOGIN_URL=BASE_URL+"methodPostRemoteLogin";
    public static final String CLAIMS_REQUEST_URL=BASE_URL+"getMethodMyClaims";
    public static final String INSERT_NEW_CLAIM_URL = BASE_URL + "postInsertNewClaim";


    public static String md5(String input) {
        return new String(Hex.encodeHex(DigestUtils.md5(input)));
    }
    public static void logout(Context c){
        c.getSharedPreferences("mySharedPref",MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(c, LoginActivity.class);
        c.startActivity(intent);
    }
    public static LatLng locToLatLng(String s){
        String[] latlong=s.split(",");
        return new LatLng(Double.parseDouble(latlong[0]),Double.parseDouble(latlong[1]));

    }
}
