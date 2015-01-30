package com.linroid.radio.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.linroid.radio.IRadioService;
import com.linroid.radio.R;
import com.linroid.radio.data.ApiService;
import com.linroid.radio.model.Program;
import com.linroid.radio.service.RadioPlaybackService;
import com.linroid.radio.ui.base.InjectableFragment;
import com.linroid.radio.utils.BlurTransformation;
import com.linroid.radio.utils.RadioUtils;
import com.linroid.radio.widgets.PlayPauseButton;
import com.linroid.radio.widgets.PlayPauseProgressButton;
import com.linroid.radio.widgets.SlidingUpPanelLayout;
import com.linroid.radio.widgets.VisualizerView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.michaelevans.colorart.library.ColorArt;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * Created by linroid on 1/15/15.
 */
public class PlayerFragment extends InjectableFragment implements ServiceConnection {
    @InjectView(R.id.container)
    ViewGroup playerRootView;
    @InjectView(R.id.player_thumbnail)
    ImageView playerThumbnailIV;
    @InjectView(R.id.player_program_title)
    TextView playerProgramNameTV;
    @InjectView(R.id.player_author)
    TextView playerAuthorTV;
    @InjectView(R.id.playPauseProgressButton)
    PlayPauseProgressButton playPauseProgressButton;
    @InjectView(R.id.action_button_play)
    PlayPauseButton actionPlayPauseButton;
    @InjectView(R.id.progressBarBackground)
    ProgressBar progressBarBackground;
    @InjectView(R.id.circularProgressBar)
    ProgressBar circularProgressBar;

    @InjectView(R.id.btn_fast_rewind)
    ImageButton fastRewindButton;
    @InjectView(R.id.btn_fast_forward)
    ImageButton fastForwardButton;
    @InjectView(R.id.btn_play_pause)
    PlayPauseButton playPauseButton;
    @InjectView(R.id.center_thumbnail)
    ImageView centerThumbnailIV;

    @InjectView(R.id.progress_seekbar)
    DiscreteSeekBar seekbar;
    @InjectView(R.id.elapsed_duration)
    TextView elapsedDurationTV;
    @InjectView(R.id.program_played_count)
    TextView playedCountTV;

    @InjectView(R.id.article_visualizer_switcher)
    ViewSwitcher viewSwitcher;
    @InjectView(R.id.visualizer)
    VisualizerView visualizerView;
    @InjectView(R.id.article)
    TextView articleTV;

    SlidingUpPanelLayout slidingUpPanelLayout;

    @Inject
    Picasso picasso;
    @Inject
    ApiService apiService;
    Program program;
    private int statusColor;
    private int homeStatusColor;

    SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
    RadioReceiver receiver;
    IRadioService service;

