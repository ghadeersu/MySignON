package learn;

/**
 * Created by daniah on 2/19/2016.
 */

public class signature {
    private String key;
    private String signatureBase64;
    private String signatureName;
    private String signerID;
    public signature(String key, String signatureBase64, String signatureName, String signerID){
        this.key=key;
        this.signatureBase64=signatureBase64;
        this.signatureName=signatureName;
        this.signerID=signerID;
    }

    public void setsignatureBase64(String signatureBase64){
        this.signatureBase64=signatureBase64;
    }
    public void setsignatureName(String signatureName){
        this.signatureName=signatureName;
    }
    public void setsignerID(String signerID){
        this.signerID=signerID;
    }

    public String getsignatureBase64(){return signatureBase64;}
    public String getSignatureName(){return signatureName;}
    public String getSignerID(){return signerID;};

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}