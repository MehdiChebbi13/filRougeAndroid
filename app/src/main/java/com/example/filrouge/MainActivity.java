package com.example.filrouge;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imageView = findViewById(R.id.imageViewEarth);
        imageView.setImageResource(R.drawable.animation);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        
        if (animationDrawable != null) {
            imageView.post(animationDrawable::start);
        }

        Button btnDefault = findViewById(R.id.btn_defaut);
        btnDefault.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ControlActivity.class);
            intent.putExtra(ControlActivity.EXTRA_INDEX,0);
            startActivity(intent);
        });

        Button btnOption1 = findViewById(R.id.btn_option1);
        btnOption1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ControlActivity.class);
            // Index 4 = Fragment5 (Carte des incidents) in ControlActivity.tabFragments
            intent.putExtra(ControlActivity.EXTRA_INDEX, 4);
            startActivity(intent);
        });
    }
}