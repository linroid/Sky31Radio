// IRadioService.aidl
package com.linroid.radio;
import com.linroid.radio.model.Program;
interface IRadioService {
    void start();
    void pause();
    void stop();
    void seekToPercent(int percent);
    void seekToPosition(int position);
    void next();
    void previous();
    long getDuration();
    long getPosition();
    int getPlayerSessionId();
    boolean isPlaying();
    Program getPlayingProgram();
}
