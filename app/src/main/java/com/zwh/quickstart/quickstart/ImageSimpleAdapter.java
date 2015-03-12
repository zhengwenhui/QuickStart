package com.zwh.quickstart.quickstart;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ImageSimpleAdapter extends CursorAdapter {
    private LayoutInflater mInflater;
    private Context mContext;

    public ImageSimpleAdapter(Context context, Cursor c) {
        super(context, c);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.listitem, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView iconImageView = (ImageView) view.findViewById(R.id.icon);
        TextView labelTextView = (TextView) view.findViewById(R.id.label);

        Log.e("xxxx", "cur="+cursor.getCount()+",c_count="+cursor.getColumnCount());
        Log.e("xxxx", cursor.getString(1)+","+cursor.getString(2)+","+cursor.getString(3));

        labelTextView.setText(cursor.getString(1));
        Drawable drawable = getIcon(cursor.getString(3), cursor.getString(2));
        if( null!=drawable ){
            iconImageView.setImageDrawable(drawable);
        }
    }

    private Drawable getIcon(String pkg, String cls){
        Drawable drawable = null;
        ComponentName  name = new ComponentName(pkg, cls);
        PackageManager mPackageManager = mContext.getPackageManager();
        try {
            drawable = mPackageManager.getActivityIcon(name);
            //mImage.setImageDrawable(drawable);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return drawable;
    }
}