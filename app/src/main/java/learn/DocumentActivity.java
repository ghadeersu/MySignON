package learn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

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
                Intent list =new Intent(DocumentActivity.this, DocumentOwnerList.class);
                list.putExtra("choice", "owner");
                startActivity(list);
            }
        });
        ImageButton docsigned = (ImageButton) findViewById(R.id.docsignimageButton);
        docsigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent list =new Intent(DocumentActivity.this, DocumentOwnerList.class);

                list.putExtra("choice","signed");
                startActivity(list);
            }
        });
        ImageButton docpending = (ImageButton) findViewById(R.id.docwaitimageButton);
        docpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list =new Intent(DocumentActivity.this, DocumentOwnerList.class);

                list.putExtra("choice","pending");
                startActivity(list);
            }
        });
        ImageButton docCom = (ImageButton) findViewById(R.id.doccomimageButton);
        docCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list =new Intent(DocumentActivity.this, DocumentOwnerList.class);

                list.putExtra("choice","completed");
                startActivity(list);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DocumentActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();

    }
}
