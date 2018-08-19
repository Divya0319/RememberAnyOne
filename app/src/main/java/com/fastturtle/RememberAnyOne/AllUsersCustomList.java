package com.fastturtle.RememberAnyOne;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.roaringtiger.custom_listview.entities.Users;

import java.util.ArrayList;

public class AllUsersCustomList extends AppCompatActivity {

    DatabaseHelper myDbHelper;
    Cursor cursorUser;
    public AllUsersListAdapter listAdapter;
    Integer clickPosition = 0;
    byte[] byteArray;
    public ArrayList<Users> userDetails;
    ListView listViewUser;
    TextView textId;

    @Override
    public void onBackPressed() {
        Intent i = new Intent(AllUsersCustomList.this, DashBoardActivity.class);
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
        cursorUser = myDbHelper.getAlldata();

        if (cursorUser.moveToFirst()) {
            do {
                Users u = new Users();
                u.setId(cursorUser.getInt(cursorUser.getColumnIndex(myDbHelper.Id)));
                u.setName(cursorUser.getString(cursorUser.getColumnIndex(myDbHelper.Name)));
                u.setAge(cursorUser.getString(cursorUser.getColumnIndex(myDbHelper.Age)));
                u.setEmail(cursorUser.getString(cursorUser.getColumnIndex(myDbHelper.Email)));
                u.setMobileNo(cursorUser.getString(cursorUser.getColumnIndex(myDbHelper.Mobile_No)));
                u.setDob(cursorUser.getString(cursorUser.getColumnIndex(myDbHelper.DOB)));
                u.setImage(cursorUser.getBlob(cursorUser.getColumnIndex(myDbHelper.Key_Image)));
                userDetails.add(u);
            } while (cursorUser.moveToNext());
        }
        listAdapter = new AllUsersListAdapter(AllUsersCustomList.this, userDetails);
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
                ImageView imageView = view.findViewById(R.id.img);
                Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                byteArray = AddUserActivity.getBytes(bmp);
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
                i.putExtra("BITMAP_SHARED_KEY", userDetails.get(clickPosition).getImage());
                startActivity(i);

                return true;

            case R.id.delete:
                // remove stuff here
                Toast.makeText(getApplicationContext(),
                        "User with name " + userDetails.get(clickPosition).getName() + " deleted successfully",
                        Toast.LENGTH_LONG).show();
                myDbHelper.delete(userDetails.get(clickPosition).getId());
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