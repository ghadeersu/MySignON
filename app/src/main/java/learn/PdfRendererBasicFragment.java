package learn;

/**
 * Created by Naseebah on 13/04/16.
 */
/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.Fragment;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Matrix;
        import android.graphics.pdf.PdfRenderer;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Environment;
        import android.os.ParcelFileDescriptor;
        import android.util.Base64;
        import android.view.LayoutInflater;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;
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

        import java.io.ByteArrayOutputStream;
        import java.io.File;
        import java.io.FileDescriptor;
        import java.io.FileInputStream;
        import java.io.FileOutputStream;
        import java.io.IOException;

/**
 * This fragment has a big {@ImageView} that shows PDF pages, and 2 {@link android.widget.Button}s to move between
 * pages. We use a {@link android.graphics.pdf.PdfRenderer} to render PDF pages as {@link android.graphics.Bitmap}s.
 */
public class PdfRendererBasicFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {
    public float xTouch;
    public float yTouch;
    public int counter=0;
    /**
     * Key string for saving the state of current page index.
     */
    private static final String STATE_CURRENT_PAGE_INDEX = "current_page_index";

    /**
     * File descriptor of the PDF.
     */
    private ParcelFileDescriptor mFileDescriptor;

    /**
     * {@link android.graphics.pdf.PdfRenderer} to render the PDF.
     */
    private PdfRenderer mPdfRenderer;

    /**
     * Page that is currently shown on the screen.
     */
    private PdfRenderer.Page mCurrentPage;

    /**
     * {@link android.widget.ImageView} that shows a PDF page as a {@link android.graphics.Bitmap}
     */
    private ImageView mImageView;

    /**
     * {@link android.widget.Button} to move to the previous page.
     */
    private Button mButtonPrevious;

    /**
     * {@link android.widget.Button} to move to the next page.
     */
    private Button mButtonNext;

    /**
     * Button to share
     */
    private Button mShare;
    /**
     * Button to select signature
     */
    private Button mSelectSignature;
    /**
     * Button to sign Document
     */
    private Button mSign;
    /**
     * signature image view
     */
    private ImageView mSignatureImage;

    /**
     * file Document
     */
    private File file;
    /**
     * progress dialog
     */
    private ProgressDialog progress;
    /**
     * user signature
     */
    public byte[] signatureByte;
    /**
     * temp path for new file
     */
    public String newP = Environment.getExternalStorageDirectory().getAbsolutePath() + "/signon/l.pdf";
    /**
     * file digest
     */
    private String messagedigest;
    /**
     * file digest
     */
    private String path;
    /**
     * stamp file
     */
    private PdfReader pdfReader;


