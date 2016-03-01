package com.askme.smart_map.activity.AtlasBuilder;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.askme.smart_map.activity.AtlasActivity;
import com.askme.smart_map.activity.DatabaseBuilder.MyFile;
import com.askme.smart_map.activity.DetailGalaryActivity;
import com.squareup.picasso.Picasso;
import com.yalantis.guillotine.sample.R;

import java.io.File;
import java.util.ArrayList;

public class CustomClusterListView extends BaseAdapter {
    private static LayoutInflater inflater = null;
    ArrayList<MyFile> result;
    Context context;

    public CustomClusterListView(AtlasActivity atlasActivity, ArrayList<MyFile> result) {
        // TODO Auto-generated constructor stub
        this.result = result;
        this.context = atlasActivity;

        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return result.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        final MyFile myFile = result.get(position);

        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.dialog_box_row, null);
        holder.fileTitle = (TextView) rowView.findViewById(R.id.fileTitle);
        holder.fileDate = (TextView) rowView.findViewById(R.id.FileDate);
        holder.fileLocation = (TextView) rowView.findViewById(R.id.fileLocation);

        holder.fileImage = (ImageView) rowView.findViewById(R.id.fileImage);

        holder.fileTitle.setText(myFile.getTitle());
        holder.fileDate.setText(myFile.getDate());

        holder.fileLocation.setText(myFile.getLocationLocalName());

        Picasso.with(context)
                .load(new File(myFile.getPath()).getAbsoluteFile())
                .resize(1024, 600)
                .centerInside()
                .into(holder.fileImage);


        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent openGalary = new Intent(context, DetailGalaryActivity.class);
                openGalary.putExtra("data", myFile.getData());


                context.startActivity(openGalary);
            }
        });
        return rowView;
    }

    public Boolean isAvailable() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1    www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal == 0);
            if (reachable) {
                System.out.println("Internet access");
                return reachable;
            } else {
                System.out.println("No Internet access");
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        return false;
    }

    public class Holder {
        TextView fileTitle;
        TextView fileDate;
        TextView fileLocation;
        ImageView fileImage;
    }

}