package asmund.thomas.group_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class LoginActivity extends AppCompatActivity {
    EditText usernameEditText, passwordEditText;
    TextView errorText;

    final String LOGIN_URL = "http://10.0.2.2:8080/methodPostRemoteLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEditText = findViewById(R.id.username_input);
        passwordEditText = findViewById(R.id.password_input);
        errorText = findViewById(R.id.error_textview);

    }

    public void verifyLogin(View view) {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        RequestQueue queue = Volley.newRequestQueue(this);
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                errorText.setText("Response is: "+ response);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                errorText.setText("That didn't work!");
                System.out.println(error.getMessage());
            }
        };
        LoginRequest loginRequest = new LoginRequest(username, password, listener, errorListener);
        queue.add(loginRequest);


    }
}