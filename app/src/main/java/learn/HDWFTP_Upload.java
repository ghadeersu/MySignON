package learn;

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

    protected Long doInBackground(String... FULL_PATH_TO_LOCAL_FILE ) {

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
                //ftpPrintFilesList("/"+session.userkey+"/");
///////////////create directory
                if(!(ftpClient.changeWorkingDirectory("/htdocs/"+session.userkey+"/"))){
                    ftpClient.makeDirectory("/htdocs/"+session.userkey+"/");
                    ftpClient.changeWorkingDirectory("/htdocs/" + session.userkey + "/");
                }
                // ftpClient.changeToParentDirectory();
//                int length=ftpClient.listNames().length;
                //      System.out.println("length"+length);

                //         String[] names=ftpClient.listNames();

                String Picture_File_name = new File(FULL_PATH_TO_LOCAL_FILE[0]).getName();
                // boolean exist=false;
             /*   for (String name : names) {
                    if (name.equals(Picture_File_name))
                        exist = true;
                }*/
                boolean exist=false;


                System.out.println(exist);
                //exist=checkName(new File(FULL_PATH_TO_LOCAL_FILE[0]));


                System.out.println("inside f");
                if (ftpClient.getReplyString().contains("250")) {
                    ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
                    //////////////////encrypt////////////////////////////
                    try {
                        f = new File(FULL_PATH_TO_LOCAL_FILE[0]);
                        ekey = AESencryptionSecond.getencryptioKey();
                        key = ekey.getBytes(Charset.forName("ASCII"));
                        AESencryptionSecond.encrypt(key, f, f);
                        System.out.println("enc suc");
                        System.out.println(ekey);
                    }
                    catch (CryptoException ex) {
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

                    FTPFile[] ftpFiles =ftpClient.listFiles();
                    System.out.println("CHECK ME "+ftpFiles.length);
                    for (int x=0;x<ftpFiles.length;x++) {
                        //  System.out.println("Check me"+ftpFiles[x].getName() + exist);
                        if ((ftpFiles[x].getName()).equals(Picture_File_name)){
                            exist = true;
                        }

                    }
                    if(!exist){

                        boolean result = ftpClient.storeFile(Picture_File_name, buffIn);

                        if (result) {
                            System.out.println("Success");
                            documentName=Picture_File_name;
                            documentOwnerID=session.userkey;
                            documentURL="ftp.byethost4.com/htdocs/"+session.userkey+"/"+Picture_File_name+"/";
                            messagedigest=SHA512.calculateSHA512(new File(FULL_PATH_TO_LOCAL_FILE[0]));
                            ///temp
                            document=new documents(null,messagedigest,ekey,documentURL,documentOwnerID,documentName);
                            documentAdapter=new documentsArrayAdapter(context){
                                public void onChildAdded(DataSnapshot dataSnapshot, String previeousChildName){}

                            };
                            documentAdapter.addItem(document);
                        }


                        System.out.println("File saved");


                        ftpClient.logout();
                        ftpClient.disconnect();
                        try {
                            AESencryptionSecond.decrypt(key, f, f);
                        }
                        catch (CryptoException ex) {
                            System.out.println(ex.getMessage());
                            ex.printStackTrace();
                        }

                    }
                    else{

                        context.startActivity(new Intent(context, alertDialog.class));

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

            return null;

        }


    }

    public void ftpPrintFilesList(String dir_path)
    {
        try {
            FTPFile[] ftpFiles = ftpClient.listFiles(dir_path);
            int length = ftpFiles.length;
            for (int i = 0; i < length; i++) {
                String name = ftpFiles[i].getName();
                boolean isFile = ftpFiles[i].isFile();

                if (isFile) {
                    Log.i(TAG, "File : " + name);
                }
                else {
                    Log.i(TAG, "Directory : " + name);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    boolean checkName(File f){
        FTPClient mFTPClient = null;

        try {
            String TAG="MESSAGE";
            mFTPClient = new FTPClient();
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            // connecting to the host
            mFTPClient.connect("ftp.byethost4.com", 21);

            // now check the reply code, if positive mean connection success
            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                // login using username & password
                boolean status = mFTPClient.login("b4_17442719", "pnuisalie");
                Log.i(TAG, "connection : " + status);
            /* Set File Transfer Mode
             *
             * To avoid corruption issue you must specified a correct
             * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
             * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE
             * for transferring text, image, and compressed files.
             */
                mFTPClient.enterLocalPassiveMode();
                FTPFile[] ftpFiles = mFTPClient.listFiles("/htdocs/"+session.userkey+"/");
                int length = ftpFiles.length;
                Log.i(TAG, "connection : " + length);
                for (int i = 0; i < length; i++) {
                    String name = ftpFiles[i].getName();
                    if(name.equals(f.getName()))
                        return true;

                }
                return false;
            }} catch(Exception e) {
            e.printStackTrace();
        }


        return false;
    }

   /* private void createHandler() {
        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"cant",Toast.LENGTH_LONG).show();
                        handler.removeCallbacks(this);
                        Looper.myLooper().quit();
                    }
                }, 2000);

                Looper.loop();
            }
        };
        thread.start();
    }*/
}
