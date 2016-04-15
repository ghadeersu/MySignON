package learn;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;


/**
 * Created by daniah on 2/29/2016.
 */
////////////////////////////// wherever you want to upload use new HDWFTP_Upload().execute("/sdcard/signon/word.pdf");
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

public class HDWFTP_Upload extends AsyncTask<String, Void, Long> {

    private Context context;
    public ProgressDialog progress;
    String messagedigest, ekey, documentOwnerID, documentName, documentURL;
    documents document;
    documentsArrayAdapter documentAdapter;
    FTPClient ftpClient;
    String TAG="HELP";
    File f ;

    byte[] key;
    Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/users");
    HDWFTP_Upload(Context context){
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = ProgressDialog.show(context, "Uploading", "Uploading Document ", true, false);
    }

    protected Long doInBackground(String... FULL_PATH_TO_LOCAL_FILE ) {
        long returning=0;
        {
            Firebase.setAndroidContext(context);

            ftpClient = new FTPClient();
            int reply;
            try {
                System.out.println("Entered Data Upload loop!");
                ftpClient.connect("ftp.byethost4.com", 21);
                ftpClient.login("b4_17442719", "pnuisalie");
                ftpClient.sendCommand("QUOTE SITE NAMEFMT 1");
                ftpClient.changeWorkingDirectory("/htdocs/");


                reply = ftpClient.getReplyCode();


                if (FTPReply.isPositiveCompletion(reply)) {
                    System.out.println("Connected Success");
                } else {
                    System.out.println("Connection Failed");
                    ftpClient.disconnect();
                }
                ///////////////create directory/////////////////////////////////
                if(!(ftpClient.changeWorkingDirectory("/htdocs/"+session.userkey+"/"))){
                    ftpClient.makeDirectory("/htdocs/"+session.userkey+"/");
                    ftpClient.changeWorkingDirectory("/htdocs/" + session.userkey + "/");
                }
                String Picture_File_name = new File(FULL_PATH_TO_LOCAL_FILE[0]).getName();
                boolean exist=false;
                if (ftpClient.getReplyString().contains("250")) {
                    ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
                    messagedigest=SHA512.calculateSHA512(new File(FULL_PATH_TO_LOCAL_FILE[0]));
                    //////////////////encrypt////////////////////////////
                    try {
                        f = new File(FULL_PATH_TO_LOCAL_FILE[0]);
                        ekey = AESencryptionSecond.getencryptioKey();
                        key = ekey.getBytes(Charset.forName("ASCII"));
                        AESencryptionSecond.encrypt(key, f, f);
                        System.out.println("enc suc");
                        System.out.println(ekey);
                    } catch (CryptoException ex) {
                        System.out.println("enc not s");
                        System.out.println(ex.getMessage());
                        ex.printStackTrace();
                    }


                    BufferedInputStream buffIn = null;
                    System.out.println("Created an input stream buffer");
                    System.out.println(FULL_PATH_TO_LOCAL_FILE.toString());

                    buffIn = new BufferedInputStream(new FileInputStream(FULL_PATH_TO_LOCAL_FILE[0]));
                    ftpClient.enterLocalPassiveMode();

                    System.out.println("Entered binary and passive modes");
                    long size = 0;

                    FTPFile[] ftpFiles = ftpClient.listFiles();

                    for (int x = 0; x < ftpFiles.length; x++) {
                        try{
                            size +=getFileSize(ftpClient,ftpFiles[x].getName());}catch (Exception e){System.out.println("error from getsize");}
                        System.out.println("Folder size :" + size);
                        if ((ftpFiles[x].getName()).equals(Picture_File_name)) {
                            exist = true;
                        }

                    }
                    if (!exist) {
                        //////////size limit/////////////
                        if (size < 20000000) {


                        boolean result = ftpClient.storeFile(Picture_File_name, buffIn);

                        if (result) {
                            System.out.println("Success");
                            documentName = Picture_File_name;
                            documentOwnerID = session.userkey;
                            documentURL = "ftp.byethost4.com/htdocs/" + session.userkey + "/" + Picture_File_name + "/";
                            document = new documents(null, messagedigest, ekey, documentURL, documentOwnerID, documentName);
                            documentAdapter = new documentsArrayAdapter(context) {
                                public void onChildAdded(DataSnapshot dataSnapshot, String previeousChildName) {
                                }

                            };
                            documentAdapter.addItem(document);
                        }


                        System.out.println("File saved");

                            returning=3;
                        ftpClient.logout();
                        ftpClient.disconnect();
                        try {
                            AESencryptionSecond.decrypt(key, f, f);
                        } catch (CryptoException ex) {
                            System.out.println(ex.getMessage());
                            ex.printStackTrace();
                        }

                    }
                        else {System.out.println("Size overlimit");

                            returning=2;}
                }
                    else{

                        returning=1;

                    }

                }


            } catch (SocketException e) {
                Log.e("HDW FTP", e.getStackTrace().toString());
                System.out.println("Socket Exception!");
            } catch (UnknownHostException e) {
                Log.e("HDW FTP", e.getStackTrace().toString());
            } catch (IOException e) {
                Log.e("HDW FTP", e.getStackTrace().toString());
                System.out.println("IO Exception!");
            }

            return returning;

        }


    }

    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);
        int caseswitch=aLong.intValue();
        String message;
        Intent alert=new Intent(context, alertDialog.class);
        switch (caseswitch){
            case 0:
                message="Error while uploading file.";
                break;
            case 1:
                message="A file with the same name already exists.";
                break;
            case 2:
                message="You have reached the maximum size for storage, please delete a file.";
                break;
            case 3:
                message="File uploaded successfully.";

                break;
            default:
                message="Error while uploading file.";
                break;

        }
        alert.putExtra("message", message);
        progress.dismiss();
        context.startActivity(alert);
    }

   private long getFileSize(FTPClient ftp, String filePath) throws Exception {
       long fileSize = 0;
       FTPFile[] files = ftp.listFiles(filePath);
       if (files.length == 1 && files[0].isFile()) {
           fileSize = files[0].getSize();
       }
       Log.i(TAG, "File size = " + fileSize);
       return fileSize;
   }
}
