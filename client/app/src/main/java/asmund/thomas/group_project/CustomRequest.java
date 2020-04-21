package asmund.thomas.group_project;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

public class CustomRequest extends StringRequest {
    private Map<String, String> params;

    public CustomRequest(int method, String url, Map<String, String> params, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.params = params;
    }
    @Override
    public Map<String, String> getParams() { return params; }
}
