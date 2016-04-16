package learn;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class HomeActivity extends BaseActivity {
    Query numberofRequestQuery;
    TextView Emailtext;
    private static final String TAG = "Snap";
    ImageView signatureImageView;
    TextView pendingTextViewHome;
    String path;

    int numberofRequests;
    ImageView imageView;
    private static final int FILE_SELECT_CODE = 0;
    View rootview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        pendingTextViewHome=(TextView)findViewById(R.id.pendingTextViewHome);
        rootview = findViewById(R.id.activity1_container);
        mNavigationView.getMenu().getItem(0).setChecked(true);
        Firebase.setAndroidContext(this);
        numberofRequests=0;
        test();
        if(session.homecounter==0){
        Bundle extras = getIntent().getExtras();
        session.userkey = extras.getString("key");
        session.userEmail = extras.getString("Email");
        session.homecounter=1;
            test();
        }
        signatureImageView=(ImageView)findViewById(R.id.homeSignatureImageView);
        if(SaveSharedPreference.getUserName(HomeActivity.this).isEmpty()) {
            Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/users/" + session.userkey + "/username/");
            Query queryRef = ref.orderByValue();
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TextView usernametext = (TextView) findViewById(R.id.textView15);
                    usernametext.setText((String) dataSnapshot.getValue());
                    System.out.println(session.userEmail);
                    String x = session.userEmail;
                    Emailtext = (TextView) findViewById(R.id.textView16);
                    Emailtext.setText(x);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            };

            queryRef.addListenerForSingleValueEvent(listener);

        }
        else{
            TextView usernametext = (TextView) findViewById(R.id.textView15);
            usernametext.setText(SaveSharedPreference.getName(HomeActivity.this));
            Emailtext = (TextView) findViewById(R.id.textView16);
            Emailtext.setText(SaveSharedPreference.getEmail(HomeActivity.this));
        }
    }
    public void uploadDocument(View v){
    ///////////////check permission////////////////
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);

        }
        else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            try {


                startActivityForResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        FILE_SELECT_CODE);
            } catch (android.content.ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(this, "Please install a File Manager.",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    try{
                        Uri uri = data.getData();
                        Log.d(TAG, "File Uri: " + uri.toString());
                        // Get the path

                        String uriString=uri.getPath();
                        Log.d(TAG, "String Path: " + uriString);
                        path = FileUtils.getPath(this, uri);

                        if(path!=null){
                        String  Type=path.substring(path.lastIndexOf(".") + 1, path.length());
                        Log.d(TAG, "File Path: " + path);

                            switch (Type){
                                case "txt":
                                    /////////txt to pdf/////////////////////
                                    BufferedReader br = null;

                                    try {
                                        File file=new File(path);
                                        String newPath=path.replace(".txt",".pdf");
                                        Document pdfDoc = new Document(PageSize.A4);
                                        String output_file =  newPath;
                                        System.out.println("## writing to: " + output_file);
                                        PdfWriter.getInstance(pdfDoc, new FileOutputStream(output_file)).setPdfVersion(PdfWriter.VERSION_1_7);;

                                        pdfDoc.open();

                                        Font myfont = new Font();
                                        myfont.setStyle(Font.NORMAL);
                                        myfont.setSize(11);

                                        pdfDoc.add(new Paragraph("\n"));

                                        if (file.exists()) {

                                            br = new BufferedReader(new FileReader(file));
                                            String strLine;

                                            while ((strLine = br.readLine()) != null) {
                                                Paragraph para = new Paragraph(strLine + "\n", myfont);
                                                para.setAlignment(Element.ALIGN_JUSTIFIED);
                                                pdfDoc.add(para);
                                            }
                                        } else {
                                            System.out.println("no such file exists!");

                                        }
                                        pdfDoc.close();
                                        path=newPath;
                                    }

                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                case "pdf":
                                case "ico":
                                case "jpg":
                                case "jpeg":
                                case "bmp":
                                case "png":
                                    new HDWFTP_Upload(HomeActivity.this).execute(path);
                                    break;
                                default:
                                    AlertDialog alert = new AlertDialog.Builder(HomeActivity.this).setMessage("Invalid Type").setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                          public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                          }
                                        }).show();

                                        break;
                            }

                        }
                        else{
                            AlertDialog alert = new AlertDialog.Builder(HomeActivity.this).setMessage("Please use another File manager").setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            }).show();}


                    }
                    catch (URISyntaxException e){
                        Toast.makeText(this, "Please install a File Manager.",
                                Toast.LENGTH_SHORT).show();}

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //////////////////upload code/////////////////////////////////////////////////////
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);

                    try {


                        startActivityForResult(
                                Intent.createChooser(intent, "Select a File to Upload"),
                                FILE_SELECT_CODE);
                    } catch (android.content.ActivityNotFoundException ex) {
                        // Potentially direct the user to the Market with a Dialog
                        Toast.makeText(this, "Please install a File Manager.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(HomeActivity.this, "Permission deny to read your External storage", Toast.LENGTH_SHORT).show();
                }

            }


        }
    }


    public void createSignatureButton(View v) {
        startActivity(new Intent(HomeActivity.this, CaptureSignatureActivity.class));
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this).setTitle("Log out").setMessage("Are you sure you want to log out?").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ////////logout//////////////////////
                session.destructor();
                SaveSharedPreference.clearShared(HomeActivity.this);
                Firebase ref=new Firebase("https://torrid-heat-4458.firebaseio.com");
                ref.unauth();
                finish();
                new Intent(HomeActivity.this, IntroActivity.class);
            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        }).show();

    }
    @Override
    public void onResume(){

       changeImageView();
      //  pendingTextViewHome.setText("Pending Documents: " + session.oldNumberOfRequests + "");
        super.onResume();

    }

    public void changeImageView(){
        final ImageView test=(ImageView)findViewById(R.id.homeSignatureImageView);
        Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/signature");
        Query queryRef2 = ref.orderByChild("signerID").equalTo(session.userkey);
        ValueEventListener listener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                        session.base64=child.getKey();
                        byte[] temp= Base64.decode(child.child("signatureBase64").getValue(String.class), Base64.NO_WRAP);
                        Bitmap img= BitmapFactory.decodeByteArray(temp, 0, temp.length);
                        test.setImageBitmap(img);
                    }
                }
                else
                    test.setImageResource(0);


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        queryRef2.addValueEventListener(listener2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

void test(){
    numberofRequests=0;
    Firebase ref2 = new Firebase("https://torrid-heat-4458.firebaseio.com/requests/");
    numberofRequestQuery = ref2.orderByChild("SignerEmail").equalTo(session.userEmail);
    numberofRequestQuery.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot Req) {
            for (DataSnapshot child : Req.getChildren()) {
                if (child.child("status").getValue(String.class).equals("waiting")) {
                    numberofRequests++;

                }

            }
            pendingTextViewHome.setText("Pending Documents: " + numberofRequests + "");
            session.oldNumberOfRequests=numberofRequests;
            numberofRequestQuery.removeEventListener(this);
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    });

    }


}
