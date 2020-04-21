package asmund.thomas.group_project;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ClaimsRequest extends StringRequest {
    private static final String CLAIMS_REQUEST_URL = "http://10.0.2.2:8080/getMethodMyClaims";

    private Map<String, String> params;

    public ClaimsRequest(String id, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Method.GET, CLAIMS_REQUEST_URL, listener , errorListener);
        params = new HashMap<>();
        params.put("id", id);
    }

    @Override
    public Map<String, String> getParams() { return params; }
}

