package com.example.etatcivil;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.VIBRATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button insertionBTN , rechercheBTN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!checkPermission()) {
            Toast.makeText(this, "Vous devez accepter les permissions requise pour utiliser l'application", Toast.LENGTH_LONG).show();
            requestPermisssion();
        }

        insertionBTN = findViewById(R.id.insertionBTN);
        rechercheBTN = findViewById(R.id.recherche);
        rechercheBTN.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Recherche.class)));
        insertionBTN.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Ajouter.class)));
    }
    private boolean checkPermission(){
        int read = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        int cam_permission = ContextCompat.checkSelfPermission(this,CAMERA);
        int storage_permission = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int vibration_permission = ContextCompat.checkSelfPermission(this, VIBRATE);
        return cam_permission == PackageManager.PERMISSION_GRANTED && storage_permission == PackageManager.PERMISSION_GRANTED &&
                vibration_permission == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermisssion(){
        final int PERMISSION_CODE = 200;
        ActivityCompat.requestPermissions(this, new String[]{
                CAMERA, VIBRATE, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
    }
}