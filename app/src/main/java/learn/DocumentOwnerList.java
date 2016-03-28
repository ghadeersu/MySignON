package learn;

/**
 * Created by Naseebah on 06/03/16.
 */

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;


/**
 * Created by Naseebah on 26/02/16.
 */
public class DocumentOwnerList extends ListActivity {
    private documentsArrayAdapter mAdapter;
    boolean checked = false;
    int i;
    boolean[] canRequest;
    boolean request;
    boolean canDelete=true;
    private Firebase mFirebase ;// = new Firebase("https://torrid-heat-4458.firebaseio.com/requests/"+session.userkey) ; //= new Firebase("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_owner_list);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //getListView().setChoiceMode (ListView.);
        //  getListView().setOnItemSelectedListener(new MyselectClickListener());
        mAdapter = new documentsArrayAdapter(this) {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previeousChildName) {

                final String Skey = dataSnapshot.getKey();
                final String documentName = dataSnapshot.child("documentName").getValue(String.class);
                final String documentOwnerID = dataSnapshot.child("documentOwnerID").getValue(String.class);
                final String documentURL = dataSnapshot.child("documentURL").getValue(String.class);
                final String ekey = dataSnapshot.child("ekey").getValue(String.class);
                final String messagedigest = dataSnapshot.child("messagedigest").getValue(String.class);

                Firebase userFire = new Firebase("https://torrid-heat-4458.firebaseio.com/users/" + session.userkey + "/");
                if (documentOwnerID.equals(session.userkey)) {

                    Query queryRef = userFire.orderByValue();
                    // userFire.orderByKey().equalTo(session.userkey).addListenerForSingleValueEvent(new ValueEventListener() {
                    ValueEventListener listener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String key = dataSnapshot.getKey();
                            String email = dataSnapshot.child("Email").getValue(String.class);
                            String a = dataSnapshot.child("a").getValue(String.class);
                            String birthdate = dataSnapshot.child("birthdate").getValue(String.class);
                            String infinity = dataSnapshot.child("infinity").getValue(String.class);
                            String password = dataSnapshot.child("password").getValue(String.class);
                            String username = dataSnapshot.child("username").getValue(String.class);
                            String x = dataSnapshot.child("x").getValue(String.class);
                            String y = dataSnapshot.child("y").getValue(String.class);
                            String pk = dataSnapshot.child("PK").getValue(String.class);
                            String p = dataSnapshot.child("PK").getValue(String.class);
                            User mUser = new User(key, email, birthdate, password, username);
                            // mUser.setA(a);
                            // mUser.setP();
                            // mUser.setPK();
                            // mUser.setX();
                            // mUser.setY();
                            documents doc = new documents(Skey, messagedigest, ekey, documentURL, documentOwnerID, documentName);
                            doc.setOwner(mUser);
                            mdocuments.add(0, doc);// add to the top

                            notifyDataSetChanged();// update adapter
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    };
                    queryRef.addValueEventListener(listener);
                }

                notifyDataSetChanged();// update adapter

                setListAdapter(mAdapter);

            }
        };
    }

    String DocURL , EncKey,DocName,DocOwner;
    String Operation;

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final documents currentdocuments;
        currentdocuments = (documents) getListAdapter().getItem(position);
        session.docKey = currentdocuments.getKey();
        DocURL = currentdocuments.getDocumentURL();
        EncKey = currentdocuments.getEkey();
        DocName=currentdocuments.getDocumentName();
        DocOwner=currentdocuments.getDocumentOwnerID();
        i=0;
        canRequest = new boolean[3];
        request=true;

          //***********************************FireBase RequesterID**************************
        /////////////////////////////////////////////////////////////////////////////////


//***************PRINT********************

