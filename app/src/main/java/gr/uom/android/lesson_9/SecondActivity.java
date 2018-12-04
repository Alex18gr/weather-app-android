package gr.uom.android.lesson_9;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    private TextView txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        txtName = findViewById(R.id.txtHello);

        Intent intent = getIntent();

        String name = intent.getStringExtra(MainActivity.NAME_STRING);

        if(name != null) {
            txtName.setText("Hello " + name);
        }


    }
}
