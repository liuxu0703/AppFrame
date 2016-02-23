package lx.af.demo.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseDemoActivity;
import lx.af.demo.utils.Paths;
import lx.af.dialog.MessageDialog;
import lx.af.utils.ActivityLauncher.ActivityResultCallback;
import lx.af.utils.ActivityLauncher.CodeScannerLauncher;
import lx.af.utils.BitmapUtils;
import lx.af.utils.FileUtils;
import lx.af.utils.QRDecoder;
import lx.af.utils.QRGenerator;
import lx.af.utils.ViewInject.ViewInject;

/**
 * author: lx
 * date: 15-12-8
 */
public class ActivityScanner extends BaseDemoActivity implements
        View.OnClickListener,
        ActivityResultCallback<String>,
        ActionBar.Default {

    @ViewInject(id = R.id.scanner_btn_scan_all, click = "onClick")
    Button btn1;
    @ViewInject(id = R.id.scanner_btn_scan_qr_code, click = "onClick")
    Button btn2;
    @ViewInject(id = R.id.scanner_btn_scan_bar_code, click = "onClick")
    Button btn3;
    @ViewInject(id = R.id.scanner_btn_generate_qr_code, click = "onClick")
    Button btn4;
    @ViewInject(id = R.id.scanner_btn_read_pic, click = "onClick")
    Button btn5;
    @ViewInject(id = R.id.scanner_qr_code_image, click = "onClick")
    ImageView image;
    @ViewInject(id = R.id.scanner_text)
    TextView text;
    @ViewInject(id = R.id.scanner_editor_text)
    EditText editor;

    private Bitmap qrcodeBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scanner_btn_scan_all: {
                CodeScannerLauncher.of(this).start(this);
                break;
            }
            case R.id.scanner_btn_scan_qr_code: {
                CodeScannerLauncher.of(this).modeQRCode().start(this);
                break;
            }
            case R.id.scanner_btn_scan_bar_code: {
                CodeScannerLauncher.of(this).mode1D().start(this);
                break;
            }
            case R.id.scanner_btn_read_pic: {
                QRDecoder.decodeFromGallery(this, new QRDecoder.QRDecodeCallback() {
                    @Override
                    public void onQRDecodeResult(String text) {
                        displayQRCodeText(text);
                    }
                });
                break;
            }
            case R.id.scanner_btn_generate_qr_code: {
                String content = editor.getText().toString();
                displayQRCodeImage(content);
                break;
            }
            case R.id.scanner_qr_code_image: {
                new MessageDialog.Builder(this)
                        .setMessage("Save QRCode image to sdcard?")
                        .setCancelListener(null)
                        .setConfirmListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String path = Paths.generateCropImagePath().toString();
                                if (BitmapUtils.saveBitmap(qrcodeBitmap, path)) {
                                    FileUtils.scanFile(path);
                                    toastLong("save to path " + Paths.QR_CODE_PATH + "/");
                                } else {
                                    toastShort("save fail");
                                }
                            }
                        }).create().show();
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int resultCode, @NonNull String result) {
        displayQRCodeText(result);
    }

    private void displayQRCodeText(String content) {
        Log.d("liuxu", "bar code decode result: " + content);
        if (TextUtils.isEmpty(content)) {
            toastShort("decode failed");
        } else {
            image.setVisibility(View.GONE);
            text.setVisibility(View.VISIBLE);
            text.setText(content);
        }
    }

    private void displayQRCodeImage(String content) {
        if (TextUtils.isEmpty(content)) {
            toastShort("content should not be empty");
            return;
        }
        qrcodeBitmap = QRGenerator.with(content)
                .cache(true).logo(R.drawable.ic_launcher).logoBackground(true).create();
        if (qrcodeBitmap != null) {
            image.setVisibility(View.VISIBLE);
            text.setVisibility(View.GONE);
            image.setImageBitmap(qrcodeBitmap);
        } else {
            toastShort("generate bitmap fail");
        }
    }
}
