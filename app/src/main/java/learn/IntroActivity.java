package learn;

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

import com.firebase.client.AuthData;
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

import learn.R;

public class IntroActivity extends FragmentActivity {

    Intent homeintent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        homeintent = new Intent(this, learn.HomeActivity.class);
        // Create a callback which logs the current auth state
        Firebase refLogged = new Firebase("https://torrid-heat-4458.firebaseio.com");

            if (refLogged.getAuth()!=null) {
                System.out.println("User " + refLogged.getAuth().getUid() + " is logged in with " + refLogged.getAuth().getProvider());
                // Stay at the current activity.
                homeintent.putExtra("key", SaveSharedPreference.getUserName(this));
                homeintent.putExtra("Email",SaveSharedPreference.getEmail(this));
                startActivity(homeintent);


            } else {
                System.out.println("User is logged out");
                setContentView(R.layout.activity_intro);
                Button introActivityLoginButton = (Button) findViewById(R.id.introActivityLoginButton);
                Button introActivityRegisterButton = (Button) findViewById(R.id.introActivityRegisterButton);

                final EditText email, password;
                email = (EditText) findViewById(R.id.introEmailEditText);
                password = (EditText) findViewById(R.id.introPasswordEditText);
                introActivityLoginButton.setOnClickListener(
                        new View.OnClickListener() {
                            public void onClick(View v) {

                                Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com");
                                ref.authWithPassword(email.getText().toString(), password.getText().toString(), new Firebase.AuthResultHandler() {
                                    @Override
                                    public void onAuthenticated(AuthData authData) {
                                        System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());

                                        Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/users");
                                        Query queryRef = ref.orderByChild("Email").equalTo(email.getText().toString());

                                        ValueEventListener listener = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot child : dataSnapshot.getChildren()) {


                                                        homeintent.putExtra("key", child.getKey());
                                                        homeintent.putExtra("Email", email.getText().toString());
                                                        SaveSharedPreference.setUserName(IntroActivity.this, child.getKey());
                                                        SaveSharedPreference.setEmail(IntroActivity.this, email.getText().toString());
                                                        startActivity(homeintent);

                                                    }
                                                } else {
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

                                    @Override
                                    public void onAuthenticationError(FirebaseError firebaseError) {
                                        // there was an error
                                    }
                                });

                            }

                        }

                );

                introActivityRegisterButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(IntroActivity.this, learn.RigesterActivity.class));

                            }

                        }

                );


            }
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
                            homeintent.putExtra("Email", email);
                            SaveSharedPreference.setUserName(IntroActivity.this, userkey);
                            SaveSharedPreference.setEmail(IntroActivity.this, email);
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

