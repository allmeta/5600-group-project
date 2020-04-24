package asmund.thomas.group_project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ClaimsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    LocationManager locationManager;
    private PopupWindow popupWindow;
    private EditText claimDesEditText;
    private ImageView claimImage;
    private TextView noClaimsTextView;
    String currentPhotoPath;
    GPSTracker gps;

    private List<Claim> claimList;

    SharedPreferences sh;
    Person currentUser;

    private View.OnClickListener listOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            Claim claim = claimList.get(position);
            Intent intent = new Intent(getApplicationContext(), ViewClaimActivity.class);
            // Pass claim to new activity
            intent.putExtra("claim", new Gson().toJson(claim));
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claims);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        noClaimsTextView = findViewById(R.id.no_claims_tv);

        gps = new GPSTracker();
        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String personJsonString = sh.getString("person", "");
        Gson g = new Gson();
        currentUser = g.fromJson(personJsonString, Person.class);

        claimList = Utils.loadClaims(getApplicationContext());
        if (claimList == null) {
            getClaimsForPerson(currentUser.getId());
        } else {
            noClaimsTextView.setVisibility(View.INVISIBLE);
            adapter = new ClaimAdapter(claimList, listOnClickListener);
            recyclerView.setAdapter(adapter);
        }
    }

    private void getClaimsForPerson(String personId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                claimList = new ArrayList<>();
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray claimIds = jsonObject.getJSONArray("claimId");
                        JSONArray claimDescriptions = jsonObject.getJSONArray("claimDes");
                        JSONArray claimPhotos = jsonObject.getJSONArray("claimPhoto");
                        JSONArray claimLocations = jsonObject.getJSONArray("claimLocation");
                        for (int i = 0; i < claimIds.length(); i++) {
                            String claimId = claimIds.getString(i);
                            if (!claimId.equals("na")) {
                                String claimDes = claimDescriptions.getString(i);
                                String claimPhoto = claimPhotos.getString(i);
                                String claimLocation = claimLocations.getString(i);
                                Claim c = new Claim(claimId, claimDes, claimPhoto, claimLocation);
                                claimList.add(i, c);
                            }
                        }
                        if (claimList.size() == 0) {
                            noClaimsTextView.setVisibility(View.VISIBLE);
                        } else {
                            adapter = new ClaimAdapter(claimList, listOnClickListener);
                            recyclerView.setAdapter(adapter);
                        }
                        Utils.saveClaims(claimList, getApplicationContext());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
        String urlWithIdParam = Utils.CLAIMS_REQUEST_URL + "?id=" + personId;
        StringRequest claimsRequest = new StringRequest(urlWithIdParam, listener, errorListener);
        queue.add(claimsRequest);
    }

    public void openNewClaimWindow(View view) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestMultiplePermissions();
            return;
        }
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 5000, 10, gps);
        LayoutInflater layoutInflater = getLayoutInflater();
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.popup_new_claim, null);
        claimDesEditText = container.findViewById(R.id.claim_des_et);
        claimImage = container.findViewById(R.id.claim_iv);
        int width = recyclerView.getWidth();
        int height = recyclerView.getHeight();

        popupWindow = new PopupWindow(container, width - width / 4, height - height / 2, true);
        popupWindow.showAtLocation(recyclerView, Gravity.CENTER, 0, 0);
    }

    private void requestMultiplePermissions() {
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

    public void addClaim(View view) {
        if (claimList.size() >= Utils.MAX_NUMBER_OF_CLAIMS) {
            Toast.makeText(this, "Max number of claims reached", Toast.LENGTH_SHORT).show();
            return;
        }
        String description = claimDesEditText.getText().toString();

        if (currentPhotoPath == null || description.equals("")) {
            Toast.makeText(this, "Please fill out description and take photo!", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != "OK") {
                    Toast.makeText(getApplicationContext(), "Claim added!", Toast.LENGTH_LONG).show();
                    Claim c = new Claim(claimList.size() + "", claimDesEditText.getText().toString(), currentPhotoPath, gps.getLatitude() + "," + gps.getLongitude());
                    claimList.add(claimList.size(), c);
                    Utils.saveClaims(claimList, getApplicationContext());
                    adapter = new ClaimAdapter(claimList, listOnClickListener);
                    recyclerView.setAdapter(adapter);
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
        params.put("indexUpdateClaim", claimList.size() + "");
        params.put("newClaimDes", claimDesEditText.getText().toString());
        params.put("newClaimPho", currentPhotoPath);
        params.put("newClaimLoc", gps.getLatitude() + "," + gps.getLongitude());

        CustomRequest insertNewClaimRequest = new CustomRequest(Request.Method.POST, Utils.INSERT_NEW_CLAIM_URL, params, listener, errorListener);
        queue.add(insertNewClaimRequest);
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
        return image;
    }

    public void alertDialog(String message, String buttonName, final String settings, final Uri uri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setNegativeButton(buttonName, new DialogInterface.OnClickListener() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = Utils.loadImageFromFile(currentPhotoPath, claimImage.getWidth(), claimImage.getHeight());
            claimImage.setImageBitmap(bitmap);
        }
    }

    public void logout(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?\nThis will erase all local data.")
                .setIcon(android.R.drawable.ic_menu_help)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getSharedPreferences("MySharedPref", MODE_PRIVATE).edit().clear().commit();
                        Intent intent = new Intent(getApplicationContext(), LauncherActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
