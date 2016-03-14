package learn.navdrawbase;


/**
 * Created by OTAKU on 28/01/2016.
 */

import java.math.BigInteger;
import java.util.Random;


/**
 *
 * @author User
 */
public class ECDSA {
    private BigInteger dA;    //private key of sender 'A', a randomly selected int less than n
    private BigInteger n;     //the order of the curve
    private Curve curve;    //the elliptic curve
    private Point G;        //the generator point, an elliptic curve domain parameter
    private Point QA;    //public key of sender 'A'

    public ECDSA() {
        dA = BigInteger.ZERO;
        curve = new Curve("P-256");
        n = curve.getN();
        G = curve.getG();
        QA = new Point();
    }

    public BigInteger getdA() {
        return dA;
    }

    //Generate public key from private key
    public void setdA(BigInteger dA) {
        this.dA = dA;
        QA = G.multiplication(this.dA);
    }

    public Point getQA() {
        return QA;
    }

    public void setQA(Point mypub){
        QA= mypub;

    }


    //BigInteger random generator in closed set [1, n]
    private BigInteger randomBigInteger(BigInteger n) {
        Random rnd = new Random();
        int maxNumBitLength = n.bitLength();
        BigInteger aRandomBigInt;
        do {
            aRandomBigInt = new BigInteger(maxNumBitLength, rnd);
            // compare random number lessthan ginven number
        } while (aRandomBigInt.compareTo(n) > 0);
        return aRandomBigInt;
    }

    //For signing a message m by sender A, using A’s private key dA
    //Returns signature in hex string representation
    public String signingMessage(String m) throws Exception{
        Point signPoint = signatureGeneration(m);
        String signPointString = signPoint.toHexString();
        return signPointString;
    }

    //For checking A's signature in message m. signature is in hex string representation
    //Returns true if the signature is valid, returns false if it is invalid
    public boolean checkSignature(String m, String signature){
        int len = signature.length();
        Point signPoint = new Point();
        signPoint.setX(new BigInteger(signature.substring(0, len/2), 16));
        signPoint.setY(new BigInteger(signature.substring(len/2), 16));
        return signatureVerification(m, signPoint);
    }

    //For generating a signature using private key dA on message m
    //Returns signature in point representation
    private Point signatureGeneration(String m){
        BigInteger e, k, r, s = BigInteger.ZERO;
        // e = HASH(m)
        e = new BigInteger("7e16b5527c77ea58bac36dddda6f5b444f32e81b", 16);
        Point x1y1 = new Point();
        Random rand = new Random();
        do{
            k = randomBigInteger(n.subtract(BigInteger.ONE));
            x1y1 = G.multiplication(k);
            r = x1y1.getX().mod(n);
            if (! (r.compareTo(BigInteger.ZERO) == 0)){
                if (k.gcd(n).compareTo(BigInteger.ONE) == 0){
                    BigInteger temp = k.modInverse(n);
                    s = (temp.multiply((dA.multiply(r)).add(e))).mod(n);
                }
            }
        } while ((r.compareTo(BigInteger.ZERO) == 0) || (s.compareTo(BigInteger.ZERO) == 0));
        Point signature = new Point();
        signature.setX(r);
        signature.setY(s);
        return signature;
    }

    //Authenticate A's point signature
    //Returns true if the signature is valid, returns false if it is invalid
    private boolean signatureVerification(String m, Point signature){
        BigInteger r = signature.getX();
        BigInteger s = signature.getY();
        BigInteger e, w, u1, u2;
        if ((r.compareTo(BigInteger.ONE) >= 0) &&
                (r.compareTo(n.subtract(BigInteger.ONE)) <= 0) &&
                (s.compareTo(BigInteger.ONE) >= 0) &&
                (s.compareTo(n.subtract(BigInteger.ONE)) <= 0)){
            // e = HASH(m)
            e = new BigInteger("7e16b5527c77ea58bac36dddda6f5b444f32e81b", 16);
            w = s.modInverse(n);
            u1 = (e.multiply(w)).mod(n);
            u2 = (r.multiply(w)).mod(n);
            Point x1y1 = new Point();
            x1y1 = (G.multiplication(u1)).addition(QA.multiplication(u2));
            if ((x1y1.getX().mod(n)).compareTo(r.mod(n)) == 0){
                return true;
            } else {
                System.out.println("x1 = " + x1y1.getX().mod(n) + " | " + "r(mod n) = " + r.mod(n));
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {


        // the content of this main will be moved to signing activity *ghadeer

        /*
      try {
            // sign ducoment
            String mydocument = "Im ghadeer";
            ECDSA app = new ECDSA();  // eliptic curve opject
            String primarykey = "4435345888889823424880345";
            BigInteger test = new BigInteger(primarykey);

            BigInteger password = new BigInteger("4");

            app.setdA(password);
           String signature = app.signingMessage(mydocument);
            Point pubkey = app.getQA();
signture.setText(signature);



            // varify document

ECDSA app2 = new ECDSA();
            app2.setQA(pubkey);
boolean check = app2.checkSignature(mydocument,signature);
            varify.setText(" varifcation is " + check );


        }
        catch (java.lang.Exception e1){  //
            signture.setText("in java.lang exciption");
              Toast toast = Toast.makeText(getApplicationContext(),"Error please try again later",Toast.LENGTH_LONG);
        }
    }
*/

    }
}