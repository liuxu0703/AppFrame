package lx.af.demo.activity.DemoUtils;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;
import lx.af.test.TestImageHelper;
import lx.af.utils.ScreenUtils;
import lx.af.utils.UIL.UILLoader;

/**
 * author: lx
 * date: 16-5-4
 */
public class UILLoaderDemo extends BaseActivity implements
        ActionBar.Default {

    @BindView(R.id.uil_loader_demo_container1)
    LinearLayout container1;
    @BindView(R.id.uil_loader_demo_container2)
    LinearLayout container2;
    @BindView(R.id.uil_loader_image1)
    ImageView image1;
    @BindView(R.id.uil_loader_image2)
    ImageView image2;
    @BindView(R.id.uil_loader_image3)
    ImageView image3;
    @BindView(R.id.uil_loader_image4)
    ImageView image4;
    @BindView(R.id.uil_loader_image5)
    ImageView image5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uilloader_demo);
        ButterKnife.bind(this);

        displayCircleImages();
        displaySquareImages();
        displayImages();
    }

    private void displayCircleImages() {
        container1.removeAllViews();
        int n = 4;
        int size = ScreenUtils.getScreenWidth() / n;
        int padding = ScreenUtils.dip2px(3);
        for (int i = 0; i < n; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            ImageView imageView = new ImageView(this);
            imageView.setPadding(padding, padding, padding, padding);
            imageView.setLayoutParams(params);
            container1.addView(imageView);

            String uri;
            if (i == 0) {
                uri = "http://111"; // simulate wrong uri
            } else if (i == 1) {
                uri = null; // simulate empty uri
            } else {
                uri = TestImageHelper.randomImageL();
            }
            UILLoader.of(imageView, uri)
                    .imageDefault(R.drawable.img_gallery_default)
                    .asCircle()
                    .border(10, Color.WHITE)
                    .animateScaleIn()
                    .delayBeforeLoading((i + 1) * 400)
                    .display();
        }
    }

    private void displaySquareImages() {
        container2.removeAllViews();
        int n = 4;
        int size = ScreenUtils.getScreenWidth() / n;
        int padding = ScreenUtils.dip2px(3);
        for (int i = 0; i < n; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            ImageView imageView = new ImageView(this);
            imageView.setPadding(padding, padding, padding, padding);
            imageView.setLayoutParams(params);
            container2.addView(imageView);

            UILLoader.of(imageView, TestImageHelper.randomImageL())
                    .imageDefault(R.drawable.img_default)
                    .asSquare()
                    .corner(20)
                    .animateFloatIn()
                    .delayBeforeLoading((i + 1) * 400)
                    .resetBeforeLoading()
                    .display();
        }
    }

    private void displayImages() {
        String uri = TestImageHelper.randomCartoonL();
        UILLoader.of(image1, uri).display();
        UILLoader.of(image2, uri).blur(2).border(5, Color.WHITE).display();
        UILLoader.of(image3, uri).blur(5).display();
        UILLoader.of(image4, uri).blur(10).corner(30).display();
        UILLoader.of(image5, uri).border(8, Color.CYAN).corner(30).display();
    }

    @OnClick(R.id.uil_loader_btn_reload)
    public void onClick() {
        displayCircleImages();
        displaySquareImages();
        displayImages();
    }
}
