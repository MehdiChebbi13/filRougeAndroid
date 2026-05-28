package com.example.filrouge;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.filrouge.Interfaces.Notifiable;

public class Fragment6 extends Fragment {
    private static final int FRAGMENT_ID= 5;
    private Notifiable notifiable;

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if(context instanceof Notifiable){
            notifiable= (Notifiable) context;
        }else{
            throw new AssertionError("Classe " + requireActivity().getClass().getName() + " ne met pas en œuvre Notifiable.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        notifiable = null;
    }

    @Override
    public void onStart(){
        super.onStart();
        if(notifiable!=null){
            notifiable.onFragmentDisplayed(FRAGMENT_ID);
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment6, container, false);
    }
}