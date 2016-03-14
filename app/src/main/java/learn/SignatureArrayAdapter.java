package learn.navdrawbase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by daniah on 2/22/2016.
 */
public class SignatureArrayAdapter extends BaseAdapter implements ChildEventListener {


    private final LayoutInflater mInflater;
    private List<signature> msignature;
    private Firebase mFireBase;

    public SignatureArrayAdapter(Context context) {
        Firebase.setAndroidContext(context);
        mInflater = LayoutInflater.from(context);
        msignature = new ArrayList<signature>();
        mFireBase=new Firebase("https://torrid-heat-4458.firebaseio.com/signature");
        mFireBase.addChildEventListener(this);
    }

    @Override
    public int getCount() {
        return msignature.size();
    }

    public void removeItem(signature signature) {
        //TODO: Remove data from Firebase
        mFireBase.child(signature.getKey()).removeValue();
    }

    public void addItem(signature signature) {
        //TODO: Push new data to Firebase
        Map<String, String> newSignature = new HashMap<String, String>();
        newSignature.put("signatureBase64", signature.getsignatureBase64());
        newSignature.put("signatureName", signature.getSignatureName());
        newSignature.put("signerID", signature.getSignerID());
        mFireBase.push().setValue(newSignature);

    }

    public void updateItem(signature signature, String newMovie, String newQuote) {
        //TODO: Push changes to Firebase
    }

    @Override
    public signature getItem(int position) {
        return msignature.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.signature_list, parent, false);
        } else {
            view = convertView;
        }
        ImageView signatureView = (ImageView) view.findViewById(R.id.icon);
        signature signature = getItem(position);
        ///convert

        byte[] temp= Base64.decode(signature.getsignatureBase64(), Base64.NO_WRAP);
        Bitmap img= BitmapFactory.decodeByteArray(temp, 0, temp.length);
        signatureView.setImageBitmap(img);
        TextView quoteTextView = (TextView) view.findViewById(R.id.Itemname);
        quoteTextView.setText(signature.getSignatureName());
        return view;
    }
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previeousChildName) {
        String key = dataSnapshot.getKey();
        String signatureName = dataSnapshot.child("signatureName").getValue(String.class);
        String signatureBase64 = dataSnapshot.child("signatureBase64").getValue(String.class);
        String signerID = dataSnapshot.child("signerID").getValue(String.class);
        if(signerID.equals(session.userkey)){
            msignature.add(0, new signature(key, signatureBase64, signatureName, signerID));}// add to the top
        notifyDataSetChanged();// update adapter
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        String key = dataSnapshot.getKey();
        String signatureName = dataSnapshot.child("signatureName").getValue(String.class);
        String signatureBase64 = dataSnapshot.child("signatureBase64").getValue(String.class);
        String signerID = dataSnapshot.child("signerID").getValue(String.class);
        for ( signature newsignature : msignature)
        {
            if (key.equals(newsignature.getKey()))
            {
                newsignature.setsignatureBase64(signatureBase64);
                newsignature.setsignatureName(signatureName);
                newsignature.setsignerID(signerID);
                break;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        for ( signature newsignature : msignature)
        {
            if (key.equals(newsignature.getKey()))
            {
                msignature.remove(newsignature);
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