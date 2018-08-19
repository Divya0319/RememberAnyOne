package com.fastturtle.RememberAnyOne;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.roaringtiger.custom_listview.entities.Users;

import java.util.ArrayList;

public class AllUsersListAdapter extends ArrayAdapter<Users> {

    private ArrayList<Users> uDetails;
    private Typeface typefaceForAll;
    private Context context;

    AllUsersListAdapter(@NonNull Context context, ArrayList<Users> userDetails) {
        super(context, R.layout.list_single, userDetails);
        this.context = context;
        this.uDetails = userDetails;
        typefaceForAll = Typeface.createFromAsset(context.getAssets(), "fonts/Exo-Medium.otf");
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

        holder.imgUser.setImageBitmap(AddUserActivity.getImage(u.getImage()));
        return convertView;
    }

    static class ListViewHolder {
        TextView tvUId, tvName, tvAge, tvEmail, tvMobNo, tvDOB;
        ImageView imgUser;
    }

    @Override
    public int getCount() {
        return uDetails.size();
    }
}
