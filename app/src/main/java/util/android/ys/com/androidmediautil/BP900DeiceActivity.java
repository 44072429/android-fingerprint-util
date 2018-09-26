package util.android.ys.com.androidmediautil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import util.android.ys.com.fingerprnt.FingerUtilV2;
import util.android.ys.com.fingerprnt.Fingerprint;

public class BP900DeiceActivity extends AppCompatActivity {

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.tv_texet)
    TextView tvTexet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bp900_deice);
        ButterKnife.bind(this);
        FingerUtilV2.globalInit(this , "BP900");
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

                break;
            case R.id.Btn2:

                break;
            case R.id.Btn3:

                tvTexet.setText("请录入指纹");
                FingerUtilV2.fingerEnrollStart(new Fingerprint.FingerprinEnrollEventlistener() {
                    @Override
                    public void onSuccess(final int captureMode, final byte[] imageBuffer, final int[] imageAttributes, final byte[] templateBuffer, final int timesLeft) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvTexet.setText("请再按" + timesLeft  + "次");
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
        }
    }
}
