package learn;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;

import learn.R;
import learn.SHA512;
import learn.SignAndVerifyActivity;

public class hash extends AppCompatActivity {
    public TextView checksum;
    public TextView equalhash;
    public Button checkHashButton;
    public Button hashButton;
    public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/signon";

    String hashValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hash);

        checksum= (TextView) findViewById(R.id.checksum);
        equalhash=(TextView) findViewById(R.id.equalhash);
        hashButton=(Button) findViewById(R.id.hashButton);
        checkHashButton=(Button) findViewById(R.id.checkHashButton);
        File dir = new File(path);
        dir.mkdirs();
        File file = new File (path + "/word.pdf");
        hashValue = SHA512.calculateSHA512(file);
        checksum.setText(hashValue);

    }
    public void hashButtonClick(View v){
        File file = new File (path + "/word.pdf");
        hashValue=SHA512.calculateSHA512(file);
        checksum.setText(hashValue);

    }

    public void gotoSign(View v){

        Intent intent = new Intent(this,SignAndVerifyActivity.class);
        intent.putExtra("SHAhash", hashValue);
        startActivity(intent);


    }


    public void performButtonClick(View v){
        File file = new File (path + "/word2.pdf");
        if(SHA512.checkSHA512(hashValue,file))
            equalhash.setText("Same File");
        else
            equalhash.setText("Different File");


    }
}
