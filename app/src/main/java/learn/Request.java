package learn;

/**
 * Created by Omaimah on 2/23/2016.
 */
public class Request {

    private String key;
    private String signerEmail;
    private String docID;
    private String requesterID;
    private String Order;
    private String Status;



    public Request(){}

    public Request(String key, String signerEmail, String docID, String requesterID, String order, String status) {
        this.key = key;
        this.signerEmail = signerEmail;
        this.docID = docID;
        this.requesterID = requesterID;
        this.Order = order;
        this.Status = status;

    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSignerEmail() {
        return signerEmail;
    }

    public void setSignerEmail(String signerEmail) {
        this.signerEmail = signerEmail;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getOrder() {
        return Order;
    }

    public void setOrder(String order) {
        Order = order;
    }

    public String getRequesterID() {
        return requesterID;
    }

    public void setRequesterID(String requesterID) {
        this.requesterID = requesterID;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }


}
