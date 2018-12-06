 package gr.uom.android.lesson_9;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


// + onoma polis kai ton kairo xwris tin imerominia
 // saved instance gia na min diavazei 50 fores ton kairo naoume :/
 // savedInstanceState koita edw sto repo 3
 // vale kai time task scheduler h kati tetoio na oume

 public class MainActivity extends AppCompatActivity {

    private EditText txtName;
    private Button btnMain;

     private static final String TAG = "MainActivity";

    public static final String NAME_STRING = "name";
    private Fragment mWeatherFragment;

     @Override
     protected void onSaveInstanceState(Bundle outState) {

         Log.d(TAG, "onSaveInstanceState: in onSaveInstanceState, saving fragment instance...");
         getSupportFragmentManager().putFragment(outState, "weatherFragment", mWeatherFragment);
         Log.d(TAG, "onSaveInstanceState: fragment Instance saved");
         super.onSaveInstanceState(outState);
     }

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState != null) {
            mWeatherFragment = getSupportFragmentManager().getFragment(savedInstanceState, "weatherFragment");
        }

        txtName = findViewById(R.id.txtName);
        btnMain = findViewById(R.id.mainButton);
        mWeatherFragment = getSupportFragmentManager().findFragmentById(R.id.weatherFragment);



        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, SecondActivity.class);
                i.putExtra(NAME_STRING, txtName.getText().toString());
                startActivity(i);
            }
        });

    }
}
