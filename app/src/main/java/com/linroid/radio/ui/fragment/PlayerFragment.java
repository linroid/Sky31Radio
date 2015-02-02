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
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
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
import com.linroid.radio.ui.HomeActivity;
import com.linroid.radio.ui.base.InjectableFragment;
import com.linroid.radio.utils.BlurTransformation;
import com.linroid.radio.utils.ColorUtils;
import com.linroid.radio.utils.RadioUtils;
import com.linroid.radio.view.EqualizerView;
import com.linroid.radio.view.PlayPauseButton;
import com.linroid.radio.view.PlayPauseProgressButton;
import com.linroid.radio.view.SlidingUpPanelLayout;
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
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Created by linroid on 1/15/15.
 */
public class PlayerFragment extends InjectableFragment implements ServiceConnection {
    @InjectView(R.id.container)
    ViewGroup playerRootView;
    @InjectView(R.id.player_thumbnail)
    ImageView playerThumbnailIV;
    @InjectView(R.id.player_info)
    ViewGroup playerInfoView;
    @InjectView(R.id.player_program_title)
    TextView playerProgramNameTV;
    @InjectView(R.id.player_author)
    TextView playerAuthorTV;
    @InjectView(R.id.playPauseProgressButton)
    PlayPauseProgressButton playPauseProgressButton;
    @InjectView(R.id.action_button_play)
    PlayPauseButton actionPlayPauseButton;
    @InjectView(R.id.circularProgressBar)
    ProgressBar circularProgressBar;

    @InjectView(R.id.action_share)
    ImageButton shareBtn;

    @InjectView(R.id.btn_play_pause)
    PlayPauseButton playPauseButton;
    @InjectView(R.id.center_thumbnail)
    ImageView centerThumbnailIV;

    @InjectView(R.id.progress_seekbar)
    DiscreteSeekBar seekBar;
    @InjectView(R.id.duration_time)
    TextView durationTimeTV;
    @InjectView(R.id.position_time)
    TextView positionTimeTV;
    @InjectView(R.id.program_played_count)
    TextView playedCountTV;

    @InjectView(R.id.article_equalizer_switcher)
    ViewSwitcher viewSwitcher;
    @InjectView(R.id.visualizer)
    EqualizerView equalizerView;
    @InjectView(R.id.article)
    TextView articleTV;
    @InjectView(R.id.anchor_nickname)
    TextView centerAnchorNicknameTV;
    @InjectView(R.id.anchor_avatar)
    ImageView anchorAvatarIV;

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
    Subscription subscription;

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
                    RadioUtils.seekToPosition(seekBar.getContext(), msg.arg1);
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
                if(duration == 0){
                    return;
                }


                int percent = (int) (position*100 / duration);

                circularProgressBar.setProgress(percent);
                seekBar.setMax((int) duration);
                seekBar.setMin(0);
                seekBar.setProgress((int) position);

