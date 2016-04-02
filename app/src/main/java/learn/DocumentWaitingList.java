package learn;
/**
 * Created by Naseebah on 04/03/16.
 */

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Naseebah on 26/02/16.
 */
public class DocumentWaitingList extends ListActivity {
    private documentsArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_waiting_list);

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mAdapter = new documentsArrayAdapter(this) {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previeousChildName) {

                final String Skey = dataSnapshot.getKey();
                final String documentName = dataSnapshot.child("documentName").getValue(String.class);
                final String documentOwnerID = dataSnapshot.child("documentOwnerID").getValue(String.class);
                final String documentURL = dataSnapshot.child("documentURL").getValue(String.class);
                final String ekey = dataSnapshot.child("ekey").getValue(String.class);
                final String messagedigest = dataSnapshot.child("messagedigest").getValue(String.class);
                final Firebase requestFire = new Firebase("https://torrid-heat-4458.firebaseio.com/requests/");
                Query qDocID = requestFire.orderByChild("rDocumentId").equalTo(dataSnapshot.getKey());
                ValueEventListener listener0 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot DocID) {
                        if (DocID.exists()) {
                            System.out.println(" 2 find Same Doc ID ");
                            for (final DataSnapshot child : DocID.getChildren()) {
                                System.out.println(" 2 find Same Doc ID child ");
                                System.out.println(" 2 find Same Doc ID child" + child.child("SignerEmail").getValue() + "  " + child.child("status").getValue() +"  "+child.child("SignerEmail").getKey()+"");
                                if (child.child("SignerEmail").getValue().equals(session.userEmail))
                                    if (child.child("status").getValue().equals("waiting")) {
                                        System.out.println(" 2 signer Email + status waiting ");

                                       session.requestID = child.getKey();
// not sure if this is the correct place to set requestID // ghadeer
                                        /////////////////////////////////////////

                                       // Pdftry.RequestInfo(RequestID);
                                        /*
                                            static String RID;
                                            public static void RequestInfo (String RKey) {RID = RKey; }
                                        */
                                        /////////////////////////////////////////

                                        Firebase userFire = new Firebase("https://torrid-heat-4458.firebaseio.com/users/" + child.child("requesterID").getValue() + "/");
                                        Query qUser = userFire.orderByValue();
                                        ValueEventListener listener3 = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {

                                                    System.out.println(" 2 find User requester ");

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

                                                    System.out.println("find user " + username + "");
                                                    // mUser.setA(a);
                                                    // mUser.setP();
                                                    // mUser.setPK();
                                                    // mUser.setX();
                                                    // mUser.setY();
                                                    System.out.println("" + documentOwnerID + "  " + documentName + "");
                                                    documents doc = new documents(Skey, messagedigest, ekey, documentURL, documentOwnerID, documentName);
                                                    doc.setOwner(mUser);
                                                    mdocuments.add(0, doc);// add to the top
                                                    notifyDataSetChanged();// update adapter
                                                }
                                            }

                                            @Override
                                            public void onCancelled(FirebaseError firebaseError) {
                                            }
                                        };
                                        qUser.addValueEventListener(listener3);
                                        //    notifyDataSetChanged();// update adapter
                                    }

                            }


                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                };
                qDocID.addValueEventListener(listener0);
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

        final Button viewB = (Button) findViewById(R.id.docWlistviewButton);
        final Button signB= (Button)findViewById(R.id.docWlistsignButton);
        viewB.setEnabled(true);
        viewB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Operation = "View";
                FTP_Download.iniate(DocName, EncKey, DocOwner, Operation);
                new FTP_Download(DocumentWaitingList.this).execute(DocURL);
                v.setEnabled(false);
                signB.setEnabled(false);

            }
        });

        signB.setEnabled(true);
        signB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Operation = "Sign";
                FTP_Download.iniate(DocName, EncKey, DocOwner, Operation);
                new FTP_Download(DocumentWaitingList.this).execute(DocURL);
                v.setEnabled(false);
                viewB.setEnabled(false);


            }
        });
        super.onListItemClick(l, v, position, id);
    }
}
