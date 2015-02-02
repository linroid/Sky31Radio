package com.linroid.radio.view;

/**
 * Created by linroid on 1/14/15.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.linroid.radio.R;
import com.linroid.radio.utils.RadioUtils;

/**
 * This class handles the playpause button as well as the circular progress bar
 * it self-updates the progress bar but the containing activity/fragment
 * needs to add code to pause/resume this button to prevent unnecessary
 * updates while the activity/fragment is not visible
 */
public class PlayPauseProgressButton extends FrameLayout {
    private static final long UPDATE_FREQUENCY_MS = 500;
    private ProgressBar mProgressBar;
    private PlayPauseButton mPlayPauseButton;
    private Runnable mUpdateProgress;
    private boolean mPaused;

    public PlayPauseProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        // set enabled to false as default so that calling enableAndShow will execute
        setEnabled(false);

        // set paused to false since we shouldn't be typically created while not visible
        mPaused = false;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mPlayPauseButton = (PlayPauseButton)findViewById(R.id.action_button_play);
        mProgressBar = (ProgressBar)findViewById(R.id.circularProgressBar);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Make the play pause button size dependent on the container size
        int horizontalPadding = getMeasuredWidth() / 4;
        int verticalPadding = getMeasuredHeight() / 4;
        mPlayPauseButton.setPadding(
                horizontalPadding, horizontalPadding,
                verticalPadding, verticalPadding);

        // rotate the progress bar 90 degrees counter clockwise so that the
        // starting position is at the top
        mProgressBar.setPivotX(mProgressBar.getMeasuredWidth() / 2);
        mProgressBar.setPivotY(mProgressBar.getMeasuredHeight() / 2);
        mProgressBar.setRotation(-90);
    }

    /**
     * Enable and shows the container
     */
    public void enableAndShow() {
        // enable
        setEnabled(true);

        // make our view visible
        setVisibility(VISIBLE);
    }

    /**
     * Disables and sets the visibility to gone for the container
     */
    public void disableAndHide() {
        // disable
        setEnabled(false);

        // hide our view
        setVisibility(GONE);
    }

    @Override
    public void setEnabled(boolean enabled) {
        // if the enabled state isn't changed, quit
        if (enabled == isEnabled()) return;

        super.setEnabled(enabled);

        // signal our state has changed
        onStateChanged();
    }

    /**
     * Pauses the progress bar periodic update logic
     */
    public void pause() {
        if (!mPaused) {
            mPaused = true;

            // signal our state has changed
            onStateChanged();
        }
    }

    /**
     * Resumes the progress bar periodic update logic
     */
    public void resume() {
        if (mPaused) {
            mPaused = false;

            // signal our state has changed
            onStateChanged();
        }
    }

    /**
     * @return play pause button
     */
    public PlayPauseButton getPlayPauseButton() {
        return mPlayPauseButton;
    }

    /**
     * Signaled if the state has changed (either the enabled or paused flag)
     * When the state changes, we either kick off the updates or remove them based on those flags
     */
    private void onStateChanged() {
        // if we are enabled and not paused
        if (isEnabled() && !mPaused) {
            // update the state of the progress bar and play/pause button
            updateState();

            // kick off update states
            postUpdate();
        } else {
            // otherwise remove our update
            removeUpdate();
        }
    }

    /**
     * Updates the state of the progress bar and the play pause button
     */
    private void updateState() {
        final long duration = RadioUtils.duration();

        if (duration > 0) {
            final long pos = RadioUtils.position();

            int progress = (int) (mProgressBar.getMax() * pos / duration);
            mProgressBar.setProgress(progress);
        } else {
            // this is when there are no tracks loaded or some kind of error condition
            mProgressBar.setProgress(0);
        }

        mPlayPauseButton.updateState();
    }

    /**
     * Creates and posts the update runnable to the handler
     */
    private void postUpdate() {
        if (mUpdateProgress == null) {
            mUpdateProgress = new Runnable() {
                @Override
                public void run() {
                    updateState();
                    postDelayed(mUpdateProgress, UPDATE_FREQUENCY_MS);
                }
            };
        }

        // remove any existing callbacks
        removeCallbacks(mUpdateProgress);

        // post ourselves as a delayed
        postDelayed(mUpdateProgress, UPDATE_FREQUENCY_MS);
    }

    /**
     * Removes the runnable from the handler
     */
    private void removeUpdate() {
        if (mUpdateProgress != null) {
            removeCallbacks(mUpdateProgress);
        }
    }
}

