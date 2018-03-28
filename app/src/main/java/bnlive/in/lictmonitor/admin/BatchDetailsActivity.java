package bnlive.in.lictmonitor.admin;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;

import bnlive.in.lictmonitor.R;
import bnlive.in.lictmonitor.model.MergeSheduleUniversity;
import bnlive.in.lictmonitor.model.UniversityDetailsModel;

public class BatchDetailsActivity extends AppCompatActivity {
private TextView university;
private TextView batchcode;
private TextView status;
private TextView location;
private TextView address;
private TextView lat_long;
private TextView trainername;
private TextView trainercontact;
private ImageButton callbtn;
private MergeSheduleUniversity data;
private UniversityDetailsModel umodel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_details);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        batchcode=findViewById(R.id.textView4);
        university=findViewById(R.id.textView6);
        status=findViewById(R.id.textView8);
        address=findViewById(R.id.textView10);
        location=findViewById(R.id.textView12);
        lat_long=findViewById(R.id.textView14);
        trainername=findViewById(R.id.textView15);
        trainercontact=findViewById(R.id.textView16);
        callbtn=findViewById(R.id.imageButton);

        Gson gson=new Gson();
        data=gson.fromJson(getIntent().getStringExtra("data"),MergeSheduleUniversity.class);
        umodel=data.getUniversity();
        batchcode.setText(data.getBatchCode());
        university.setText(umodel.getUniversity_name());
        status.setText(data.getStatus());
        address.setText(umodel.getAddress());
        location.setText(umodel.getLocation());
        lat_long.setText(umodel.getLat_long());
        trainername.setText("Not Set");
        trainercontact.setText("Not Set");
        callbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:break;
                    case MotionEvent.ACTION_UP:break;
                    case MotionEvent.ACTION_DOWN:break;
                }
                return false;
            }
        });

    }

    public MergeSheduleUniversity getData() {
        return data;
    }

    public void setData(MergeSheduleUniversity data) {
        this.data = data;
    }
}
