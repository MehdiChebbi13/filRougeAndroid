package com.example.filrouge;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.filrouge.Interfaces.Picturable;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class CameraFragment extends Fragment {

    private static final String PICTURE_TAKEN  = "isPictureTaken";
    private static final String FIRST_LAUNCH   = "isFirstLaunchChecked";
    private static final String PHOTOFILE_PATH = "photoFilePath";
    private final String TAG = "frallo " + getClass().getSimpleName();

    private ImageView picture;
    private boolean isPictureTaken       = false;
    private boolean isFirstLaunchChecked = false;
    private File    photoFile;
    private Picturable picturable;

    public CameraFragment() {}

    // ------------------------------------------------------------------ lifecycle

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            isPictureTaken       = savedInstanceState.getBoolean(PICTURE_TAKEN, false);
            isFirstLaunchChecked = savedInstanceState.getBoolean(FIRST_LAUNCH, false);
            String filePath      = savedInstanceState.getString(PHOTOFILE_PATH);
            if (filePath != null) photoFile = new File(filePath);
        }

        // Écoute le canal "picture_channel" : Fragment4 nous envoie
        // le chemin d'une photo existante à afficher
        getParentFragmentManager().setFragmentResultListener(
                "picture_channel", this, (requestKey, result) -> {
                    String path = result.getString("photo_path");
                    Log.d(TAG, "photo reçue via channel : " + path);
                    if (path != null && !path.isEmpty() && picture != null) {
                        File file = new File(path);
                        if (file.exists()) {
                            Picasso.get().load(file).into(picture);
                            isPictureTaken = true;
                        }
                    }
                });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Picturable) {
            picturable = (Picturable) context;
        } else {
            throw new AssertionError(requireActivity().getClass().getName()
                    + " ne met pas en œuvre Picturable.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        picturable = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_camera, container, false);
        picture = layout.findViewById(R.id.picture);

        // Restauration après rotation
        if (savedInstanceState != null && isPictureTaken
                && photoFile != null && photoFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            picture.setImageBitmap(bitmap);
        }

        // Bouton pour (re)prendre une photo
        layout.findViewById(R.id.takePicture).setOnClickListener(v -> {
            isFirstLaunchChecked = false;
            isPictureTaken       = false;
            tryToTakePicture();
        });

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        tryToTakePicture();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PICTURE_TAKEN, isPictureTaken);
        outState.putBoolean(FIRST_LAUNCH, isFirstLaunchChecked);
        if (photoFile != null) outState.putString(PHOTOFILE_PATH, photoFile.getAbsolutePath());
    }

    // ------------------------------------------------------------------ caméra

    private void tryToTakePicture() {
        if (!isFirstLaunchChecked && !isPictureTaken) {
            isFirstLaunchChecked = true;
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        }
    }

    private void takePicture() {
        try {
            photoFile = File.createTempFile("IMG_", ".jpg", requireContext().getCacheDir());
            Uri photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    photoFile);
            takePictureLauncher.launch(photoUri);
        } catch (IOException e) {
            Log.e(TAG, "Erreur création fichier photo", e);
        }
    }

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && photoFile != null && photoFile.exists()) {
                    Log.d(TAG, "Photo enregistrée : " + photoFile.getAbsolutePath());
                    isPictureTaken = true;
                    Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                    picture.setImageBitmap(bitmap);
                    // Notifie ControlActivity avec le chemin de la photo
                    picturable.onPictureTaken(photoFile.getAbsolutePath());
                } else {
                    Log.d(TAG, "Photo annulée.");
                }
            });

    // ------------------------------------------------------------------ permissions

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    takePicture();
                } else {
                    explainPermission();
                }
            });

    private void explainPermission() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Permission caméra requise")
                .setMessage("L'accès à la caméra est nécessaire pour associer une photo à un incident.")
                .setPositiveButton("Autoriser", (d, w) ->
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA))
                .setNegativeButton("Refuser", (d, w) -> showFinalNoDialog())
                .setCancelable(false)
                .show();
    }

    private void showFinalNoDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Fonctionnalité désactivée")
                .setMessage("Sans permission caméra, vous ne pourrez pas associer de photos aux incidents. Vous pouvez l'activer plus tard dans les paramètres.")
                .setNeutralButton("OK", null)
                .setCancelable(false)
                .show();
    }
}