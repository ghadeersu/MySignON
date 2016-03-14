package learn;

/**
 * Created by Naseebah on 06/03/16.
 */
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.io.File;

import learn.Pdftry;
import learn.R;
import learn.Request_Signture;
import learn.User;
import learn.documents;
import learn.documentsArrayAdapter;
import learn.session;


/**
 * Created by Naseebah on 26/02/16.
 */
public class DocumentOwnerList extends ListActivity {
    private documentsArrayAdapter mAdapter;
    boolean checked = false;

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
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final documents currentdocuments;
        currentdocuments = (documents) getListAdapter().getItem(position);
        session.docKey = currentdocuments.getKey();



        Button viewB = (Button) findViewById(R.id.docOviewbutton);
        viewB.setEnabled(true);
        viewB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /// go to webview activity
                // File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/signon/DES.pdf");
                // File file = new File(currentdocuments.getDocumentURL());

                //////////////////////////// path from download
                File file = new File("ftp.byethost4.com/htdocs/w.pdf/");

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        Button signB = (Button) findViewById(R.id.docOsignbutton);
        signB.setEnabled(true);
        signB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// go to Mypdfviewer activity
                String newPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/signon/word.pdf";
                Intent i = new Intent(DocumentOwnerList.this, MyPdfViewerActivity.class);
                i.putExtra(Pdftry.EXTRA_PDFFILENAME, newPath);
                startActivity(i);

            }
        });

        Button requestB = (Button) findViewById(R.id.docOrequestbutton);
        requestB.setEnabled(true);
        requestB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ////// go to request Activity
                // search for (documentId + session.userkey) in requests => if snapshot.exist() => cannot request
                //                                                                              else => start request activity
                Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/requests");

                Query query = ref.orderByChild("requesterId").equalTo(session.userkey);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            if (snapshot.child("rDocumentId").equals("1")) {
                                Toast toast = Toast.makeText(DocumentOwnerList.this, "you have already request signers to sign this document", Toast.LENGTH_LONG);
                                toast.show();
                            } else {
                                startActivity(new Intent(DocumentOwnerList.this, Request_Signture.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
                ////// go to request Activity


            }
        });

        super.onListItemClick(l, v, position, id);
    }
}

