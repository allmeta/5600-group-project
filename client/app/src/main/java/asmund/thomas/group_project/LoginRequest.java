package asmund.thomas.group_project;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "http://10.0.2.2:8080/methodPostRemoteLogin";
    private Map<String, String> params;

    public LoginRequest(String username, String password, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Method.POST, LOGIN_REQUEST_URL, listener , errorListener);
        params = new HashMap<>();
        params.put("em", username);
        params.put("ph", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
