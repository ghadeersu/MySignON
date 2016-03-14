package learn;

import learn.User;

/**
 * Created by Naseebah on 24/02/16.
 */
public class documents {
    private String key;
    private String documentName;
    private String documentOwnerID;
    private User Owner;
    private String documentURL;
    private String ekey;
    private String messagedigest;

    public documents(){}
    public documents(String key, String messagedigest, String ekey, String documentURL, String documentOwnerID, String documentName) {
        this.key=key;
        this.messagedigest = messagedigest;
        this.ekey = ekey;
        this.documentURL = documentURL;
        this.documentOwnerID = documentOwnerID;
        this.documentName = documentName;
    }
    public User getOwner() {
        return Owner;
    }

    public void setOwner(User owner) {
        Owner = owner;
    }
    public String Ownerkey (){return Owner.getKey();}
    public String getEkey() {
        return ekey;
    }

    public void setEkey(String ekey) {
        this.ekey = ekey;
    }
    public String getMessagedigest() {
        return messagedigest;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentOwnerID() {
        return this.documentOwnerID;

       // return Owner.getKey();
    }
    public void setDocumentOwnerID(String documentOwnerID) {
        this.documentOwnerID = documentOwnerID;
    }

    public String getDocumentURL() {
        return documentURL;
    }

    public void setDocumentURL(String documentURL) {
        this.documentURL = documentURL;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setMessagedigest(String messagedigest) {
        this.messagedigest = messagedigest;
    }


   /* public void geteKey() {


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
        // Key = new String();
        setEkey(storeStr);

    }*/
}
