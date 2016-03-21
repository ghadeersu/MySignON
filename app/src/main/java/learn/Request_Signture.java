package learn;

import android.app.Dialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Request_Signture extends ListActivity {

    private RequestArrayAdapter mAdapter;
    boolean flag = true;
    static int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_request_signature);
        mAdapter = new RequestArrayAdapter(this);
        setListAdapter(mAdapter);
        counter=1;

    }

    public void AddSignerButtonOnClick(View view) {
        if (counter == 4)
        {
            flag = false;
            Button btnAdd = (Button) findViewById(R.id.add_signer_button);
            btnAdd.setEnabled(false);
            Toast.makeText(Request_Signture.this, "you are only allowed to request 3 signers at most for each document", Toast.LENGTH_SHORT).show();
            finish();

        }
        if (flag)
        {
            final Dialog dialog = new Dialog(this);
            dialog.setTitle("Add Signer");
            dialog.setContentView(R.layout.activity_request__signture);
            dialog.show();

            final EditText etEmail;
            etEmail = (EditText) dialog.findViewById(R.id.add_dialog_signer_email);

            Button btnRequest = (Button) dialog.findViewById(R.id.add_dialog_ok);

            btnRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email;
                    email = etEmail.getText().toString();
                    Request request;
                    if (counter > 1)
                    {
                        request = new Request(null, email, "", session.userkey, String.valueOf(counter), "waiting2", "");
                    }
                    else
                    {
                        request = new Request(null, email, "", session.userkey, String.valueOf(counter), "waiting", "");
                        session.requesterID = request.getRequesterID();
                    }
                    CheckEmail(request);
                    dialog.cancel();
                }
            });
        }

    }

    public void DoneButtonOnClick (View view)
    {
        finish();

    }


    private void CheckEmail(final Request request) {
        Firebase.setAndroidContext(getApplicationContext());
        Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/users");
        final Query queryRef = ref.orderByChild("Email").equalTo(request.getSignerEmail());
        final ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        AddRequest(request);
                        break;
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "email not found", Toast.LENGTH_LONG);
                    toast.show();

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        };
        queryRef.addListenerForSingleValueEvent(listener);
    }

    private void AddRequest(Request request) {
        Firebase reqRef = new Firebase("https://torrid-heat-4458.firebaseio.com/requests");
        Map<String, String> newRequest = new HashMap<String, String>();
        newRequest.put("SignerEmail", request.getSignerEmail());
        newRequest.put("rDocumentId", "");
        newRequest.put("requesterID", request.getRequesterID());
        newRequest.put("signingSeq", request.getOrder());
        newRequest.put("status", request.getStatus());
        newRequest.put("signature",request.getSignature());
        reqRef.push().setValue(newRequest);
        Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
        counter++;


    }
}