    public static final int MSG_UPDATE = 0x1;
    private static final int MSG_SEEK = 0x2;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UPDATE:
                    updatePlayingStatus();
                    try {
                        if(!service.isPlaying()){
                            Timber.i("player not playing, stop update");
                            return;
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    nextUpdate();
                    break;
                case MSG_SEEK:
                    Timber.i("msg_seek, position:%d", msg.arg1);
                    RadioUtils.seekToPosition(seekbar.getContext(), msg.arg1);
                    break;
            }
        }
    };

    private void updatePlayingStatus() {
        try {
            if(service==null){
                return;
            }
            boolean isPlaying = service.isPlaying();
            if(isPlaying){
                long position = service.getPosition();
                long duration = service.getDuration();
                int percent = (int) (position*100 / duration);

                circularProgressBar.setProgress(percent);
                seekbar.setMax((int) duration);
                seekbar.setMin(0);
                seekbar.setProgress((int) position);

                String elapsedDurationText = getResources().getString(
                        R.string.tpl_elapsed_duration,
                        dateFormat.format(new Date(position)),
                        dateFormat.format(new Date(duration))
                );
                SpannableString ss = new SpannableString(elapsedDurationText);
                ss.setSpan(new AbsoluteSizeSpan(12, true), 0, 5, 0);
                elapsedDurationTV.setText(ss);
            }
            actionPlayPauseButton.setPlaying(isPlaying);
            playPauseButton.setPlaying(isPlaying);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private void updatePlayingProgram(final Program playingProgram){
        this.program = playingProgram;
        String playedCountText = getResources().getString(R.string.tpl_played_count, program.getTotalPlay());

        playedCountTV.setText(playedCountText);
        playerAuthorTV.setText(program.getAuthor());
        playerProgramNameTV.setText(program.getTitle());
        articleTV.setText(R.string.loading_article);
//        picasso.load(program.getThumbnail()).into(playerThumbnailIV);
//        picasso.load(program.getThumbnail()).into(centerThumbnailIV);
        picasso.load(program.getThumbnail())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        playerThumbnailIV.setImageBitmap(bitmap);
                        centerThumbnailIV.setImageBitmap(bitmap);
                        Timber.d("onBitmapLoaded:%s(%s)", from.name(), bitmap.toString());
                        ColorArt colorArt = new ColorArt(bitmap);
//                        articleTV.setTextColor(colorArt.getDetailColor());
                        statusColor = colorArt.getBackgroundColor();
                        visualizerView.setWaveColor(colorArt.getDetailColor());
                        if(slidingUpPanelLayout.isPanelExpanded()){
                            setStatusColor(statusColor);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Timber.e("onBitmapFailed");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        Timber.e("onPrepareLoad");
                    }
                });
//        DisplayMetrics dm  = getResources().getDisplayMetrics();
        picasso.load(program.getCover())
                .transform(new BlurTransformation(getActivity(), program.getCover()))
//                .centerCrop()
//                .resize(dm.widthPixels, dm.heightPixels)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Timber.d("onBitmapLoaded: %s (%s)", from.name(), bitmap.toString());
                        playerRootView.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Timber.e("onBitmapFailed");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        Timber.e("onPrepareLoad");
                    }
                });
        apiService.programDetail(program.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<Program>() {
                        @Override
                        public void call(Program program) {
                            if (TextUtils.isEmpty(program.getArticle())) {
                                articleTV.setText(R.string.empty_article);
                            } else {
                                String newPlayedCountText = getResources().getString(R.string.tpl_played_count, program.getTotalPlay());
                                playedCountTV.setText(newPlayedCountText);
                                Spanned article = Html.fromHtml(program.getArticle());
//                            article.setSpan(new BackgroundColorSpan(0), 0, article.length(), 0);
                                articleTV.setText(article);
                            }

                        }
                    })
                    .subscribe();
    }

    @OnClick(R.id.visualizer)
    public void onVisualizerClick(){
        viewSwitcher.showNext();
    }
    @OnClick(R.id.article)
    public void onArticleClick(){
        viewSwitcher.showPrevious();
    }
    private void nextUpdate() {
        try {
            long position = service.getPosition();
            long delay = 1000 - position%1000;
            handler.sendEmptyMessageDelayed(MSG_UPDATE, delay);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e){
            Timber.e(e, "nextUpdate");
        }
    }

    public PlayerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new RadioReceiver();
        TypedValue tv = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorPrimaryDark, tv, true);

        homeStatusColor = tv.data;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        slidingUpPanelLayout = (SlidingUpPanelLayout) getActivity().findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setPanelSlideListener(mSlidingListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.inject(this, view);
        articleTV.setMovementMethod(ScrollingMovementMethod.getInstance());
        playPauseButton.setOnStateChangedListener(playPauseButtonListener);
        actionPlayPauseButton.setOnStateChangedListener(playPauseButtonListener);
        seekbar.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                return value;
            }

            @Override
            public String transformToString(int value) {
                return dateFormat.format(value);
            }
            @Override
            public boolean useStringTransform() {
                return true;
            }
        });
        seekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(final DiscreteSeekBar seekBar, final int value, boolean fromUser) {
                if(fromUser){
                    Timber.d("onProgressChanged: %d", value);
                    handler.removeMessages(MSG_SEEK);
                    Message msg = handler.obtainMessage(MSG_SEEK, value, 0);
                    handler.sendMessageDelayed(msg, 300);
                }
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent serviceIntent = new Intent(getActivity(), RadioPlaybackService.class);
        getActivity().bindService(serviceIntent, this, 0);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RadioPlaybackService.ACTION_PLAYING_CHANGED);
        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
        getActivity().unbindService(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeMessages(MSG_UPDATE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        this.service = IRadioService.Stub.asInterface(binder);
        Timber.w("onServiceConnected");
        try {
            visualizerView.linkPlayer(service.getPlayerSessionId());
            if(service.isPlaying()){
                updatePlayingProgram(service.getPlayingProgram());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        nextUpdate();
    }
    @OnClick(R.id.btn_fast_forward)
    public void onFastForwardBottonClick(View view){
        RadioUtils.next(getActivity());
    }
    @OnClick(R.id.btn_fast_rewind)
    public void onFastRewindBottonClick(View view){
        RadioUtils.previous(getActivity());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        this.service = null;
        handler.removeMessages(MSG_UPDATE);
        visualizerView.release();
        Timber.w("onServiceDisconnected");
    }
    public class RadioReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.i("receive intent: %s", intent.toString());
            switch (intent.getAction()){
                case RadioPlaybackService.ACTION_PLAYING_CHANGED:{
                    Program newProgram = intent.getParcelableExtra(RadioPlaybackService.KEY_PROGRAM);
                    updatePlayingProgram(newProgram);
                    Timber.i("ACTION_PLAYING_CHANGED");
                    nextUpdate();
                    break;
                }
                default:
                    Timber.w("unknown intent: %s", intent.toString());
            }
        }
    }
    SlidingUpPanelLayout.PanelSlideListener mSlidingListener = new SlidingUpPanelLayout.SimplePanelSlideListener() {
        float previousOffset = 1.f;
        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            if(previousOffset>slideOffset){
                playPauseProgressButton.setVisibility(View.VISIBLE);
            }
            previousOffset = slideOffset;
            playPauseProgressButton.setScaleX(1-slideOffset);
            playPauseProgressButton.setScaleY(1-slideOffset);
        }

        @Override
        public void onPanelCollapsed(View panel) {
            playPauseProgressButton.setVisibility(View.VISIBLE);
            setStatusColor(homeStatusColor);
        }

        @Override
        public void onPanelExpanded(View panel) {
            playPauseProgressButton.setVisibility(View.GONE);
            setStatusColor(statusColor);
        }
    };

    PlayPauseButton.OnStateChangedListener playPauseButtonListener = new PlayPauseButton.OnStateChangedListener(){

        @Override
        public void onPlay() {
            RadioUtils.play(getActivity());
        }

        @Override
        public void onPause() {
            RadioUtils.pause(getActivity());
        }
    };


}
