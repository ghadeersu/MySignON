package learn;

/**
 * Created by OTAKU on 06/03/2016.
 */
public class session {

    public static String userkey;
    public static String base64;
    public static String docKey="-KBsnv7uKfs3FCmq3uLG";
    public static int homecounter = 0;
    public static String requesterID;
    public static String userEmail;
    public static String requestID;


    public static void destructor(){
        userkey=null;
        base64=null;
        docKey=null;
        homecounter=0;
        requesterID=null;
        userEmail=null;
        requestID=null;

    }

}
