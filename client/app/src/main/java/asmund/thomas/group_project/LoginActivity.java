package asmund.thomas.group_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {
    EditText usernameEditText, passwordEditText;
    Button loginButton;
    ProgressBar spinner;

    final String LOGIN_URL = "http://10.0.2.2:8080/methodPostRemoteLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEditText = findViewById(R.id.username_input);
        passwordEditText = findViewById(R.id.password_input);
        spinner = findViewById(R.id.progressBar);
        loginButton = findViewById(R.id.login_button);

    }

    public void verifyLogin(View view) {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        loginButton.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(this);
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                loginButton.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.INVISIBLE);
                if (response != null) {
                    Gson g = new Gson();
                    Person person = g.fromJson(response, Person.class);
                    final SharedPreferences.Editor editor = getSharedPreferences("MySharedPref", MODE_PRIVATE).edit();
                    editor.putString("user", response);
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
                loginButton.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.INVISIBLE);
            }
        };
        LoginRequest loginRequest = new LoginRequest(username, Utils.md5(password), listener, errorListener);
        queue.add(loginRequest);


    }
}
