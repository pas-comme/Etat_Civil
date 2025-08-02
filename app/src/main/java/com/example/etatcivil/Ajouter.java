package com.example.etatcivil;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class Ajouter extends AppCompatActivity {
    //variable & constante
    private static final int PICK_IMAGE_REQUEST = 100, TAKE_IMAGE_REQUEST = 200;
    byte[] img;
    String id_prs;
    Bitmap bitmap;
    QRGEncoder encodeur;

    //Widget
    ImageView pdp, affichageQRCODE;
    Button pdpBTN, downloadBTN;
    ViewFlipper viewFlipper;
    RequestQueue requestQueue;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter);

        requestQueue = Volley.newRequestQueue(this);
        viewFlipper = findViewById(R.id.vfInsertion);
        affichageQRCODE = findViewById(R.id.IVcode);


        //bouton retour à l'acceuil
        Button retourAcceuil = findViewById(R.id.retourAcceuil);
        retourAcceuil.setOnClickListener(view -> {
            startActivity(new Intent(Ajouter.this, MainActivity.class));
            finish();
        });

        //bouton de téléchargement
        downloadBTN = findViewById(R.id.dowBTN);
        downloadBTN.setOnClickListener(view1 -> {
            Toast.makeText(this, "identification téléchargée",  Toast.LENGTH_LONG).show();
            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "qrcode", null );
        });

        String url = "http://10.11.123.17:5000/API/citizens/lastID";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> id_prs = response,
                error -> {
                    Toast.makeText(this, "ato : " + error.toString(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, error.toString());
                });
        requestQueue.add(stringRequest);

        traitementAjout();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
                Uri imageFilePath = data.getData();
                pdp.setImageURI(imageFilePath);
                InputStream ipt = getContentResolver().openInputStream(imageFilePath);
                pdp.setVisibility(View.VISIBLE);
                pdpBTN.setText(R.string.changerPDP);
                img = getBytes(ipt);
            }
            else if(requestCode == TAKE_IMAGE_REQUEST && resultCode == RESULT_OK){
                assert data != null;
                Bitmap selectedImage = (Bitmap)  data.getExtras().get("data");
                pdp.setImageBitmap(selectedImage);
                pdp.setVisibility(View.VISIBLE);
                pdpBTN.setText(R.string.changerPDP);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                img = stream.toByteArray();
            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void choisirImage() {
        @SuppressLint("InflateParams") View photoView = LayoutInflater.from(this).inflate(R.layout.dlg_photo, null);
        Dialog builder = new Dialog(this);
        builder.setContentView(photoView);
        Objects.requireNonNull(builder.getWindow()).setBackgroundDrawableResource(R.drawable.bg_dialogue);
        Button cameraBTN = photoView.findViewById(R.id.camera),
                galleryBTN = photoView.findViewById(R.id.gallery);
        ImageView closePhoto = photoView.findViewById(R.id.closePhoto);
        closePhoto.setOnClickListener(view -> builder.dismiss());
        cameraBTN.setOnClickListener(view -> {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(Intent.createChooser(takePicture, "Prendre une photo"), TAKE_IMAGE_REQUEST);
            builder.dismiss();
        });
        galleryBTN.setOnClickListener(view -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(pickPhoto, "Choisir une photo") , PICK_IMAGE_REQUEST);
            builder.dismiss();
        });
        builder.show();
    }

    @SuppressLint("SetTextI18n")
    public void traitementAjout(){
        EditText anarana = findViewById(R.id.anarana),
                fanampiny = findViewById(R.id.fanampiny),
                asa = findViewById(R.id.asa),
                cin = findViewById(R.id.cin),
                adresse = findViewById(R.id.adresse);

        RadioButton lahy = findViewById(R.id.MasculinButton);
        RadioButton vavy = findViewById(R.id.FéminiButton);
        RadioButton hafa = findViewById(R.id.AutresButton);
        Button validerAjoutPrs = findViewById(R.id.validerAjoutPers);

        //photo de profile
        pdp = findViewById(R.id.pdp) ;
        pdpBTN = findViewById(R.id.pdpBTN);
        pdpBTN.setOnClickListener(view2 -> choisirImage());

        //date de naissance
        Button datyBTN = findViewById(R.id.datyBTN);
        TextView Daty = findViewById(R.id.Daty);
        datyBTN.setOnClickListener(view1 -> {
            DatePickerDialog dlg = new DatePickerDialog(this,
                    (datePicker, a, a1, a2) -> {}, 2000, 0, 1);
            dlg.show();
            dlg.setOnDismissListener(dialogInterface1 -> {
                DatePicker dt = dlg.getDatePicker();
                datyBTN.setText("Changer date de naissance");
                Daty.setText(dt.getDayOfMonth() + "-" + (dt.getMonth() + 1)+ "-" + dt.getYear());
            });
        });

        //click sur insertion de la personne à adresse
        validerAjoutPrs.setOnClickListener(view1 ->  {

            // traitement sexe de l'individu
            String sexe="";
            if (lahy.isChecked())
                sexe = "Masculin";
            else if (vavy.isChecked())
                sexe = "Féminin";
            else if (hafa.isChecked())
                sexe = "Autres";

            if (pdp.getVisibility() == View.GONE)
                Toast.makeText(this,"vous devez fournir une photo de profil", Toast.LENGTH_LONG).show();
            else if(anarana.getText().toString().isEmpty() || fanampiny.getText().toString().isEmpty()
                    || Daty.getText().toString().isEmpty() || sexe.isEmpty()
                    || adresse.getText().toString().isEmpty())
                Toast.makeText(this,"SEULS LES 2 DERNIERS CHAMPS PEUVENT ETRE VIDE", Toast.LENGTH_LONG).show();
            else if(datyIsValide(Daty.getText().toString()))
                Toast.makeText(this, "ERREUR : DATE DE NAISSANCE INVALIDE", Toast.LENGTH_LONG).show();
            else if (isCINvalide(Daty.getText().toString(), cin.getText().toString()))
                Toast.makeText(this,"CIN INVALIDE (laissez un champ vide ou saisissez 'aucun'  pour les mineurs)", Toast.LENGTH_LONG).show();
            else{
                String image = Base64.encodeToString(img, Base64.DEFAULT);
                String urla = "http://10.11.123.17:5000/API/citizens/newPRS";
                String finalSexe = sexe, CIN, ASA;

                if (cin.getText().toString().isEmpty())
                    CIN = "aucun";
                else
                    CIN = cin.getText().toString();
                if (asa.getText().toString().isEmpty())
                    ASA = "aucun";
                else
                    ASA = asa.getText().toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, urla,
                        response -> Toast.makeText(this, response, Toast.LENGTH_LONG).show(),
                        error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()){

                    //add Parametres
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("anarana", anarana.getText().toString());
                        params.put("fanampiny", fanampiny.getText().toString());
                        params.put("adiresy", adresse.getText().toString());
                        params.put("daty", Daty.getText().toString());
                        params.put("sexe", finalSexe);
                        params.put("cin", CIN);
                        params.put("asa", ASA);
                        params.put("img", image);
                        return params;
                    }
                };
                requestQueue.add(stringRequest);

                WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                int dimen = Math.min(point.x, point.y);
                dimen = dimen * 3 / 4;
                int temp = Integer.parseInt(id_prs) + 1;

                encodeur = new QRGEncoder(String.valueOf(temp), null, QRGContents.Type.TEXT, dimen);

                try {
                    bitmap = encodeur.getBitmap();
                    affichageQRCODE.setImageBitmap(bitmap);
                } catch (Exception e) {
                    //noinspection CallToPrintStackTrace
                    e.printStackTrace();
                }
                viewFlipper.showNext();

            }
        });
    }
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream tab = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int tabSize;
        while ((tabSize = inputStream.read(buffer)) != -1 )
            tab.write(buffer, 0, tabSize);
        return tab.toByteArray();
    }
    public  boolean datyIsValide(String date){
        String[] temp = date.split("-");
        Date actu = new Date();
        @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String[] x = format.format(actu).split("-");
        if (Integer.parseInt(temp[2]) > Integer.parseInt(x[2]))
            return true;
        else if (Integer.parseInt(temp[2]) == Integer.parseInt(x[2]) && Integer.parseInt(temp[1]) > Integer.parseInt(x[1]))
            return true;
        else return Integer.parseInt(temp[2]) == Integer.parseInt(x[2]) && Integer.parseInt(temp[1]) == Integer.parseInt(x[1]) &&
                    Integer.parseInt(temp[0]) > Integer.parseInt(x[0]);
    }
    public boolean isCINvalide(String date, String cin){
        String[] daty = date.split("-");
        Date actu = new Date();
        @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String[] temp = format.format(actu).split("-");
        if ((Integer.parseInt(daty[2]) + 18) > Integer.parseInt(temp[2])) {
            return !cin.isEmpty() && !cin.equals("aucun");
        } else return !cin.matches("[0-9]{12}");

    }


}