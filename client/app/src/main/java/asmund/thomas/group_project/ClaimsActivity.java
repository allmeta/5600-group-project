package asmund.thomas.group_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClaimsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LayoutInflater layoutInflater;
    private RecyclerView.LayoutManager layoutManager;
    private PopupWindow popupWindow;

    static final int CLAIMS_ITEMS_ARRAY_SIZE = 5;
    private List<Claim> claimList;

    private View.OnClickListener listOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            Claim claim = claimList.get(position);
            Intent intent = new Intent(getApplicationContext(), ViewClaimActivity.class);
            // Pass claim to new activity
            intent.putExtra("claim",new Gson().toJson(claim));
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
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String personJsonString = sh.getString("user", "");
        Gson g = new Gson();
        Person person = g.fromJson(personJsonString, Person.class);
        getClaimsForPerson(person);
    }

    private void getClaimsForPerson(Person person) {
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
                        for(int i = 0; i < claimIds.length(); i++){
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
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
        String urlWithIdParam = Utils.CLAIMS_REQUEST_URL + "?id="+person.id;
        StringRequest claimsRequest = new StringRequest(urlWithIdParam, listener, errorListener);
        queue.add(claimsRequest);
    }

    public void openNewClaimWindow(View view) {
        LayoutInflater layoutInflater = getLayoutInflater();
        ViewGroup container = (ViewGroup)layoutInflater.inflate(R.layout.popup_new_claim, null);
        int width = recyclerView.getWidth();
        int height = recyclerView.getHeight();

        popupWindow = new PopupWindow(container, width-width/4, height-height/2, true);
        popupWindow.showAtLocation(recyclerView, Gravity.CENTER, 0, 0);
    }

    public void addClaim(View view) {
        RequestQueue queue = Volley.newRequestQueue(this);
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);

                if (response != null) {
                    Toast.makeText(getApplicationContext(), "Response is: " + response, Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Invalid password",Toast.LENGTH_LONG).show();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"could not contact server",Toast.LENGTH_LONG).show();
                System.out.println(error.getMessage());
            }
        };
        HashMap<String, String> params = new HashMap<>();

        params.put("userId", "0");
        params.put("indexUpdateClaim", "1");
        params.put("newClaimDes", "Første claim");
        params.put("newClaimPho", "/photo/test");
        params.put("newClaimLoc", "koordinater");

        CustomRequest insertNewClaimRequest= new CustomRequest(Request.Method.POST, Utils.INSERT_NEW_CLAIM_URL,  params, listener, errorListener);
        queue.add(insertNewClaimRequest);
    }
}
