package com.fastturtle.RememberAnyOne.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.fastturtle.RememberAnyOne.R;
import com.fastturtle.RememberAnyOne.adapters.AllUsersListAdapter;
import com.fastturtle.RememberAnyOne.entities.Users;
import com.fastturtle.RememberAnyOne.helperClasses.Constants;
import com.fastturtle.RememberAnyOne.helperClasses.DatabaseHelper;
import com.fastturtle.RememberAnyOne.helperClasses.Utils;

import java.util.ArrayList;

public class AllUsersListActivity extends AppCompatActivity {

    DatabaseHelper myDbHelper;
    Cursor cursorUser;
    public AllUsersListAdapter listAdapter;
    Integer clickPosition = 0;
    public ArrayList<Users> userDetails;
    ListView listViewUser;
    AppCompatTextView textId;

    @Override
    public void onBackPressed() {
        Intent i = new Intent(AllUsersListActivity.this, DashBoardActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        userDetails = new ArrayList<>();
        listViewUser = findViewById(R.id.listView);
        myDbHelper = new DatabaseHelper(this);
        cursorUser = myDbHelper.getAllData();

        if (cursorUser.moveToFirst()) {
            do {
                Users u = new Users();
                u.setId(cursorUser.getInt(cursorUser.getColumnIndexOrThrow(Constants.Id)));
                u.setName(cursorUser.getString(cursorUser.getColumnIndexOrThrow(Constants.Name)));
                u.setAge(cursorUser.getString(cursorUser.getColumnIndexOrThrow(Constants.Age)));
                u.setEmail(cursorUser.getString(cursorUser.getColumnIndexOrThrow(Constants.Email)));
                u.setMobileNo(cursorUser.getString(cursorUser.getColumnIndexOrThrow(Constants.Mobile_No)));
                u.setDob(cursorUser.getString(cursorUser.getColumnIndexOrThrow(Constants.DOB)));
                u.setImagePathUri(cursorUser.getString(cursorUser.getColumnIndexOrThrow(Constants.Key_Image)));
                userDetails.add(u);
            } while (cursorUser.moveToNext());
        }
        listAdapter = new AllUsersListAdapter(AllUsersListActivity.this, userDetails);
        listViewUser.setLongClickable(true);
        listViewUser.setAdapter(listAdapter);
        cursorUser.close();
        listViewUser.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                clickPosition = position;
                Users u = new Users();
                textId = view.findViewById(R.id.txtUserId);
                String IdString = textId.getText().toString();
                int intId = Integer.parseInt(IdString);
                u.setId(intId);
                registerForContextMenu(listViewUser);
                return false;
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.listView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main, menu);

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.update:

                // edit stuff here
                Intent i = new Intent(getApplicationContext(), UpdateUserActivity.class);
                i.putExtra("UserId", userDetails.get(clickPosition).getId());
                startActivity(i);

                return true;

            case R.id.delete:
                // remove stuff here
                myDbHelper.delete(userDetails.get(clickPosition).getId());
                Toast.makeText(getApplicationContext(),
                        "User with name " + userDetails.get(clickPosition).getName() + " deleted successfully",
                        Toast.LENGTH_LONG).show();
                userDetails.remove(info.position);
                myDbHelper.close();
                listAdapter.notifyDataSetChanged();


                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        View emptyText = findViewById(R.id.emptyText);
        listViewUser = findViewById(R.id.listView);
        listViewUser.setEmptyView(emptyText);
    }
}