package ch.hsr.mge.gadgeothek;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class ExampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(getString(R.string.app_name), " Created!");
    }
}
