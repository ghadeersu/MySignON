package learn.navdrawbase;

/**
 * Created by Mona on 3/6/2016.
 */

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DownloadFile extends AppCompatActivity {

    DownloadManager downloadManager;

    final String DOWNLOAD_FILE = "http://signon-pnu.byethost6.com/word.pdf";
    final String strPref_Download_ID = "PREF_DOWNLOAD_ID";

    SharedPreferences preferenceManager;

    private long myDownloadRefernce;
    private BroadcastReceiver receiverDownloadComplete;
    private BroadcastReceiver receiverNotificationClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_document);


        preferenceManager
                = PreferenceManager.getDefaultSharedPreferences(this);
        downloadManager
                = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);

        Button btnStartDownload = (Button)findViewById(R.id.button2);
        btnStartDownload.setOnClickListener(btnStartDownloadOnClickListener);

    }

    Button.OnClickListener btnStartDownloadOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            Uri downloadUri = Uri.parse(DOWNLOAD_FILE);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            String nameOfFile = URLUtil.guessFileName(DOWNLOAD_FILE, null,
                    MimeTypeMap.getFileExtensionFromUrl(DOWNLOAD_FILE));
            // request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS,nameOfFile);
            request.setDestinationInExternalPublicDir("/signon/download", nameOfFile);


            long id = downloadManager.enqueue(request);

            //Save the request id
            SharedPreferences.Editor PrefEdit = preferenceManager.edit();
            PrefEdit.putLong(strPref_Download_ID, id);
            PrefEdit.commit();

        }};

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        IntentFilter intentFilter
                = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        unregisterReceiver(downloadReceiver);
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(preferenceManager.getLong(strPref_Download_ID, 0));
            Cursor cursor = downloadManager.query(query);
            if(cursor.moveToFirst()){
                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int status = cursor.getInt(columnIndex);
                if(status == DownloadManager.STATUS_SUCCESSFUL){

                    //Retrieve the saved request id
                    long downloadID = preferenceManager.getLong(strPref_Download_ID, 0);

                    ParcelFileDescriptor file;
                    try {
                        file = downloadManager.openDownloadedFile(downloadID);
                        FileInputStream fileInputStream
                                = new ParcelFileDescriptor.AutoCloseInputStream(file);
                        Bitmap bm = BitmapFactory.decodeStream(fileInputStream);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        }
    };
}



//easy way
    /*    public void Download1(View view){
        Intent DownloadFile = new Intent (Intent.ACTION_VIEW,Uri.parse("http://signon-pnu.byethost6.com/Link.pdf"));
        startActivity(DownloadFile);
    }*/

