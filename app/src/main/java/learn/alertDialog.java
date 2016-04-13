package learn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
/**
 * Created by Daniyah.
 */
////////////////////this class is created to allow alert dialog inside class that is not an activity///////////////////////////////////////////

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
