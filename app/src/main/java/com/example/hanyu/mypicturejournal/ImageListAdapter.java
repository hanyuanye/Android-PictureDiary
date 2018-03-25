package com.example.hanyu.mypicturejournal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.File;
import java.util.ArrayList;

/**
 * Created by hanyu on 3/11/2018.
 */

public class ImageListAdapter extends BaseAdapter{
    private ArrayList<Image> mImages;
    private Context mContext;
    private LayoutInflater mInflater;
    private static class ViewHolder {
        TextView emotion;
        TextView date;
        ImageView picture;
    }

    public ImageListAdapter(Context context) {
        this.mContext=context;
    }
    
    public void addImage(final Image image) {
        mImages.add(image);
        notifyDataSetChanged();
    }

    public void instantiate(ArrayList<Image> images) {
        this.mImages = images;
        notifyDataSetChanged();
    }
    
    @Override
    public int getCount() {
        if (mImages == null) {
            return 0;
        }
        if (mImages.size() == 0 || mImages.isEmpty()) {
            return 0;
        }
        return mImages.size();
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public Image getItem(int position) {
        return mImages.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Image image = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.item_image_list, parent, false);
            viewHolder.emotion = (TextView) convertView.findViewById(R.id.emotion);
            viewHolder.date = (TextView) convertView.findViewById(R.id.date);
            viewHolder.picture = (ImageView) convertView.findViewById(R.id.picture);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.date.setText("Date: " + image.getMonth() + " " + Integer.toString(image.getDay()));
        viewHolder.emotion.setText("Emotion: " + image.getEmotion());
        File imageJpeg = new File(image.getFilePath());
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.outHeight = viewHolder.picture.getHeight();
        bmOptions.outWidth = Math.round(viewHolder.picture.getHeight() * 4 / 3);
        Bitmap bitmap = BitmapFactory.decodeFile(imageJpeg.getAbsolutePath(), bmOptions);
        bitmap = RotateBitmap(bitmap, 270);
        viewHolder.picture.setImageBitmap(bitmap);
        return convertView;
    }

    private Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


}
