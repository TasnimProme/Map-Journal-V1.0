package com.askme.smart_map.activity.AddToFavourite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.askme.smart_map.activity.DatabaseBuilder.DBadapter;
import com.yalantis.guillotine.sample.R;

import java.util.ArrayList;

public class AddToFavouriteAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    ArrayList<String[]> result;
    Context context;
    DBadapter dBadapter;
    public AddToFavouriteAdapter(AddToFavourite addToFavourite, ArrayList<String[]> result) {
        // TODO Auto-generated constructor stub
        this.result = result;
        this.context = addToFavourite;
        dBadapter=new DBadapter(context);
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

        final String[] singleSms = result.get(position);

        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.add_to_fav_row, null);
        holder.placeName = (TextView) rowView.findViewById(R.id.favPlaceName);
        holder.placeLatLong = (TextView) rowView.findViewById(R.id.favPlaceLatLng);
        double lat=Double.parseDouble(singleSms[2]);
        double lng=Double.parseDouble(singleSms[3]);
        holder.placeName.setText(singleSms[1]);
        holder.placeLatLong.setText(String.format("Lat: %.3f, Lng: %.3f", lat, lng));

        return rowView;
    }

    public class Holder {
        TextView placeName;
        TextView placeLatLong;
    }




}