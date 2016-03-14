package learn.navdrawbase;

/**
 * Created by Naseebah on 10/02/16.
 */
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * A utility class that encrypts or decrypts a file.
 * @author www.codejava.net
 *
 */
public class AESencryptionSecond {

    //////////// To specify type of encryption/ decryption method
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    ///////////if we want to encryption store key in a text file as bytes ---> call this method with encryption
    public static void storeKey (byte[] ekey)throws IOException
    {
        try {
            //////////// change the path to where you want to store the key
            String keyPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/signon/wordKeydan.txt";
            ///////////////////////////////////////////////////////////////////
            File keyFile = new File(keyPath);

            FileOutputStream output = new FileOutputStream(keyFile);
            output.write(ekey);
            output.close();
        } catch(IOException ex) {
            throw new IOException ("Error encrypting/decrypting file", ex);
        }

    }

    ///////////if we want to read encryption key that was stored in a text file as bytes ---> call this method with decryption
    public static byte[] ReadKey ()throws IOException
    {
        try {
            //////////// change the path to where you want to read the key from
            String keyPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/signon/wordKeydan.txt";
            //////////////////////////////////////////////////////////////////////

            File keyFile = new File(keyPath);

            FileInputStream inputStream = new FileInputStream(keyFile);
            byte[] inputBytes =readFully(inputStream);
            inputStream.read(inputBytes);
            inputStream.close();
            return inputBytes;
        } catch(IOException ex) {
            throw new IOException ("Error encrypting/decrypting file", ex);
        }

    }

    /////////// call to encrypt file
    public static void encrypt(byte[] key, File inputFile, File outputFile)
            throws CryptoException {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

    //////////  call to decrypt file
    public static void decrypt(byte[] key, File inputFile, File outputFile)
            throws CryptoException {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }

    ///////// To read all bytes in the input stream
    public static byte[] readFully(InputStream stream) throws IOException
    {
        byte[] buffer = new byte[8192];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead;
        while ((bytesRead = stream.read(buffer)) != -1)
        {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }


    private static void doCrypto(int cipherMode, byte[] key, File inputFile,
                                 File outputFile) throws CryptoException {
        try {
            //Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Key secretKey = new SecretKeySpec(key, ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            FileInputStream inputStream = new FileInputStream(inputFile);

            // byte[] inputBytes = new byte[(int) inputFile.length()];

            byte[] inputBytes =readFully(inputStream);
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();

        } catch (NoSuchPaddingException ex)
        {throw new CryptoException("Error NoSuchPaddingException", ex);}
        catch  (NoSuchAlgorithmException ex)
        {throw new CryptoException("Error NoSuchAlgorithmException", ex);}
        catch (InvalidKeyException ex)
        {throw new CryptoException("Error InvalidKeyException", ex);}
        catch(BadPaddingException ex) {throw new CryptoException("Error BadPaddingException", ex);}
        catch(IllegalBlockSizeException ex) {throw new CryptoException("Error IllegalBlockSizeException", ex);}
        catch(IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }

    ///////////// call this method to generate new key for encryption BUT REMEMPER to save the key to decrypt the file later
    ////////////  when call:
    // 1. String encryptionKeyString = AESencryptionSecond.getencryptioKey();
    // 2. byte[] encryptionKeyByteArray = ekey.getBytes(Charset.forName("ASCII"));
    public static String getencryptioKey() {


        KeyGenerator keyGen;


        byte[] dataKey = null;

        String Key = null;
        try {


            // Generate 256-bit key


            keyGen = KeyGenerator.getInstance("AES");


            keyGen.init(256);


            SecretKey secretKey = keyGen.generateKey();


            dataKey = secretKey.getEncoded();


        } catch (NoSuchAlgorithmException e) {


            // TODO Auto-generated catch block


            e.printStackTrace();

        }
        String storeStr = new String(dataKey, Charset.forName("ASCII"));
        return storeStr;

    }

/*    public static byte[] getencryptioKey(){


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


    }*/
}
