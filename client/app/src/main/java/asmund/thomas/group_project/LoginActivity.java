package asmund.thomas.group_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    EditText usernameEditText, passwordEditText;
    Button loginButton;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEditText = findViewById(R.id.username_input);
        passwordEditText = findViewById(R.id.password_input);
        spinner = findViewById(R.id.progressBar);
        loginButton = findViewById(R.id.login_button);
        System.out.println(System.getenv("host"));
    }

    public void verifyLogin(View view) {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if(username.equals("") || password.equals("")){
            Toast.makeText(this, "Enter username and password ", Toast.LENGTH_SHORT).show();
            return;
        }
        loginButton.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(this);
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                loginButton.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);
                if (response != null) {
                    Gson g = new Gson();
                    Person person = g.fromJson(response, Person.class);
                    if(person != null){
                        Utils.savePerson(person,getApplicationContext());
                        Intent intent = new Intent(getApplicationContext(), ClaimsActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(),"Invalid password or username",Toast.LENGTH_LONG).show();
                    }
                }

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"could not contact server",Toast.LENGTH_LONG).show();
                System.out.println(error.getMessage());
                loginButton.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);
            }
        };
        HashMap<String, String> params = new HashMap<>();
        params.put("em", username);
        params.put("ph", Utils.md5(password));
        CustomRequest loginRequest= new CustomRequest(Request.Method.POST,Utils.LOGIN_URL,  params, listener, errorListener);
        queue.add(loginRequest);

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
