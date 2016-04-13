package learn;
//hi
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import com.itextpdf.text.pdf.PdfWriter;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.decrypt.PDFAuthenticationFailureException;
import com.sun.pdfview.decrypt.PDFPassword;
import com.sun.pdfview.font.PDFFont;

import net.sf.andpdf.nio.ByteBuffer;
import net.sf.andpdf.pdfviewer.gui.FullScrollView;
import net.sf.andpdf.refs.HardReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

//////////////////////////////////////Editing to PDF Viewer library.java/////////////////////////////

/**
 * U:\Android\android-sdk-windows-1.5_r1\tools\adb push u:\Android\simple_T.pdf /data/test.pdf
 * @author ferenc.hechler
 */
public abstract class Imagetry extends Activity {
    boolean ownerFlag;
    String Type;
    private static final int STARTPAGE = 1;
    private static final float STARTZOOM = 1.0f;//2.0f for full page on HD
    public static boolean allOrNot;
    ////////////////////////////////////////////////////Just for more time to solve problems /////////////////////////////
    private static final float MIN_ZOOM = 0.80f; //0.80f;
    private static final float MAX_ZOOM = 3.0f;//3.0f;
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final float ZOOM_INCREMENT = 1.5f;

    private static final String TAG = "PDFVIEWER";

    public static final String EXTRA_PDFFILENAME = "net.sf.andpdf.extra.PDFFILENAME";
    public static final String EXTRA_SHOWIMAGES = "net.sf.andpdf.extra.SHOWIMAGES";
    public static final String EXTRA_ANTIALIAS = "net.sf.andpdf.extra.ANTIALIAS";
    public static final String EXTRA_USEFONTSUBSTITUTION = "net.sf.andpdf.extra.USEFONTSUBSTITUTION";
    public static final String EXTRA_KEEPCACHES = "net.sf.andpdf.extra.KEEPCACHES";

    public static final boolean DEFAULTSHOWIMAGES = true;
    public static final boolean DEFAULTANTIALIAS = true;
    public static final boolean DEFAULTUSEFONTSUBSTITUTION = false;
    public static final boolean DEFAULTKEEPCACHES = false;

    private final static int MENU_NEXT_PAGE = 1;
    private final static int MENU_PREV_PAGE = 2;
    private final static int MENU_GOTO_PAGE = 3;
    private final static int MENU_ZOOM_IN   = 4;
    private final static int MENU_ZOOM_OUT  = 5;
    private final static int MENU_BACK      = 6;
    private final static int MENU_CLEANUP   = 7;
    private final static int MENU_SIGNATURE =8;
    private final static int MENU_Share =9;
    private final static int DIALOG_PAGENUM = 1;

    public int counter=0;

    public GraphView mOldGraphView;
    public GraphView mGraphView;
    private String pdffilename;

    private int mPage;
    private float mZoom;
    private File mTmpFile;
    private ProgressDialog progress;

    public String signPath;
    public String newP ;
    public byte[] signatureByte;


    private boolean touchcheck =false;

    private Thread backgroundThread;
    private Handler uiHandler;
    public String messagedigest;
    private  String RequestID;
    @Override
    public Object onRetainNonConfigurationInstance() {
        // return a reference to the current instance
        Log.e(TAG, "onRetainNonConfigurationInstance");
        return this;
    }

    private boolean restoreInstance() {
        mOldGraphView = null;
        Log.e(TAG, "restoreInstance");
        if (getLastNonConfigurationInstance()==null)
            return false;
        Imagetry inst =(Imagetry)getLastNonConfigurationInstance();
        if (inst != this) {
            Log.e(TAG, "restoring Instance");
            mOldGraphView = inst.mGraphView;
            mPage = inst.mPage;
            mTmpFile = inst.mTmpFile;
            mZoom = inst.mZoom;
            pdffilename = inst.pdffilename;
            backgroundThread = inst.backgroundThread;
            // mGraphView.invalidate();
        }
        return true;
    }

