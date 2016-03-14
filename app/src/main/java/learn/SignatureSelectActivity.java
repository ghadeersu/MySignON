package learn;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;


import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import learn.R;

public class SignatureSelectActivity extends ListActivity {

    private SignatureArrayAdapter mAdapter;
    private Button SignatureSelectAddButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature_select);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setMultiChoiceModeListener(new MyMultiClickListener());
        mAdapter = new SignatureArrayAdapter(this);
        setListAdapter(mAdapter);
        SignatureSelectAddButton=(Button) findViewById(R.id.signatureSelectAddButton);
    }

    private class MyMultiClickListener implements AbsListView.MultiChoiceModeListener {

        private ArrayList<signature> mSignatureToDelete = new ArrayList<signature>();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.context, menu);
            mode.setTitle(R.string.context_delete_title);
            return true; // gives tactile feedback
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.context_delete:
                    deleteSelectedItems();
                    mode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            signature item = (signature) getListAdapter().getItem(position);
            if (checked) {
                mSignatureToDelete.add(item);
            } else {
                mSignatureToDelete.remove(item);
            }
            mode.setTitle("Selected " + mSignatureToDelete.size() + " signatures");
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // purposefully empty
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mSignatureToDelete = new ArrayList<signature>();
            return true;
        }

        private void deleteSelectedItems() {
            for (signature signature : mSignatureToDelete) {
                mAdapter.removeItem(signature);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        final signature currentsignature = (signature) getListAdapter().getItem(position);
        session.base64=currentsignature.getKey();
        super.onListItemClick(l, v, position, id);
    }
    //////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                // add
                addItem();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addItem() {
/*
        DialogFragment df = new DialogFragment() {
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.content_main, container);
                getDialog().setTitle("Add a movie and quote");
                final Button confirmButton = (Button) view.findViewById(R.id.add);
                final Button cancelButton = (Button) view.findViewById(R.id.birth_txt);
                final EditText movieTitleEditText = (EditText) view.findViewById(R.id.selectSignatureName);
                final EditText movieQuoteEditText = (EditText) view.findViewById(R.id.selectSignatureID);

                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String movieTitleText = movieTitleEditText.getText().toString();
                        String movieQuoteText = movieQuoteEditText.getText().toString();
                        Toast.makeText(SignatureSelectActivity.this,
                                "Got the title " + movieTitleText + " and quote " + movieQuoteText, Toast.LENGTH_LONG)
                                .show();
                        signature currentQuote = new signature(null,"6666", movieTitleText, movieQuoteText);
                        mAdapter.addItem(currentQuote);
                        dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                return view;
            }
        };
        df.show(getFragmentManager(), "");*/
    }
    public void signatureSelectAddButtonClick(View v){
        startActivity(new Intent(SignatureSelectActivity.this, CaptureSignatureActivity.class));

    }


}

