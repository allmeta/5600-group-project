package asmund.thomas.group_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;

public class ViewClaimActivity extends AppCompatActivity {
    Claim claim;
    TextView claimIdText;
    TextView desc;
    ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_claim);
        claim=new Gson().fromJson(getIntent().getStringExtra("claim"),Claim.class);
        claimIdText = findViewById(R.id.claimid);
        desc = findViewById(R.id.desc);
        photo=findViewById(R.id.photo);

        claimIdText.setText(String.format("Claim id: %s", claim.id));
        desc.setText(claim.des);
        Bitmap p=Utils.loadImageFromFile(claim.photo,photo.getMaxWidth(),photo.getMaxHeight());
        if(p!=null){
            photo.setImageBitmap(p);
        }
        else{
            Toast.makeText(getApplicationContext(), "Image not found: "+claim.photo, Toast.LENGTH_SHORT).show();
        }
    }
    public void edit(View view){
        Intent intent = new Intent(getApplicationContext(), EditClaimActivity.class);
        intent.putExtra("claim",new Gson().toJson(claim));
        startActivity(intent);
    }
    public void map(View view){
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        intent.putExtra("location",claim.getLocation());
        startActivity(intent);
    }
    public void onBackPressed() {
        Intent intent=new Intent(getApplicationContext(),ClaimsActivity.class);
        startActivity(intent);
    }
}
