package learn.navdrawbase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DocumentActivity extends BaseActivity {

    View rootview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);
        rootview = findViewById(R.id.document_container);
        mNavigationView.getMenu().getItem(1).setChecked(true);
    }

}
