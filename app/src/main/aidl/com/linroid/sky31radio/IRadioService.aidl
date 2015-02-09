// IRadioService.aidl
package com.linroid.sky31radio;
import com.linroid.sky31radio.model.Program;
interface IRadioService {
    void play();
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