    public PdfRendererBasicFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
       changeImageView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pdf_render_basic, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Retain view references.
        mImageView = (ImageView) view.findViewById(R.id.image);
        mButtonPrevious = (Button) view.findViewById(R.id.previous);
        mButtonNext = (Button) view.findViewById(R.id.next);
        mShare= (Button) view.findViewById(R.id.share);
        mSelectSignature=(Button) view.findViewById(R.id.select);
        mSign=(Button) view.findViewById(R.id.sign);
        mSignatureImage=(ImageView)view.findViewById(R.id.signatureImage);
        changeImageView();
        // Bind events.
        mButtonPrevious.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);
        mImageView.setOnTouchListener(this);
        mShare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/pdf");
                Uri uri = Uri.parse("file://" + file.getPath());
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                try {
                    startActivity(Intent.createChooser(intent, "Share File"));
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        mSelectSignature.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //signature select
                Intent pickContactIntent = new Intent(getActivity(),SignatureSelectActivity.class);
                startActivity(pickContactIntent);
            }
        });
        mSign.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //signature
                final File test = new File(path);

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
System.out.println("step 14: "+mSignatureImage.getX()+"," +mSignatureImage.getY());
                                            merge(mSignatureImage.getX(), mSignatureImage.getY(), mCurrentPage.getIndex()+1);

                                            File f2 = new File(newP);

                                            System.out.println("path " + f2.getPath());
                                            File ff = new File(path);

                                            f2.renameTo(test);
                                            new HDWFTP_Upload_Update(getActivity()).execute(path);


                                        } else {
                                            AlertDialog alert = new AlertDialog.Builder(getActivity()).setMessage("You Altered the file").setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
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
        });
        // Show the first page by default.
        int index = 0;
        // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
        if (null != savedInstanceState) {
            index = savedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX, 0);
        }
        showPage(index);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            openRenderer(activity);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
            activity.finish();
        }
    }

    @Override
    public void onDetach() {
        try {
            closeRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mCurrentPage) {
            outState.putInt(STATE_CURRENT_PAGE_INDEX, mCurrentPage.getIndex());
        }
    }

    float x = 0.0f;
    float y = 0.0f;
    boolean moving=false;
    /*
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();*/
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(session.base64.isEmpty())
        Toast.makeText(getActivity(),"Please select signature",Toast.LENGTH_LONG).show();
            mImageView.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mSignatureImage.setVisibility(ImageView.VISIBLE);
                    moving = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(moving)
                    {
                        x = event.getX();//getRawX();//-(mPage/(mImageView.getHeight()*mZoom));//- mImageView.getWidth()/2;
                        y = event.getY();//getRawY();//-(mPage/(mImageView.getWidth()*mZoom));//- mImageView.getHeight()*3/2;
                        xTouch=event.getRawX();
                        yTouch=event.getRawY();
                        mSignatureImage.setX(x);
                        mSignatureImage.setY(y);
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

        return true;
    }

    /**
     * Sets up a {@link android.graphics.pdf.PdfRenderer} and related resources.
     */
    private void openRenderer(Context context) throws IOException {
        // In this sample, we read a PDF from the assets directory.
        // This is the PdfRenderer we use to render the PDF.
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            path = extras.getString("path");

            file = new File(path);
            System.out.println(file.getAbsoluteFile());
            mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            System.out.println(mFileDescriptor.toString());
            mPdfRenderer = new PdfRenderer(mFileDescriptor);
        }
        else System.out.println("not in if");
    }

    /**
     * Closes the {@link android.graphics.pdf.PdfRenderer} and related resources.
     *
     * @throws java.io.IOException When the PDF file cannot be closed.
     */
    private void closeRenderer() throws IOException {
        if (null != mCurrentPage) {
            mCurrentPage.close();
        }
        mPdfRenderer.close();
        mFileDescriptor.close();
    }

    /**
     * Shows the specified page of PDF to the screen.
     *
     * @param index The page index.
     */
    private void showPage(int index) {
        if (mPdfRenderer.getPageCount() <= index) {
            return;
        }
        // Make sure to close the current page before opening another one.
        if (null != mCurrentPage) {
            mCurrentPage.close();
        }
        // Use `openPage` to open a specific page in PDF.
        mCurrentPage = mPdfRenderer.openPage(index);
        // Important: the destination bitmap must be ARGB (not RGB).
        Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(),
                Bitmap.Config.ARGB_8888);
        // Here, we render the page onto the Bitmap.
        // To render a portion of the page, use the second and third parameter. Pass nulls to get
        // the default result.
        // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
        mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        // We are ready to show the Bitmap to user.
        mImageView.setImageBitmap(bitmap);
        updateUi();
    }

    /**
     * Updates the state of 2 control buttons in response to the current page index.
     */
    private void updateUi() {
        int index = mCurrentPage.getIndex();
        int pageCount = mPdfRenderer.getPageCount();
        mButtonPrevious.setEnabled(0 != index);
        mButtonNext.setEnabled(index + 1 < pageCount);
        getActivity().setTitle(getString(R.string.app_name_with_index, index + 1, pageCount));
    }

    /**
     * Gets the number of pages in the PDF. This method is marked as public for testing.
     *
     * @return The number of pages.
     */
    public int getPageCount() {
        return mPdfRenderer.getPageCount();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.previous: {
                // Move to the previous page
                showPage(mCurrentPage.getIndex() - 1);
                break;
            }
            case R.id.next: {
                // Move to the next page
                showPage(mCurrentPage.getIndex() + 1);
                break;
            }
        }
    }

    public void changeImageView(){
System.out.println("step 1: "+session.userkey);
        Firebase ref = new Firebase("https://torrid-heat-4458.firebaseio.com/signature");
        Query queryRef = ref.orderByChild("signerID").equalTo(session.userkey);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                        if(child.getKey().equals(session.base64)){
                            System.out.println("step 2: "+session.base64);
                            signatureByte= Base64.decode(child.child("signatureBase64").getValue(String.class), Base64.NO_WRAP);
                            System.out.println("step 3: "+signatureByte.toString());
                            Bitmap img= BitmapFactory.decodeByteArray(signatureByte, 0, signatureByte.length);
                            mSignatureImage.setImageBitmap(img);

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

    public void merge(float x,float y, int pageNum) {
        try {

            System.out.println("step 9: "+signatureByte);
            Image image = Image.getInstance( signatureByte);
            //finish();
            pdfReader = new PdfReader(path);
            //fix y
            y=pdfReader.getCropBox(pageNum).getHeight()-(y/2);
            x=pdfReader.getCropBox(pageNum).getWidth()-(x/4);
           // x+=mSignatureImage.getWidth()/5;
            y-=(mSelectSignature.getHeight()/4);
            System.out.println("step 14: "+x+"," +y);
            //x=xTouch;
            //y=yTouch;
            System.out.println("step 15: "+x+"," +y);
            PdfStamper pdfStamper = new PdfStamper(pdfReader,
                    new FileOutputStream(newP));
            System.out.println();


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
    public byte[] BitMapToByte(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        return b;
    }
}
