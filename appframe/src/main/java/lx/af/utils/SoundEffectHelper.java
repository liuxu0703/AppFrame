package lx.af.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lx.af.manager.GlobalThreadManager;

/**
 * author: liuxu
 * date: 15-8-18.
 * helper class to play hint sound effect. based on {@link SoundPool}
 */
public class SoundEffectHelper {

    private static final String DEFAULT_KEY = "sound_effect_helper_default_key";

    private SoundPool mSoundPool;
    private Context mContext;
    private HashMap<String, Integer> mSounds = new HashMap<>(5);
    private ArrayList<Data> mList;
    private volatile boolean mIsInit = false;

    public static SoundEffectHelper newInstance(Context context) {
        return new SoundEffectHelper(context);
    }

    public SoundEffectHelper(final Context context) {
        mSoundPool = new SoundPool(3, AudioManager.STREAM_SYSTEM, 5);
        mContext = context;
    }

    /**
     * add an audio file from asset as sound effect.
     * the audio should not be too long.
     */
    public SoundEffectHelper addSoundFromAsset(String assetPath) {
        return addSoundFromAsset(DEFAULT_KEY, assetPath);
    }

    /**
     * add an audio file from resource as sound effect.
     * the audio should not be too long.
     */
    public SoundEffectHelper addSoundFromRaw(int rawResId) {
        return addSoundFromRaw(DEFAULT_KEY, rawResId);
    }

    /**
     * add an audio file from asset as sound effect.
     * the audio should not be too long.
     * @param key to be later used to play the sound.
     */
    public SoundEffectHelper addSoundFromAsset(String key, String assetPath) {
        Data data = new Data();
        data.key = key;
        data.assetPath = assetPath;
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.add(data);
        return this;
    }

    /**
     * add an audio file from resource as sound effect.
     * the audio should not be too long.
     * @param key to be later used to play the sound.
     */
    public SoundEffectHelper addSoundFromRaw(String key, int rawResId) {
        Data data = new Data();
        data.key = key;
        data.rawResId = rawResId;
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.add(data);
        return this;
    }

    /**
     * construct {@link SoundPool} instance will all the sound added.
     * this method must be called before {@link #play()} is called.
     */
    public SoundEffectHelper init() {
        GlobalThreadManager.runInThreadPool(new Runnable() {
            @Override
            public void run() {
                try {
                    for (Data d : mList) {
                        if (d.assetPath != null) {
                            int id = mSoundPool.load(mContext.getAssets().openFd(d.assetPath), 1);
                            mSounds.put(d.key, id);
                        } else if (d.rawResId != 0) {
                            int id = mSoundPool.load(mContext, d.rawResId, 1);
                            mSounds.put(d.key, id);
                        }
                    }
                } catch (IOException e) {
                    Log.e("liuxu", "SoundEffectHelper, fail to load sound", e);
                }
                mIsInit = true;
            }
        });
        return this;
    }

    /**
     * play sound effect added without a key. AKA, the default sound.
     * must call {@link #init()} first before calling this method.
     */
    public void play() {
        play(DEFAULT_KEY);
    }

    /**
     * play sound effect associated with the given key.
     * must call {@link #init()} first before calling this method.
     */
    public void play(String key) {
        if (!mIsInit) {
            return;
        }
        try {
            mSoundPool.play(mSounds.get(key), 1f, 1f, 0, 0, 1f);
        } catch (Exception e) {
            Log.e("liuxu", "SoundEffectHelper, fail to play sound", e);
        }
    }

    /**
     * release resources
     */
    public void release() {
        mSoundPool.release();
        mSounds.clear();
    }

    private static class Data {
        String key;
        String assetPath;
        int rawResId;
    }

}
