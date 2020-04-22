package asmund.thomas.group_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

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

    private PopupWindow popupWindow;
    private EditText claimDesEditText;
    private ImageView claimImage;
    String currentPhotoPath;

    private List<Claim> claimList;

    SharedPreferences sh;
    Person currentUser;

    private View.OnClickListener listOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            Claim claim = claimList.get(position);
            System.out.println(claim.des);
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

        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String personJsonString = sh.getString("user", "");
        Gson g = new Gson();
        currentUser = g.fromJson(personJsonString, Person.class);
        getClaimsForPerson(currentUser.getId());
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
                        currentUser.setNumberOfClaims(jsonObject.getInt("numberOfClaims"));
                        JSONArray claimIds = jsonObject.getJSONArray("claimId");
                        JSONArray claimDescriptions = jsonObject.getJSONArray("claimDes");
                        JSONArray claimPhotos = jsonObject.getJSONArray("claimPhoto");
                        JSONArray claimLocations = jsonObject.getJSONArray("claimLocation");
                        for (int i = 0; i < claimIds.length(); i++) {
                            String claimId = claimIds.getString(i);
                            String claimDes = claimDescriptions.getString(i);
                            String claimPhoto = claimPhotos.getString(i);
                            String claimLocation = claimLocations.getString(i);
                            Claim c = new Claim(claimId, claimDes, claimPhoto, claimLocation);
                            claimList.add(i, c);
                        }
                        adapter = new ClaimAdapter(claimList, listOnClickListener);
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println("claims: " + currentUser.getNumberOfClaims());
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
        LayoutInflater layoutInflater = getLayoutInflater();
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.popup_new_claim, null);
        claimDesEditText = container.findViewById(R.id.claim_des_et);
        claimImage = container.findViewById(R.id.claim_iv);
        int width = recyclerView.getWidth();
        int height = recyclerView.getHeight();

        popupWindow = new PopupWindow(container, width - width / 4, height - height / 2, true);
        popupWindow.showAtLocation(recyclerView, Gravity.CENTER, 0, 0);
    }

    public void addClaim(View view) {
        String description = claimDesEditText.getText().toString();

        if(currentPhotoPath == null || description.equals("")){
            Toast.makeText(this, "Please fill out description and take photo!", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);

                if (response != "OK") {
                    Toast.makeText(getApplicationContext(), "Claim added!", Toast.LENGTH_LONG).show();
                }

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "could not contact server", Toast.LENGTH_LONG).show();
                System.out.println(error.getMessage());
            }
        };
        HashMap<String, String> params = new HashMap<>();

        params.put("userId", currentUser.getId());
        params.put("indexUpdateClaim", currentUser.getNumberOfClaims() + "");
        params.put("newClaimDes", claimDesEditText.getText().toString());
        params.put("newClaimPho", currentPhotoPath);
        params.put("newClaimLoc", "koordinater");

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
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "asmund.thomas.group_project.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Utils.REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
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
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            claimImage.setImageBitmap(imageBitmap);
        }
    }

}
