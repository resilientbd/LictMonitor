package bnlive.in.lictmonitor.admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bnlive.in.lictmonitor.R;
import bnlive.in.lictmonitor.model.BatchStatusModel;
import bnlive.in.lictmonitor.model.MergeSheduleUniversity;
import bnlive.in.lictmonitor.model.UniversityDetailsModel;

/**
 * Created by Sk Faisal on 3/26/2018.
 */

public class AdminMapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private MapView mapView;
    private UniversityDetailsModel universityDetailsModel;
    private  List<MergeSheduleUniversity> listMerge;
    FirebaseFirestore db;
    private boolean isDataUpdated=false;
    String TAG="adminmapfragment";
    View view;
    Bundle savedInstanceState;
    public boolean isActivityDistroyed=false;
    public int datasize=0;
    Handler handler;

    class MapThread extends Thread{
        int currentSize=0;
     public void run()
     {
         while (true) {
             try {
                 if(isActivityDistroyed==true)
                 {
                     Log.d("mapcheck","Thread distroyed");
                     currentThread().isInterrupted();
                     break;
                 }
                 Thread.sleep(100);
                 Log.d("mapcheck", "Thread Datasize: " + listMerge.size());
                 if(currentSize!=listMerge.size()){
                     handler.post(new Runnable() {
                         @Override
                         public void run() {
                             mapFunc();
                         }
                     });

                     Log.d("mapcheck", "Changed Datasize: " + listMerge.size());
                     currentSize=listMerge.size();
                 }
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
         }
     }

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.activity_batch_maps,container,false);
        this.savedInstanceState=savedInstanceState;
        db=FirebaseFirestore.getInstance();
        Log.d(TAG, "Fragment Started ");
        listMerge=new ArrayList<MergeSheduleUniversity>();
        MapThread mapThread=new MapThread();
        handler=new Handler();
        mapThread.start();
        realtimeupdate();



        return view;
    }
void mapFunc()
{
    mapView = (MapView) view.findViewById(R.id.map);
    mapView.onCreate(savedInstanceState);
    mapView.onResume();
    mapView.getMapAsync(this);
}
MainMapFragment mapFragment;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapFragment=new MainMapFragment();
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        int mIndex=listMerge.size()/2;

        final HashMap<Marker,MergeSheduleUniversity> markermap=new HashMap<>();
        for(int i=0;i<listMerge.size();i++)
        {
            MergeSheduleUniversity mergeSheduleUniversity=listMerge.get(i);

            String ltln=listMerge.get(i).getUniversity().getLat_long();
            String[] str=ltln.split(",");
//         Marker marker=mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(str[0]),Double.parseDouble(str[1]))).title("Batch: "+mergeSheduleUniversity.getBatchCode()).snippet(""+mergeSheduleUniversity.getStatus()));

Marker marker=mapFragment.placeMarker(getActivity(),mergeSheduleUniversity,mMap);
markermap.put(marker,mergeSheduleUniversity);
            if(i==mIndex)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(str[0]),Double.parseDouble(str[1]))));
Log.d(TAG,"Lat: "+str[0]+" Long: "+str[1]);
//marker.showInfoWindow();

        }

mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
    @Override
    public void onInfoWindowClick(Marker marker) {
        Gson gson=new Gson();
        MergeSheduleUniversity mergeSheduleUniversity=markermap.get(marker);
        BatchDetailsActivity batchDetailsActivity=new BatchDetailsActivity();
        batchDetailsActivity.setData(mergeSheduleUniversity);
        String str=gson.toJson(mergeSheduleUniversity);
        Intent intent=new Intent(getActivity().getBaseContext(),batchDetailsActivity.getClass());
        intent.putExtra("data",str);
        startActivity(intent);
        Toast.makeText(getContext(),"Location: "+mergeSheduleUniversity.getUniversity().getAddress(),Toast.LENGTH_LONG).show();
    }
});
        //mMap.setZ
        Log.d("mapcheck","Dataset Size: "+listMerge.size());
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    public void realtimeupdate()
    {
        db.collection("batch_status")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                        if(e!=null)
                        {
                           // Log.d(TAG,"Error: "+e);
                        }
                        if(querySnapshot!=null)
                        {

                            for(DocumentSnapshot snapshot:querySnapshot)
                            {
                                String id=snapshot.getId();
                                BatchStatusModel model=snapshot.toObject(BatchStatusModel.class);
                                model.setId(id);


                                        getUniversity(model,model.getUniversity_name());
                               // UniversityDetailsModel umodel=getUniversityDetailsModel();

//                                list.add(model);
                               // Log.d(TAG,"Data: "+model.toString());
                            }
                         //   mapFunc();
                            Log.d("mapcheck","Flag set to true");
                            Log.d("mapcheck","DataSet: "+listMerge.size());
//                            setListMerge(list);
                           // setDataList(list);
                            for (DocumentChange documentChange:querySnapshot.getDocumentChanges())
                            {
                                switch (documentChange.getType())
                                {
                                    case ADDED:
                                        String id=documentChange.getDocument().getId();
                                        BatchStatusModel model=documentChange.getDocument().toObject(BatchStatusModel.class);
                                        model.setId(id);
                                       // Log.d(TAG,"Data Added: "+model.toString());
                                        break;
                                    case MODIFIED:
                                        String id2=documentChange.getDocument().getId();
                                        BatchStatusModel model2=documentChange.getDocument().toObject(BatchStatusModel.class);
                                        model2.setId(id2);
                                       // Log.d(TAG,"Data Modified: "+model2.toString());
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
//                                        Log.d(TAG,"Data Modified: "+model3.toString());
                                        break;
                                }
                            }
                        }
                    }
                });
    }
    int iterator=0;
    public UniversityDetailsModel getUniversity(final BatchStatusModel bmodel, final String universityname)
    {

      db.collection("university_details")
        .whereEqualTo("university_name",universityname)
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                  QuerySnapshot document = task.getResult();
                    if (document != null) {
                        for(DocumentSnapshot doc:document){
                            setUniversityDetailsModel(bmodel,doc.toObject(UniversityDetailsModel.class));
                            iterator++;
                            Log.d(TAG, "DocumentSnapshot data: "+iterator+" " + doc.toObject(UniversityDetailsModel.class).toString());
                           // setUniversityDetailsModel(umodel);
                        }
                    isDataUpdated=true;
                     //  UniversityDetailsModel umodel=new UniversityDetailsModel(""+document.get("address"),""+document.get("lat_long"),""+document.get("location"),""+document.get("university_name"));
                        //umodel=document.getData().to
                       //
                    } else {
                       Log.d(TAG, "No such document named "+universityname);
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return getUniversityDetailsModel();

    }

    public UniversityDetailsModel getUniversityDetailsModel() {
        return universityDetailsModel;
    }

    public void setUniversityDetailsModel(BatchStatusModel bmodel,UniversityDetailsModel universityDetailsModel) {

        this.universityDetailsModel = universityDetailsModel;
        MergeSheduleUniversity m=new MergeSheduleUniversity(bmodel.getBatch_code(),bmodel.getStatus(),universityDetailsModel);
        listMerge.add(m);
        Log.d(TAG,"reached! "+listMerge.size()+" Data: "+m.toString());
//        if(listMerge.size()==5)
//            mapFunc();
    }

    public List<MergeSheduleUniversity> getListMerge() {
        return listMerge;
    }

    public void setListMerge(List<MergeSheduleUniversity> listMerge) {
        for(MergeSheduleUniversity university:listMerge)
        {
            UniversityDetailsModel universityDetailsModel=university.getUniversity();
           // Log.d(TAG,""+universityDetailsModel.toString());
        }
        this.listMerge = listMerge;
    }

    public void setDataUpdated(boolean dataUpdated) {
        isDataUpdated = dataUpdated;
    }

    @Override
    public void onDestroy() {
        isActivityDistroyed=true;
        super.onDestroy();
    }
}
