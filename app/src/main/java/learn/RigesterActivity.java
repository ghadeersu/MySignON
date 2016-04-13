package learn;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class RigesterActivity extends AppCompatActivity {

    private Calendar calendar;
    private int year, month, day;
    private String birthdate;
    private EditText etBirthdate;
    EditText etName, etEmail, etPassword, etRePassword;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(learn.R.layout.activity_rigester);
        Firebase.setAndroidContext(this);
        etBirthdate = (EditText) findViewById(learn.R.id.registerBirthdateEditText);


        etName = (EditText) findViewById(learn.R.id.registerNameEditText);
        etEmail = (EditText) findViewById(learn.R.id.registerEmailEditText);
        etPassword = (EditText) findViewById(learn.R.id.registerPasswordEditText);
        etRePassword = (EditText) findViewById(learn.R.id.registerRePasswordEditText);
        register = (Button) findViewById(learn.R.id.registerRegisterButton);

    }

    boolean isEmailValid (CharSequence email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public void registerRegisterButtonClick (View v)  {



        String name = etName.getText().toString();
        final String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString().trim();
        String repassword = etRePassword.getText().toString().trim();
        birthdate = etBirthdate.getText().toString();

        String msg;
        Toast MSG;
        if (name.isEmpty()) {
            msg = "username field cannot be empty";
            MSG = Toast.makeText(RigesterActivity.this, msg, Toast.LENGTH_SHORT);
            MSG.show();
        } else if (email.isEmpty()) {
            msg = "email field cannot be empty";
            MSG = Toast.makeText(RigesterActivity.this, msg, Toast.LENGTH_SHORT);
            MSG.show();
        } else if (password.isEmpty()) {
            msg = "password field cannot be empty";
            MSG = Toast.makeText(RigesterActivity.this, msg, Toast.LENGTH_SHORT);
            MSG.show();
        } else if (!password.equals(repassword)) {
            msg = "passwords do not match";
            MSG = Toast.makeText(RigesterActivity.this, msg, Toast.LENGTH_SHORT);
            MSG.show();
        } else if (!isEmailValid(email))
        {
            msg = "Invalid Email";
            MSG = Toast.makeText(RigesterActivity.this, msg, Toast.LENGTH_SHORT);
            MSG.show();
        } else if (birthdate.isEmpty())
        {
            msg = "Birthdate field cannot be empty";
            MSG = Toast.makeText(RigesterActivity.this, msg, Toast.LENGTH_SHORT);
            MSG.show();
        }
        else if((password.length() < 6))
        {
            msg = "password is too short";
            MSG = Toast.makeText(RigesterActivity.this, msg, Toast.LENGTH_SHORT);
            MSG.show();

        }
        else
        {
            Firebase.setAndroidContext(this);
            final Firebase mFirebase = new Firebase ("https://torrid-heat-4458.firebaseio.com/users");
            final learn.UserAdapter mAdapter = new learn.UserAdapter(this);
            final learn.User CurrentUser = new learn.User(null, email, birthdate, name);
            EmailExist(email, CurrentUser, mAdapter);


        }
    }

    private void EmailExist(String email, final learn.User currentUser, final learn.UserAdapter mAdapter) {
        Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/users");
        Query queryRef = ref.orderByChild("Email").equalTo(email);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                        Toast toast = Toast.makeText(RigesterActivity.this, "email is already registered", Toast.LENGTH_LONG);
                        toast.show();
                        break;
                    }
                }
                else {
                    createAccount(currentUser, mAdapter);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        };
        queryRef.addListenerForSingleValueEvent(listener);

    }

    public void createAccount (final learn.User CurrentUser, final learn.UserAdapter mAdapter)
    {
        //final long PrK = CurrentUser.GeneratePK();
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
        //long PrK = System.currentTimeMillis();
        BigInteger PrK = BigInteger.valueOf(Long.parseLong(timeStamp));
        CurrentUser.CreateECDSAobject(PrK);

        Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com");
        ref.createUser(CurrentUser.getEmail(), etPassword.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                System.out.println("Successfully created user account with uid: " + result.get("uid"));
                mAdapter.addItem(CurrentUser);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                // there was an error
            }
        });





        Toast MSG = Toast.makeText(RigesterActivity.this, "register is successful", Toast.LENGTH_SHORT);
        MSG.show();
       Intent intent = new Intent(RigesterActivity.this, IntroActivity.class);
        startActivity(intent);
        finish();

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
        etBirthdate.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RigesterActivity.this, IntroActivity.class);
        startActivity(intent);
        finish();

    }


}
