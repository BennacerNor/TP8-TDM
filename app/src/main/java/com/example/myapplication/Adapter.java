package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Adapter extends ArrayAdapter<MyFile> {
    
        private int layoutResource;

        public Adapter(Context context, int layoutResource, ArrayList<MyFile> data) {
            super(context, layoutResource, data);
            this.layoutResource = layoutResource;
        }




    @NonNull
//        @Override
//        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            View view = convertView;
//            if (view == null) {
//                view = getLayoutInflater().inflate(layoutResource, parent, false);
//            }}

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if(convertView == null){

                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item,parent,
                            false);
                }

            MyFile myFile = getItem(position);
            TextView tvName = convertView.findViewById(R.id.tv_name);
            TextView tvPath = convertView.findViewById(R.id.tv_path);
            TextView tvDuration = convertView.findViewById(R.id.tv_duration);
            tvName.setText(myFile.getName());
            tvPath.setText(myFile.getPath());
            tvDuration.setText(myFile.getDuration() + " sec");

            return convertView;
        }


}


