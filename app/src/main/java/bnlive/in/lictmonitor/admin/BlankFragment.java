package bnlive.in.lictmonitor.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bnlive.in.lictmonitor.R;
import bnlive.in.lictmonitor.RegistrationLogin;

/**
 * Created by Sk Faisal on 3/27/2018.
 */

public class BlankFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        Intent intent=new Intent(getActivity().getBaseContext(), RegistrationLogin.class);
//        startActivity(intent);
      //  getActivity().finish();
        View view=inflater.inflate(R.layout.activity_blank,container,false);
        return view;
    }
}
