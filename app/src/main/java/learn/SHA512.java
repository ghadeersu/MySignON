package learn.navdrawbase;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by daniah on 1/26/2016.
 */
public class SHA512 {
    private static final String TAG = "SHA-512";

    public static boolean checkSHA512(String sha512, File updateFile) {
        if (TextUtils.isEmpty(sha512) || updateFile == null) {
            Log.e(TAG, "SHA-512 string empty or updateFile null");
            return false;
        }

        String calculatedDigest = calculateSHA512(updateFile);
        if (calculatedDigest == null) {
            Log.e(TAG, "calculatedDigest null");
            return false;
        }

        Log.v(TAG, "Calculated digest: " + calculatedDigest);
        Log.v(TAG, "Provided digest: " + sha512);

        return calculatedDigest.equalsIgnoreCase(sha512);
    }

    public static String calculateSHA512(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] sha512sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, sha512sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for SHA-512", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception on closing SHA-512 input stream", e);
            }
        }
    }
}
