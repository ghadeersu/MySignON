package learn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import learn.R;
import learn.User;
import learn.session;

/**
 * Created by Naseebah on 26/02/16.
 */
public class documentsArrayAdapter extends BaseAdapter implements ChildEventListener {


    private final LayoutInflater mInflater;
    public List<documents> mdocuments;
    private List<documents> waitdocuments;
    private Firebase mFireBase;
    private Firebase userFire;

    public documentsArrayAdapter(Context context) {
        Firebase.setAndroidContext(context);
        mInflater = LayoutInflater.from(context);
        mdocuments = new ArrayList<documents>();
        waitdocuments = new ArrayList<documents>();
        mFireBase=new Firebase("https://torrid-heat-4458.firebaseio.com/documents");
        mFireBase.addChildEventListener(this);

    }

    @Override
    public int getCount() {
        return mdocuments.size();
    }

    public void removeItem(documents doc) {
        //TODO: Remove data from Firebase
        mFireBase.child(doc.getKey()).removeValue();
    }

    public void addItem(documents doc) {
        //TODO: Push new data to Firebase
        Map<String, String> newDocument = new HashMap<String, String>();
        newDocument.put("documentName", doc.getDocumentName());
        newDocument.put("documentOwnerID", doc.getDocumentOwnerID());
        newDocument.put("documentURL", doc.getDocumentURL());
        newDocument.put("ekey", doc.getEkey());
        newDocument.put("messagedigest", doc.getMessagedigest());
        mFireBase.push().setValue(newDocument);

    }

    public void updateItem(documents doc) {
        //TODO: Push changes to Firebase
        Map<String, String> newDocument = new HashMap<String, String>();
        newDocument.put("documentName", doc.getDocumentName());
        //newDocument.put("documentOwnerID", doc.getDocumentOwnerID());
        //newDocument.put("documentURL", doc.getDocumentURL());
        newDocument.put("ekey", doc.getEkey());
        newDocument.put("messagedigest", doc.getMessagedigest());
        mFireBase.child(doc.getKey()).child("documentName").setValue(doc.getDocumentName());
       // mFireBase.child(doc.getKey()).child("documentOwnerID").setValue(doc.getDocumentOwnerID());
        //mFireBase.child(doc.getKey()).child("documentURL").setValue(doc.getDocumentURL());
        mFireBase.child(doc.getKey()).child("ekey").setValue(doc.getEkey());
        mFireBase.child(doc.getKey()).child("messagedigest").setValue(doc.getMessagedigest());
    }

    @Override
    public documents getItem(int position) {
        return mdocuments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.docexlist_child, parent, false);
        } else {
            view = convertView;
        }

        documents child_doc = getItem(position);
        TextView childtxt= (TextView)view.findViewById(R.id.docchildtxt);
        TextView childOwnertxt= (TextView)view.findViewById(R.id. docOwnertxt);
        childtxt.setText(child_doc.getDocumentName());
        childOwnertxt.setText(child_doc.getOwner().getUsername());
        Button viewB= (Button)view.findViewById(R.id.docexviewbutton);
        Button signB= (Button)view.findViewById(R.id.docexsignbutton);
        Button requestB= (Button)view.findViewById(R.id.docexrequestbutton);
        viewB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /// go to webview activity

            }
        });
        signB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// go to Mypdfviewer activity

            }
        });
        requestB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ////// go to request Activity

            }
        });
        return view;
    }
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previeousChildName) {
        final String Skey = dataSnapshot.getKey();
        final String documentName = dataSnapshot.child("documentName").getValue(String.class);
        final String documentOwnerID = dataSnapshot.child("documentOwnerID").getValue(String.class);
        final String documentURL = dataSnapshot.child("documentURL").getValue(String.class);
        final String ekey = dataSnapshot.child("ekey").getValue(String.class);
        final String messagedigest = dataSnapshot.child("messagedigest").getValue(String.class);

        userFire = new Firebase ("https://torrid-heat-4458.firebaseio.com/users/"+session.userkey+"/");
        if(documentOwnerID.equals(session.userkey))
        {

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

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        String Skey = dataSnapshot.getKey();
        String documentName = dataSnapshot.child("documentName").getValue(String.class);
        String documentOwnerID = dataSnapshot.child("documentOwnerID").getValue(String.class);
        String documentURL = dataSnapshot.child("documentURL").getValue(String.class);
        String ekey = dataSnapshot.child("ekey").getValue(String.class);
        String messagedigest = dataSnapshot.child("messagedigest").getValue(String.class);
        for (documents newDocument : mdocuments) {
            if (Skey.equals(newDocument.getKey())) {
                newDocument.setDocumentName(documentName);
                newDocument.setDocumentOwnerID(documentOwnerID);
                newDocument.setDocumentURL(documentURL);
                newDocument.setEkey(ekey);
                newDocument.setMessagedigest(messagedigest);
                break;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        for ( documents newdocuments : mdocuments)
        {
            if (key.equals(newdocuments.getKey()))
            {
                mdocuments.remove(newdocuments);
                break;
            }
        }
        notifyDataSetChanged();

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }


}
