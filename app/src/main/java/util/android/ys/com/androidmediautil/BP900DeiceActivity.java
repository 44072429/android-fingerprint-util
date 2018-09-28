package util.android.ys.com.androidmediautil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import util.android.ys.com.fingerprnt.FingerUtilV2;
import util.android.ys.com.fingerprnt.Fingerprint;

public class BP900DeiceActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.tv_texet)
    TextView tvTexet;

    // 语音
    private WeakReference<TextToSpeech> ttsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bp900_deice);
        ButterKnife.bind(this);
        FingerUtilV2.globalInit(this, "BP900");

        // 语音相关
        ttsRef = new WeakReference<TextToSpeech>(new TextToSpeech(BP900DeiceActivity.this, BP900DeiceActivity.this));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FingerUtilV2.globalRelease();
    }

    @OnClick({R.id.Btn1, R.id.Btn2, R.id.Btn3, R.id.Btn4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.Btn1:
                tvTexet.setText("请验证指纹");
                FingerUtilV2.fingerVerifyStart(new Fingerprint.FingerprinEventlistener() {
                    @Override
                    public void onSuccess(int captureMode, final byte[] imageBuffer, int[] imageAttributes, final byte[] templateBuffer) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bmp = BitmapFactory.decodeByteArray(imageBuffer, 0, imageBuffer.length);
                                image.setImageBitmap(bmp);
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
                            @Override
                            public void run() {
                                tvTexet.setText("请再按" + timesLeft + "次");
                                Bitmap bmp = BitmapFactory.decodeByteArray(imageBuffer, 0, imageBuffer.length);
                                image.setImageBitmap(bmp);
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
                        return true;
                    }

                }, 3);
                break;
            case R.id.Btn4:
                ttsRef.get().speak("说话", TextToSpeech.QUEUE_FLUSH, null);
                break;
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = ttsRef.get().setLanguage(Locale.CHINESE);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                showMessage("不支持中文语音");
            }
        }
    }

}
