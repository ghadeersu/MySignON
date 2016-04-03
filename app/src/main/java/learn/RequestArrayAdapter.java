package learn;

/**
 * Created by Omaimah on 2/26/2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestArrayAdapter extends BaseAdapter implements ChildEventListener {


    private final LayoutInflater mInflater;
    private List<Request> mRequests;
    private Firebase mFirebase;

    public RequestArrayAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mRequests = new ArrayList<Request>();
        Firebase.setAndroidContext(context);
        mFirebase = new Firebase ("https://torrid-heat-4458.firebaseio.com/requests");
        Query q1 = mFirebase.orderByChild("rDocumentId").equalTo(session.docKey);
        q1.addChildEventListener(this);
    }

    @Override
    public int getCount() {
        return mRequests.size();
    }

    public void removeItem(Request request) {
        //TODO: Remove data from Firebase
        mFirebase.child(request.getKey()).removeValue();
    }

    public void addItem(Request request) {
        //TODO: Push new data to Firebase
        Map<String, String> rq = new HashMap<String, String>();
        rq.put("rDocumentId",session.docKey);
        rq.put("signingSeq",request.getOrder());
        rq.put("SignerEmail",request.getSignerEmail());
        rq.put("status","waiting");
        rq.put("requesterID",session.userkey);
        mFirebase.push().setValue(rq);
    }

    public void updateItem(Request request, String newEmail, String newOrder) {
        //TODO: Push changes to Firebase
        Map<String, String> rq = new HashMap<String, String>();
        rq.put("signingSeq", request.getOrder());
        rq.put("SignerEmail", request.getSignerEmail());
        mFirebase.child(request.getKey()).child("signingSeq").setValue(newOrder);
        mFirebase.child(request.getKey()).child("SignerEmail").setValue(newEmail);
    }

    @Override
    public Request getItem(int position) {
        return mRequests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(android.R.layout.simple_expandable_list_item_2, parent, false);
        } else {
            view = convertView;
        }
        TextView OrderTextView = (TextView) view.findViewById(android.R.id.text2);
        Request request = getItem(position);
        OrderTextView.setText("Order: "+request.getOrder() +"   Status: "+request.getStatus());
        TextView EmailTextView = (TextView) view.findViewById(android.R.id.text1);
        EmailTextView.setText(request.getSignerEmail());

        return view;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previeousChildName) {
        String key = dataSnapshot.getKey();
        String DocId = dataSnapshot.child("rDocumentId").getValue(String.class);
        String status = dataSnapshot.child("status").getValue(String.class);
        String requesterID = dataSnapshot.child("requesterID").getValue(String .class);
        String email = dataSnapshot.child("SignerEmail").getValue(String.class);
        String order = dataSnapshot.child("signingSeq").getValue(String.class);
        mRequests.add(0, new Request(key, email, DocId, requesterID,order, status));// add to the top
        notifyDataSetChanged();// update adapter
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        String key = dataSnapshot.getKey();
        String DocId = dataSnapshot.child("rDocumentId").getValue(String.class);
        String status = dataSnapshot.child("status").getValue(String.class);
        String requesterID = dataSnapshot.child("requesterID").getValue(String .class);
        String email = dataSnapshot.child("SignerEmail").getValue(String.class);
        String order = dataSnapshot.child("signingSeq").getValue(String.class);
        for ( Request rq : mRequests)
        {
            if (key.equals(rq.getKey()))
            {
                rq.setDocID(DocId);
                rq.setOrder(order);
                rq.setRequesterID(requesterID);
                rq.setSignerEmail(email);
                rq.setStatus(status);
                break;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        for ( Request rq : mRequests)
        {
            if (key.equals(rq.getKey()))
            {
                mRequests.remove(rq);
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