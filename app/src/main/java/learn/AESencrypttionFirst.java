
package learn.navdrawbase;

/**
 * Created by Naseebah on 10/02/16.
 */

import android.support.v7.app.AppCompatActivity;

/**
 * A tester for the CryptoUtils class.
 * @author www.codejava.net
 *
 */
public class AESencrypttionFirst extends AppCompatActivity {
 /*   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main();
    }
    public static void main() {


        /////////////////////////////Upload
        String newPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/signon/word.pdf";

        ////////////////////////////Upload & Download
        String encPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/signon/wordedanenc.enc";

        ////////////////////////////Download
        String decPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/signon/wordtrykeydan.pdf";


        ////////////////////////////Upload & Download
        /*
         Firebase.setAndroidContext(this);
        final Firebase mFirebase = new Firebase ("https://torrid-heat-4458.firebaseio.com/documents");
        final documentsArrayAdapter Docadapter = new documentsArrayAdapter(this);
        final documents CurrentDocument = new documents(null, String messagedigest, null, String documentURL, String documentOwnerID, String documentName) ;
        CurrentDocument.geteKey();

        //////////////////////////////
        //first message digest
        /////////////////////////////
        Docadapter.addItem(CurrentDocument);

        *
        *
        *
        *
        *
        * */
       /*final documents CurrentDocument = new documents();
        CurrentDocument.geteKey();

        String Skey = CurrentDocument.getEkey();
        byte[] ekey = Skey.getBytes(Charset.forName("ASCII"));


        //byte[] ekey = Skey.getBytes(Charset.forName("UTF-8"));
        //byte[] key = getKey();*/

   /*     File inputFile = new File(newPath);
        File encryptedFile = new File(encPath);
        File decryptedFile = new File(decPath);

        try {
           // AESencryptionSecond.encrypt(ekey, inputFile, inputFile);
            byte[] ekey = AESencryptionSecond.ReadKey();
            AESencryptionSecond.decrypt(ekey, inputFile, inputFile);

            //AESencryptionSecond.encrypt(ekey, inputFile, encryptedFile);
            //AESencryptionSecond.decrypt(ekey, encryptedFile, decryptedFile);
           // AESencryptionSecond.storeKey(ekey);
        } catch (CryptoException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
 /*   private static byte[] getKey(){


        KeyGenerator keyGen;


        byte[] dataKey=null;

        String Key=null;
        try {


            // Generate 256-bit key


            keyGen = KeyGenerator.getInstance("AES");


            keyGen.init(256);


            SecretKey secretKey = keyGen.generateKey();


            dataKey=secretKey.getEncoded();



        } catch (NoSuchAlgorithmException e) {


            // TODO Auto-generated catch block


            e.printStackTrace();

        }


        return dataKey;


    }
    /*
    public static byte[] readFully(String stream) throws IOException
    {
        byte[] buffer = new byte[256];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead;
        while ((bytesRead = stream.read(buffer)) != -1)
        {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }*/
}

