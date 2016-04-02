package learn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.math.BigInteger;

import android.net.UrlQuerySanitizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
//kkkk
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.lang.String;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import learn.R;


/**
 * Created by OTAKU on 16/03/2016.
 */
public class DigitalSignatureSignAndVerfiy {

    private Context context;
     private    String requestID;
     private    String documentID; // this variable will be used in both signing and verifying
     private    String thedigest;
       private String signature;



    public void Startsigningowner(){ // get public key from FIREBASE


        Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/users/"+session.userkey+"/");

        Query queryref = ref.orderByValue();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                BigInteger privatekey;

                privatekey=new BigInteger(dataSnapshot.child("PK").getValue(String.class));

                ECDSASIGNINGowner(privatekey);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        queryref.addValueEventListener(listener);


    }


    private void ECDSASIGNINGowner (BigInteger privkey){

        try {
            // sign ducoment

            ECDSA app = new ECDSA();  // eliptic curve opject
            // public key will be stored in firebase

            app.setdA(privkey);
            signature = app.signingMessage(thedigest);

            //  ECDSATextview.setText(signature);
            Request request = new Request(null, session.userEmail, session.docKey, session.userkey,"1", "done", signature);
            AddRequest(request);


        } catch (java.lang.Exception e1) {  //
            Log.e("sign and verify", e1.getStackTrace().toString());
        }


    }


    public void signdocument(boolean isowner,String DID,String RID) {

if(isowner){

    Startsigningowner();
}
        else {

        documentID=DID;
        requestID=RID;
        Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/documents/"+documentID+"/");
        Query queryref = ref.orderByValue();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                thedigest=  dataSnapshot.child("messagedigest").getValue().toString();
                Startsigning();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        queryref.addValueEventListener(listener);}

    }


        public void Startsigning(){ // get public key from FIREBASE


        //    thedigest;

            Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/users/"+session.userkey+"/");

            Query queryref = ref.orderByValue();
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    BigInteger privatekey;

                    privatekey=new BigInteger(dataSnapshot.child("PK").getValue(String.class));

                    ECDSASIGNING(privatekey);

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            };

