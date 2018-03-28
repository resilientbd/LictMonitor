package bnlive.in.lictmonitor.admin;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import bnlive.in.lictmonitor.R;
import bnlive.in.lictmonitor.model.BatchStatusModel;

/**
 * Created by Sk Faisal on 3/25/2018.
 */

public class DashboardFragment extends Fragment{
    View view;
    Context context;
    private RecyclerView myListView;
    private RecyclerView.Adapter adapter;
    private List<BatchStatusModel> dataList;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseFirestore db;
    private String TAG="dashboardfragment";

    NotificationManager notif;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.layout_dshboardfragment,container,false);
        context=getActivity().getBaseContext();

       Toast.makeText(context,"Service will be started",Toast.LENGTH_LONG).show();
        db=FirebaseFirestore.getInstance();
        myListView=view.findViewById(R.id.statusRecycle);

        myListView.setHasFixedSize(true);
        dataList=new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getActivity());
        myListView.setLayoutManager(mLayoutManager);
        notif=(NotificationManager)getActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        BatchStatusModel statusModel=new BatchStatusModel();
//        statusModel.setDate("03/26/2018");
//        statusModel.setBatch_code("tup-us-40");
//        statusModel.setStatus("Start");
//        dataList.add(statusModel);
//        statusModel=new BatchStatusModel();
//        statusModel.setDate("03/26/2018");
//        statusModel.setBatch_code("tup-us-41");
//        statusModel.setStatus("Start");
       // dataList.add(statusModel);



//        adapter.notifyDataSetChanged();
        realtimeUpdate();

        return view;
    }

    public void setDataList(List<BatchStatusModel> dataList) {
        this.dataList = dataList;
        adapter=new CustomStatusAdapter(dataList);
        myListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    long currenttime;


    public void realtimeUpdate()
    {
        db.collection("batch_status")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                        if(e!=null)
                        {
                            Log.d(TAG,"Error: "+e);
                        }
                        if(querySnapshot!=null)
                        {
                            List<BatchStatusModel> list=new ArrayList<>();
                            for(DocumentSnapshot snapshot:querySnapshot)
                            {
                                String id=snapshot.getId();
                                BatchStatusModel model=snapshot.toObject(BatchStatusModel.class);
                                model.setId(id);
                                list.add(model);
                                Log.d(TAG,"Data: "+model.toString());
                            }
                            setDataList(list);
                            for (DocumentChange documentChange:querySnapshot.getDocumentChanges())
                            {
                                    switch (documentChange.getType())
                                    {
                                        case ADDED:
                                            String id=documentChange.getDocument().getId();
                                            BatchStatusModel model=documentChange.getDocument().toObject(BatchStatusModel.class);
                                            model.setId(id);
                                            Log.d(TAG,"Data Added: "+model.toString());
                                            break;
                                        case MODIFIED:
                                            String id2=documentChange.getDocument().getId();
                                            BatchStatusModel model2=documentChange.getDocument().toObject(BatchStatusModel.class);
                                            model2.setId(id2);
                                            Log.d(TAG,"Data Modified: "+model2.toString());
//                                            Notification notification=new Notification.Builder
//                                                    (getActivity().getApplicationContext()).setContentTitle("Batch Status Updated!").setContentText("Batch "+model2.getBatch_code()+" has been "+model2.getStatus()).
//                                                    setContentTitle("Date: "+model2.getDate()).setSmallIcon(R.drawable.ic_menu_gallery).build();
//
//                                            notification.flags |= Notification.FLAG_AUTO_CANCEL;
//                                            notif.notify(1, notification);
                                            break;
                                        case REMOVED:
                                            String id3=documentChange.getDocument().getId();
                                            BatchStatusModel model3=documentChange.getDocument().toObject(BatchStatusModel.class);
                                            model3.setId(id3);
                                            Log.d(TAG,"Data Modified: "+model3.toString());
                                            break;
                                    }
                            }
                        }
                    }
                });
    }
}
