package asmund.thomas.group_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;

public class EditClaimActivity extends AppCompatActivity {

    Claim claim;
    EditText desc;
    ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_claim);
        claim=new Gson().fromJson(getIntent().getStringExtra("claim"),Claim.class);
        desc = findViewById(R.id.desc);
        photo=findViewById(R.id.photo);
    }
    public void editFinish(View view){
        Claim newClaim=new Claim(claim.id,desc.getText().toString(),"new photo path",claim.location);
        //Update server
        //update local
        //switch to view


        Intent intent = new Intent(getApplicationContext(), ViewClaimActivity.class);
        intent.putExtra("claim",new Gson().toJson(claim));
        startActivity(intent);

    }
}
