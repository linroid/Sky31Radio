package com.linroid.sky31radio.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.audiofx.Visualizer;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.linroid.sky31radio.R;

import timber.log.Timber;

/**
 * Created by linroid on 1/22/15.
 */
public class EqualizerView extends View{
    Visualizer visualizer;

    Paint wavePaint;
    float waveDividerWidth;
    float waveWidth;

    byte[] waveformBytes;
    byte[] fftBytes;
    float[] mWavePoints;
    float[] mFFTPoints;

    RectF mWaveRect;
    Rect mFFTRect;
    public EqualizerView(Context context) {
        super(context);
        init(context, null);
    }

    public EqualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EqualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EqualizerView);


        Resources res = getResources();
        waveWidth = ta.getDimensionPixelOffset(R.styleable.EqualizerView_waveWidth, TypedValue.complexToDimensionPixelSize(32, res.getDisplayMetrics()));
        waveDividerWidth = ta.getDimensionPixelSize(R.styleable.EqualizerView_waveDividerWidth, TypedValue.complexToDimensionPixelSize(32, res.getDisplayMetrics()));
        int waveColor = ta.getColor(R.styleable.EqualizerView_waveColor, Color.parseColor("#99FFFFFF"));

        wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint.setStrokeWidth(waveWidth);
        wavePaint.setAntiAlias(true);
        wavePaint.setColor(waveColor);
        wavePaint.setStrokeWidth(waveWidth);
        mWaveRect = new RectF();
        mFFTRect = new Rect();
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
        visualizer.setDataCaptureListener(dataCaptureListener, Visualizer.getMaxCaptureRate() / 2, true, true);
        visualizer.setEnabled(true);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(visualizer!=null){
            visualizer.setEnabled(enabled);
        }
    }

    public void release(){
        if(visualizer != null){
            visualizer.setEnabled(false);
            visualizer.release();
            visualizer = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawWave(canvas);
        drawFFT(canvas);
    }

    private void drawFFT(Canvas canvas) {
        if (waveformBytes == null) {
            return;
        }
        if (mFFTPoints == null || fftBytes.length < fftBytes.length * 4) {
            mFFTPoints = new float[fftBytes.length * 4];
        }

        int mDivisions = (int) waveDividerWidth;
        mWaveRect.set(0, 0, getWidth(), getHeight());
        for (int i = 0; i < fftBytes.length / mDivisions; i++) {
            mFFTPoints[i * 4] = i * 4 * mDivisions;
            mFFTPoints[i * 4 + 2] = i * 4 * mDivisions;
            byte rfk = fftBytes[mDivisions * i];
            byte ifk = fftBytes[mDivisions * i + 1];
            float magnitude = (rfk * rfk + ifk * ifk);
            int dbValue = (int) (10 * Math.log10(magnitude));

            mFFTPoints[i * 4 + 1] = mWaveRect.height();
            mFFTPoints[i * 4 + 3] = mWaveRect.height() - (dbValue * 8 - 10);
        }

        canvas.drawLines(mFFTPoints, wavePaint);
    }

    int waveCount;
    int perWaveBytesCount;
    int perWaveBytesSum;

    private void drawWave(Canvas canvas) {
        if (waveformBytes == null) {
            return;
        }
        if (mWavePoints == null || mWavePoints.length < waveformBytes.length * 4) {
            mWavePoints = new float[waveformBytes.length * 4];
        }

        mWaveRect.set(0, 0, getWidth(), getHeight());


        waveCount = (int) (mWaveRect.width() / (waveDividerWidth+waveWidth));
        perWaveBytesCount = (mWavePoints.length/ waveCount);
//        if(waveformBytes.length < waveCount){
//            return;
//        }
        for (int i = 0; i < waveCount; i++) {
            //x0
            mWavePoints[i*4] = (waveDividerWidth+waveWidth) * i;
            //y0
            mWavePoints[i*4+1] = mWaveRect.bottom;
            //x1
            mWavePoints[i*4+2] = mWavePoints[i*4];
            //y1
            perWaveBytesSum = 0;
            for(int j= perWaveBytesCount * i; j< perWaveBytesCount *(i+1)&&j<waveformBytes.length ; j++){
                perWaveBytesSum += waveformBytes[j];
            }
            mWavePoints[i*4+3] = mWaveRect.bottom - ((perWaveBytesSum/(perWaveBytesCount*1.0f)) / 256f) * mWaveRect.height();
//            Timber.d("[%f,%f] [%f,%f]",mWavePoints[i*4], mWavePoints[i*4+1], mWavePoints[i*4+2],mWavePoints[i*4+3]);
        }
        canvas.drawLines(mWavePoints, wavePaint);
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
        fftBytes = data;
    }

    private void updateVisualizerWave(byte[] data) {
        waveformBytes = data;
        ViewCompat.postInvalidateOnAnimation(this);
    }
}
