package learn;

/**
 * Created by Naseebah on 12/04/16.
 */
public class MyImageViewerActivity extends Imagetry {


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
    public int getShareDocumentImageResource(){return R.drawable.share;}
}
