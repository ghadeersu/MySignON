package learn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Naseebah on 02/04/16.
 */
public class digsignatureArryAdapter extends BaseAdapter {


    private final LayoutInflater mInflater;
    public List<digsignature> mdigsignature;

    private Firebase mFireBase;
    private Firebase userFire;
    private Context currentContext;

    public digsignatureArryAdapter(Context context) {
        Firebase.setAndroidContext(context);
        mInflater = LayoutInflater.from(context);
        mdigsignature = new ArrayList<digsignature>();

        mFireBase=new Firebase("https://torrid-heat-4458.firebaseio.com/digsignature");
        currentContext=context;

    }

    @Override
    public int getCount() {
        return mdigsignature.size();
    }

    public void removeItem(digsignature sign) {
        //TODO: Remove data from Firebase
        mFireBase.child(sign.getKey()).removeValue();
    }

    public void addItem(digsignature sign) {
        //TODO: Push new data to Firebase
        Map<String, String> newDigSignature = new HashMap<String, String>();
        newDigSignature.put("signature", sign.getSignature());
        newDigSignature.put("docID", sign.getDocID());
        newDigSignature.put("signerID", sign.getSignerID());
        mFireBase.push().setValue(newDigSignature);

    }

    public void updateItem(digsignature sign) {
        //TODO: Push changes to Firebase
        Map<String, String> newDigSignature = new HashMap<String, String>();
        newDigSignature.put("signature", sign.getSignature());
        newDigSignature.put("docID", sign.getDocID());
        newDigSignature.put("signerID", sign.getSignerID());
        mFireBase.child(sign.getKey()).child("signature").setValue(sign.getSignature());
        mFireBase.child(sign.getKey()).child("docID").setValue(sign.getDocID());
        mFireBase.child(sign.getKey()).child("signerID").setValue(sign.getSignerID());
    }

    @Override
    public digsignature getItem(int position) {
        return mdigsignature.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return convertView;
    }
}
