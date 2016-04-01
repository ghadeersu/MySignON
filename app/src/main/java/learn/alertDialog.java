package learn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import learn.R;


public class alertDialog extends AppCompatActivity {
    private TextView message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_dialog);
        message=(TextView)findViewById(R.id.messageAlert);
        Bundle extras = getIntent().getExtras();
        message.setText(extras.getString("message"));
    }
    public void close(View v){
        finish();


    }

}
