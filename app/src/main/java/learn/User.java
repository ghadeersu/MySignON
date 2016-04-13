package learn;

import java.math.BigInteger;

import java.math.BigInteger;

/**
 * Created by Omaimah on 2/19/2016.
 */
public class User {

    private String key;
    private String Email;
    private BigInteger a;
    private BigInteger p;
    private String birthdate;
    boolean infinity;
    private String username;
    private BigInteger x;
    private BigInteger y;
    private BigInteger PK;

    public BigInteger getPK() {
        return PK;
    }

    public void setPK(BigInteger PK) {
        this.PK = PK;
    }

    public BigInteger getA() {
        return a;
    }

    public void setA(BigInteger a) {
        this.a = a;
    }

    public BigInteger getP() {
        return p;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public boolean isInfinity() {
        return infinity;
    }

    public void setInfinity(boolean infinity) {
        this.infinity = infinity;
    }

    public BigInteger getY() {
        return y;
    }

    public void setY(BigInteger y) {
        this.y = y;
    }

    public BigInteger getX() {
        return x;
    }

    public void setX(BigInteger x) {
        this.x = x;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }




    public User(String key, String email, String birthdate, String username) {
        this.key = key;
        Email = email;
        this.birthdate = birthdate;
        this.username = username;
    }

    public void CreateECDSAobject ( BigInteger PrK )
    {
        PK = PrK;
        ECDSA obj = new ECDSA();
        obj.setdA(PrK);
        Point QA = obj.getQA(); // public key
        a = QA.getA();   // store values
        p = QA.getP();
        x = QA.getX();
        y = QA.getY();

        if(QA.isInfinity()){
            infinity = true;
        }
        else {
            infinity = false;
        }

    }

}
