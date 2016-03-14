package learn;


/**
 * Created by Naseebah on 28/01/16.
 */

        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.os.Environment;
        import android.util.Base64;
        import android.widget.ImageView;
        import android.widget.Toast;

        import com.firebase.client.DataSnapshot;
        import com.firebase.client.Firebase;
        import com.firebase.client.FirebaseError;
        import com.firebase.client.Query;
        import com.firebase.client.ValueEventListener;
        import com.itextpdf.text.DocumentException;
        import com.itextpdf.text.Image;
        import com.itextpdf.text.pdf.PdfContentByte;
        import com.itextpdf.text.pdf.PdfReader;
        import com.itextpdf.text.pdf.PdfStamper;

        import java.io.FileOutputStream;
        import java.io.IOException;
        import learn.R;

public class MyPdfViewerActivity extends Pdftry {//implements View.OnTouchListener {



    public int getPreviousPageImageResource() {

        return R.drawable.left_arrow;
    }

    public int getNextPageImageResource() {
        return R.drawable.right_arrow;
    }

    public int getZoomInImageResource() {
        return R.drawable.zoom_in;
    }

    public int getZoomOutImageResource() {
        return R.drawable.zoom_out;
    }
    public int getSelectSignatureImageResource(){
        return  R.drawable.select_signature;
    }
    public int getPdfPasswordLayoutResource() {
        return R.layout.pdf_file_password;
    }

    public int getPdfPageNumberResource() {
        return R.layout.dialog_pagenumber;
    }

    public int getPdfPasswordEditField() {
        return R.id.etPassword;
    }

    public int getPdfPasswordOkButton() {
        return R.id.btOK;
    }

    public int getPdfPasswordExitButton() {
        return R.id.btExit;
    }

    public int getPdfPageNumberEditField() {

        return R.id.pagenum_edit;}


    /////////////////////////////////////////////////////////////////////////
    public int getsignatureImageReasource(){return R.drawable.signature;}
    /////////////////////////////////////////////////////////////////////////


    // @Override
    // public int getNextPageImageResource() {
    // return 0;
    // }
    //
    // @Override
    // public int getPdfPageNumberEditField() {
    // return 0;
    // }
    //
    // @Override
    // public int getPdfPageNumberResource() {
    // return 0;
    // }
    //
    // @Override
    // public int getPdfPasswordEditField() {
    // return 0;
    // }
    //
    // @Override
    // public int getPdfPasswordExitButton() {
    // // TODO Auto-generated method stub
    // return 0;
    // }
    //
    // @Override
    // public int getPdfPasswordLayoutResource() {
    // // TODO Auto-generated method stub
    // return 0;
    // }
    //
    // @Override
    // public int getPdfPasswordOkButton() {
    // // TODO Auto-generated method stub
    // return 0;
    // }

}