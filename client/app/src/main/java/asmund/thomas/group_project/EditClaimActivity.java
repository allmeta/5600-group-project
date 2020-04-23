package asmund.thomas.group_project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class EditClaimActivity extends AppCompatActivity {

    Claim claim;
    EditText desc;
    ImageView photo;
    private String currentPhotoPath;
    private Person currentUser;
    LocationManager locationManager;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_claim);
        claim=new Gson().fromJson(getIntent().getStringExtra("claim"),Claim.class);
        desc = findViewById(R.id.desc);
        photo=findViewById(R.id.photo);

        desc.setText(claim.des);
        Bitmap p=Utils.loadImageFromFile(claim.photo,photo.getMaxWidth(),photo.getMaxHeight());
        if(p!=null){
            photo.setImageBitmap(p);
        }
        else{
            Toast.makeText(getApplicationContext(), "Image not found: "+claim.photo, Toast.LENGTH_SHORT).show();
        }
        String personJsonString = getSharedPreferences("MySharedPref", MODE_PRIVATE) .getString("user", "");
        Gson g = new Gson();
        currentUser = g.fromJson(personJsonString, Person.class);
        gps = new GPSTracker();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestMultiplePermissions();
            return;
        }
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 5000, 10, gps);
    }
    public void editFinish(View view){
        Claim newClaim=new Claim(claim.id,desc.getText().toString(),currentPhotoPath,gps.getLatitude()+","+gps.getLongitude());
        //update local
        List<Claim> claims=Utils.loadClaims(getApplicationContext());
        claims.remove(Integer.parseInt(claim.id));
        claims.add(Integer.parseInt(claim.id),newClaim);
        Utils.saveClaims(claims,getApplicationContext());
        //Update server
        RequestQueue queue = Volley.newRequestQueue(this);
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != "OK") {
                    Toast.makeText(getApplicationContext(), "Claim updated!", Toast.LENGTH_LONG).show();
                }

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "could not contact server", Toast.LENGTH_LONG).show();
            }
        };

        HashMap<String, String> params = new HashMap<>();

        params.put("userId", currentUser.getId());
        params.put("indexUpdateClaim", newClaim.id);
        params.put("updateClaimDes", newClaim.des);
        params.put("updateClaimPho", newClaim.photo);
        params.put("updateClaimLoc", newClaim.location);

        CustomRequest insertNewClaimRequest = new CustomRequest(Request.Method.POST, Utils.UPDATE_CLAIM_URL, params, listener, errorListener);
        queue.add(insertNewClaimRequest);
        //switch to view
        Intent intent = new Intent(getApplicationContext(), ViewClaimActivity.class);
        intent.putExtra("claim",new Gson().toJson(newClaim));
        startActivity(intent);

    }

    public void openCamera(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "asmund.thomas.group_project.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Utils.REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    // Create an image file for the image to be stored in
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        System.out.println(currentPhotoPath);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = Utils.loadImageFromFile(currentPhotoPath, photo.getMaxWidth(), photo.getMaxHeight());
            photo.setImageBitmap(bitmap);
        }
    }
    private void requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions accepted", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            alertDialog("Missing permissions! Activate location and storage services",
                                    "Settings", Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));

                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
    public void alertDialog(String message, String buttonName, final String settings, final Uri uri){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setNegativeButton(buttonName, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (settings == null)
                            return;
                        Intent intent = new Intent(settings);
                        if (uri != null)
                            intent.setData(uri);
                        startActivityForResult(intent, 233);
                    }
                })
                .create()
                .show();
    }
}
