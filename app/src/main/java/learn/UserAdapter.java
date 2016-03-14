package learn.navdrawbase;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
ghadeer
  ECDSA app = new ECDSA();
        app.setdA(new BigInteger("34234234"));
        Point pubkey = app.getQA();
  mFirebase.child(userID).child("x").setValue(pubkey.getX().toString());
        mFirebase.child(userID).child("y").setValue(pubkey.getY().toString());
        mFirebase.child(userID).child("a").setValue(pubkey.getA().toString());
        mFirebase.child(userID).child("p").setValue(pubkey.getP().toString());
        if(pubkey.isInfinity())
        mFirebase.child(userID).child("infinity").setValue("TRUE");
        else
            mFirebase.child(userID).child("infinity").setValue("FALSE");
 */

public class UserAdapter extends BaseAdapter implements ChildEventListener {


   private List<User> mUsers;
    private Firebase mFirebase;

    public UserAdapter(Context context) {
        mUsers = new ArrayList<User>();
        Firebase.setAndroidContext(context);
        mFirebase = new Firebase ("https://torrid-heat-4458.firebaseio.com/users");
        mFirebase.addChildEventListener(this);
    }



    @Override
    public int getCount() {
        return mUsers.size();
    }

    public void removeItem(User user) {
        //TODO: Remove data from Firebase
        mFirebase.child(user.getKey()).removeValue();
    }

    public void addItem(final User user) {
        //TODO: Push new data to Firebase
        Map<String, String> newUser = new HashMap<String, String>();
        newUser.put("Email", user.getEmail());
        newUser.put("a", user.getA().toString());
        newUser.put("birthdate", user.getBirthdate());
        if (user.isInfinity()) {
            newUser.put("infinity","TRUE");
        }
        else {
            newUser.put("infinity", "FALSE");
        }
        newUser.put("password", user.getPassword());
        newUser.put("username", user.getUsername());
        newUser.put("x", user.getX().toString());
        newUser.put("y", user.getY().toString());
        newUser.put("p", user.getP().toString());
        newUser.put("PK", String.valueOf(user.getPK()));

        mFirebase.push().setValue(newUser);

    }

    public void updateItem(User user) {
        //TODO: Push changes to Firebase
        Map<String, String> newUser = new HashMap<String, String>();
        newUser.put("Email",user.getEmail());
        newUser.put("birthdate", user.getBirthdate());
        newUser.put("password", user.getPassword());
        newUser.put("username", user.getUsername());
        mFirebase.child(user.getKey()).child("Email").setValue(user.getEmail());
        mFirebase.child(user.getKey()).child("birthdate").setValue(user.getBirthdate());
        mFirebase.child(user.getKey()).child("password").setValue(user.getPassword());
        mFirebase.child(user.getKey()).child("username").setValue(user.getUsername());
    }

    @Override
    public User getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }


    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previeousChildName) {
        String key = dataSnapshot.getKey();
        String email = dataSnapshot.child("Email").getValue(String.class);
        //String a = dataSnapshot.child("a").getValue(String.class);
        String birthdate = dataSnapshot.child("birthdate").getValue(String.class);
        //String infinity = dataSnapshot.child("infinity").getValue(String.class);
        String password = dataSnapshot.child("password").getValue(String.class);
        String username = dataSnapshot.child("username").getValue(String.class);
        //String x = dataSnapshot.child("x").getValue(String.class);
        //String y = dataSnapshot.child("y").getValue(String.class);
        mUsers.add(0, new User(key,email,birthdate,password,username));// add to the top
        notifyDataSetChanged();// update adapter
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        String key = dataSnapshot.getKey();
        String email = dataSnapshot.child("Email").getValue(String.class);
        String birthdate = dataSnapshot.child("birthdate").getValue(String.class);
        String password = dataSnapshot.child("password").getValue(String.class);
        String username = dataSnapshot.child("username").getValue(String.class);
        for ( User newUser : mUsers)
        {
            if (key.equals(newUser.getKey()))
            {
                newUser.setEmail(email);
                newUser.setBirthdate(birthdate);
                newUser.setPassword(password);
                newUser.setUsername(username);
                break;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        for ( User newUser : mUsers)
        {
            if (key.equals(newUser.getKey()))
            {
                mUsers.remove(newUser);
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