//-------------------

        final Button viewB = (Button) findViewById(R.id.docOviewbutton);
        final Button signB = (Button) findViewById(R.id.docOsignbutton);
        final Button requestB = (Button) findViewById(R.id.docOrequestbutton);
        final Button deleteB = (Button) findViewById(R.id.docOdelete);

        viewB.setEnabled(true);
        viewB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Operation = "View";
                FTP_Download.iniate(DocName, EncKey, DocOwner, Operation);
                new FTP_Download(DocumentOwnerList.this).execute(DocURL);
                v.setEnabled(false);
                signB.setEnabled(false);
                requestB.setEnabled(false);
            }
        });


        signB.setEnabled(true);
        signB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Operation = "Sign";
                FTP_Download.iniate(DocName, EncKey, DocOwner, Operation);
                new FTP_Download(DocumentOwnerList.this).execute(DocURL);
                v.setEnabled(false);
                viewB.setEnabled(false);
                requestB.setEnabled(false);
            }
        });

        /////////////////////////////Delete Button/////////////////
        deleteB.setEnabled(true);
        deleteB.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {

                                           Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/requests");
                                           Query queryRef = ref.orderByChild("rDocumentId").equalTo(session.docKey);

                                           queryRef.addValueEventListener(new ValueEventListener() {
                                               @Override
                                               public void onDataChange(DataSnapshot dataSnapshot) {
                                                   //    Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/requests");
                                                   for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                       if (dataSnapshot.exists()) {

                                                           if (snapshot.child("rDocumentId").getValue().toString().equals(session.docKey)) {
                                                               Toast.makeText(DocumentOwnerList.this, "you cannot delete this file", Toast.LENGTH_SHORT).show();
                                                               canDelete = false;

                                                           }

                                                      /*     if (snapshot.child("status").getValue().toString().equals("done")) {
                                                               //  Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/requests");
                                                               //removeItem(ref);
                                                               //         System.out.println("PAAAAAAATH "+ session.requesterID);mFirebase = snapshot.getRef();
                                                               mFirebase = snapshot.getRef();
                                                               mFirebase.removeValue();
                                                               System.out.println("here in if condition !!! ");*/
                                                           // Toast.makeText(DocumentOwnerList.this, "You cannot delete this file!", Toast.LENGTH_SHORT).show();
                                                       }

                                                   }
                                                   if (canDelete) {
                                                       Toast.makeText(DocumentOwnerList.this, "delete", Toast.LENGTH_SHORT).show();
                                                       deleteDoc();
                                                       Firebase refDoc = new Firebase("https://torrid-heat-4458.firebaseio.com/documents");
                                                       refDoc.child(session.docKey).removeValue();
                                                   }


                                               }


                                               @Override
                                               public void onCancelled(FirebaseError firebaseError) {

                                               }
                                           });

                                               /*Query queryRefDoc = refDoc.orderByChild("rDocumentId").equalTo(session.docKey);

                                               queryRefDoc.addValueEventListener(new ValueEventListener() {
                                                                                     public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                         //    Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/requests");
                                                                                         for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                                                                                 if (dataSnapshot.exists()) {

                                                                                             }
                                                                                         }

                                                                                     }

                                                                                     @Override
                                                                                     public void onCancelled(FirebaseError firebaseError) {

                                                                                     }
                                                                                 }

                                               );*/

                                       }
                                   }

        );


//requestB
        requestB.setEnabled(true);
        requestB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //    Operation = "Request";


                ////// go to request Activity
                // search for (documentId + session.userkey) in requests => if snapshot.exist() => cannot request
                //                                                                              else => start request activity
                i = 0;
                request = true;
                canRequest = new boolean[3];
                final Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/requests");
                Query query = ref.orderByChild("requesterID").equalTo(session.userkey);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (i < 3) {
                                if (dataSnapshot.exists()) {
                                    if (snapshot.child("rDocumentId").getValue().toString().equals(session.docKey)) {
                                        if (!snapshot.child("status").getValue().toString().equals("done")) {
                                            Toast.makeText(DocumentOwnerList.this, "please wait untill requests are finished  ", Toast.LENGTH_SHORT).show();
                                            request = false;
                                            canRequest[i] = false;
                                            i++;
                                        }
                                    } else {
                                        canRequest[i] = true;
                                    }
                                } else {
                                    canRequest[0] = true;
                                    canRequest[1] = true;
                                    canRequest[2] = true;
                                    i = 3;
                                }
                                                            /*if (dataSnapshot.exists() && snapshot.child("rDocumentId").getValue().toString().equals(session.docKey) && !snapshot.child("status").getValue().toString().equals("done") && (snapshot.child("status").getValue().toString().equals("waiting") || snapshot.child("status").getValue().toString().equals("waiting2"))) {
                                                                Toast.makeText(DocumentOwnerList.this, "please wait untill requests are finished  ", Toast.LENGTH_SHORT).show();
                                                                canRequest[i] = false;
                                                                i++;
                                                            }*/
                            }
                        }
                        if (request) {
                            ref.removeEventListener(this);
                            startActivity(new Intent(DocumentOwnerList.this, Request_Signture.class));
                        }
                        //i = 0;
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

                v.setEnabled(false);
                signB.setEnabled(false);
                viewB.setEnabled(false);

            }
        });

        super.onListItemClick(l, v, position, id);
    }

private void deleteDoc() {
        ///////////////////delete button code////////////////////////

        Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/documents/");
        Query queryRef = ref.orderByKey().equalTo(session.docKey);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getKey().equals(session.docKey)) {
                            String fileName;
                            fileName = child.child("documentName").getValue(String.class);
                            System.out.println("name"+fileName);
                            new HDWFTP_Delete().execute(fileName);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        };
        queryRef.addValueEventListener(listener);
////////////////delete end///////////////////////////////////////

    }

}

