package learn;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.Calendar;


public class SettingActivity extends BaseActivity {

    private String email;
    private String birthday;
    private EditText Birthday;
    private int year, month, day;
    private Calendar calendar;
    UserAdapter mAdapter;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        mNavigationView.getMenu().getItem(3).setChecked(true);

        Button save = (Button) findViewById(R.id.SaveChangesButton);


        setContentView(R.layout.setting);
        Firebase.setAndroidContext(this);
        Birthday = (EditText) findViewById(R.id.BirthdayEditText);
        getInfo();


    }

    private void getInfo() {

        Firebase mFirebase = new Firebase("https://torrid-heat-4458.firebaseio.com/users");
        Query qRef = mFirebase.orderByKey().equalTo(session.userkey);
        qRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String key = child.getKey();
                    String name, password, birthday;
                    name = (String) child.child("username").getValue(String.class);
                    email = (String) child.child("Email").getValue(String.class);
                    birthday = (String) child.child("birthdate").getValue(String.class);
                    User user = new User(key, email, birthday, name);
                    showInfo(user);
                }
            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void showInfo(User user) {

        Firebase mFirebase = new Firebase("https://torrid-heat-4458.firebaseio.com");



        EditText Name = (EditText) findViewById(R.id.NameEditText);


        Name.setText(user.getUsername());

        System.out.println(mFirebase.getAuth().getProvider());
        Birthday.setText(user.getBirthdate());

    }

    public void SaveChangesOnClick ( View v )
    {
        EditText Name = (EditText) findViewById(R.id.NameEditText);
        EditText Password = (EditText) findViewById(R.id.PasswordEditText);
        EditText Birthday = (EditText) findViewById(R.id.BirthdayEditText);
        EditText OldPassword = (EditText) findViewById(R.id.OldPasswordEditText);
        String key = session.userkey;
        String name = Name.getText().toString();
        String birthday = Birthday.getText().toString();
        user = new User(key, email, birthday,  name);
        mAdapter = new UserAdapter(this);

//////////////////////////////////////update password///////////////////
        if(!Password.getText().toString().isEmpty() || !OldPassword.getText().toString().isEmpty()) {
            String password = Password.getText().toString();
            String oldPassword=OldPassword.getText().toString();
            Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com");
            System.out.println("check 1" + password);
            System.out.println("check 2" + ref.getAuth().getProviderData().get("email").toString());
            System.out.println("check 3" + ref.getAuth().getUid());
            System.out.println("check 4" + ref.getAuth().getProviderData().get("password"));
            ref.changePassword(ref.getAuth().getProviderData().get("email").toString(), oldPassword, password, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    mAdapter.updateItem(user);
                    Toast.makeText(SettingActivity.this, "Information Updated Successfully", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    Toast.makeText(SettingActivity.this, "old password is incorrect", Toast.LENGTH_SHORT).show();

                }
            });


        }else
        {mAdapter.updateItem(user);
            Toast.makeText(SettingActivity.this, "Information Updated Successfully", Toast.LENGTH_SHORT).show();}
    }

    public void editSignatures(View v){
        startActivity(new Intent(SettingActivity.this, SignatureSelectActivity.class));

    }

    public void setDate(View view) {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        Dialog dialog = new DatePickerDialog(this, myDateListener, year, month, day);
        dialog.show();
    }


    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        Birthday.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();

    }

}
