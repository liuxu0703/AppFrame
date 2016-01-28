package lx.af.activity.ImageSelector;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lx.af.R;
import lx.af.manager.GlobalThreadManager;
import lx.af.view.ProgressWheel;

/**
 * import and modified by liuxu on 2015.04.22 (heavily modified ...)
 *
 * Created by Nereo on 2015/4/7. link:
 * https://github.com/lovetuzitong/MultiImageSelector
 */
public class ImageSelectFragment extends Fragment implements
        ImageGridAdapter.OnItemClickListener {

    private static final String TAG = "MultiImageSelector";

    /** select number limit */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /** enable/disable taken image by camera. default is true */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /** pre-selected image items */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_result";

    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;

    // activity request code
    private static final int AC_REQUEST_CAMERA = 100;
    private static final int AC_IMAGE_BROWSER = 101;

    // delay time for time line text to hide
    private static final int TIME_LINE_HIDE_DELAY = 1500;

    private ArrayList<String> mResultList = new ArrayList<>();
    private ArrayList<FolderModel> mResultFolder = new ArrayList<>();

    private ImageGridView mGridView;
    private FolderListView mFolderListView;
    private TextView mTimeLineText;
    private TextView mCategoryText;
    private ProgressWheel mLoadingProgress;

    private ImageGridAdapter mImageAdapter;
    private Callback mCallback;
    private File mCameraFile;
    private Animation mAnimTimeLineHide;

    private int mDesireImageCount;
    private boolean mIsShowCamera;
    private boolean mIsFolderGenerated = false;

    private Handler mUIHandler = new Handler();

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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    "The Activity must implement ImageSelectFragment.Callback interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mis_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDesireImageCount = getArguments().getInt(EXTRA_SELECT_COUNT);
        mIsShowCamera = getArguments().getBoolean(EXTRA_SHOW_CAMERA, true);

        if (mDesireImageCount > 1) {
            ArrayList<String> tmp = getArguments().getStringArrayList(EXTRA_DEFAULT_SELECTED_LIST);
            if (tmp != null && tmp.size() > 0) {
                mResultList = tmp;
            }
        }

        mFolderListView = (FolderListView) view.findViewById(R.id.mis_fragment_folder_list);
        initFolderList();

        mTimeLineText = (TextView) view.findViewById(R.id.mis_fragment_time_line);
        hideTimeLineDelayed();

        mCategoryText = (TextView) view.findViewById(R.id.mis_fragment_btn_show_folder);
        mCategoryText.setText(R.string.mis_folder_all);
        mCategoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFolderListView.switchShowHide();
            }
        });

        mGridView = (ImageGridView) view.findViewById(R.id.mis_fragment_img_grid_view);
        mLoadingProgress = (ProgressWheel) view.findViewById(R.id.mis_fragment_loading_progress);
        initImageGridView();
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

    @Override
    public void onItemCameraClicked() {
        if (!isMaxCount()) {
            startCamera();
        }
    }

    @Override
    public void onItemImageClicked(ImageItemView view, ImageModel data) {
        startImageBrowser(Uri.parse("file://" + data.path).toString());
    }

    @Override
    public void onItemCheckClicked(ImageItemView view, ImageModel data) {
        selectImageFromGrid(view, data);
    }

    private void initFolderList() {
        mFolderListView.hide();

        mFolderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    getActivity().getSupportLoaderManager().restartLoader(
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
                        getActivity().getSupportLoaderManager().restartLoader(
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == AC_REQUEST_CAMERA) {
            if (mCameraFile != null) {
                mCallback.onCameraShot(mCameraFile);
            }
        } else if (requestCode == AC_IMAGE_BROWSER) {
            if (data != null) {
                ArrayList<String> tmp = data.getStringArrayListExtra(
                        ImageSelectBrowser.EXTRA_RESULT);
                if (tmp != null && tmp.size() > 0) {
                    if (mDesireImageCount == 1) {
                        // single select mode
                        mCallback.onSelectDone(tmp);
                    } else {
                        // multi select mode
                        boolean done = data.getBooleanExtra(
                                ImageSelectBrowser.EXTRA_RESULT_DONE, false);
                        if (done) {
                            // done select, set result and finish
                            mCallback.onSelectDone(tmp);
                            return;
                        }
                        mResultList = tmp;
                        mImageAdapter.setDefaultSelected(mResultList);
                        mCallback.onRefreshImageSelected(mResultList);
                    }
                }
            }
        }
    }

    private void hideTimeLineDelayed() {
        mUIHandler.removeCallbacks(mHideTimeLineRunnable);
        mUIHandler.postDelayed(mHideTimeLineRunnable, TIME_LINE_HIDE_DELAY);
    }

    private boolean isMaxCount() {
        if (mDesireImageCount == mResultList.size()) {
            Toast.makeText(getActivity(), R.string.mis_toast_amount_limit, Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    private void startCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (!dcim.exists() && !dcim.mkdir()) {
                Toast.makeText(getActivity(),
                        R.string.toast_path_create_dir_fail, Toast.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
            mCameraFile = new File(dcim, df.format(new Date()) + ".jpg");
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraFile));
            startActivityForResult(cameraIntent, AC_REQUEST_CAMERA);
        } else {
            Toast.makeText(getActivity(), R.string.mis_toast_no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    private void startImageBrowser(String currentImageUri) {
        Intent intent = new Intent(getActivity(), ImageSelectBrowser.class);
        intent.putExtra(ImageSelectBrowser.EXTRA_IMAGE_URI_LIST, mImageAdapter.getImageUriList());
        intent.putExtra(ImageSelectBrowser.EXTRA_CURRENT_IMAGE_URI, currentImageUri);
        intent.putExtra(ImageSelectBrowser.EXTRA_SELECT_COUNT, mDesireImageCount);
        intent.putExtra(ImageSelectBrowser.EXTRA_SELECTED_LIST, mImageAdapter.getSelectedImageUriList());
        startActivityForResult(intent, AC_IMAGE_BROWSER);
    }

    private void selectImageFromGrid(ImageItemView view, ImageModel image) {
        if (image != null) {
            if (mDesireImageCount > 1) {
                // multi select mode
                if (mResultList.contains(image.path)) {
                    mResultList.remove(image.path);
                    mCallback.onImageUnselected(image.path);
                } else {
                    if (isMaxCount()) {
                        return;
                    }
                    mResultList.add(image.path);
                    mCallback.onImageSelected(image.path);
                }
                mImageAdapter.select(image);
                view.toggleCheck();
            } else {
                // single select mode
                view.toggleCheck();
                ArrayList<String> paths = new ArrayList<>(1);
                paths.add(image.path);
                mCallback.onSelectDone(paths);
            }
        }
    }

    // use thumbnail when possible
    private String getImageThumbnail(long id) {
        if (getActivity() == null) {
            return null;
        }
        final String thumb_DATA = MediaStore.Images.Thumbnails.DATA;
        final String thumb_IMAGE_ID = MediaStore.Images.Thumbnails.IMAGE_ID;
        String[] projection = {thumb_DATA, thumb_IMAGE_ID};
        String selection =
                thumb_IMAGE_ID + "=" + id + " AND " +
                MediaStore.Images.Thumbnails.KIND + "=" + MediaStore.Images.Thumbnails.MINI_KIND;
        String thumbPath = null;
        Cursor thumbCursor = null;

        try {
            thumbCursor = getActivity().getContentResolver().query(
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
                return new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null,
                        IMAGE_PROJECTION[2] + " DESC");
            } else if (id == LOADER_CATEGORY) {
                return new CursorLoader(getActivity(),
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
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
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


    private static String formatPhotoDate(String path){
        File file = new File(path);
        if (file.exists()) {
            long time = file.lastModified();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            return format.format(new Date(time));
        }
        return "1970-01-01";
    }


    /**
     * activity callbacks
     */
    public interface Callback{
        void onImageSelected(String path);
        void onImageUnselected(String path);
        void onRefreshImageSelected(List<String> paths);
        void onCameraShot(File imageFile);
        void onSelectDone(ArrayList<String> paths);
    }
}
