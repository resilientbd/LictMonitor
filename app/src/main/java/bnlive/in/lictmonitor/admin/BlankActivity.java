package bnlive.in.lictmonitor.admin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import bnlive.in.lictmonitor.R;

public class BlankActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);
        finish();
    }
}
