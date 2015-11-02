package jerome.socketclientvideo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

import NetworkUtility.NIOSocketUtil;
import NetworkUtility.NIOSocketUtil.DataCallbackListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{

    private Context mContext = null;
    private ImageView mImageView = null;
    private EditText editTextAddress, editTextPort;
    private Button buttonConnect;
    private String message = "";
    private static String kq = "";

    private static boolean flag = true;
    private byte[] tempByteData = null;
    Socket socket = null;
    private int mLog = 0;
    private int mLog2 = 0;
    private int mLog3 = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextAddress = (EditText) findViewById(R.id.address);
        editTextPort = (EditText) findViewById(R.id.port);
        buttonConnect = (Button) findViewById(R.id.connect);
        mImageView = (ImageView) findViewById(R.id.imageView3);
        mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mContext = this;
        buttonConnect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextAddress.setVisibility(View.GONE);
                editTextPort.setVisibility(View.GONE);
                buttonConnect.setVisibility(View.GONE);
                // TODO Auto-generated method stub
//				myClientTask = new ClientTask(editTextAddress.getText()
//						.toString(), Integer.parseInt(editTextPort.getText()
//						.toString()));
//				myClientTask.execute();
                NIOSocketUtil clientRxThread =
                        new NIOSocketUtil(
                                editTextAddress.getText().toString(),
                                Integer.parseInt(editTextPort.getText().toString()));
                clientRxThread.setDataCallbackListener(new DataCallbackListener()
                {

                    @Override
                    public void callback(byte[] data)
                    {
                        try
                        {
                            mLog3++;
                            Log.v("Log", "mLog3: "+mLog3);
                            final Bitmap image  = BitmapFactory.decodeByteArray(data, 0, data.length);
                            if (image != null)
                            {
                                ((Activity)mContext).runOnUiThread(new Runnable() {
                                    public void run()
                                    {
                                        mImageView.setImageBitmap(image);
                                    }
                                });

                            }
                            else
                            {
                                if (tempByteData == null)
                                {

                                    tempByteData = new byte[data.length];
                                    System.arraycopy(data, 0, tempByteData, 0, data.length);
                                }
                                else
                                {
                                    int tempByteDataLength = tempByteData.length;
                                    byte[] tempData = new byte[tempByteData.length];
                                    System.arraycopy(tempByteData, 0, tempData, 0, tempByteData.length);

                                    int tempByteDataLength2 = data.length + tempData.length;
                                    tempByteData = new byte[data.length + tempData.length];
                                    System.arraycopy(tempData, 0, tempByteData, 0, tempData.length);
                                    System.arraycopy(data, 0, tempByteData, tempData.length, data.length);
                                    tempData = null;
                                    final Bitmap image2  = BitmapUtility.DecodeBitmap.decodeBitmapFromByteArray(tempByteData, mImageView.getWidth(),mImageView.getHeight());
                                    if (image2 != null)
                                    {
                                        mLog++;
                                        tempByteData = null;
                                        Log.v("Log", "mLog: "+mLog);
                                        ((Activity)mContext).runOnUiThread(new Runnable() {
                                            public void run()
                                            {
                                                mLog2++;
                                                Log.v("Log", "mLog2: "+mLog2);
                                                //((BitmapDrawable)mImageView.getDrawable()).getBitmap().recycle();
                                                Drawable drawable = mImageView.getDrawable();
                                                if (drawable instanceof BitmapDrawable) {
                                                    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                                                    Bitmap bitmap = bitmapDrawable.getBitmap();
                                                    bitmap.recycle();
                                                }
                                                mImageView.setImageBitmap(image2);
                                            }
                                        });


                                    }
                                }
                            }

                        }
                        catch(OutOfMemoryError oom )
                        {
                            Log.v("Log", "OutOfMemoryError: "+oom.toString());
                            tempByteData = null;
                        }
                        catch(Exception ex)
                        {
                            Log.v("Log", "Exception: "+ex.toString());
                            tempByteData = null;
                        }

                    }

                });
                clientRxThread.start();
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}