    /** Called when the activity is first created. */
//new sign button
    LinearLayout Rlayout;
    Button sign;
    Button signAll;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);
        Log.i(TAG, "onCreate");
        Bundle extras = getIntent().getExtras();
        String value;
        if (extras != null) {
            value = extras.getString(Imagetry.EXTRA_PDFFILENAME,"FullPath");
            signPath= value;
            Type=signPath.substring(signPath.lastIndexOf(".") + 1, signPath.length());

            newP= Environment.getExternalStorageDirectory().getAbsolutePath() + "/signon/temp364535."+Type;

        }

        Rlayout = (LinearLayout)findViewById(R.id.Rlayout);
        Firebase.setAndroidContext(this);
        uiHandler = new Handler();
        restoreInstance();
        if (mOldGraphView != null) {
            mGraphView = new GraphView(this);
            mGraphView.mBi = mOldGraphView.mBi;
            mOldGraphView = null;
            mGraphView.mImageView.setImageBitmap(mGraphView.mBi);
            mGraphView.updateTexts();

            Rlayout.addView(mGraphView);
        }
        else {
            mGraphView = new GraphView(this);
            Intent intent = getIntent();
            Log.i(TAG, ""+intent);

            boolean showImages = getIntent().getBooleanExtra(Imagetry.EXTRA_SHOWIMAGES, Imagetry.DEFAULTSHOWIMAGES);
            PDFImage.sShowImages = showImages;
            boolean antiAlias = getIntent().getBooleanExtra(Imagetry.EXTRA_ANTIALIAS, Imagetry.DEFAULTANTIALIAS);
            PDFPaint.s_doAntiAlias = antiAlias;
            boolean useFontSubstitution = getIntent().getBooleanExtra(Imagetry.EXTRA_USEFONTSUBSTITUTION, Imagetry.DEFAULTUSEFONTSUBSTITUTION);
            PDFFont.sUseFontSubstitution= useFontSubstitution;
            boolean keepCaches = getIntent().getBooleanExtra(Imagetry.EXTRA_KEEPCACHES, Imagetry.DEFAULTKEEPCACHES);
            HardReference.sKeepCaches= keepCaches;
            if (intent != null) {
                if ("android.intent.action.VIEW".equals(intent.getAction())) {
                    pdffilename = storeUriContentToFile(intent.getData());
                }
                else {
                    pdffilename = getIntent().getStringExtra(Imagetry.EXTRA_PDFFILENAME);
                }
            }

            if (pdffilename == null)
                pdffilename = "no file selected";

            mPage = STARTPAGE;
            mZoom = STARTZOOM;

            setContent(null);
            /////////////////////////////////////////////////////////////////////////////////////////////////////
            //signature ImageButton
            Button signatureIB=(Button)findViewById(R.id.signimage);

            signatureIB.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    touchcheck = !touchcheck;
                }
            });

            // sign button
            sign=(Button)findViewById(R.id.pdfVsignbutton);
            signAll=(Button)findViewById(R.id.pdfVsignallbutton);
            sign.setEnabled(true);
            signAll.setEnabled(false);
            signAll.setVisibility(Button.GONE);
            // sign.setEnabled(false);
            sign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mGraphView.signature.getVisibility()==ImageView.VISIBLE)
                    {
                        mGraphView.setsignbutton();
                        sign.setEnabled(false);
                        signAll.setEnabled(false);
                    }
                    else
                    {
                        Toast toast = Toast.makeText(getApplicationContext(), "Put the signature on the page to sign", Toast.LENGTH_LONG);
                        toast.show();
                    }


                }
            });

            //select signature button
            ImageButton bSelect=(ImageButton)findViewById(R.id.pdfVselectsignimageButton);
            bSelect.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //signature select function
                    selectSignature();
                }
            });

            //share button
            ImageButton share=(ImageButton)findViewById(R.id.pdfVshareimageButton);
            share.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //signature select function
                    shareDocument();
                }
            });


        }
    }

    private void setContent(String password) {
        try {
            parsePDF(pdffilename, password);
            // setContentView(mGraphView);
            Rlayout.addView(mGraphView);
            startRenderThread(mPage, mZoom);
        }
        catch (PDFAuthenticationFailureException e) {
            setContentView(getPdfPasswordLayoutResource());
            final EditText etPW= (EditText) findViewById(getPdfPasswordEditField());
            Button btOK= (Button) findViewById(getPdfPasswordOkButton());
            Button btExit = (Button) findViewById(getPdfPasswordExitButton());
            btOK.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String pw = etPW.getText().toString();
                    setContent(pw);
                }
            });
            btExit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
    private synchronized void startRenderThread(final int page, final float zoom) {
        if (backgroundThread != null)
            return;

        mGraphView.showText("reading page " + page + ", zoom:" + zoom);
        //progress = ProgressDialog.show(PdfViewerActivity.this, "Loading", "Loading PDF Page");
        backgroundThread = new Thread(new Runnable() {
            public void run() {
                try {
                    if (true){
                        showPage(page, zoom);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                backgroundThread = null;
            }
        });
        updateImageStatus();
        backgroundThread.start();
    }


    private void updateImageStatus() {
        if (backgroundThread == null) {
            mGraphView.updateUi();
            return;
        }
        mGraphView.updateUi();
        mGraphView.postDelayed(new Runnable() {
            public void run() {
                updateImageStatus();
            }
        }, 1000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MENU_PREV_PAGE, Menu.NONE, "Previous Page").setIcon(getPreviousPageImageResource());
        menu.add(Menu.NONE, MENU_NEXT_PAGE, Menu.NONE, "Next Page").setIcon(getNextPageImageResource());
        menu.add(Menu.NONE, MENU_GOTO_PAGE, Menu.NONE, "Goto Page");
        menu.add(Menu.NONE, MENU_ZOOM_OUT, Menu.NONE, "Zoom Out").setIcon(getZoomOutImageResource());
        menu.add(Menu.NONE, MENU_ZOOM_IN, Menu.NONE, "Zoom In").setIcon(getZoomInImageResource());
        menu.add(Menu.NONE, MENU_SIGNATURE,Menu.NONE,"Select signature").setIcon(getSelectSignatureImageResource());
        menu.add(Menu.NONE, MENU_Share,Menu.NONE,"Share Document").setIcon(getShareDocumentImageResource());
        if (HardReference.sKeepCaches)
            menu.add(Menu.NONE, MENU_CLEANUP, Menu.NONE, "Clear Caches");

        return true;
    }

    /**
     * Called when a menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case MENU_ZOOM_IN: {
                zoomIn();
                break;
            }
            case MENU_ZOOM_OUT: {
                zoomOut();
                break;
            }
            case MENU_BACK: {
                finish();
                break;
            }
            case MENU_CLEANUP: {
                HardReference.cleanup();
                break;
            }
            case MENU_SIGNATURE:{

                selectSignature();
                break;
            }
            case MENU_Share:{
                shareDocument();
                break;
            }
        }
        return true;
    }


    private void zoomIn() {
            if (mZoom < MAX_ZOOM) {
                mZoom *= ZOOM_INCREMENT;
                if (mZoom > MAX_ZOOM)
                    mZoom = MAX_ZOOM;

                if (mZoom >= MAX_ZOOM) {
                    Log.d(TAG, "Disabling zoom in button");
                    mGraphView.bZoomIn.setEnabled(false);
                }
                else
                    mGraphView.bZoomIn.setEnabled(true);

                mGraphView.bZoomOut.setEnabled(true);
                startRenderThread(mPage, mZoom);
            }
    }

    private void zoomOut() {
        if (true){//mPdfFile != null) {
            if (mZoom > MIN_ZOOM) {
                mZoom /= ZOOM_INCREMENT;
                if (mZoom < MIN_ZOOM)
                    mZoom = MIN_ZOOM;

                if (mZoom <= MIN_ZOOM) {
                    Log.d(TAG, "Disabling zoom out button");
                    mGraphView.bZoomOut.setEnabled(false);
                }
                else
                    mGraphView.bZoomOut.setEnabled(true);

                mGraphView.bZoomIn.setEnabled(true);

                //progress = ProgressDialog.show(PdfViewerActivity.this, "Rendering", "Rendering PDF Page");
                startRenderThread(mPage, mZoom);
            }
        }
    }


    private void selectSignature() {
        if (true){//mPdfFile != null) {


            Intent pickContactIntent = new Intent(this,SignatureSelectActivity.class);
            startActivity(pickContactIntent);

            //mGraphView.signature=getsignatureImageReasource();

            //Toast.makeText(this, getsignatureImageReasource().toString(), Toast.LENGTH_LONG);

        }
    }

     private void  shareDocument(){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        Uri uri=Uri.parse("file://"+signPath);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        try{
            startActivity(Intent.createChooser(intent,"Share File"));
        }catch(Exception e){Toast.makeText(Imagetry.this,"Error sending the file",Toast.LENGTH_LONG);}





    }

    public class GraphView extends FullScrollView implements View.OnTouchListener {
        //private String mText;
        //private long fileMillis;
        //private long pageParseMillis;
        //private long pageRenderMillis;
        private Bitmap mBi;
        //private String mLine1;
        //private String mLine2;
        //private String mLine3;
        private ImageView mImageView;
        //private TextView mLine1View;
        //private TextView mLine2View;
        //private TextView mLine3View;
        private Button mBtPage;
        private Button mBtPage2;
        ImageButton bZoomOut;
        ImageButton bZoomIn;
        ImageButton bSelect;
        ImageButton share;
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        //private Fragment sign_bar;
        private Button send;
        private Button sign;
        private Button signAll;
        //float relativeX;
        //float relativeY;
        ImageButton signatureBottun;
        ImageView signature;
        TextView Coord;
        TextView Coordx;
        ///////////////////////////////////////////////////////////////////////////////////////////////////

        public GraphView(Context context){
            super(context);


            //setContentView(R.layout.graphics_view);
            // layout params
            LinearLayout.LayoutParams lpWrap1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,1);
            LinearLayout.LayoutParams lpWrap10 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,10);

            // vertical layout
            LinearLayout vl=new LinearLayout(context);
            vl.setLayoutParams(lpWrap10);
            vl.setOrientation(LinearLayout.VERTICAL);

            if (mOldGraphView == null)
                progress = ProgressDialog.show(Imagetry.this, "Loading", "Loading PDF Page", true, true);

            addNavButtons(vl);
            // remember page button for updates
            mBtPage2 = mBtPage;

            mImageView = new ImageView(context);
            setPageBitmap(null);
            updateImage();
            mImageView.setLayoutParams(lpWrap1);
            mImageView.setScaleType(ImageView.ScaleType.MATRIX);
            mImageView.setPadding(5, 5, 5, 5);
            mImageView.setOnTouchListener(this);
            vl.addView(mImageView);

            setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 100));
            setBackgroundColor(Color.LTGRAY);
            setHorizontalScrollBarEnabled(true);
            setHorizontalFadingEdgeEnabled(true);
            setVerticalScrollBarEnabled(true);
            setVerticalFadingEdgeEnabled(true);

       LinearLayout.LayoutParams imsize = new LinearLayout.LayoutParams(140,100,1);

            // sign Image
            signature=new ImageView(context);
            signature.setLayoutParams(imsize);
            //bNext.setText(">");
            //bNext.setWidth(40);
            changeImageView();
            //signature.setImageResource(getsignatureImageReasource());
            signature.setScaleType(ImageView.ScaleType.FIT_CENTER);
            signature.setVisibility(ImageView.GONE);
            vl.addView(signature);
            /////////////////////////////////////////////////////////////////////////////////////////
            addSpace(vl, 20, 20);
            addView(vl);


        }

        float x = 0.0f;
        float y = 0.0f;
        boolean moving=false;
        /*
        private Matrix matrix = new Matrix();
        private Matrix savedMatrix = new Matrix();*/
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(touchcheck)
            {
                mImageView.getParent().requestDisallowInterceptTouchEvent(true);
                 switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //		flaoat startX = (int) event.getX();
                        //		startY = (int) event.getY();
                        signature.setVisibility(ImageView.VISIBLE);
                        moving = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(moving)
                        {
                            x = event.getRawX()-(mPage/(mImageView.getHeight()*mZoom));//- mImageView.getWidth()/2;
                            y = event.getRawY()-(mPage/(mImageView.getWidth()*mZoom));//- mImageView.getHeight()*3/2;
                            signature.setX(x);
                            signature.setY(y);
                            // Coord.setText(Float.toString(y));
                            // Coordx.setText(Float.toString(x));
                        }
                        break;
                    case MotionEvent.ACTION_UP:
						/*relativeX = (event.getX() - values[2]) / values[0];
						relativeY = (event.getY() - values[5]) / values[4];
						signature.setX(relativeX);
						signature.setY(relativeY);*/
                        moving = false;
                        break;
                }
            }else {mImageView.getParent().requestDisallowInterceptTouchEvent(false);}
            return true;
        }

        public void setsignbutton(){
            final File test = new File(signPath);

            ////////////////check hash//////////////////////////////

            Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/documents/");
            Query queryRef = ref.orderByKey().equalTo(session.docKey);

            System.out.println(session.docKey);
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (counter == 0) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                if (child.getKey().equals(session.docKey)) {
                                    messagedigest = child.child("messagedigest").getValue(String.class);
                                    System.out.println(messagedigest + "      " + "message Di");
                                    System.out.println("now     " + SHA512.calculateSHA512(test));
                                    if (session.docKey != null) {
                                        //        System.out.println(session.docKey);
                                        System.out.println("yes");

                                    } else {
                                        System.out.println("no ");

                                    }
                                    if (SHA512.checkSHA512(messagedigest, test)) {
                                        Matrix matrix = signature.getImageMatrix();
                                        // Get the values of the matrix
                                        float[] values = new float[9];
                                        matrix.getValues(values);
                                        // values[2] and values[5] are the x,y coordinates of the top left corner of the drawable image, regardless of the zoom factor.
                                        // values[0] and values[4] are the zoom factors for the image's width and height respectively. If you zoom at the same factor, these should both be the same value.
                                        values[0] = mZoom;
                                        values[4] = mZoom;
                                        // event is the touch event for MotionEvent.ACTION_UP
                                        float relativeX = (signature.getX() - values[2]) / values[0];
                                        float relativeY = (signature.getY() - values[5]) / values[4];
                                        //////////////////////////option for signing//////////////////////
                                            merge(relativeX, relativeY, 1);
                                        File f2 = new File(newP);

                                        //PdfChecksum = SHA512.calculateSHA512(f2);
                                        //documentToUpdate=new documents(session.docKey,PdfChecksum,child.child("ekey").getValue(String.class),"ftp.byethost4.com/htdocs/"+session.userkey+"/"+f2.getName()+"/",child.child("documentOwnerID").getValue(String.class),child.child("documentName").getValue(String.class));
                                        //documentAdapter=new documentsArrayAdapter(Imagetry.this);
                                        //fileTosign=new File(newP);
                                        //documentAdapter.updateItem(documentToUpdate);
                                        System.out.println("path " + f2.getPath());
                                        File ff = new File(signPath);

                                       f2.renameTo(test);
                                        new HDWFTP_Upload_Update(Imagetry.this).execute(signPath);


                                    } else {
                                        AlertDialog alert = new AlertDialog.Builder(Imagetry.this).setMessage("You Altered the file").setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // do nothing
                                            }
                                        }).show();
                                    }
                                }
                                break;


                            }
                        }
                        counter++;
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }

            };
            queryRef.addValueEventListener(listener);


            //Toast.makeText(Imagetry.this,messagedigest,Toast.LENGTH_LONG);
            //Toast.makeText(Imagetry.this,SHA512.calculateSHA512(f), Toast.LENGTH_LONG);


            /////////////////////firebase////////////////////////////
            // DigitalS();
        }

        private void addNavButtons(ViewGroup vg) {

            addSpace(vg, 6, 6);

            LinearLayout.LayoutParams lpChild1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,1);
            LinearLayout.LayoutParams lpWrap10 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,10);

            Context context = vg.getContext();
            LinearLayout hl=new LinearLayout(context);
            hl.setLayoutParams(lpWrap10);
            hl.setOrientation(LinearLayout.HORIZONTAL);



            // zoom out button
            bZoomOut=new ImageButton(context);
            bZoomOut.setBackgroundDrawable(null);
            bZoomOut.setLayoutParams(lpChild1);
            //bZoomOut.setText("-");
            //bZoomOut.setWidth(40);
            bZoomOut.setImageResource(getZoomOutImageResource());
            bZoomOut.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    zoomOut();
                }
            });
            hl.addView(bZoomOut);

            // zoom in button
            bZoomIn=new ImageButton(context);
            bZoomIn.setBackgroundDrawable(null);
            bZoomIn.setLayoutParams(lpChild1);
            //bZoomIn.setText("+");
            //bZoomIn.setWidth(40);
            bZoomIn.setImageResource(getZoomInImageResource());
            bZoomIn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    zoomIn();
                }
            });
            hl.addView(bZoomIn);

            addSpace(hl, 6, 6);

            // prev button
            ImageButton bPrev=new ImageButton(context);
            bPrev.setBackgroundDrawable(null);
            bPrev.setLayoutParams(lpChild1);
            //bPrev.setText("<");
            //bPrev.setWidth(40);
            bPrev.setImageResource(getPreviousPageImageResource());
            bPrev.setEnabled(false);
            hl.addView(bPrev);

            // page button
            mBtPage=new Button(context);
            mBtPage.setLayoutParams(lpChild1);

            mBtPage.setText("1" + "/ 1");
            mBtPage.setEnabled(false);
            hl.addView(mBtPage);

            // next button
            ImageButton bNext=new ImageButton(context);
            bNext.setBackgroundDrawable(null);
            bNext.setLayoutParams(lpChild1);
            //bNext.setText(">");
            //bNext.setWidth(40);
            bNext.setImageResource(getNextPageImageResource());
            bNext.setEnabled(false);
            hl.addView(bNext);

            // addSpace(hl, 20, 20);

            // exit button

            Button bExit=new Button(context);
            bExit.setLayoutParams(lpChild1);
            bExit.setText("X");
            bExit.setWidth(4);
            bExit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            hl.addView(bExit);

            vg.addView(hl);

            addSpace(vg, 6, 6);
        }




        private void addSpace(ViewGroup vg, int width, int height) {
            TextView tvSpacer=new TextView(vg.getContext());
            tvSpacer.setLayoutParams(new LinearLayout.LayoutParams(width,height,1));
            tvSpacer.setText("");
//			tvSpacer.setWidth(width);
//			tvSpacer.setHeight(height);
            vg.addView(tvSpacer);

        }

        private void showText(String text) {
            Log.i(TAG, "ST='"+text+"'");
            //mText = text;
            updateUi();
        }

        private void updateUi() {
            uiHandler.post(new Runnable() {
                public void run() {
                    updateTexts();
                }
            });
        }

        private void updateImage() {
            uiHandler.post(new Runnable() {
                public void run() {
                    mImageView.setImageBitmap(mBi);
                }
            });
        }

        private void setPageBitmap(Bitmap bi) {
            if (bi != null)
                mBi = bi;
            else {

            }
        }

        protected void updateTexts() {


        }

    }



    private void showPage(int page, float zoom) throws Exception {

        try {
            // free memory from previous page
            mGraphView.setPageBitmap(null);
            mGraphView.updateImage();

        Bitmap f= BitmapFactory.decodeFile(signPath);
            int w = f.getWidth();
            int h = f.getHeight();
            //String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/signon/download/sign.jpg";
            Bitmap f2 = Bitmap.createScaledBitmap(f, (int)(w* zoom), (int)(h* zoom), false);
         //  Bitmap f2= Bitmap.createBitmap(f,w,h,(int)(w* zoom), (int)(h* zoom));

         //  Bitmap f2= f.createBitmap((int)(w* zoom), (int)(h* zoom), f.getConfig());
        //    f.createBitmap((int)(f.getHeight() * zoom),(int)(f.getWidth()*zoom), Bitmap.Config.RGB_565);

        //   f.setHeight((int)(h*zoom));
          //  f.setWidth((int)(w*zoom));

         // Bitmap bi = mPdfPage.getImage((int)(width*zoom), (int)(height*zoom), clip, true, true);
            mGraphView.setPageBitmap(f2);
            mGraphView.updateImage();
            //getImageReasource();
            if (progress != null)
                progress.dismiss();
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage(), e);
            mGraphView.showText("Exception: " + e.getMessage());
        }
    }

    private void parsePDF(String filename, String password) throws PDFAuthenticationFailureException {
        //long startTime = System.currentTimeMillis();
        try {
            File f = new File(filename);
            long len = f.length();
            if (len == 0) {
                mGraphView.showText("file '" + filename + "' not found");
            }
            else {
                mGraphView.showText("file '" + filename + "' has " + len + " bytes");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            mGraphView.showText("Exception: " + e.getMessage());
        }
     }


    /**
     * <p>Open a specific pdf file.  Creates a DocumentInfo from the file,
     * and opens that.</p>
     *
     * <p><b>Note:</b> Mapping the file locks the file until the PDFFile
     * is closed.</p>
     *
     * @throws IOException
     */

    private String storeUriContentToFile(Uri uri) {
        String result = null;
        try {
            if (mTmpFile == null) {
                File root = Environment.getExternalStorageDirectory();
                if (root == null)
                    throw new Exception("external storage dir not found");
                mTmpFile = new File(root,"AndroidPdfViewer/AndroidPdfViewer_temp.pdf");
                mTmpFile.getParentFile().mkdirs();
                mTmpFile.delete();
            }
            else {
                mTmpFile.delete();
            }
            InputStream is = getContentResolver().openInputStream(uri);
            OutputStream os = new FileOutputStream(mTmpFile);
            byte[] buf = new byte[1024];
            int cnt = is.read(buf);
            while (cnt > 0) {
                os.write(buf, 0, cnt);
                cnt = is.read(buf);
            }
            os.close();
            is.close();
            result = mTmpFile.getCanonicalPath();
            mTmpFile.deleteOnExit();
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return result;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTmpFile != null) {
            mTmpFile.delete();
            mTmpFile = null;
        }
    }

    public abstract int getPreviousPageImageResource(); // R.drawable.left_arrow
    public abstract int getNextPageImageResource(); // R.drawable.right_arrow
    public abstract int getZoomInImageResource(); // R.drawable.zoom_int
    public abstract int getZoomOutImageResource(); // R.drawable.zoom_out
    public abstract int getSelectSignatureImageResource();//R.drawable.select_signature
    public abstract int getShareDocumentImageResource();//R.drawable.share
    public abstract int getPdfPasswordLayoutResource(); // R.layout.pdf_file_password
    public abstract int getPdfPageNumberResource(); // R.layout.dialog_pagenumber

    public abstract int getPdfPasswordEditField(); // R.id.etPassword
    public abstract int getPdfPasswordOkButton(); // R.id.btOK
    public abstract int getPdfPasswordExitButton(); // R.id.btExit
    public abstract int getPdfPageNumberEditField(); // R.id.pagenum_edit

    ////////////////////////////////////////////////////////////////////////////////////////
    public abstract int getsignatureImageReasource();
    public void merge(float x,float y, int pageNum) {
try {
    System.out.println("step 1"+ signPath);
    Bitmap imageToSign = BitmapFactory.decodeFile(signPath);
    //stamp image code
    int w = imageToSign.getWidth();
    int h = imageToSign.getHeight();
    //String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/signon/download/sign.jpg";
    Bitmap result = Bitmap.createBitmap(w, h, imageToSign.getConfig());
    System.out.println("step 2" + result.getByteCount());
    Canvas canvas = new Canvas(result);

    canvas.drawBitmap(imageToSign, 0, 0, null);
    //Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    Bitmap waterMark = BitmapFactory.decodeByteArray(signatureByte, 0, signatureByte.length);
    canvas.drawBitmap(waterMark, x, y, null);
    FileOutputStream fos = new FileOutputStream(newP);
    result.compress(Bitmap.CompressFormat.JPEG, 90, fos);
    fos.close();
}
catch(Exception o){System.out.println("Image error"+o.getMessage());}
    }
    public void displayAlertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(Imagetry.this);
        alert.setTitle("Signing");
        alert.setMessage("Do you want to sign on all pages?");
        alert.setCancelable(false);
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                allOrNot=false;

            }
        });

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                allOrNot=true;

            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();

    }

    public void changeImageView(){

        Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/signature");
        Query queryRef = ref.orderByChild("signerID").equalTo(session.userkey);;

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                        if(child.getKey().equals(session.base64)){

                            signatureByte= Base64.decode(child.child("signatureBase64").getValue(String.class), Base64.NO_WRAP);
                            Bitmap img= BitmapFactory.decodeByteArray(signatureByte, 0, signatureByte.length);
                            mGraphView.signature.setImageBitmap(img);

                        }
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        queryRef.addValueEventListener(listener);
    }
    @Override
    protected void onResume(){
        changeImageView();

        super.onResume();
    }

    public void getChecksum(){
    }
    public void changeChecksum(){

        Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/documents/");
        Query queryRef = ref.orderByKey().equalTo(session.docKey);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    //child.child("messagedigest")=chec
                    break;


                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        queryRef.addValueEventListener(listener);


    }
    private void DigitalS(){

        // check if ownerID is digital
        Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/documents");
        Query queryRef = ref.orderByChild("documentOwnerID").equalTo(session.userkey);
        final ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ownerFlag= false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (dataSnapshot.exists())  {
                        if(session.docKey== snapshot.getKey()) {
                            ownerFlag = true;
                        }
                    }}


                System.out.println("pdf try Digitals");
                DigitalSignatureSignAndVerfiy app = new DigitalSignatureSignAndVerfiy();
                app.signdocument(ownerFlag,session.docKey,RequestID);


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        };
        queryRef.addListenerForSingleValueEvent(listener);}
    // omaimah

    private void AddRequest(Request request) {
        Firebase reqRef = new Firebase("https://torrid-heat-4458.firebaseio.com/requests");
        Map<String, String> newRequest = new HashMap<String, String>();
        newRequest.put("SignerEmail", request.getSignerEmail());
        newRequest.put("rDocumentId", "");
        newRequest.put("requesterID", request.getRequesterID());
        newRequest.put("signingSeq", request.getOrder());
        newRequest.put("status", request.getStatus());
        reqRef.push().setValue(newRequest);

    }
}

