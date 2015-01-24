package com.linroid.radio.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.view.View;

import com.linroid.radio.R;

import timber.log.Timber;

/**
 * Created by linroid on 1/22/15.
 */
public class VisualizerView extends View{
    Visualizer visualizer;
    Equalizer equalizer;

    Paint wavePaint;
    byte[] waveformBytes;
    byte[] fftBytes;
    float[] mPoints;

    Rect mRect;

    public VisualizerView(Context context) {
        super(context);
        init(context, null);
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public VisualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VisualizerView);
        int waveColor = ta.getColor(R.styleable.VisualizerView_waveColor, Color.parseColor("#99bdbdbd"));
        wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        wavePaint.setStrokeWidth(10);
        wavePaint.setColor(waveColor);
        mRect = new Rect();
    }
    public void setWaveColor(int color){
        wavePaint.setColor(color);
        invalidate();
    }

    public void linkPlayer(int sessionId){
        Timber.d("linkPlayer, session id: %d", sessionId);
        visualizer = new Visualizer(sessionId);
        if(!visualizer.getEnabled()){
            visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        }
        visualizer.setEnabled(true);
        visualizer.setDataCaptureListener(dataCaptureListener, Visualizer.getMaxCaptureRate() / 2, true, true);
        equalizer = new Equalizer(0, sessionId);
        equalizer.setEnabled(true);
    }
    public void release(){
        if(visualizer != null){
            visualizer.setEnabled(false);
            visualizer.release();
            equalizer.release();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (waveformBytes == null) {
            return;
        }
        if (mPoints == null || mPoints.length < waveformBytes.length * 4) {
            mPoints = new float[waveformBytes.length * 4];
        }

        mRect.set(0, 0, getWidth(), getHeight());
        for (int i = 0; i < waveformBytes.length - 1; i++) {
            mPoints[i * 4] = mRect.width() * i / (waveformBytes.length - 1);
            mPoints[i * 4 + 1] = mRect.height() / 2
                    + ((byte) (waveformBytes[i] + 128)) * (mRect.height() / 2) / 128;
            mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (waveformBytes.length - 1);
            mPoints[i * 4 + 3] = mRect.height() / 2
                    + ((byte) (waveformBytes[i + 1] + 128)) * (mRect.height() / 2) / 128;
        }

        canvas.drawLines(mPoints, wavePaint);
    }

    Visualizer.OnDataCaptureListener dataCaptureListener = new Visualizer.OnDataCaptureListener() {
        @Override
        public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
            updateVisualizerWave(waveform);
        }
        @Override
        public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
            updateVisualizerFFT(fft);
        }
    };

    private void updateVisualizerFFT(byte[] data) {
        Timber.i("updateVisualizerFFT(%d)", data.length);
        fftBytes = data;
        invalidate();
    }

    private void updateVisualizerWave(byte[] data) {
        Timber.i("updateVisualizerWave(%d)", data.length);
        waveformBytes = data;
        invalidate();
    }
}
