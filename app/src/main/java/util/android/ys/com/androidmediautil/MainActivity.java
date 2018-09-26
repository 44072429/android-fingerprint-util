package util.android.ys.com.androidmediautil;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zkteco.android.biometric.core.utils.ToolUtils;
import com.zkteco.android.biometric.module.fingerprint.FingerprintCaptureListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import util.android.ys.com.fingerprnt.FingerUtilV2;
import util.android.ys.com.fingerprnt.Fingerprint;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.tv_texet)
    TextView tvTexet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FingerUtilV2.globalInit(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FingerUtilV2.globalRelease();
    }

    @OnClick({R.id.Btn1, R.id.Btn2, R.id.Btn3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.Btn1:

                final boolean b = FingerUtilV2.fingerVerifyStart(new Fingerprint.FingerprinEventlistener() {
                    @Override
                    public void onSuccess(final int captureMode, final byte[] imageBuffer, final int[] imageAttributes, final byte[] templateBuffer) {
                        runOnUiThread(new Runnable() {

                            final int[] attributes = imageAttributes;
                            final byte[] imgBuffer = imageBuffer;
                            final byte[] tmpBuffer = templateBuffer;
                            final int capMode = captureMode;
//                            final int nTemplen = fingerprintSensor.getLastTempLen();
//                            final String strTempBase64 = Base64.encodeToString(templateBuffer,0, nTemplen, Base64.NO_WRAP);

                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_SHORT).show();
                                if (captureMode == FingerprintCaptureListener.MODE_CAPTURE_TEMPLATEANDIMAGE) {

                                    Bitmap mBitMap = ToolUtils.renderCroppedGreyScaleBitmap(imgBuffer, attributes[0], attributes[1]);
                                    image.setImageBitmap(mBitMap);

                                }
                            }
                        });

                    }

                    @Override
                    public void onFailure(String err) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                break;
            case R.id.Btn2:

                FingerUtilV2.fingerVerifyStop();

                break;
            case R.id.Btn3:

                tvTexet.setText("请录入指纹");
                FingerUtilV2.fingerEnrollStart(new Fingerprint.FingerprinEnrollEventlistener() {
                    @Override
                    public void onSuccess(final int captureMode, final byte[] imageBuffer, final int[] imageAttributes, final byte[] templateBuffer, final int timesLeft) {

                        runOnUiThread(new Runnable() {

                            final int[] attributes = imageAttributes;
                            final byte[] imgBuffer = imageBuffer;
                            final byte[] tmpBuffer = templateBuffer;
                            final int capMode = captureMode;

                            @Override
                            public void run() {

                                tvTexet.setText("请再按" + timesLeft  + "次");
                                if (captureMode == FingerprintCaptureListener.MODE_CAPTURE_TEMPLATEANDIMAGE) {
                                    Bitmap mBitMap = ToolUtils.renderCroppedGreyScaleBitmap(imgBuffer, attributes[0], attributes[1]);
                                    image.setImageBitmap(mBitMap);
                                }

                            }
                        });

                    }

                    @Override
                    public void onFailure(final String err) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvTexet.setText(err);
                            }
                        });
                    }

                    @Override
                    public void onEnrollSuccess(byte[] templateBuffer) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvTexet.setText("录入成功");
                            }
                        });
                    }

                    @Override
                    public boolean onCaptureTime() {
                        return false;
                    }

                }, 3);


                break;
        }
    }
}
