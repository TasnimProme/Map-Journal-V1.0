package com.askme.smart_map.activity.GalaryBuilder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.askme.smart_map.activity.DatabaseBuilder.MyFile;
import com.squareup.picasso.Picasso;
import com.yalantis.guillotine.sample.R;

import java.io.File;
import java.util.ArrayList;

public class GalaryListAadapter extends RecyclerView.Adapter<GalaryListAadapter.ViewHolder> {

    Context mContext;
    OnItemClickListener mItemClickListener;
    AdapterView.OnItemLongClickListener mItemLongClickListener;
    ArrayList<MyFile> data = new ArrayList<>();

    public GalaryListAadapter(Context context, ArrayList<MyFile> info) {
        this.mContext = context;
        this.data = info;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_places, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position != this.data.size()) {
            MyFile myFile = this.data.get(position);
            String placeName = "";
            if (!myFile.getTitle().equalsIgnoreCase("NO TITLE"))
                placeName = myFile.getTitle();
            else if (!myFile.getTitle().equalsIgnoreCase("NO DESCRIPTION"))
                placeName = myFile.getDescription();
            else
                placeName = "";


            holder.placeName.setText(placeName);

            Bitmap photo = null;
            File imgFile = new File(myFile.getPath());
            if (imgFile.exists()) {
                photo = scaleBitmap(imgFile.getAbsolutePath());
                Picasso.with(mContext)
                        .load(imgFile.getAbsoluteFile())
                        .resize(1024, 600)
                        .centerInside()
                        .into(holder.placeImage);
            }

            Palette.generateAsync(photo, new Palette.PaletteAsyncListener() {
                public void onGenerated(Palette palette) {
                    int mutedLight = palette.getMutedColor(mContext.getResources().getColor(android.R.color.black));
                    holder.placeNameHolder.setBackgroundColor(mutedLight);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    private Bitmap scaleBitmap(String imagePath) {
        int targetW = 600;
        int targetH = 600;
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        return bitmap;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout placeHolder;
        public LinearLayout placeNameHolder;
        public TextView placeName;
        public ImageView placeImage;

        public ViewHolder(View itemView) {
            super(itemView);
            placeHolder = (LinearLayout) itemView.findViewById(R.id.mainHolder);
            placeName = (TextView) itemView.findViewById(R.id.placeName);
            placeNameHolder = (LinearLayout) itemView.findViewById(R.id.placeNameHolder);
            placeImage = (ImageView) itemView.findViewById(R.id.placeImage);
            placeHolder.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getPosition());
            }
        }
    }

}
