package com.example.etatcivil;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.ScanMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Recherche extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 100, TAKE_IMAGE_REQUEST = 200;
    byte[] img;

    Button scan, tous, retour1, retour2, pdpBTN;
    ViewFlipper viewFlipper;
    ListView list;
    ImageView pdp;
    RequestQueue requestQueue;
    CodeScannerView scanView;
    CodeScanner scanner;
    ProgressBar prg;
    JSONObject one;
    ArrayList<JSONObject> array = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche);

        requestQueue = Volley.newRequestQueue(this);
        scan = findViewById(R.id.scan);
        tous = findViewById(R.id.tous);
        retour1 = findViewById(R.id.retourAcceuil);
        retour2 = findViewById(R.id.retour);
        viewFlipper = findViewById(R.id.vfRecherche);
        list = findViewById(R.id.list);
        prg = findViewById(R.id.prg);

        retour2.setOnClickListener(view -> {
            startActivity(new Intent(Recherche.this, MainActivity.class));
            finish();
        });
        retour1.setOnClickListener(view -> {
            startActivity(new Intent(Recherche.this, MainActivity.class));
            finish();
        });
        scan.setOnClickListener(view -> {
            viewFlipper.showNext();
            //scanner
            scanView = findViewById(R.id.camView);
            codeScanner();
        });

        tous.setOnClickListener(view -> {

            prg.setVisibility(View.VISIBLE);
            String url = "http://10.11.123.17:5000/API/citizens/persons";
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                    response -> {

                        try {
                            for (int j = 0; j < response.length(); j++) {
                                array.add((JSONObject) response.get(j));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        list.setAdapter(new JSONadapter(this, R.layout.item, array));
                        viewFlipper.showNext();viewFlipper.showNext();
                        list.setOnItemClickListener((adapterView, view2, i, l) -> {
                            one = array.get(i);
                            Dialog itemDialog = new Dialog(this);
                            @SuppressLint("InflateParams") View itemView = LayoutInflater.from(this).inflate(R.layout.dialog_m_s, null);
                            itemDialog.setContentView(itemView);
                            Button modifBTN = itemView.findViewById(R.id.modifBTN);
                            Button supprBTN = itemView.findViewById(R.id.supprBTN);
                            ImageView closeDLG = itemView.findViewById(R.id.closeDLG);
                            closeDLG.setOnClickListener(view3 -> itemDialog.dismiss());
                            itemDialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialogue);
                            supprBTN.setOnClickListener(view3 -> {
                                Toast.makeText(this, "Personne supprimée", Toast.LENGTH_LONG).show();
                                array.remove(i);
                                itemDialog.dismiss();
                                JSONadapter adapteur = new JSONadapter(this, R.layout.item,array);
                                list.setAdapter(adapteur);
                                String urli = null;
                                try {
                                    urli = "http://10.11.123.17:5000/API/citizens/delPRS?id=" + one.getInt("id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                StringRequest stringRequest = new StringRequest(Request.Method.DELETE, urli,
                                        null,
                                        error -> Toast.makeText(this, "Erreur", Toast.LENGTH_LONG).show());
                                requestQueue.add(stringRequest);
                            });
                            modifBTN.setOnClickListener(view3 -> {
                                itemDialog.dismiss();
                                traitementDialog(one, false, i);
                            });
                            itemDialog.show();
                        });
                    },
                    error -> {
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, error.toString());
            });
            requestQueue.add(request);
        });

    }
    private void codeScanner(){
        scanner = new CodeScanner(this, scanView);
        scanner.setCamera(CodeScanner.CAMERA_BACK);
        scanner.setFormats(CodeScanner.ALL_FORMATS);
        scanner.setAutoFocusMode(AutoFocusMode.SAFE);
        scanner.setScanMode(ScanMode.CONTINUOUS);
        scanner.setAutoFocusEnabled(true);
        scanner.setFlashEnabled(false);

        scanner.setDecodeCallback(result -> runOnUiThread(() -> {
            scanner.stopPreview();
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);

            String urlt = "http://10.11.123.17:5000/API/citizens/exists?id=" + result.getText();
            @SuppressLint("SetTextI18n") JsonObjectRequest requesti = new JsonObjectRequest(Request.Method.GET, urlt, null,
                    reponse -> {
                        String ter = "";
                        try {
                            ter = reponse.getString("reponse");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (ter.equals("cet utilisateur n'existe plus"))
                            Toast.makeText(this, ter, Toast.LENGTH_LONG).show();
                        else{
                            String url = "http://10.11.123.17:5000/API/citizens/onePRS?id=" + result.getText();
                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                                    response -> {
                                        traitementDialog(response, true, 0);
                                        Toast.makeText(this, "identifiant scanné", Toast.LENGTH_LONG).show();
                                        array.clear();
                                    },
                                    error -> Toast.makeText(this, "Erreur", Toast.LENGTH_LONG).show());
                            requestQueue.add(request);

                            viewFlipper.showPrevious();
                        }
                    },
                    error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show());
            requestQueue.add(requesti);

        }));
        scanner.setErrorCallback(error -> runOnUiThread(() ->
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show()));
        scanner.startPreview();
    }
    @SuppressLint("SetTextI18n")
    public void traitementDialog(JSONObject prs, boolean bool, int position){
        @SuppressLint("InflateParams") View alertView = LayoutInflater.from(this).inflate(R.layout.dialog, null);
        Dialog dialog = new Dialog(this);
        dialog.setContentView(alertView);
        EditText anarana = alertView.findViewById(R.id.anarana),
                fanampiny = alertView.findViewById(R.id.fanampiny),
                asa = alertView.findViewById(R.id.asa),
                adresse = alertView.findViewById(R.id.adresse),
                cin = alertView.findViewById(R.id.cin);

        RadioGroup radioGroupSexe = alertView.findViewById(R.id.radioGroupSexe);
        RadioButton lahy = alertView.findViewById(R.id.MasculinButton);
        RadioButton vavy = alertView.findViewById(R.id.FéminiButton);
        RadioButton hafa = alertView.findViewById(R.id.AutresButton);
        Button validerAjoutPrs = alertView.findViewById(R.id.validerAjoutPers);
        Button suppPRS = alertView.findViewById(R.id.suppPers);
        if (bool){
            suppPRS.setVisibility(View.VISIBLE);
            suppPRS.setOnClickListener(view -> {
                Toast.makeText(this, "Personne supprimée", Toast.LENGTH_LONG).show();

                dialog.dismiss();
                String url2 = null;
                try {
                    url2 = "http://10.11.123.17:5000/API/citizens/delPRS?id=" + prs.getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url2,
                        null,
                        error -> Toast.makeText(this, "Erreur", Toast.LENGTH_LONG).show());
                requestQueue.add(stringRequest);
            });
        }

        //bouton de fermeture
        ImageView closeDialog = alertView.findViewById(R.id.close);
        closeDialog.setOnClickListener(view1 -> dialog.dismiss());

        //photo de profile
        pdp = alertView.findViewById(R.id.pdp) ;
        pdpBTN = alertView.findViewById(R.id.pdpBTN);
        pdpBTN.setOnClickListener(view2 -> choisirImage());

        //date de naissance
        Button datyBTN = alertView.findViewById(R.id.datyBTN);
        TextView Daty = alertView.findViewById(R.id.Daty);
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

        try {
            img = Base64.decode(prs.getString("image"), Base64.DEFAULT);
            pdp.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
            pdp.setVisibility(View.VISIBLE);
            pdpBTN.setText("Modifier la photo de profile");
            datyBTN.setText("Changer date de naisssance");

            adresse.setText(prs.getString("adiresy"));
            anarana.setText(prs.getString("anarana"));
            fanampiny.setText(prs.getString("fanampiny"));
            asa.setText(prs.getString("asa"));
            Daty.setText(prs.getString("daty"));
            cin.setText(prs.getString("cin"));

            switch (prs.getString("sexe")){
                case "Masculin" : radioGroupSexe.check(lahy.getId());
                    break;
                case "Féminin" : radioGroupSexe.check(vavy.getId());
                    break;
                case "Autres" : radioGroupSexe.check(hafa.getId());
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                    || Daty.getText().toString().isEmpty() || sexe.isEmpty()|| adresse.getText().toString().isEmpty())
                Toast.makeText(this,"SEULS LES 2 DERNIERS CHAMPS PEUVENT ETRE VIDE", Toast.LENGTH_LONG).show();
            else if(datyIsValide(Daty.getText().toString()))
                Toast.makeText(this, "DATE DE NAISSANCE INVALIDE", Toast.LENGTH_LONG).show();
            else if (isCINvalide(Daty.getText().toString(), cin.getText().toString()))
                Toast.makeText(this,"CIN INVALIDE (laissez un champ vide ou saisissez 'aucun'  pour les mineurs)", Toast.LENGTH_LONG).show();
            else{
                Toast.makeText(this, "modification effectuée", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                String image = Base64.encodeToString(img, Base64.DEFAULT);

                String url = "http://10.11.123.17:5000/API/citizens/changePRS";
                String finalCIN; String finalASA;
                if (cin.getText().toString().isEmpty())
                    finalCIN = "aucun";
                else
                    finalCIN = cin.getText().toString();
                if (asa.getText().toString().isEmpty())
                    finalASA = "aucun";
                else
                    finalASA = asa.getText().toString();
                if(!bool){
                    try {
                        //one = new JSONObject();
                        one = one.put("anarana", anarana.getText().toString());
                        one = one.put("fanampiny", fanampiny.getText().toString());
                        one = one.put("sexe", sexe);
                        one = one.put("adiresy", adresse.getText().toString());
                        one = one.put("daty", Daty.getText().toString());
                        one = one.put("cin", cin.getText().toString());
                        one = one.put("asa", asa.getText().toString());
                        one = one.put("image", image);
                        one = one.put("id", prs.getInt("id"));
                        one = one.put("phone", prs.getString("phone"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    array.remove(position);
                    array.add(position, one);
                    list.setAdapter(new JSONadapter(this, R.layout.item, array));
                }

                String finalCIN1 = finalCIN;
                String finalASA1 = finalASA;
                String finalSexe = sexe;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, null,
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
                            params.put("cin", finalCIN1);
                            params.put("asa", finalASA1);
                            params.put("img", image);
                        try {
                            params.put("id", prs.getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return params;
                    }
                };
                requestQueue.add(stringRequest);

            }
        });
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialogue);
        dialog.show();
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
        builder.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialogue);
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
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream tab = new ByteArrayOutputStream();
        int bufferSize = 2048;
        byte[] buffer = new byte[bufferSize];
        int tabSize;
        while ((tabSize = inputStream.read(buffer)) != -1 )
            tab.write(buffer, 0, tabSize);
        return tab.toByteArray();
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
    public  boolean datyIsValide(String date){
        if (date.isEmpty())
            return false;
        else{
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

    }


}