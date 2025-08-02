package com.example.etatcivil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class JSONadapter extends ArrayAdapter<JSONObject> {
    private final Context context;
    private final int mResource;
    public JSONadapter(@NonNull Context context, int resource, ArrayList<JSONObject> objects) {
        super(context, resource, objects);
        this.context = context;
        this.mResource = resource;
    }
    @NonNull
    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(mResource, parent, false);
        ImageView pdpItem = convertView.findViewById(R.id.IVitem);

        TextView asaTV = convertView.findViewById(R.id.item_asa);
        TextView telTV = convertView.findViewById(R.id.item_tel);
        TextView sexeTV = convertView.findViewById(R.id.item_sexe);
        TextView adresseTV = convertView.findViewById(R.id.item_lot);
        TextView cinTV = convertView.findViewById(R.id.item_CIN);
        TextView datyTV = convertView.findViewById(R.id.item_date);
        TextView ligne1 = convertView.findViewById(R.id.ligne1);


        try {
            byte[] img = Base64.decode(Objects.requireNonNull(getItem(position)).getString("image"), Base64.DEFAULT);
            ligne1.setText(Objects.requireNonNull(getItem(position)).getString("anarana") + "  " +
                    Objects.requireNonNull(getItem(position)).getString("fanampiny"));
            pdpItem.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
            datyTV.setText(Objects.requireNonNull(getItem(position)).getString("daty"));
            adresseTV.setText(Objects.requireNonNull(getItem(position)).getString("adiresy"));
            sexeTV.setText(Objects.requireNonNull(getItem(position)).getString("sexe"));
            telTV.setText(Objects.requireNonNull(getItem(position)).getString("phone"));
            asaTV.setText(Objects.requireNonNull(getItem(position)).getString("asa"));
            cinTV.setText(Objects.requireNonNull(getItem(position)).getString("cin"));

        } catch (JSONException e) {
           Log.e("erreur", e.toString());
        }

        return convertView;
    }

}
