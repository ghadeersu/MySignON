package learn;

/**
 * Created by Naseebah on 02/04/16.
 */
public class digsignature {
    private String key;
    private String signature;
    private String docID;
    private String signerID;

    public digsignature(String key, String signerID, String docID, String signature) {
        this.key = key;
        this.signerID = signerID;
        this.docID = docID;
        this.signature = signature;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getSignerID() {
        return signerID;
    }

    public void setSignerID(String signerID) {
        this.signerID = signerID;
    }
}