                durationTimeTV.setText(dateFormat.format(new Date(duration)));
                positionTimeTV.setText(dateFormat.format(new Date(position)));
            }
            showPlayingStatus(isPlaying);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private void updatePlayingProgram(final Program playingProgram){
        this.program = playingProgram;
        slidingUpPanelLayout.setSlidingEnabled(true);
        viewSwitcher.setDisplayedChild(0);
        String playedCountText = getResources().getString(R.string.tpl_played_count, program.getTotalPlay());

        playedCountTV.setText(playedCountText);
        playerAuthorTV.setText(program.getAuthor());
        playerProgramNameTV.setText(program.getTitle());
        articleTV.setText(R.string.loading_article);
        picasso.load(program.getThumbnail())
                .error(R.drawable.ic_launcher_square)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        playerThumbnailIV.setImageBitmap(bitmap);
                        ColorArt colorArt = new ColorArt(bitmap);
                        int backgroundColor = colorArt.getBackgroundColor();
                        statusColor = ColorUtils.transformIfTooWhite(backgroundColor);
                        Timber.d("BackgroundColor: %s, statusColor:%s", Integer.toHexString(backgroundColor), Integer.toHexString(statusColor));
                        if (slidingUpPanelLayout.isPanelExpanded()) {
                            setStatusColor(statusColor);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Timber.e("onBitmapFailed");
                        playerThumbnailIV.setImageDrawable(errorDrawable);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        Timber.e("onPrepareLoad");
                    }
                });
        picasso.load(program.getCover())
                .transform(new BlurTransformation(getActivity(), program.getCover()))
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Timber.d("onBitmapLoaded: %s (%s)", from.name(), bitmap.toString());
                        playerRootView.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
                        centerThumbnailIV.setImageBitmap(bitmap);
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
        subscription = apiService.programDetail(program.getId())
                    .observeOn(AndroidSchedulers.mainThread())

                    .subscribe(new Observer<Program>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable throwable) {

                        }

                        @Override
                        public void onNext(Program program) {
                            PlayerFragment.this.program = program;
                            if (program.getAnchor() != null) {
                                picasso.load(program.getAnchor().getAvatar()).into(anchorAvatarIV);
                                centerAnchorNicknameTV.setText(program.getAnchor().getNickname());
                            }
                            if (TextUtils.isEmpty(program.getArticle())) {
                                articleTV.setText(R.string.empty_article);
                            } else {
                                String newPlayedCountText = getResources().getString(R.string.tpl_played_count, program.getTotalPlay());
                                playedCountTV.setText(newPlayedCountText);
                                Spanned article = Html.fromHtml(program.getArticle());
                                articleTV.setText(article);
                            }

                        }
                    });
    }

    @OnClick({R.id.article_equalizer_switcher, R.id.article})
    public void onSwitcherClick(){
        if(viewSwitcher.getDisplayedChild() == 0) {
            viewSwitcher.showNext();
        }else{
            viewSwitcher.showPrevious();
        }
    }
    @OnClick(R.id.btn_skip_previous)
    public void onSkipPreviousButtonClick(View view){
        RadioUtils.next(getActivity());
    }
    @OnClick(R.id.btn_skip_next)
    public void onSkipNextButtonClick(View view){
        RadioUtils.previous(getActivity());
    }

    @OnClick(R.id.action_share)
    public void onShareProgramButtonClick(View view){
        ShareFragment.shareProgram(program)
                .show(getFragmentManager(), "share");
    }
    @OnClick(R.id.anchor_avatar)
    public void onAnchorAvatarClick(){
        Timber.d("onAnchorAvatarClick");
        if(program.getAnchor()!=null){
            slidingUpPanelLayout.collapsePanel();
            HomeActivity activity = (HomeActivity) getActivity();
            activity.onAnchorSelected(program.getAnchor());
        }
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
//        initVisualizer();
        articleTV.setMovementMethod(ScrollingMovementMethod.getInstance());
        playPauseButton.setOnStateChangedListener(playPauseButtonListener);
        actionPlayPauseButton.setOnStateChangedListener(playPauseButtonListener);
        seekBar.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
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
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(final DiscreteSeekBar seekBar, final int value, boolean fromUser) {
                if (fromUser) {
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
        intentFilter.addAction(RadioPlaybackService.ACTION_PROGRAM_CHANGED);
        intentFilter.addAction(RadioPlaybackService.ACTION_PLAYING_STATUS_CHANGED);
        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
        getActivity().unbindService(this);

        equalizerView.release();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(subscription!=null){
            subscription.unsubscribe();
        }
        ButterKnife.reset(this);
        handler.removeMessages(MSG_UPDATE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        this.service = IRadioService.Stub.asInterface(binder);
        Timber.w("onServiceConnected");
        try {

            equalizerView.linkPlayer(service.getPlayerSessionId());
            if(service.isPlaying()){
                updatePlayingProgram(service.getPlayingProgram());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        nextUpdate();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Timber.w("onServiceDisconnected");
        this.service = null;
        handler.removeMessages(MSG_UPDATE);
        equalizerView.release();
    }
    private void showPlayingStatus(boolean playing){
        actionPlayPauseButton.setPlaying(playing);
        playPauseButton.setPlaying(playing);
    }
    public class RadioReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.i("receive intent: %s", intent.toString());
            switch (intent.getAction()){
                case RadioPlaybackService.ACTION_PROGRAM_CHANGED:{
                    Program newProgram = intent.getParcelableExtra(RadioPlaybackService.KEY_PROGRAM);
                    updatePlayingProgram(newProgram);
                    Timber.i("ACTION_PROGRAM_CHANGED");
                    nextUpdate();
                    break;
                }
                case RadioPlaybackService.ACTION_PLAYING_STATUS_CHANGED:{
                    Timber.i("ACTION_PLAYING_STATUS_CHANGED");
                    boolean isPlaying = intent.getBooleanExtra(RadioPlaybackService.KEY_IS_PLAYING, true);
                    showPlayingStatus(isPlaying);
                    nextUpdate();
                    break;
                }
                default:
                    Timber.w("unknown intent: %s", intent.toString());
            }
        }
    }
    SlidingUpPanelLayout.PanelSlideListener mSlidingListener = new SlidingUpPanelLayout.SimplePanelSlideListener() {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            playerThumbnailIV.setTranslationX(-playerThumbnailIV.getWidth()*slideOffset);
            playerThumbnailIV.setScaleX(1f - slideOffset);
            playerThumbnailIV.setScaleY(1f-slideOffset);
            playerInfoView.setTranslationX(playerThumbnailIV.getTranslationX());
            if(slideOffset <= 0.5f){
                playPauseProgressButton.setVisibility(View.VISIBLE);
                shareBtn.setVisibility(View.INVISIBLE);
                playPauseProgressButton.setScaleX(1f-slideOffset*2f);
                playPauseProgressButton.setScaleY(1f-slideOffset*2f);
            }else{
                playPauseProgressButton.setVisibility(View.INVISIBLE);
                shareBtn.setVisibility(View.VISIBLE);
                shareBtn.setScaleX(slideOffset*2f-1f);
                shareBtn.setScaleY(slideOffset*2f-1f);
            }

        }

        @Override
        public void onPanelCollapsed(View panel) {
            playPauseProgressButton.setVisibility(View.VISIBLE);
            shareBtn.setVisibility(View.INVISIBLE);
            setStatusColor(homeStatusColor);

        }

        @Override
        public void onPanelExpanded(View panel) {
            playPauseProgressButton.setVisibility(View.INVISIBLE);
            shareBtn.setVisibility(View.VISIBLE);
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
