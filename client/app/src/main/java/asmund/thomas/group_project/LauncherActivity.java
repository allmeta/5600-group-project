package asmund.thomas.group_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        new android.os.Handler().postDelayed(
            new Runnable() {
                public void run() {
                    if(getSharedPreferences("MySharedPref", MODE_PRIVATE).contains("person")){
                        // go to claims instead
                        Intent intent = new Intent(getApplicationContext(), ClaimsActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }

                }
            }
            ,500 );
    }
}
