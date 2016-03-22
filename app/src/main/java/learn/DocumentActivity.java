package learn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import learn.R;

public class DocumentActivity extends BaseActivity {

    View rootview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);
        rootview = findViewById(R.id.document_container);
        mNavigationView.getMenu().getItem(1).setChecked(true);
        ImageButton docowner = (ImageButton) findViewById(R.id.docuserimageButton);
        docowner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DocumentActivity.this, DocumentOwnerList.class));
            }
        });
        ImageButton docsigned = (ImageButton) findViewById(R.id.docsignimageButton);
        docsigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DocumentActivity.this, DocumentSignedList.class));
            }
        });
        ImageButton docpending = (ImageButton) findViewById(R.id.docwaitimageButton);
        docpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DocumentActivity.this, DocumentWaitingList.class));
            }
        });

    }

}
