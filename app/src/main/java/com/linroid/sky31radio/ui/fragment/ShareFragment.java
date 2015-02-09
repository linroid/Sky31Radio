package com.linroid.sky31radio.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;

import com.linroid.sky31radio.BuildConfig;
import com.linroid.sky31radio.R;
import com.linroid.sky31radio.model.Program;
import com.linroid.sky31radio.ui.base.InjectableActivity;
import com.linroid.sky31radio.utils.BitmapUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import javax.inject.Inject;

/**
 * Created by linroid on 1/30/15.
 */
public class ShareFragment extends DialogFragment implements DialogInterface.OnClickListener {
    public static final String ARG_PROGRAM = "program";

    private Program sharingProgram;
    @Inject
    Picasso picasso;
    public static ShareFragment shareProgram(Program program){
        Bundle args = new Bundle();
        args.putParcelable(ARG_PROGRAM, program);
        ShareFragment fragment = new ShareFragment();
        fragment.setArguments(args);
        return  fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        sharingProgram = args.getParcelable(ARG_PROGRAM);
        InjectableActivity activity = (InjectableActivity) getActivity();
        activity.inject(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                new String[]{"微信好友", "朋友圈",  "其他地方"}
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title_share_program)
                .setAdapter(adapter, this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case 0:
                shareToWechatFriends();
                break;
            case 1:
                shareToWechatMoments();
                break;
            default:
                shareOtherWay();
                break;
        }
    }

    /**
     * 分享给微信好友
     */
    private void shareToWechatFriends() {
        this.sendRequestToWX(SendMessageToWX.Req.WXSceneSession);
    }

    /**
     * 分享到微信朋友圈
     */
    private void shareToWechatMoments() {
        this.sendRequestToWX(SendMessageToWX.Req.WXSceneTimeline);
    }

    /**
     * 分享到其他地方
     */
    private void shareOtherWay() {
        String text = getResources().getString(R.string.tpl_share_program,
                                                getString(R.string.app_name),
                                                sharingProgram.getTitle(),
                                                BuildConfig.APP_DOWNLOAD_URL);
        Intent targetIntent = new Intent(Intent.ACTION_SEND);
        targetIntent.setType("text/plain");
        targetIntent.putExtra(Intent.EXTRA_TEXT, text);
        Intent intent = Intent.createChooser(targetIntent, getString(R.string.title_share_program));
        startActivity(intent);
    }

    private void sendRequestToWX(final int scene){
        picasso.load(sharingProgram.getThumbnail())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        byte[] data = BitmapUtils.encodeToByteArray(bitmap);
                        this.sendRequest(data);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        this.sendRequest(null);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                    private void sendRequest(byte[] thumbData){
                        IWXAPI iwxapi = WXAPIFactory.createWXAPI(getActivity(), BuildConfig.WECHAT_APP_ID, true);
                        iwxapi.registerApp(BuildConfig.WECHAT_APP_ID);

                        WXMusicObject object = new WXMusicObject();
                        object.musicUrl = BuildConfig.APP_DOWNLOAD_URL;
                        object.musicDataUrl = sharingProgram.getAudio().getSrc();
                        object.musicLowBandUrl = sharingProgram.getThumbnail();
                        object.musicLowBandDataUrl = sharingProgram.getThumbnail();
                        WXMediaMessage message = new WXMediaMessage();
                        message.mediaObject = object;
                        message.title = sharingProgram.getTitle();
                        message.description = getString(R.string.tpl_wx_share_description,
                                getString(R.string.app_name),
                                sharingProgram.getAuthor());
                        message.thumbData  = thumbData;

                        SendMessageToWX.Req request = new SendMessageToWX.Req();
                        request.message = message;
                        request.transaction = String.valueOf(System.currentTimeMillis());
                        request.scene = scene;

                        iwxapi.sendReq(request);
                        iwxapi.unregisterApp();
                    }
                });

    }

}
