package learn.navdrawbase;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

public class IntroActivity extends FragmentActivity {

    Intent homeintent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        homeintent= new Intent(this,HomeActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(80, 23, 140));
        }


        setContentView(R.layout.activity_intro);


        Button introActivityLoginButton=(Button)findViewById(R.id.introActivityLoginButton);
        Button introActivityRegisterButton=(Button) findViewById(R.id.introActivityRegisterButton);

        final EditText email, password;
        email = (EditText) findViewById(R.id.introEmailEditText);
        password = (EditText) findViewById(R.id.introPasswordEditText);

        introActivityLoginButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                        searchforuser(email.getText().toString(), password.getText().toString());

                    }

                }

        );

        introActivityRegisterButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(IntroActivity.this, RigesterActivity.class));

                    }

                }

        );
    }
    public void storeSignatureButtonClick(View v){
        startActivity(new Intent(IntroActivity.this, hash.class));

    }
    public void sha512ButtonClick(View v){
        startActivity(new Intent(IntroActivity.this, SignatureSelectActivity.class));


    }
    public void picButtonClick(View v){


    }

    public void searchforuser(final String email, final String password) {
        final EditText emailtext = (EditText) findViewById(R.id.introEmailEditText);
        Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/users");
        Query queryRef = ref.orderByChild("Email").equalTo(email);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child: dataSnapshot.getChildren()) {

                        passwordsearch(password,child.getKey(), email);

                    }
                }
                else {
                    Toast toast = Toast.makeText(IntroActivity.this, "email not found", Toast.LENGTH_LONG);
                    toast.show();

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        queryRef.addValueEventListener(listener);
    }

    public void passwordsearch(String password, final String userkey, final String email){

        Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/users/");
        Query queryRef = ref.orderByChild("password").equalTo(password);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                        if(userkey==child.getKey()) {
                            homeintent.putExtra("key", userkey);
                            homeintent.putExtra("Email",email);
                            startActivity(homeintent);
                            break;
                        }
                    }
                }
                else {
                    Toast toast = Toast.makeText(IntroActivity.this, "Incorrect password", Toast.LENGTH_LONG);
                    toast.show();

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        queryRef.addValueEventListener(listener);

    }
}

