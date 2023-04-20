package com.example.dontopenit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSubmit;
    private ListView listViewData;
    private List<User> userList;
    private UserListAdapter userListAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUsername = findViewById(R.id.edittext_username);
        editTextEmail = findViewById(R.id.edittext_email);
        editTextPassword = findViewById(R.id.edittext_password);
        buttonSubmit = findViewById(R.id.button_submit);
        listViewData = findViewById(R.id.listview_data);

        userList = new ArrayList<>();
        userListAdapter = new UserListAdapter(this, userList);
        listViewData.setAdapter(userListAdapter);

        databaseHelper = new DatabaseHelper(this);

        loadData();

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = new User(0, username, email, password);

                long id = databaseHelper.addUser(user);

                if (id != -1) {
                    user.setId((int) id);
                    userList.add(user);
                    userListAdapter.notifyDataSetChanged();

                    editTextUsername.setText("");
                    editTextEmail.setText("");
                    editTextPassword.setText("");

                    Toast.makeText(MainActivity.this, "User added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error adding user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadData() {
        userList.clear();
        userList.addAll(databaseHelper.getAllUsers());
        userListAdapter.notifyDataSetChanged();
    }

    private class UserListAdapter extends BaseAdapter {
        private Context context;
        private List<User> userList;

        public UserListAdapter(Context context, List<User> userList) {
            this.context = context;
            this.userList = userList;
        }

        @Override
        public int getCount() {
            return userList.size();
        }

        @Override
        public Object getItem(int position) {
            return userList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return userList.get(position).getId();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_item_user, null);
            }
            TextView textViewUsername = view.findViewById(R.id.textview_username);
            TextView textViewEmail = view.findViewById(R.id.textview_email);
            TextView textViewPassword = view.findViewById(R.id.textview_password);
            Button buttonDelete = view.findViewById(R.id.button_delete);
            Button buttonUpdate = view.findViewById(R.id.button_update);

            final User user = userList.get(position);

            textViewUsername.setText(user.getUsername());
            textViewEmail.setText(user.getEmail());
            textViewPassword.setText(user.getPassword());

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Are you sure you wants to delete this");

                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseHelper.deleteUser(user);
                            userList.remove(user);
                            userListAdapter.notifyDataSetChanged();
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            });

            buttonUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Update User");

                    LinearLayout linearLayout = new LinearLayout(context);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    final EditText editTextUsername = new EditText(context);
                    editTextUsername.setText(user.getUsername());
                    linearLayout.addView(editTextUsername);

                    final EditText editTextEmail = new EditText(context);
                    editTextEmail.setText(user.getEmail());
                    linearLayout.addView(editTextEmail);

                    final EditText editTextPassword = new EditText(context);
                    editTextPassword.setText(user.getPassword());
                    linearLayout.addView(editTextPassword);

                    builder.setView(linearLayout);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String username = editTextUsername.getText().toString();
                            String email = editTextEmail.getText().toString();
                            String password = editTextPassword.getText().toString();

                            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                                Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            user.setUsername(username);
                            user.setEmail(email);
                            user.setPassword(password);

                            databaseHelper.updateUser(user);
                            userListAdapter.notifyDataSetChanged();
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });

            return view;
        }
    }
}