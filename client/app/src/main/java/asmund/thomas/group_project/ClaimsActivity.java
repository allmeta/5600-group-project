package asmund.thomas.group_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

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
import java.util.List;

public class ClaimsActivity extends AppCompatActivity {
    private List<Claim> claimList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LayoutInflater layoutInflater;
    private RecyclerView.LayoutManager layoutManager;
    private static final String CLAIMS_REQUEST_URL = "http://10.0.2.2:8080/getMethodMyClaims";

    static final int CLAIMS_ITEMS_ARRAY_SIZE = 5;



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
        setContentView(R.layout.layout_recycler_view);

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
        String urlWithIdParam = CLAIMS_REQUEST_URL + "?id="+person.id;
        StringRequest claimsRequest = new StringRequest(urlWithIdParam, listener, errorListener);
        queue.add(claimsRequest);
    }
}
