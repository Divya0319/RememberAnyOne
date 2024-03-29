package com.fastturtle.rememberMe.adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import com.fastturtle.rememberMe.R;
import com.fastturtle.rememberMe.entities.Users;
import com.fastturtle.rememberMe.helperClasses.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AllUsersListAdapter extends ArrayAdapter<Users> {

    private ArrayList<Users> uDetails;
    private Typeface typefaceForAll;
    private Context context;

    public AllUsersListAdapter(@NonNull Context context, ArrayList<Users> userDetails) {
        super(context, R.layout.list_single, userDetails);
        this.context = context;
        this.uDetails = userDetails;
        typefaceForAll = ResourcesCompat.getFont(context, R.font.exo_medium);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ListViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_single, parent, false);

            holder = new ListViewHolder();
            holder.tvUId = convertView.findViewById(R.id.txtUserId);
            holder.tvName = convertView.findViewById(R.id.txtName);
            holder.tvAge = convertView.findViewById(R.id.txtAge);
            holder.tvEmail = convertView.findViewById(R.id.txtEmail);
            holder.tvMobNo = convertView.findViewById(R.id.txtMobNo);
            holder.tvDOB = convertView.findViewById(R.id.txtDOB);
            holder.imgUser = convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        } else {
            holder = (ListViewHolder) convertView.getTag();
        }
        Users u = uDetails.get(position);
        holder.tvUId.setText(u.getId() + "");
        holder.tvName.setText(context.getResources().getString(R.string.name) + " " + u.getName());
        holder.tvEmail.setText(context.getResources().getString(R.string.email) + " " + u.getEmail());
        holder.tvAge.setText(context.getResources().getString(R.string.age) + " " + u.getAge());
        holder.tvMobNo.setText(context.getResources().getString(R.string.mobno) + " " + u.getMobileNo());
        holder.tvDOB.setText(context.getResources().getString(R.string.dob) + " " + u.getDob());
        holder.tvName.setTypeface(typefaceForAll);
        holder.tvEmail.setTypeface(typefaceForAll);
        holder.tvMobNo.setTypeface(typefaceForAll);
        holder.tvAge.setTypeface(typefaceForAll);
        holder.tvDOB.setTypeface(typefaceForAll);

        holder.imgUser.setImageBitmap(Utils.getImage(u.getImage()));
        return convertView;
    }

    static class ListViewHolder {
        AppCompatTextView tvUId, tvName, tvAge, tvEmail, tvMobNo, tvDOB;
        AppCompatImageView imgUser;
    }

    @Override
    public int getCount() {
        return uDetails.size();
    }
}
