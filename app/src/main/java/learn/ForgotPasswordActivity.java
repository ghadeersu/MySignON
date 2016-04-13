package learn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
    }
    public void SubmitOnClick(View view)
    {
        EditText etEmail = (EditText) findViewById(R.id.etEmail);
        String email = etEmail.getText().toString();
        Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com");
        ref.resetPassword(email, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                // password reset email sent
                Toast.makeText(ForgotPasswordActivity.this, "password reset Email sent!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(ForgotPasswordActivity.this, "Email cannot be found", Toast.LENGTH_SHORT).show();

            }

        });
    }
}
