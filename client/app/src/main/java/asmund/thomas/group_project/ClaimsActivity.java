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
        claimList = getClaimsForPerson(person);

        adapter = new ClaimAdapter(claimList, listOnClickListener);
        recyclerView.setAdapter(adapter);

    }

    private List<Claim> getClaimsForPerson(Person person) {
        RequestQueue queue = Volley.newRequestQueue(this);
        ArrayList list = new ArrayList<Claim>();
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray claimIds = jsonObject.getJSONArray("claimId");
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
        return list;
    }
}
