package bnlive.in.lictmonitor.admin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;

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
 * Created by Sk Faisal on 3/26/2018.
 */

public class DocChangeService extends Service {
    FirebaseFirestore db;
    String TAG="docchangeservice";
    NotificationManager notif;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"service started");
        db=FirebaseFirestore.getInstance();
        notif=(NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        Toast.makeText(getBaseContext(),"Service Started",Toast.LENGTH_LONG).show();
        realtimeUpdate();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void realtimeUpdate()
    {
        try {


            db.collection("batch_status")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.d(TAG, "Error: " + e);
                            }
                            if (querySnapshot != null) {
                                List<BatchStatusModel> list = new ArrayList<>();
                                for (DocumentSnapshot snapshot : querySnapshot) {
                                    String id = snapshot.getId();
                                    BatchStatusModel model = snapshot.toObject(BatchStatusModel.class);
                                    model.setId(id);
                                    list.add(model);
                                    Log.d(TAG, "Data: " + model.toString());
                                }

                                for (DocumentChange documentChange : querySnapshot.getDocumentChanges()) {
                                    switch (documentChange.getType()) {
                                        case ADDED:
                                            String id = documentChange.getDocument().getId();
                                            BatchStatusModel model = documentChange.getDocument().toObject(BatchStatusModel.class);
                                            model.setId(id);
                                            Log.d(TAG, "Data Added: " + model.toString());
                                            break;
                                        case MODIFIED:
                                            String id2 = documentChange.getDocument().getId();
                                            BatchStatusModel model2 = documentChange.getDocument().toObject(BatchStatusModel.class);
                                            model2.setId(id2);
                                            Log.d(TAG, "Data Modified: " + model2.toString());
//                                        NotificationCompat.Builder builder =
//                                                new NotificationCompat.Builder(getBaseContext())
//                                                        .setSmallIcon(R.drawable.ic_menu_gallery)
//                                                        .setContentTitle("Batch Status Updated!")
//                                                        .setContentText("Batch "+model2.getBatch_code()+" has been "+model2.getStatus())
//                                                        .setContentTitle("Date: "+model2.getDate())
                                            Intent targetIntent = new Intent(getBaseContext(), AdminNav.class);
                                            PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                            Notification notification = new Notification.Builder(getBaseContext())
                                                    .setSmallIcon(R.drawable.ic_menu_gallery)
                                                    .setContentTitle("Batch Status Updated!")
                                                    .setContentText("Batch " + model2.getBatch_code() + " has been " + model2.getStatus())
                                                    .setContentTitle("Date: " + model2.getDate())
                                                    .setContentIntent(contentIntent)
                                                    .build();
                                            notification.defaults |=Notification.DEFAULT_VIBRATE;
                                            notification.defaults |=Notification.DEFAULT_SOUND;
//                                        setContentTitle("Batch Status Updated!").setContentText("Batch "+model2.getBatch_code()+" has been "+model2.getStatus()).
//                                                setContentTitle("Date: "+model2.getDate()).setSmallIcon(R.drawable.ic_menu_gallery).build();

                                            int NOTIFICATION_ID = 12345;


                                            //notification.setContentIntent(contentIntent);
                                            NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            nManager.notify(NOTIFICATION_ID, notification);
                                            break;
                                        case REMOVED:
                                            String id3 = documentChange.getDocument().getId();
                                            BatchStatusModel model3 = documentChange.getDocument().toObject(BatchStatusModel.class);
                                            model3.setId(id3);
                                            Log.d(TAG, "Data Modified: " + model3.toString());
                                            break;
                                    }
                                }
                            }
                        }
                    });
        }catch (Exception e)
        {
            Log.d(TAG,"Error" +e);
        }
    }
}