            queryref.addValueEventListener(listener);


        }


        private void ECDSASIGNING (BigInteger privkey){

            try {
                // sign ducoment

                ECDSA app = new ECDSA();  // eliptic curve opject
                // public key will be stored in firebase

                app.setdA(privkey);
                signature = app.signingMessage(thedigest);
                //  ECDSATextview.setText(signature);
                storeSignature(signature, thedigest);


            } catch (java.lang.Exception e1) {  //
                Log.e("ECDSA java.lang", e1.getStackTrace().toString());
            }


        }




        private void storeSignature(String signature,String msg){

            digsignature digsignature = new digsignature(null,session.userkey,session.docKey,signature);
            digsignatureArryAdapter adapter = new digsignatureArryAdapter(context);
            adapter.addItem(digsignature);
           Firebase mFirebase = new Firebase ("https://torrid-heat-4458.firebaseio.com/requests");
            mFirebase.child(session.requestID).child("status").setValue("done");

            getseq();


        }

        private void getseq() {
            Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/requests/" + session.requestID + "");

            Query queryref = ref.child("signingSeq");
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //   ECDSATextview.setText(dataSnapshot.getValue().toString());
                    changeSingingoder(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            };

            queryref.addValueEventListener(listener);

        }
        private void changeSingingoder(final String seq){

            Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/requests");
            Query queryref= ref.orderByChild("rDocumentId").equalTo(documentID); // get reauest by request
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot child: dataSnapshot.getChildren()){

                        if(!(session.requestID==child.getKey()))
                        { // ECDSATextview.setText(ECDSATextview.getText()+"[   ]"+child.getKey());
                            checkIfItsTheNextSiner(child.getKey(),seq); }


                    }


                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            };
            queryref.addValueEventListener(listener);

        }

        private void checkIfItsTheNextSiner(String RID , final String seq){

            final Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/requests/"+RID+"");
            Query queryref = ref.child("signingSeq").orderByValue();
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    int afterseq = Integer.parseInt( dataSnapshot.getValue().toString());
                    int beforeseq = Integer.parseInt(seq);
                    if(afterseq-1==beforeseq){
                        ref.child("status").setValue("waiting");
                    }


                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            };
            queryref.addValueEventListener(listener);


        }


        public void verfiyclick(){

            requestID=session.requestID;
            setdocumentID(requestID); /// here start verifying



        }

        private void setdocumentID(final String RID){

            Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/requests/"+RID+"/rDocumentId/");
            Query queryref = ref.orderByValue();
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    documentID= dataSnapshot.getValue().toString(); // after setting documentID we dont have to pass it but we will use it later on
                    // ECDSATextview.setText(ECDSATextview.getText()+documentID);
                    retrieveEmailfromRequest(RID); // continue querying

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            };

            queryref.addValueEventListener(listener);

        }

        private void retrieveEmailfromRequest(String RID){ // here connection getting signerID and documentID from the request tree by RID

            Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/requests/"+RID+"/SignerEmail/");
            Query queryref = ref.orderByValue();
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    getUIDbyEmail(dataSnapshot.getValue().toString());

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            };


            queryref.addValueEventListener(listener);

        }
        private void getUIDbyEmail(String email){ // retrive user key from users by email
            Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/users");
            Query queryRef = ref.orderByChild("Email").equalTo(email);

            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            retrievepubKey(child.getKey());

                        }
                    }
                    else {
                        Log.e("sign and verify  ", "user not found");

                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            };
            queryRef.addValueEventListener(listener);

        }

        private void retrievepubKey(final String userID){

            Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/users/"+userID+"/");

            Query queryref = ref.orderByValue();
            // 0 key of person who signed the document
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    BigInteger x,y,a,p;
                    String inf;
                    boolean infinity;

                    x=new BigInteger(dataSnapshot.child("x").getValue(String.class));
                    y=new BigInteger(dataSnapshot.child("y").getValue(String.class));
                    a=new BigInteger(dataSnapshot.child("a").getValue(String.class));
                    p=new BigInteger(dataSnapshot.child("p").getValue(String.class));
                    inf=dataSnapshot.child("infinity").getValue(String.class);
                    if(inf=="TRUE")
                        infinity=true;
                    else
                        infinity=false;

                    Point pubkey = new Point(x,y,a,p);
                    pubkey.setInfinity(infinity);

                    //   ECDSATextview.setText(ECDSATextview.getText()+"[[[[]]]]]]"+pubkey.getX().toString());
                    retrieveMSG(pubkey);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            };

            queryref.addValueEventListener(listener);




        }

        private void retrieveMSG(final Point pubkey){

            Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/documents/"+documentID+"/");
            Query queryref = ref.orderByValue();
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String MSG=  dataSnapshot.child("messagedigest").getValue().toString();
                    // ECDSATextview.setText(MSG);
                    retrieveSign(pubkey, MSG);


                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            };

            queryref.addValueEventListener(listener);


        }


        private void retrieveSign(final Point pubkey , final String msg){

            Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/requests/"+requestID+"");
            Query queryref = ref.orderByValue();
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String signature=  dataSnapshot.child("signature").getValue(String.class);
                    verfiySignature(pubkey, signature, msg);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            };

            queryref.addValueEventListener(listener);





        }

        private void verfiySignature(Point pubkey,String sign,String msg){



            try {
                ECDSA app = new ECDSA();
                app.setQA(pubkey);
                boolean check = app.checkSignature(msg, sign);
                if (check == true) {

                    Intent alertintent = new Intent(context,alertDialog.class);
                    alertintent.putExtra("MSG","Verified");
                    context.startActivity(alertintent);

                }
                else
                {
                    Intent alertintent = new Intent(context,alertDialog.class);
                    alertintent.putExtra("MSG", "ERROR");
                    context.startActivity(alertintent);
                }

            }
            catch (java.lang.Exception e1){

                Log.e(" ECDSA verify", e1.getStackTrace().toString());

            }


        }

    private void AddRequest(Request request) {
        Firebase reqRef = new Firebase("https://torrid-heat-4458.firebaseio.com/requests");
        Map<String, String> newRequest = new HashMap<String, String>();
        newRequest.put("SignerEmail", request.getSignerEmail());
        newRequest.put("rDocumentId", "");
        newRequest.put("requesterID", request.getRequesterID());
        newRequest.put("signingSeq", request.getOrder());
        newRequest.put("status", request.getStatus());
        newRequest.put("signature", request.getSignature());
        reqRef.push().setValue(newRequest);

    }

    }


