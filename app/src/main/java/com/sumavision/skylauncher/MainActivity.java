package com.sumavision.skylauncher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity implements View.OnFocusChangeListener, View
        .OnClickListener {
    private ImageView adPoster;
    private ImageView pooqPoster;
    private ImageView premiunPoster;
    private ImageView livePoster;
    private ImageView vodPoster;
    private ImageView settingsPoster;
    private ImageView settingsIcon;
    private ImageView vodIcon;
    private ImageView liveIcon;
    private ImageView premiumIcon;
    private ImageView pooqIcon;
    private ImageView focus_img;

    private RelativeLayout pooqPosterLayout;
    private RelativeLayout premiunPosterLayout;
    private RelativeLayout livePosterLayout;
    private RelativeLayout vodPosterLayout;
    private RelativeLayout settingsPosterLayout;

    private TextView time1, time2;

    private final int MSG1 = 1;
    private final int MSG2 = 2;
    private final int MSG3 = 3;
    private final int MSG4 = 4;
    private final int MSG5 = 5;
    private final int MSG_UPDATE_TIME = 6;

    private final float IMG_X = 165;
    private final float IMG_SPACE = 90 * 1.55f;
    private final float IMG_PRE_DELTA_X = IMG_X - 40;
    private final float IMG_NEXT_DELTA_X = IMG_X + 20;

    float ZOOM_RATE = 1.06f;
    private List<Integer> iconList;

    private final String tag = "wp";

    private TimeReceiver receiver;

    private boolean isRestart = false;
    private View mFocusView = null;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG1:
                    ObjectAnimator.ofFloat(pooqIcon, "x", pooqIcon.getX(), IMG_PRE_DELTA_X,
                            IMG_NEXT_DELTA_X, IMG_X).setDuration(1000).start();
                    break;
                case MSG2:
                    ObjectAnimator.ofFloat(premiumIcon, "x", premiumIcon.getX(), IMG_PRE_DELTA_X
                            + IMG_SPACE, IMG_NEXT_DELTA_X + IMG_SPACE, IMG_X + IMG_SPACE)
                            .setDuration(1000).start();
                    break;
                case MSG3:
                    ObjectAnimator.ofFloat(liveIcon, "x", liveIcon.getX(), IMG_PRE_DELTA_X +
                            IMG_SPACE * 2, IMG_NEXT_DELTA_X + IMG_SPACE * 2, IMG_X + IMG_SPACE *
                            2).setDuration(1000).start();
                    break;
                case MSG4:
                    ObjectAnimator.ofFloat(vodIcon, "x", vodIcon.getX(), IMG_PRE_DELTA_X +
                            IMG_SPACE * 3, IMG_NEXT_DELTA_X + IMG_SPACE * 3, IMG_X + IMG_SPACE *
                            3).setDuration(1000).start();
                    break;
                case MSG5:
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(settingsIcon, "x",
                            settingsIcon.getX(), IMG_PRE_DELTA_X + IMG_SPACE * 4,
                            IMG_NEXT_DELTA_X + IMG_SPACE * 4, IMG_X + IMG_SPACE * 4).setDuration
                            (1000);
                    if (isRestart) {
                        objectAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                Log.d(tag, "MSG5 onAnimationEnd");
                                showFocusImg(mFocusView);
                            }
                        });
                        isRestart = false;
                    }
                    objectAnimator.start();
                    break;
                case MSG_UPDATE_TIME:
                    updateTime();
                    handler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 60 * 1000);
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        receiver = new TimeReceiver();
        initView();

        if (savedInstanceState != null) {
            isRestart = true;
            focus_img.setVisibility(View.GONE);
        }

        handler.sendEmptyMessageDelayed(MSG1, 200);
        handler.sendEmptyMessageDelayed(MSG2, 400);
        handler.sendEmptyMessageDelayed(MSG3, 400 * 2);
        handler.sendEmptyMessageDelayed(MSG4, 400 * 3);
        handler.sendEmptyMessageDelayed(MSG5, 400 * 4);

    }

    private void initView() {
        adPoster = (ImageView) findViewById(R.id.ad_poster);
        pooqPoster = (ImageView) findViewById(R.id.pooq_poster);
        premiunPoster = (ImageView) findViewById(R.id.premiun_poster);
        livePoster = (ImageView) findViewById(R.id.live_poster);
        vodPoster = (ImageView) findViewById(R.id.vod_poster);
        settingsPoster = (ImageView) findViewById(R.id.settings_poster);
        settingsIcon = (ImageView) findViewById(R.id.settings_icon);
        vodIcon = (ImageView) findViewById(R.id.vod_icon);
        liveIcon = (ImageView) findViewById(R.id.live_icon);
        premiumIcon = (ImageView) findViewById(R.id.premium_icon);
        pooqIcon = (ImageView) findViewById(R.id.pooq_icon);

        focus_img = (ImageView) findViewById(R.id.focus_img);

        pooqPosterLayout = (RelativeLayout) findViewById(R.id.pooq_poster_layout);
        premiunPosterLayout = (RelativeLayout) findViewById(R.id.premiun_poster_layout);
        livePosterLayout = (RelativeLayout) findViewById(R.id.live_poster_layout);
        vodPosterLayout = (RelativeLayout) findViewById(R.id.vod_poster_layout);
        settingsPosterLayout = (RelativeLayout) findViewById(R.id.settings_poster_layout);

        time1 = (TextView) findViewById(R.id.time1);
        time2 = (TextView) findViewById(R.id.time2);

        adPoster.setOnFocusChangeListener(this);
        pooqPosterLayout.setOnFocusChangeListener(this);
        premiunPosterLayout.setOnFocusChangeListener(this);
        livePosterLayout.setOnFocusChangeListener(this);
        vodPosterLayout.setOnFocusChangeListener(this);
        settingsPosterLayout.setOnFocusChangeListener(this);
        //test
        settingsIcon.setOnFocusChangeListener(this);
        vodIcon.setOnFocusChangeListener(this);
        liveIcon.setOnFocusChangeListener(this);
        premiumIcon.setOnFocusChangeListener(this);
        pooqIcon.setOnFocusChangeListener(this);

        settingsPosterLayout.setOnClickListener(this);
        settingsIcon.setOnClickListener(this);
        pooqIcon.setOnClickListener(this);
        pooqPosterLayout.setOnClickListener(this);
        liveIcon.setOnClickListener(this);
        livePosterLayout.setOnClickListener(this);
        vodIcon.setOnClickListener(this);
        vodPosterLayout.setOnClickListener(this);

        iconList = new ArrayList<>();
        iconList.add(R.id.settings_icon);
        iconList.add(R.id.vod_icon);
        iconList.add(R.id.live_icon);
        iconList.add(R.id.premium_icon);
        iconList.add(R.id.pooq_icon);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        handler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 200);
        updateTime();
        registerTimer();
    }

    private void registerTimer() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, filter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeMessages(MSG_UPDATE_TIME);
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.d(tag, "onFocusChange hasFocus:" + hasFocus);
        if (hasFocus) {
            mFocusView = v;
            zoomOut(v);
        } else {
            focus_img.setVisibility(View.GONE);
            zoomIn(v);
        }
    }

    private void zoomOut(final View view) {
        if (iconList.contains(view.getId())) {
            ZOOM_RATE = 1.2f;
        } else {
            ZOOM_RATE = 1.06f;
        }
        PropertyValuesHolder x = PropertyValuesHolder.ofFloat("scaleX", 1.0f, ZOOM_RATE);
        PropertyValuesHolder y = PropertyValuesHolder.ofFloat("scaleY", 1.0f, ZOOM_RATE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, x, y)
                .setDuration(100);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(tag, "onAnimationEnd");
                super.onAnimationEnd(animation);
                if (!isRestart || !iconList.contains(view.getId())) {
                    showFocusImg(view);
                }
            }
        });
        objectAnimator.start();
        view.bringToFront();
    }

    private void showFocusImg(View view) {
        Log.d(tag, "showFocusImg");
        if (focus_img.getVisibility() == View.GONE) {
            focus_img.setVisibility(View.VISIBLE);
        }
        int width = view.getWidth();
        int height = view.getHeight();
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams((int) (width * view
                .getScaleX()), (int) (height * view.getScaleY()));
        rl.setMargins((int) (view.getX() - width * (view.getScaleX() - 1) / 2), (int) (view.getY
                () - height * (view.getScaleY() - 1) / 2), 0, 0);
        focus_img.setLayoutParams(rl);
        focus_img.requestLayout();
        focus_img.bringToFront();
    }


    private void zoomIn(View view) {
        if (iconList.contains(view.getId())) {
            ZOOM_RATE = 1.1f;
        } else {
            ZOOM_RATE = 1.04f;
        }
        PropertyValuesHolder x = PropertyValuesHolder.ofFloat("scaleX", ZOOM_RATE, 1.0f);
        PropertyValuesHolder y = PropertyValuesHolder.ofFloat("scaleY", ZOOM_RATE, 1.0f);
        ObjectAnimator.ofPropertyValuesHolder(view, x, y).setDuration(100).start();
    }

    private void updateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm#EEEE, #MM# dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String time = formatter.format(curDate);
        if (!TextUtils.isEmpty(time)) {
            String[] times = time.split("#");
            time1.setText(times[0]);
            time2.setText(times[1] + getMonth(times[2]) + times[3]);
        }
        Log.d(tag, "time:" + time);
    }

    private String getMonth(String month) {
        String tmp = "";
        switch (month) {
            case "01":
                tmp = "Jan";
                break;
            case "02":
                tmp = "Feb";
                break;
            case "03":
                tmp = "Mar";
                break;
            case "04":
                tmp = "Apr";
                break;
            case "05":
                tmp = "May";
                break;
            case "06":
                tmp = "Jun";
                break;
            case "07":
                tmp = "Jul";
                break;
            case "08":
                tmp = "Aug";
                break;
            case "09":
                tmp = "Sep";
                break;
            case "10":
                tmp = "Oct";
                break;
            case "11":
                tmp = "Nov";
                break;
            case "12":
                tmp = "Dec";
                break;
        }
        return tmp;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        Log.d(tag, "intent onClick");
        switch (v.getId()) {
            case R.id.pooq_icon:
            case R.id.pooq_poster_layout:
                intent = getPackageManager().getLaunchIntentForPackage("kr.co.captv.pooq");
                break;
            case R.id.live_icon:
            case R.id.live_poster_layout:
                intent = getPackageManager().getLaunchIntentForPackage("com.sangdo.chinalive2");
                break;
            case R.id.settings_icon:
            case R.id.settings_poster_layout:
//                intent = getPackageManager().getLaunchIntentForPackage("com.android.settings");
                intent = new Intent();
                ComponentName componentName = new ComponentName("com.android.tv.settings", "com"
                        + ".android.tv.settings.MainSettings");
                intent.setComponent(componentName);
                break;
            case R.id.vod_icon:
            case R.id.vod_poster_layout:
                Log.d(tag, "intent R.id.vod_icon=" + R.id.vod_icon);
                intent = getPackageManager().getLaunchIntentForPackage("com.bestv.ott.baseservices");
                break;
            default:
                break;
        }
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private class TimeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateTime();
        }
    }
}
