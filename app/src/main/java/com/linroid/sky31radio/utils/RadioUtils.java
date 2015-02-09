package com.linroid.sky31radio.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.linroid.sky31radio.model.Program;
import com.linroid.sky31radio.service.RadioPlaybackService;

import java.util.List;

/**
 * Created by linroid on 1/14/15.
 */
public class RadioUtils {
    public static void playOrPause() {

    }

    public static boolean isPlaying() {
        return true;
    }

    public static long duration() {
        return 0;
    }

    public static long position() {
        return 0;
    }

    public static void sendPlayList(Context context, List<Program> programList, int selectedPosition){
        Bundle data = new Bundle();
        data.putParcelableArrayList(RadioPlaybackService.KEY_PROGRAM_LIST, (java.util.ArrayList<? extends android.os.Parcelable>) programList);
        data.putInt(RadioPlaybackService.KEY_PROGRAM_POSITION, selectedPosition);

        Intent intent = new Intent(context, RadioPlaybackService.class);
        intent.putExtras(data);
        intent.setAction(RadioPlaybackService.ACTION_SELECT_PROGRAM_LIST);
        context.startService(intent);
    }

    public static void play(Context ctx) {
        sendActionCommand(ctx, RadioPlaybackService.ACTION_PLAY);
    }

    public static void pause(Context ctx) {
        sendActionCommand(ctx, RadioPlaybackService.ACTION_PAUSE);
    }

    public static void next(Context ctx) {
        sendActionCommand(ctx, RadioPlaybackService.ACTION_NEXT);
    }

    public static void stop(Context ctx) {
        sendActionCommand(ctx, RadioPlaybackService.ACTION_STOP);
    }

    public static void previous(Context ctx) {
        sendActionCommand(ctx, RadioPlaybackService.ACTION_PREVIOUS);
    }

    public static void seekToPosition(Context ctx, int position) {
        Bundle extras = new Bundle();
        extras.putInt(RadioPlaybackService.EXTRA_POSITION, position);
        sendActionCommand(ctx, RadioPlaybackService.ACTION_SEEK_TO_POSITION, extras);
    }
    private static void sendActionCommand(Context ctx, String action) {
        sendActionCommand(ctx, action, null);
    }

    private static void sendActionCommand(Context ctx, String action, Bundle extras){
        Intent intent = new Intent();
        intent.setAction(action);
        if(extras!=null){
            intent.putExtras(extras);
        }
        ctx.sendBroadcast(intent);
    }

}
