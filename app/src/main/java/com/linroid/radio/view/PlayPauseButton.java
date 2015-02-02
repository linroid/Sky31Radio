package com.linroid.radio.view;

/**
 * Created by linroid on 1/14/15.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.linroid.radio.R;

/**
 * A custom {@link ImageButton} that represents the "play and pause" button.
 *
 * @author Andrew Neal (andrewdneal@gmail.com)
 */
public class PlayPauseButton extends ImageButton
        implements View.OnClickListener, View.OnLongClickListener {

    Drawable pauseDrawable;
    Drawable playDrawable;

    private boolean isPlaying;
    OnStateChangedListener listener;
    /**
     * @param context The {@link Context} to use
     * @param attrs The attributes of the XML tag that is inflating the view.
     */
    public PlayPauseButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PlayPauseButton);
        pauseDrawable = ta.getDrawable(R.styleable.PlayPauseButton_pauseSrc);
        playDrawable = ta.getDrawable(R.styleable.PlayPauseButton_playSrc);
        ta.recycle();
        setPlaying(false);
        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(final View v) {
        if(listener!=null){
            if(isPlaying){
                listener.onPause();
            }else{
                listener.onPlay();
            }
        }
        setPlaying(!isPlaying);

    }

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        this.listener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onLongClick(final View view) {
        if (TextUtils.isEmpty(view.getContentDescription())) {
            return false;
        } else {
//            ApolloUtils.showCheatSheet(view);
            return true;
        }
    }
    public void setPlaying (boolean playing){
        isPlaying = playing;
        if(isPlaying){
            setImageDrawable(playDrawable);
//            setImageDrawable(getResources().getDrawable(R.drawable.btn_playback_pause));
        }else{
            setImageDrawable(pauseDrawable);
//            setImageDrawable(getResources().getDrawable(R.drawable.btn_playback_play));
        }
    }

    public void updateState() {
        setPlaying(isPlaying);
    }
    public static interface OnStateChangedListener{
        void onPlay();
        void onPause();
    }
}
