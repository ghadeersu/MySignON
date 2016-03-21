package learn;


import android.os.AsyncTask;
import android.util.Log;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import learn.session;

/**
 * Created by daniah on 3/21/2016.
 */
public class HDWFTP_Delete  extends AsyncTask<String, Void, Long> {
    FTPClient ftpClient;

    protected Long doInBackground(String... FULL_PATH_TO_LOCAL_FILE ) {

        {

            ftpClient = new FTPClient();
            int reply;
            try {
                System.out.println("Entered Data Delete loop!");
                ftpClient.connect("ftp.byethost4.com", 21);
                ftpClient.login("b4_17442719", "pnuisalie");
                ftpClient.sendCommand("QUOTE SITE NAMEFMT 1");
                ftpClient.changeWorkingDirectory("/htdocs/");
                ftpClient.changeWorkingDirectory(session.userkey);

                reply = ftpClient.getReplyCode();


                if (FTPReply.isPositiveCompletion(reply)) {
                    System.out.println("Connected Success");
                } else {
                    System.out.println("Connection Failed");
                    ftpClient.disconnect();
                }
                if (ftpClient.getReplyString().contains("250")) {
                    ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);

                    ftpClient.enterLocalPassiveMode();

                    System.out.println("Entered binary and passive modes");




                        boolean result = ftpClient.deleteFile(FULL_PATH_TO_LOCAL_FILE[0]);

                        if (result) {
                            System.out.println("File deleted");
                        }


                        ftpClient.logout();
                        ftpClient.disconnect();



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

