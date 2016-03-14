package learn.navdrawbase;

/**
 * Created by daniah on 2/29/2016.
 */
////////////////////////////// wherever you want to upload use new HDWFTP_Upload().execute("/sdcard/signon/word.pdf");
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

public class HDWFTP_Upload_Update extends AsyncTask <String, Void, Long>{

    private Context context;
    String messagedigest, ekey, documentOwnerID, documentName, documentURL;
    documents document;
    documentsArrayAdapter documentAdapter;
    File f ;
    byte[] key;
    Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/documents/"+session.docKey+"/");
String originalOwner;


    HDWFTP_Upload_Update(Context context){
        this.context=context;
    }

    protected Long doInBackground(String... FULL_PATH_TO_LOCAL_FILE ) {

        {
            Firebase.setAndroidContext(context);
            ref.child("documentOwnerID").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    System.out.println("MESSAGE:" + dataSnapshot.getValue(String.class));
                    originalOwner=dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            //Query q =ref.orderByChild("documentOwnerID");
           // q.

            FTPClient ftpClient = new FTPClient();
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
///////////////create directory

                    ftpClient.changeWorkingDirectory("/htdocs/" + originalOwner + "/");
                System.out.println("ASS:" + "/htdocs/" + originalOwner + "/");
                String Picture_File_name = new File(FULL_PATH_TO_LOCAL_FILE[0]).getName();
                System.out.println("ASS:" + Picture_File_name);

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
    System.out.println(ex.getMessage());
    ex.printStackTrace();
}






                    BufferedInputStream buffIn = null;
                    System.out.println("Created an input stream buffer");
                    System.out.println(FULL_PATH_TO_LOCAL_FILE.toString());

                    buffIn = new BufferedInputStream(new FileInputStream(FULL_PATH_TO_LOCAL_FILE[0]));
                    ftpClient.enterLocalPassiveMode();

                    System.out.println("Entered binary and passive modes");



                    boolean result = ftpClient.storeFile(Picture_File_name, buffIn);

                    if (result) {
                        System.out.println("Success");
                        System.out.println("name "+Picture_File_name);
                        documentName=Picture_File_name;

                        documentURL="ftp.byethost4.com/htdocs/"+originalOwner+"/"+Picture_File_name+"/";
                        System.out.println("ftp.byethost4.com/htdocs/"+originalOwner+"/"+Picture_File_name+"/");
                        messagedigest=SHA512.calculateSHA512(new File(FULL_PATH_TO_LOCAL_FILE[0]));
                        System.out.println(messagedigest);
                        System.out.println(ekey);
                        ///temp
                        document=new documents(session.docKey,messagedigest,ekey,documentURL,documentOwnerID,documentName);
                        System.out.println(document.getKey());
                        documentAdapter=new documentsArrayAdapter(context){
                            public void onChildAdded(DataSnapshot dataSnapshot, String previeousChildName){}

                        };
                        documentAdapter.updateItem(document);
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


}
