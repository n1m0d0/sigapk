package com.example.sigapk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


public class AdapterSociety extends ArrayAdapter<ObjectSociety> {
    public AdapterSociety(Context context, ArrayList<ObjectSociety> datos) {
        super(context, 0, datos);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return intView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return intView(position, convertView, parent);
    }

    private View intView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_society, parent, false);
        }


        TextView tvText = convertView.findViewById(R.id.tvText);

        ObjectSociety data = getItem(position);

        if (data != null) {

            tvText.setText(data.getName());

        }

        return convertView;

    }
}
