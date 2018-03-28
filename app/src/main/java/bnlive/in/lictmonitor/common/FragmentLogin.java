package bnlive.in.lictmonitor.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import bnlive.in.lictmonitor.R;
import bnlive.in.lictmonitor.admin.AdminNav;
import bnlive.in.lictmonitor.trainer.TrainerNavActivity;

import static android.content.ContentValues.TAG;

/**
 * Created by Sk Faisal on 3/25/2018.
 */

public class FragmentLogin extends Fragment {
    private EditText email;
    View view;
    EditText emailText;
    EditText passwordText;

    Button loginBtn;
    FirebaseAuth mAuth;
    Context context;
    FirebaseFirestore db;
    private final String TAG="fragmentlogin";
    SharedPreferences sharedPreferences;
    private String MyPreferences="monitordb";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_login,container,false);
        emailText=view.findViewById(R.id.l_email);
        passwordText=view.findViewById(R.id.l_password);
        loginBtn=view.findViewById(R.id.l_loginBtn);


        mAuth=FirebaseAuth.getInstance();
        context=getActivity().getBaseContext();
        db=FirebaseFirestore.getInstance();
        sharedPreferences=context.getSharedPreferences(MyPreferences,Context.MODE_PRIVATE);
        if (sharedPreferences.getString("role",null)!=null)
        {
            String role=sharedPreferences.getString("role",null);
            if(role.equals("admin"))
            {
                Intent intent=new Intent(context, AdminNav.class);
                startActivity(intent);
              //  getActivity().finish();
            }

        }
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailText.getText().toString();
                String password=passwordText.getText().toString();
                String role="";

               role="admin";
                isVerified(email,password,role);
            }
        });
        return view;
    }
    private boolean isVerified(final String email, String password, final String role)
    {
        boolean flag=true;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                         getUserRole(user.getEmail());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);

                        }

                        // ...
                    }
                });
        return flag;
    }
    private String role;
    private String name;
    public void getUserRole(final String email)
    {

        db.collection("user_manage")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
//                            for (QuerySnapshot document : task.getResult())) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
//                         Object object=task.getResult();
//                         QuerySnapshot snapshots= (QuerySnapshot) object;
                            QuerySnapshot snapshots=task.getResult();

                            for(DocumentSnapshot documentSnapshot:snapshots)
                            {
                                Log.d(TAG, "Successfully get documents. Role: "+documentSnapshot.get("role"));
                                role=""+documentSnapshot.get("role");
                                name=""+documentSnapshot.get("name");
                               // String dbrole=
                                // updateUI(user);
                                Log.d(TAG, "User Role: "+role);
                                Log.d(TAG, "Input Role: "+role);
//                                if(role.equals(dbrole))
//                                {
//                                    Log.d(TAG, "Condition role: "+role);
                                  if(role.equals("admin"))
                                    {
                                        SharedPreferences.Editor editor=sharedPreferences.edit();
                                        editor.putString("email",email);
                                        editor.putString("name",name);
                                        editor.putString("role",role);
                                        editor.commit();
                                        Intent intent=new Intent(context, AdminNav.class);
                                        startActivity(intent);
                                        //getActivity().finish();
                                    }

                                //}
                                else{
                                    Toast.makeText(context,"Role Incorrect",Toast.LENGTH_LONG).show();
                                }
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


}
