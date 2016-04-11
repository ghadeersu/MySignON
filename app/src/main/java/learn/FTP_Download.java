package learn;

/**
 * Created by Naseebah on 14/03/16.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.firebase.client.Firebase;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

public class FTP_Download extends AsyncTask <String, Void, Long>{
    String Type;
    private Context context;
    static String DocName,EncKey,DocOwner, Operation;

    FTP_Download(Context context){
        this.context=context;
    }
    public static void iniate (/*String path1,*/ String DocName1 , String EncKey1,String DocOwner1,String Operation1){
        //path=path1;

        DocName=DocName1;
        EncKey=EncKey1;
        DocOwner=DocOwner1;
        Operation=Operation1;
    }
    @Override
    protected Long doInBackground(String... path) {

        Firebase.setAndroidContext(context);
        int reply;

        FTPClient ftpClient = new FTPClient();
        try {

            ftpClient.connect("ftp.byethost4.com", 21);
            ftpClient.login("b4_17442719", "pnuisalie");
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            reply = ftpClient.getReplyCode();

            if (FTPReply.isPositiveCompletion(reply)) {
                System.out.println("Connected Success");
            } else {
                System.out.println("Connection Failed");
                ftpClient.disconnect();
            }

            // APPROACH #1: using retrieveFile(String, OutputStream)
            String remoteFile1 = "/htdocs/" + DocOwner + "/" + DocName;
            File SignonDirectory = new File(Environment.getExternalStorageDirectory()+File.separator+"signon"+File.separator+"download");
            SignonDirectory.mkdirs();
            String FullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/signon/download/" + DocName;
            File downloadFile1 = new File(FullPath);
            OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
            boolean success = ftpClient.retrieveFile(remoteFile1, outputStream1);
            outputStream1.close();

            if (success) {
                System.out.println("File #1 has been downloaded successfully.");

                ///////////////////////////////////////////////////////////////////////////
                /////////////////////////////////Decryption////////////////////////////////
                ///////////////////////////////////////////////////////////////////////////

                try {
                    byte[] ekey = EncKey.getBytes(Charset.forName("ASCII"));
                    AESencryptionSecond.decrypt(ekey, downloadFile1, downloadFile1);
                    System.out.println(FullPath);
                    System.out.println(EncKey);

                } catch (CryptoException ex) {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                }

                ///////////////////////////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////////////////////////
                Type=FullPath.substring(FullPath.lastIndexOf(".") + 1, FullPath.length());
                switch (Operation)
                {
                    case "View":
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        if(Type.equals("pdf"))
                        intent.setDataAndType(Uri.fromFile(downloadFile1), "application/pdf");
                        else
                            intent.setDataAndType(Uri.fromFile(downloadFile1), "image/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        context.startActivity(intent);
                        break;
                    case "Sign":
                        Intent i = new Intent(context, MyPdfViewerActivity.class);
                        i.putExtra(Pdftry.EXTRA_PDFFILENAME, FullPath.toString());
                        context.startActivity(i);
                        break;
                 /*   case "Request":
                        context.startActivity(new Intent(context, Request_Signture.class));
                        break;*/
                    default:
                        break;
                }
            }

            // APPROACH #2: using InputStream retrieveFileStream(String)
           /* String remoteFile2 = "/htdocs/" + session.userkey + "/" + DocName;
            String FullPath1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/signon/download/" + DocName;
            File downloadFile2 = new File(FullPath1);
            OutputStream outputStream2 = new BufferedOutputStream(new FileOutputStream(downloadFile2));
            InputStream inputStream = ftpClient.retrieveFileStream(remoteFile2);
            byte[] bytesArray = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                outputStream2.write(bytesArray, 0, bytesRead);
            }

            success = ftpClient.completePendingCommand();
            if (success) {
                System.out.println("File #2 has been downloaded successfully.");
            }
            outputStream2.close();
            inputStream.close();*/


        } catch (SocketException e) {
            Log.e("Download FTP", e.getStackTrace().toString());
            System.out.println("Socket Exception!");
        } catch (UnknownHostException e) {
            Log.e("Download FTP", e.getStackTrace().toString());
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}