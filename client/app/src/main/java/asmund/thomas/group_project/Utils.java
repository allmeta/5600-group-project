package asmund.thomas.group_project;

import android.content.Context;
import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class Utils {
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static int CURRENT_USER;
    private static final String host="10.0.2.2";
    private static final String port="8080";
    private static final String BASE_URL="http://"+host+":"+port+"/";
    public static final String LOGIN_URL=BASE_URL+"methodPostRemoteLogin";
    public static final String CLAIMS_REQUEST_URL=BASE_URL+"getMethodMyClaims";
    public static final String INSERT_NEW_CLAIM_URL = BASE_URL + "postInsertNewClaim";
    public static final String UPDATE_CLAIM_URL = BASE_URL + "postUpdateClaim";


    public static String md5(String input) {
        return new String(Hex.encodeHex(DigestUtils.md5(input)));
    }
    public static void setUser(Integer user){
        CURRENT_USER=user;
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
    public static void saveClaims(List<Claim> claims,Context c){
        SharedPreferences.Editor sh=c.getSharedPreferences("mySharedPref",MODE_PRIVATE).edit();
        Gson gson=new Gson();
        Type listType= new TypeToken<List<Claim>>(){}.getType();
        String json=gson.toJson(claims,listType);
        sh.putString("claims",json);
        sh.apply();
    }
    public static List<Claim> loadClaims(Context c){
        String json=c.getSharedPreferences("mySharedPref",MODE_PRIVATE).getString("claims",null);
        if (json==null) {return null;}
        Type listType= new TypeToken<List<Claim>>(){}.getType();
        return new Gson().fromJson(json,listType);
    }
    public static Bitmap loadImageFromFile(String currentPhotoPath, int targetW, int targetH) {

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        //bitmap = imageOreintationValidator(bitmap, currentPhotoPath);
        return bitmap;
    }
}
