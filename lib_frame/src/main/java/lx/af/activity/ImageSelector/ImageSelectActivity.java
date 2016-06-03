package lx.af.activity.ImageSelector;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lx.af.R;
import lx.af.base.AbsBaseActivity;
import lx.af.manager.GlobalThreadManager;
import lx.af.view.ProgressWheel;

/**
 * author: lx
 * date: 15-04-22
 *
 * inspired by https://github.com/lovetuzitong/MultiImageSelector
 */
public class ImageSelectActivity extends AbsBaseActivity implements
        ImageGridAdapter.OnItemViewClickListener,
        View.OnClickListener {

    /** max select count, default 9 */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /** whether to show camera button as the first grid, default true */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /** pre-selected images */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";
    /** image selector activity title */
    public static final String EXTRA_ACTIVITY_TITLE = "activity_title";

    /** select result, as an ArrayList of file path  */
    public static final String EXTRA_RESULT = "select_result";


    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;

    // activity request code
    private static final int AC_REQUEST_CAMERA = 100;
    private static final int AC_IMAGE_BROWSER = 101;

    private Button mSubmitButton;
    private ImageGridView mGridView;
    private FolderListView mFolderListView;
    private TextView mTimeLineText;
    private TextView mCategoryText;
    private ProgressWheel mLoadingProgress;

    private Handler mUIHandler = new Handler();
    private ImageGridAdapter mImageAdapter;
    private Animation mAnimTimeLineHide;

    private int mDesireImageCount;
    private boolean mIsShowCamera;
    private boolean mIsFolderGenerated = false;
    private ArrayList<FolderModel> mResultFolder = new ArrayList<>();

    private File mCameraFile;
    private ArrayList<String> mResultList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_activity);
        Intent intent = getIntent();
        mDesireImageCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 9);
        mIsShowCamera = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        if (mDesireImageCount > 1 && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            mResultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }

        String title = intent.getStringExtra(EXTRA_ACTIVITY_TITLE);
        if (!TextUtils.isEmpty(title)) {
            TextView tvTitle = (TextView) findViewById(R.id.mis_activity_title);
            tvTitle.setText(title);
        }

        if (savedInstanceState != null) {
            ArrayList<String> list = savedInstanceState.getStringArrayList("selected_list");
            String cameraPath = savedInstanceState.getString("camera_path");
            if (list != null) {
                mResultList = list;
            }
            if (!TextUtils.isEmpty(cameraPath)) {
                onCameraShot(cameraPath);
                return;
            }
        }

        findViewById(R.id.mis_activity_btn_back).setOnClickListener(this);
        mSubmitButton = (Button) findViewById(R.id.mis_activity_btn_submit);
        mSubmitButton.setOnClickListener(this);
        if (mDesireImageCount == 1) {
            // single select mode does not need submit button
            mSubmitButton.setVisibility(View.INVISIBLE);
        }
        refreshSubmitButton();

        mFolderListView = (FolderListView) findViewById(R.id.mis_fragment_folder_list);
        initFolderList();

        mTimeLineText = (TextView) findViewById(R.id.mis_fragment_time_line);
        hideTimeLineDelayed();

        mCategoryText = (TextView) findViewById(R.id.mis_fragment_btn_show_folder);
        mCategoryText.setText(R.string.mis_folder_all);
        mCategoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFolderListView.switchShowHide();
            }
        });

        mGridView = (ImageGridView) findViewById(R.id.mis_fragment_img_grid_view);
        mLoadingProgress = (ProgressWheel) findViewById(R.id.mis_fragment_loading_progress);
        initImageGridView();

        getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("selected_list", mResultList);
        if (mCameraFile != null) {
            outState.putString("camera_path", mCameraFile.getAbsolutePath());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.mis_activity_btn_back) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (id == R.id.mis_activity_btn_submit) {
            onSelectDone(mResultList);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == AC_REQUEST_CAMERA) {
            if (mCameraFile != null) {
                onCameraShot(mCameraFile.getAbsolutePath());
            }
        } else if (requestCode == AC_IMAGE_BROWSER) {
            if (data == null) {
                return;
            }
            ArrayList<String> tmp = data.getStringArrayListExtra(ImageSelectBrowser.EXTRA_RESULT);
            if (data.getBooleanExtra(ImageSelectBrowser.EXTRA_RESULT_DONE, false)) {
                // done select, set result and finish
                onSelectDone(tmp);
                return;
            }
            if (tmp == null || tmp.size() == 0) {
                onRefreshImageSelected(tmp);
                mImageAdapter.setDefaultSelected(tmp);
                return;
            }
            if (mDesireImageCount == 1) {
                // single select mode
                onSelectDone(tmp);
            } else {
                // multi select mode
                onRefreshImageSelected(tmp);
                mImageAdapter.setDefaultSelected(tmp);
            }
        }
    }

    @Override
    public void onItemCameraClicked() {
        if (!isMaxCount()) {
            startCamera();
        }
    }

    @Override
    public void onItemImageClicked(ImageItemView view, ImageModel data) {
        startImageBrowser(view, Uri.parse("file://" + data.path).toString());
    }

    @Override
    public void onItemCheckClicked(ImageItemView view, ImageModel data) {
        selectImageFromGrid(view, data);
    }

    private void initImageGridView() {
        mImageAdapter = new ImageGridAdapter(mGridView, this, mIsShowCamera);

        mGridView.setSelector(R.drawable.bkg_clickable);
        mGridView.setAdapter(mImageAdapter);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int state) {
                if (state != SCROLL_STATE_IDLE) {
                    mTimeLineText.setVisibility(View.VISIBLE);
                }
                hideTimeLineDelayed();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mTimeLineText.getVisibility() == View.VISIBLE) {
                    int count = view.getAdapter().getCount();
                    ImageModel image = null;
                    if (mIsShowCamera) {
                        int index = (firstVisibleItem + 1 == count) ?
                                view.getAdapter().getCount() - 1 :
                                firstVisibleItem + 1;
                        image = (ImageModel) view.getAdapter().getItem(index);
                    } else {
                        if (count != 0) {
                            image = (ImageModel) view.getAdapter().getItem(firstVisibleItem);
                        }
                    }
                    if (image != null) {
                        mTimeLineText.setText(formatPhotoDate(image.path));
                    }
                }
            }
        });
    }

    private void initFolderList() {
        mFolderListView.hide();

        mFolderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    getSupportLoaderManager().restartLoader(
                            LOADER_ALL, null, mLoaderCallback);
                    mCategoryText.setText(R.string.mis_folder_all);
                    if (mIsShowCamera) {
                        mImageAdapter.setShowCamera(true);
                    }
                } else {
                    FolderModel folder = (FolderModel) parent.getAdapter().getItem(position);
                    if (null != folder) {
                        Bundle args = new Bundle();
                        args.putString("path", folder.path);
                        getSupportLoaderManager().restartLoader(
                                LOADER_CATEGORY, args, mLoaderCallback);
                        mCategoryText.setText(folder.name);
                    }
                    mImageAdapter.setShowCamera(false);
                }
                mFolderListView.setSelectIndex(position);
                mFolderListView.hide();

                mGridView.smoothScrollToPosition(0);
            }
        });
    }

    private void hideTimeLineDelayed() {
        mUIHandler.removeCallbacks(mHideTimeLineRunnable);
        mUIHandler.postDelayed(mHideTimeLineRunnable, 1500);
    }

    private boolean isMaxCount() {
        if (mDesireImageCount == mResultList.size()) {
            Toast.makeText(this, R.string.mis_toast_amount_limit, Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    private void startCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(this.getPackageManager()) != null) {
            File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (!dcim.exists() && !dcim.mkdir()) {
                Toast.makeText(this,
                        R.string.toast_path_create_dir_fail, Toast.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
            mCameraFile = new File(dcim, df.format(new Date()) + ".jpg");
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraFile));
            startActivityForResult(cameraIntent, AC_REQUEST_CAMERA);
        } else {
            Toast.makeText(this, R.string.mis_toast_no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    private void startImageBrowser(View view, String currentImageUri) {
        Intent intent = new Intent(this, ImageSelectBrowser.class);
        intent.putExtra(ImageSelectBrowser.EXTRA_IMAGE_URI_LIST, mImageAdapter.getImageUriList());
        intent.putExtra(ImageSelectBrowser.EXTRA_CURRENT_IMAGE_URI, currentImageUri);
        intent.putExtra(ImageSelectBrowser.EXTRA_SELECT_COUNT, mDesireImageCount);
        intent.putExtra(ImageSelectBrowser.EXTRA_SELECTED_LIST, mImageAdapter.getSelectedImageUriList());
        this.overridePendingTransition(R.anim.image_browser_show, R.anim.fade_out);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(
                view, view.getWidth() / 2, view.getHeight() / 2, 0, 0);
        ActivityCompat.startActivityForResult(this, intent, AC_IMAGE_BROWSER, options.toBundle());
    }

    private void selectImageFromGrid(ImageItemView view, ImageModel image) {
        if (image != null) {
            if (mDesireImageCount > 1) {
                // multi select mode
                if (mResultList.contains(image.path)) {
                    mResultList.remove(image.path);
                    onImageUnselected(image.path);
                } else {
                    if (isMaxCount()) {
                        return;
                    }
                    mResultList.add(image.path);
                    onImageSelected(image.path);
                }
                mImageAdapter.select(image);
                view.toggleCheck();
            } else {
                // single select mode
                view.toggleCheck();
                ArrayList<String> paths = new ArrayList<>(1);
                paths.add(image.path);
                onSelectDone(paths);
            }
        }
    }

    // use thumbnail when possible
    private String getImageThumbnail(long id) {
        final String thumb_DATA = MediaStore.Images.Thumbnails.DATA;
        final String thumb_IMAGE_ID = MediaStore.Images.Thumbnails.IMAGE_ID;
        String[] projection = {thumb_DATA, thumb_IMAGE_ID};
        String selection =
                thumb_IMAGE_ID + "=" + id + " AND " +
                        MediaStore.Images.Thumbnails.KIND + "=" + MediaStore.Images.Thumbnails.MINI_KIND;
        String thumbPath = null;
        Cursor thumbCursor = null;

        try {
            thumbCursor = getContentResolver().query(
                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, selection, null, null);
            if (thumbCursor != null && thumbCursor.getCount() > 0) {
                thumbCursor.moveToFirst();
                int idx = thumbCursor.getColumnIndex(thumb_DATA);
                thumbPath = thumbCursor.getString(idx);
            }
        } finally {
            if (thumbCursor != null) {
                thumbCursor.close();
            }
        }

        return thumbPath;
    }

    private void showLoading() {
        mLoadingProgress.spin();
        mLoadingProgress.setVisibility(View.VISIBLE);
    }

    private void dismissLoading() {
        mLoadingProgress.stopSpinning();
        mLoadingProgress.setVisibility(View.GONE);
    }

    private static String formatPhotoDate(String path){
        File file = new File(path);
        if (file.exists()) {
            long time = file.lastModified();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            return format.format(new Date(time));
        }
        return "1970-01-01";
    }

    private void onImageSelected(String path) {
        if (!mResultList.contains(path)) {
            mResultList.add(path);
        }
        refreshSubmitButton();
    }

    private void onImageUnselected(String path) {
        if (mResultList.contains(path)) {
            mResultList.remove(path);
        }
        refreshSubmitButton();
    }

    private void onRefreshImageSelected(List<String> paths) {
        mResultList.clear();
        if (paths != null && paths.size() != 0) {
            mResultList.addAll(paths);
        }
        refreshSubmitButton();
    }

    private void onCameraShot(String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            if (!mResultList.contains(imagePath)) {
                mResultList.add(imagePath);
            }
            onSelectDone(mResultList);
        }
    }

    private void onSelectDone(ArrayList<String> paths) {
        if (paths != null && paths.size() != 0) {
            mResultList = paths;
            Intent data = new Intent();
            data.putStringArrayListExtra(EXTRA_RESULT, paths);
            setResult(RESULT_OK, data);
            finish();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void refreshSubmitButton() {
        // only refresh submit button on multi-select mode
        if (mDesireImageCount > 1) {
            if (mResultList == null || mResultList.size() == 0) {
                mSubmitButton.setText(R.string.mis_finish_btn);
                mSubmitButton.setEnabled(false);
            } else {
                String txt = getString(
                        R.string.mis_finish_btn_with_amount, mResultList.size(), mDesireImageCount);
                mSubmitButton.setText(txt);
                mSubmitButton.setEnabled(true);
            }
        }
    }


    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID };

        private File mPathFile;

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String path = args == null ? null : args.getString("path");
            mPathFile = path == null ? null : new File(path);

            if (id == LOADER_ALL) {
                return new CursorLoader(ImageSelectActivity.this,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null,
                        IMAGE_PROJECTION[2] + " DESC");
            } else if (id == LOADER_CATEGORY) {
                return new CursorLoader(ImageSelectActivity.this,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[0]+ " like '%" + path + "%'", null,
                        IMAGE_PROJECTION[2] + " DESC");
            }

            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
            if (data == null || data.getCount() <= 0) {
                return;
            }

            showLoading();
            GlobalThreadManager.runInThreadPool(new Runnable() {
                @Override
                public void run() {
                    final List<ImageModel> images = new ArrayList<>();
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        if (TextUtils.isEmpty(path)) {
                            continue;
                        }
                        if (mPathFile != null && !isSubFile(path)) {
                            // we are loading image files of a dir (not all image files),
                            // if path is not first level sub file of the given path, ignore.
                            continue;
                        }

                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        long id = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                        ImageModel image = new ImageModel(path, name, dateTime, getImageThumbnail(id));
                        images.add(image);

                        if (!mIsFolderGenerated) {
                            File imageFile = new File(path);
                            File folderFile = imageFile.getParentFile();
                            FolderModel folder = new FolderModel();
                            folder.name = folderFile.getName();
                            folder.cover = image;
                            folder.path = folderFile.getAbsolutePath();

                            if (!mResultFolder.contains(folder)) {
                                List<ImageModel> imageList = new ArrayList<>();
                                imageList.add(image);
                                folder.images = imageList;
                                mResultFolder.add(folder);
                            } else {
                                FolderModel f = mResultFolder.get(mResultFolder.indexOf(folder));
                                f.images.add(image);
                            }
                        }

                    } while (!data.isClosed() && data.moveToNext());

                    mIsFolderGenerated = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoading();
                            mImageAdapter.setData(images);
                            if (mResultList != null && mResultList.size() > 0) {
                                mImageAdapter.setDefaultSelected(mResultList);
                            }
                            mFolderListView.setData(mResultFolder);
                        }
                    });
                }
            });
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }

        private boolean isSubFile(String filePath) {
            File file = new File(filePath);
            return file.getParentFile().equals(mPathFile);
        }
    };


    private Runnable mHideTimeLineRunnable = new Runnable() {
        @Override
        public void run() {
            if (mAnimTimeLineHide == null) {
                mAnimTimeLineHide = new AlphaAnimation(0.7f, 0f);
                mAnimTimeLineHide.setDuration(300);
            }
            mTimeLineText.setVisibility(View.GONE);
            mTimeLineText.startAnimation(mAnimTimeLineHide);
        }
    };

}
