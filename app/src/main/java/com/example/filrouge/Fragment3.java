package com.example.filrouge;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.filrouge.Interfaces.Notifiable;
import com.example.filrouge.models.HighwayFactory;
import com.example.filrouge.models.HighwayIssue;
import com.example.filrouge.models.Issue;
import com.example.filrouge.models.UrbanFactory;
import com.example.filrouge.models.UrbanIssue;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Locale;

public class Fragment3 extends Fragment {
    private static final int FRAGMENT_ID= 2;

    private final String TAG = "frallo " + getClass().getSimpleName();
    private EditText etTitle,etDescription;
    private TextInputLayout tilTitle, tilDescription;
    private EditText currentVoiceTarget;
    private RadioGroup rgType;
    private TimePicker timePicker;
    private Issue newIssue;
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

    public void onClickItem(){
        notifiable.onDataChange(FRAGMENT_ID,newIssue,Fragment2.Action.CREATE.ordinal(),newIssue.getSafetyProtocol());
        notifiable.onDataChange(FRAGMENT_ID,newIssue,Fragment2.Action.DISPLAY.ordinal(),newIssue.getSafetyProtocol());

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
        return inflater.inflate(R.layout.fragment3, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        etTitle=view.findViewById(R.id.et_issue_title);
        etDescription=view.findViewById(R.id.et_issue_description);
        tilTitle=view.findViewById(R.id.til_issue_title);
        tilDescription=view.findViewById(R.id.til_issue_description);
        //For voice
        tilTitle.setEndIconOnClickListener(v -> startVoiceRecognition(etTitle));
        tilDescription.setEndIconOnClickListener(v -> startVoiceRecognition(etDescription));

        rgType=view.findViewById(R.id.radio_group_context);
        timePicker=view.findViewById(R.id.time_picker);
        Button btn =view.findViewById(R.id.btn_validate);

        btn.setOnClickListener(v->{
            Issue issue = createIssue();
            if (issue != null) {
                newIssue = issue;
                onClickItem();
            }
        });

    }

    private Issue createIssue() {
        String title=etTitle.getText().toString().trim();
        String description=etDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return null;
        }
        int selectedId = rgType.getCheckedRadioButtonId();

        int hour, minute;
        hour = timePicker.getHour();
        minute = timePicker.getMinute();
        String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        if (selectedId == R.id.radio_highway) {
            return new HighwayFactory().createIssue(title, description);
        } else if (selectedId == R.id.radio_urban) {
            return new UrbanFactory().createIssue(title, description);
        } else {
            Toast.makeText(getContext(), "Veuillez choisir un type", Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    // Launcher pour la reconnaissance vocale
    private final ActivityResultLauncher<Intent> voiceLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    ArrayList<String> matches = result.getData()
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches != null && !matches.isEmpty() && currentVoiceTarget != null) {
                        currentVoiceTarget.setText(matches.get(0));
                    }
                }
            }
    );

    private void startVoiceRecognition(EditText target) {
        currentVoiceTarget = target;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Parlez pour remplir le champ...");
        try {
            voiceLauncher.launch(intent);
        } catch (Exception e) {
            Log.e(TAG, "Reconnaissance vocale non supportée sur cet appareil.", e);
            Toast.makeText(getContext(),
                    "Reconnaissance vocale non supportée", Toast.LENGTH_SHORT).show();
        }
    }
}