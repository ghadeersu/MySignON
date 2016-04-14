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
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.sun.pdfview.PDFFile;
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
public abstract class Pdftry extends Activity {
    boolean ownerFlag;
    public static int pageSign;
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

    private PdfReader pdfReader;
    private String PdfChecksum;
    private documents mDocument;
    documentsArrayAdapter documentAdapter;
    documents documentToUpdate;
    public GraphView mOldGraphView;
    public GraphView mGraphView;
    private String pdffilename;
    private PDFFile mPdfFile;
    private int mPage;
    private float mZoom;
    private File mTmpFile;
    private ProgressDialog progress;
    public byte[] signatureByte;
    public String newP = Environment.getExternalStorageDirectory().getAbsolutePath() + "/signon/l.pdf";
    public String signPath;



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*private View navigationPanel;
    private Handler closeNavigationHandler;
    private Thread closeNavigationThread;*/

    private boolean touchcheck =false;
    private PDFPage mPdfPage;

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
        Pdftry inst =(Pdftry)getLastNonConfigurationInstance();
        if (inst != this) {
            Log.e(TAG, "restoring Instance");
            mOldGraphView = inst.mGraphView;
            mPage = inst.mPage;
            mPdfFile = inst.mPdfFile;
            mPdfPage = inst.mPdfPage;
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
        //   Intent intentE = getIntent();
        //   String FullPath = extras.getString("FullPath");
        String value;
        if (extras != null) {
            value = extras.getString(Pdftry.EXTRA_PDFFILENAME,"FullPath");
            signPath= value;

        }

        Rlayout = (LinearLayout)findViewById(R.id.Rlayout);
        Firebase.setAndroidContext(this);
        //setContentView(R.layout.activity_view_document);
        //progress = ProgressDialog.show(PdfViewerActivity.this, "Loading", "Loading PDF Page");
        /*closeNavigationHandler = new Handler();
        closeNavigationThread = new Thread(new Runnable() {

        	public void run() {
        		navigationPanel.startAnimation(AnimationUtils.loadAnimation(PdfViewerActivity.this,
        				R.anim.slide_out));
        		navigationPanel.setVisibility(View.GONE);
        	}
        });*/

        /*if (navigationPanel == null) {
        	navigationPanel = ((ViewStub) findViewById(R.id.stub_navigation)).inflate();
        	navigationPanel.setVisibility(View.GONE);
        	ImageButton previous = (ImageButton)navigationPanel.findViewById(R.id.navigation_previous);
        	previous.setBackgroundDrawable(null);
        }*/

        uiHandler = new Handler();
        restoreInstance();
        if (mOldGraphView != null) {
            mGraphView = new GraphView(this);
            //mGraphView.fileMillis = mOldGraphView.fileMillis;
            mGraphView.mBi = mOldGraphView.mBi;
            //mGraphView.mLine1 = mOldGraphView.mLine1;
            //mGraphView.mLine2 = mOldGraphView.mLine2;
            //mGraphView.mLine3 = mOldGraphView.mLine3;
            //mGraphView.mText = mOldGraphView.mText;
            //mGraphView.pageParseMillis= mOldGraphView.pageParseMillis;
            //mGraphView.pageRenderMillis= mOldGraphView.pageRenderMillis;
            mOldGraphView = null;
            mGraphView.mImageView.setImageBitmap(mGraphView.mBi);
            mGraphView.updateTexts();

            Rlayout.addView(mGraphView);
        }
        else {
            mGraphView = new GraphView(this);
            Intent intent = getIntent();
            Log.i(TAG, ""+intent);

            boolean showImages = getIntent().getBooleanExtra(Pdftry.EXTRA_SHOWIMAGES, Pdftry.DEFAULTSHOWIMAGES);
            PDFImage.sShowImages = showImages;
            boolean antiAlias = getIntent().getBooleanExtra(Pdftry.EXTRA_ANTIALIAS, Pdftry.DEFAULTANTIALIAS);
            PDFPaint.s_doAntiAlias = antiAlias;
            boolean useFontSubstitution = getIntent().getBooleanExtra(Pdftry.EXTRA_USEFONTSUBSTITUTION, Pdftry.DEFAULTUSEFONTSUBSTITUTION);
            PDFFont.sUseFontSubstitution= useFontSubstitution;
            boolean keepCaches = getIntent().getBooleanExtra(Pdftry.EXTRA_KEEPCACHES, Pdftry.DEFAULTKEEPCACHES);
            HardReference.sKeepCaches= keepCaches;
            if (intent != null) {
                if ("android.intent.action.VIEW".equals(intent.getAction())) {
                    pdffilename = storeUriContentToFile(intent.getData());
                }
                else {
                    pdffilename = getIntent().getStringExtra(Pdftry.EXTRA_PDFFILENAME);
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
            signAll.setEnabled(true);
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
            signAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mGraphView.signature.getVisibility()==ImageView.VISIBLE)
                    {
                        mGraphView.setsignAllbutton();
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
                    if (mPdfFile != null) {
                        //progress = ProgressDialog.show(PdfViewerActivity.this, "Loading", "Loading PDF Page");

//			        	File f = new File("/sdcard/andpdf.trace");
//			        	f.delete();
//			        	Log.e(TAG, "DEBUG.START");
//			        	Debug.startMethodTracing("andpdf");
                        showPage(page, zoom);
//			        	Debug.stopMethodTracing();
//			        	Log.e(TAG, "DEBUG.STOP");

				        /*if (progress != null)
				        	progress.dismiss();*/
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
//		Log.i(TAG, "updateImageStatus: " +  (System.currentTimeMillis()&0xffff));
        if (backgroundThread == null) {
            mGraphView.updateUi();

			/*if (progress != null)
				progress.dismiss();*/
            return;
        }
        mGraphView.updateUi();
        mGraphView.postDelayed(new Runnable() {
            public void run() {
                updateImageStatus();

				/*if (progress != null)
					progress.dismiss();*/
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
            case MENU_NEXT_PAGE: {
                nextPage();
                break;
            }
            case MENU_PREV_PAGE: {
                prevPage();
                break;
            }
            case MENU_GOTO_PAGE: {
                gotoPage();
                break;
            }
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
        if (mPdfFile != null) {
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

                //progress = ProgressDialog.show(PdfViewerActivity.this, "Rendering", "Rendering PDF Page");

                startRenderThread(mPage, mZoom);
            }
        }
    }

    private void zoomOut() {
        if (mPdfFile != null) {
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
        if (mPdfFile != null) {


            Intent pickContactIntent = new Intent(this,SignatureSelectActivity.class);
            startActivity(pickContactIntent);

            //mGraphView.signature=getsignatureImageReasource();

            //Toast.makeText(this, getsignatureImageReasource().toString(), Toast.LENGTH_LONG);

        }
    }

    private void nextPage() {
        if (mPdfFile != null) {
            if (mPage < mPdfFile.getNumPages()) {
                mPage += 1;
                mGraphView.bZoomOut.setEnabled(true);
                mGraphView.bZoomIn.setEnabled(true);
                progress = ProgressDialog.show(Pdftry.this, "Loading", "Loading PDF Page " + mPage, true, true);
                startRenderThread(mPage, mZoom);
            }
        }
    }

    private void prevPage() {
        if (mPdfFile != null) {
            if (mPage > 1) {
                mPage -= 1;
                mGraphView.bZoomOut.setEnabled(true);
                mGraphView.bZoomIn.setEnabled(true);
                progress = ProgressDialog.show(Pdftry.this, "Loading", "Loading PDF Page " + mPage, true, true);
                startRenderThread(mPage, mZoom);
            }
        }
    }
    private void  shareDocument(){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        Uri uri=Uri.parse("file://"+signPath);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        try{
            startActivity(Intent.createChooser(intent,"Share File"));
        }catch(Exception e){Toast.makeText(Pdftry.this,"Error sending the file",Toast.LENGTH_LONG);}





    }
    private void gotoPage() {
        if (mPdfFile != null) {
            showDialog(DIALOG_PAGENUM);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_PAGENUM:
                LayoutInflater factory = LayoutInflater.from(this);
                final View pagenumView = factory.inflate(getPdfPageNumberResource(), null);
                final EditText edPagenum = (EditText)pagenumView.findViewById(getPdfPageNumberEditField());
                edPagenum.setText(Integer.toString(mPage));
                edPagenum.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (event == null || ( event.getAction() == 1)) {
                            // Hide the keyboard
                            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edPagenum.getWindowToken(), 0);
                        }
                        return true;
                    }
                });
                return new AlertDialog.Builder(this)
                        //.setIcon(R.drawable.icon)
                        .setTitle("Jump to page")
                        .setView(pagenumView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String strPagenum = edPagenum.getText().toString();
                                int pageNum = mPage;
                                try {
                                    pageNum = Integer.parseInt(strPagenum);
                                }
                                catch (NumberFormatException ignore) {}
                                if ((pageNum!=mPage) && (pageNum>=1) && (pageNum <= mPdfFile.getNumPages())) {
                                    mPage = pageNum;
                                    mGraphView.bZoomOut.setEnabled(true);
                                    mGraphView.bZoomIn.setEnabled(true);
                                    progress = ProgressDialog.show(Pdftry.this, "Loading", "Loading PDF Page " + mPage, true, true);
                                    startRenderThread(mPage, mZoom);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .create();
        }
        return null;
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
                progress = ProgressDialog.show(Pdftry.this, "Loading", "Loading PDF Page", true, true);

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
		        /*mImageView = (ImageView) findViewById(R.id.pdf_image);
		        if (mImageView == null) {
		        	Log.i(TAG, "mImageView is null!!!!!!");
		        }
		        setPageBitmap(null);
		        updateImage();*/

		        /*
		        navigationPanel = new ViewStub(PdfViewerActivity.this, R.layout.navigation_overlay);
		        final ImageButton previous = (ImageButton)navigationPanel.findViewById(R.id.navigation_previous);
		        previous.setBackgroundDrawable(null);
		        previous.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						prevPage();
					}
				});

		        final ImageButton next = (ImageButton)navigationPanel.findViewById(R.id.navigation_next);
		        next.setBackgroundDrawable(null);
		        previous.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						nextPage();
					}
				});

		        //stub.setLayoutParams(Layou)
		        vl.addView(navigationPanel);

		        vl.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (navigationPanel.getVisibility() != View.VISIBLE) {
							navigationPanel.startAnimation(AnimationUtils.loadAnimation(PdfViewerActivity.this,
									R.anim.slide_in));
							navigationPanel.setVisibility(View.VISIBLE);
						}

						return false;
					}
				});
				*/


            setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 100));
            setBackgroundColor(Color.LTGRAY);
            setHorizontalScrollBarEnabled(true);
            setHorizontalFadingEdgeEnabled(true);
            setVerticalScrollBarEnabled(true);
            setVerticalFadingEdgeEnabled(true);


            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // sign button
		/*	sign=new Button(context);
			sign.setLayoutParams(lpWrap1);
			sign.setText("SIGN");
			sign.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					merge(signature.getX(), signature.getY());
				}
			});
			vl.addView(sign);*/

            // coordinate
         /*   Coord=new TextView(context);
            Coord.setLayoutParams(lpWrap1);
            Coord.setText("SIGN");
            vl.addView(Coord);
            Coordx=new TextView(context);
            Coordx.setLayoutParams(lpWrap1);
            Coordx.setText("x");
            vl.addView(Coordx);*/

            // send button
		/*	send=new Button(context);
			send.setLayoutParams(lpWrap1);
			send.setText("SEND");
			send.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

				}
			});
			vl.addView(send);*/
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


        ////////////////////////////////////////////////////Delete All Comments after solving the problems
	/*	Bitmap bitmapMaster;
		Canvas canvasMaster;
				@Override
				public boolean onTouch(View v, MotionEvent event) {

					int action = event.getAction();
					int x = (int) event.getX();
					int y = (int) event.getY();
					switch(action){
						case MotionEvent.ACTION_DOWN:
									getProjectedColor((ImageView) v, bitmapMaster, x, y);
							break;
						case MotionEvent.ACTION_MOVE:
									getProjectedColor((ImageView) v, bitmapMaster, x, y);
							break;
						case MotionEvent.ACTION_UP:
									getProjectedColor((ImageView) v, bitmapMaster, x, y);
							break;
					}
    /*
     * Return 'true' to indicate that the event have been consumed.
     * If auto-generated 'false', your code can detect ACTION_DOWN only,
     * cannot detect ACTION_MOVE and ACTION_UP.
     */
					/*return true;
				}*/

		/*
          * Project position on ImageView to position on Bitmap
          * return the color on the position
          */
		/*private int getProjectedColor(ImageView iv, Bitmap bm, int x, int y){
			if(x<0 || y<0 || x > iv.getWidth() || y > iv.getHeight()){
				//outside ImageView
				return 1;
			}else{
				int projectedX = (int)((double)x * ((double)bm.getWidth()/(double)iv.getWidth()));
				int projectedY = (int)((double)y * ((double)bm.getHeight()/(double)iv.getHeight()));

				return bm.getPixel(projectedX, projectedY);
			}
		}*/

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
                // Get the values of the matrix

		/*	float[] values = new float[9];
			Drawable drawable = mImageView.getDrawable();
			matrix.getValues(values);*/

                //View v = (ImageView) mImageView;
                //mImageView.getMatrix();
                // values[2] and values[5] are the x,y coordinates of the top left corner of the drawable image, regardless of the zoom factor.
                // values[0] and values[4] are the zoom factors for the image's width and height respectively. If you zoom at the same factor, these should both be the same value.

                // event is the touch event for MotionEvent.ACTION_UP
                //if (mImageView.>)
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

/*			float x = 0.0f;
		float y = 0.0f;
		boolean moving=false;
		private Matrix matrix = new Matrix();
		private Matrix savedMatrix = new Matrix();
		private static final int ZOOM = 2;

			private static final int DRAG = 1;
		private static final int NONE=0;
		private int mode=NONE;
		// remember some things for zooming
		private PointF start = new PointF();
		private PointF mid = new PointF();
		private float oldDist = 1f;
		private float d = 0f;
		private float newRot = 0f;
		private float[] lastEvent = null;
	public boolean onTouch(View v, MotionEvent event) {
			// handle touch events here
		signature.getParent().requestDisallowInterceptTouchEvent(true);
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					savedMatrix.set(matrix);
					//start.set(event.getX(), event.getY());
					mode = DRAG;
					moving=true;
					lastEvent = null;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event);
					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(mid, event);
						mode = ZOOM;
					}
					lastEvent = new float[4];
					lastEvent[0] = event.getX(0);
					lastEvent[1] = event.getX(1);
					lastEvent[2] = event.getY(0);
					lastEvent[3] = event.getY(1);
					break;
				case MotionEvent.ACTION_UP: moving=false;
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					lastEvent = null;
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
						if(moving)
						{
							x = event.getRawX()-(mPage/(mImageView.getHeight()*mZoom));//- mImageView.getWidth()/2;
							y = event.getRawY()-(mPage/(mImageView.getWidth()*mZoom));//- mImageView.getHeight()*3/2;
							signature.setX(x);
							signature.setY(y);
							Coord.setText(Float.toString(y));
							Coordx.setText(Float.toString(x));
						}}else if (mode == ZOOM) {
						/*matrix.set(savedMatrix);
						float dx = event.getX() - start.x;
						float dy = event.getY() - start.y;
						matrix.postTranslate(dx, dy);*/
        //}

/*						float newDist = spacing(event);
						if (newDist > 10f) {
							matrix.set(savedMatrix);
							float scale = (newDist / oldDist);
							matrix.postScale(scale, scale, mid.x, mid.y);
						}
					}
					break;
			}
			mImageView.setImageMatrix(matrix);
			return true;
		}
		/**
		 * Determine the space between the first two fingers
		 */
/*		private float spacing(MotionEvent event) {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return (float)Math.sqrt(x * x + y * y);
		}
		/**
		 * Calculate the mid point of the first two fingers
		 */
/*		private void midPoint(PointF point, MotionEvent event) {
			float x = event.getX(0) + event.getX(1);
			float y = event.getY(0) + event.getY(1);
			point.set(x / 2, y / 2);
		}*/

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public void setsignAllbutton(){
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
                                        if (mPdfFile.getNumPages() > 1) {

                                            merge(relativeX, relativeY, -1);
                                        } else
                                            merge(relativeX, relativeY, 1);
                                        File f2 = new File(newP);

                                        //PdfChecksum = SHA512.calculateSHA512(f2);
                                        //documentToUpdate=new documents(session.docKey,PdfChecksum,child.child("ekey").getValue(String.class),"ftp.byethost4.com/htdocs/"+session.userkey+"/"+f2.getName()+"/",child.child("documentOwnerID").getValue(String.class),child.child("documentName").getValue(String.class));
                                        //documentAdapter=new documentsArrayAdapter(Pdftry.this);
                                        //fileTosign=new File(newP);
                                        //documentAdapter.updateItem(documentToUpdate);
                                        System.out.println("path " + f2.getPath());
                                        File ff = new File(signPath);

                                        f2.renameTo(test);
                                        new HDWFTP_Upload_Update(Pdftry.this).execute(signPath);


                                    } else {
                                        AlertDialog alert = new AlertDialog.Builder(Pdftry.this).setMessage("You Altered the file").setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
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
                                        if (mPdfFile.getNumPages() > 1) {

                                                merge(relativeX, relativeY, mPage);
                                        } else
                                            merge(relativeX, relativeY, 1);
                                        File f2 = new File(newP);

                                        //PdfChecksum = SHA512.calculateSHA512(f2);
                                        //documentToUpdate=new documents(session.docKey,PdfChecksum,child.child("ekey").getValue(String.class),"ftp.byethost4.com/htdocs/"+session.userkey+"/"+f2.getName()+"/",child.child("documentOwnerID").getValue(String.class),child.child("documentName").getValue(String.class));
                                        //documentAdapter=new documentsArrayAdapter(Pdftry.this);
                                        //fileTosign=new File(newP);
                                        //documentAdapter.updateItem(documentToUpdate);
                                        System.out.println("path " + f2.getPath());
                                        File ff = new File(signPath);

                                                f2.renameTo(test);
                                                new HDWFTP_Upload_Update(Pdftry.this).execute(signPath);


                                    } else {
                                        AlertDialog alert = new AlertDialog.Builder(Pdftry.this).setMessage("You Altered the file").setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
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


            //Toast.makeText(Pdftry.this,messagedigest,Toast.LENGTH_LONG);
            //Toast.makeText(Pdftry.this,SHA512.calculateSHA512(f), Toast.LENGTH_LONG);


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
            bPrev.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    prevPage();
                }
            });
            hl.addView(bPrev);

            // page button
            mBtPage=new Button(context);
            mBtPage.setLayoutParams(lpChild1);
            String maxPage = ((mPdfFile==null)?"0":Integer.toString(mPdfFile.getNumPages()));
            mBtPage.setText(mPage+"/"+maxPage);
            mBtPage.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    gotoPage();
                }
            });
            hl.addView(mBtPage);

            // next button
            ImageButton bNext=new ImageButton(context);
            bNext.setBackgroundDrawable(null);
            bNext.setLayoutParams(lpChild1);
            //bNext.setText(">");
            //bNext.setWidth(40);
            bNext.setImageResource(getNextPageImageResource());
            bNext.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    nextPage();
                }
            });
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

		        	/*if (progress != null)
		        		progress.dismiss();*/
                }
            });
        }

        private void setPageBitmap(Bitmap bi) {
            if (bi != null)
                mBi = bi;
            else {
/*
				mBi = Bitmap.createBitmap(100, 100, Config.RGB_565);
	            Canvas can = new Canvas(mBi);
	            can.drawColor(Color.RED);

				Paint paint = new Paint();
	            paint.setColor(Color.BLUE);
	            can.drawCircle(50, 50, 50, paint);

	            paint.setStrokeWidth(0);
	            paint.setColor(Color.BLACK);
	            can.drawText("Bitmap", 10, 50, paint);*/

            }
        }

        protected void updateTexts() {
			/*
            mLine1 = "PdfViewer: "+mText;
            float fileTime = fileMillis*0.001f;
            float pageRenderTime = pageRenderMillis*0.001f;
            float pageParseTime = pageParseMillis*0.001f;
            mLine2 = "render page="+format(pageRenderTime,2)+", parse page="+format(pageParseTime,2)+", parse file="+format(fileTime,2);
    		int maxCmds = PDFPage.getParsedCommands();
    		int curCmd = PDFPage.getLastRenderedCommand()+1;
    		mLine3 = "PDF-Commands: "+curCmd+"/"+maxCmds;
    		//mLine1View.setText(mLine1);
    		//mLine2View.setText(mLine2);
    		//mLine3View.setText(mLine3);
    		 */
            if (mPdfPage != null) {
                if (mBtPage != null)
                    mBtPage.setText(mPdfPage.getPageNumber()+"/"+mPdfFile.getNumPages());
                if (mBtPage2 != null)
                    mBtPage2.setText(mPdfPage.getPageNumber()+"/"+mPdfFile.getNumPages());
            }
        }

		/*private String format(double value, int num) {
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setGroupingUsed(false);
			nf.setMaximumFractionDigits(num);
			String result = nf.format(value);
			return result;
		}*/
    }



    private void showPage(int page, float zoom) throws Exception {
        //long startTime = System.currentTimeMillis();
        //long middleTime = startTime;
        try {
            // free memory from previous page
            mGraphView.setPageBitmap(null);
            mGraphView.updateImage();

            // Only load the page if it's a different page (i.e. not just changing the zoom level)
            if (mPdfPage == null || mPdfPage.getPageNumber() != page) {
                mPdfPage = mPdfFile.getPage(page, true);
            }
            //int num = mPdfPage.getPageNumber();
            //int maxNum = mPdfFile.getNumPages();
            float width = mPdfPage.getWidth();
            float height = mPdfPage.getHeight();
            //String pageInfo= new File(pdffilename).getName() + " - " + num +"/"+maxNum+ ": " + width + "x" + height;
            //mGraphView.showText(pageInfo);
            //Log.i(TAG, pageInfo);
            RectF clip = null;
            //middleTime = System.currentTimeMillis();
            Bitmap bi = mPdfPage.getImage((int)(width*zoom), (int)(height*zoom), clip, true, true);
            mGraphView.setPageBitmap(bi);
            mGraphView.updateImage();
            //getImageReasource();
            if (progress != null)
                progress.dismiss();
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage(), e);
            mGraphView.showText("Exception: "+e.getMessage());
        }
        //long stopTime = System.currentTimeMillis();
        //mGraphView.pageParseMillis = middleTime-startTime;
        //mGraphView.pageRenderMillis = stopTime-middleTime;
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
                openFile(f, password);
            }
        }
        catch (PDFAuthenticationFailureException e) {
            throw e;
        } catch (Throwable e) {
            e.printStackTrace();
            mGraphView.showText("Exception: " + e.getMessage());
        }
        //long stopTime = System.currentTimeMillis();
        //mGraphView.fileMillis = stopTime-startTime;
    }


    /**
     * <p>Open a specific pdf file.  Creates a DocumentInfo from the file,
     * and opens that.</p>
     *
     * <p><b>Note:</b> Mapping the file locks the file until the PDFFile
     * is closed.</p>
     *
     * @param file the file to open
     * @throws IOException
     */
    public void openFile(File file, String password) throws IOException {
        // first open the file for random access
        RandomAccessFile raf = new RandomAccessFile(file, "r");

        // extract a file channel
        FileChannel channel = raf.getChannel();

        // now memory-map a byte-buffer
        ByteBuffer bb =
                ByteBuffer.NEW(channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));
        // create a PDFFile from the data
        if (password == null)
            mPdfFile = new PDFFile(bb);
        else
            mPdfFile = new PDFFile(bb, new PDFPassword(password));

        mGraphView.showText("Anzahl Seiten:" + mPdfFile.getNumPages());
    }


    /*private byte[] readBytes(File srcFile) throws IOException {
    	long fileLength = srcFile.length();
    	int len = (int)fileLength;
    	byte[] result = new byte[len];
    	FileInputStream fis = new FileInputStream(srcFile);
    	int pos = 0;
		int cnt = fis.read(result, pos, len-pos);
    	while (cnt > 0) {
    		pos += cnt;
    		cnt = fis.read(result, pos, len-pos);
    	}
		return result;
	}*/

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

    /*private void postHideNavigation() {
    	// Start a time to hide the panel after 3 seconds
    	closeNavigationHandler.removeCallbacks(closeNavigationThread);
    	closeNavigationHandler.postDelayed(closeNavigationThread, 3000);
    }*/

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
            //finish();
            pdfReader = new PdfReader(signPath);
            //fix y
            y=pdfReader.getCropBox(1).getHeight()-y;
            PdfStamper pdfStamper = new PdfStamper(pdfReader,
                    new FileOutputStream(newP));
            System.out.println("step 9: "+signatureByte);
            Image image = Image.getInstance(signatureByte);

            if (pageNum==-1) {

                for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {

                    //put content under
                    PdfContentByte content = pdfStamper.getUnderContent(i);
                    image.setAbsolutePosition(x, y);
                    content.addImage(image);

                    //put content over
                    content = pdfStamper.getOverContent(i);
                    image.setAbsolutePosition(x, y);
                    content.addImage(image);

                    //Text over the existing page
                    /*BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
                            BaseFont.WINANSI, BaseFont.EMBEDDED);
                    content.beginText();
                    content.setFontAndSize(bf, 18);
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Page No: " + i, 430, 15, 0);
                    content.endText();*/
                }
            }
            else{
                PdfContentByte content =  pdfStamper.getOverContent(pageNum);
                image.setAbsolutePosition(x, y);
                content.addImage(image);

            }
            pdfStamper.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }
    public void displayAlertDialog() {
//////////
        AlertDialog.Builder alert = new AlertDialog.Builder(Pdftry.this);
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

