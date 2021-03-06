package juzi;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easychange.admin.smallrain.R;
import com.easychange.admin.smallrain.activity.LetsTestActivity;
import com.easychange.admin.smallrain.base.BaseActivity;
import com.easychange.admin.smallrain.entity.BreakNetBean;
import com.easychange.admin.smallrain.utils.AnimationHelper;
import com.easychange.admin.smallrain.utils.BitmapUtils;
import com.easychange.admin.smallrain.utils.ForegroundCallbacks;
import com.easychange.admin.smallrain.utils.GlideUtil;
import com.easychange.admin.smallrain.utils.MyUtils;
import com.easychange.admin.smallrain.utils.Player;
import com.easychange.admin.smallrain.utils.ScreenListener;
import com.easychange.admin.smallrain.views.CircleBarTime;
import com.easychange.admin.smallrain.views.IndicatorView;
import com.easychange.admin.smallrain.views.WaveCircleView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qlzx.mylibrary.util.EventBusUtil;
import com.qlzx.mylibrary.util.PreferencesHelper;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import bean.JuZiChengZu;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import http.AsyncRequest;
import http.BaseStringCallback_Host;
import http.Setting;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by chenlipeng on 2018/10/20 0020
 * describe:  句子成组训练   2个卡片
 * 训练和测试
 * 成组是2个卡片
 * 分解是4个卡片
 */
public class JuZiChengZuXunLianActivity extends BaseActivity implements AsyncRequest {
//12.	所有的训练级，点击了页面的大图或填空处，正确答案处出现小手辅助。否则2S辅助

    @BindView(R.id.ll_indicator)
    IndicatorView llIndicator;
    @BindView(R.id.iv_home)
    ImageView ivHome;
    @BindView(R.id.rl_top)
    RelativeLayout rlTop;
    @BindView(R.id.iv_img)
    ImageView ivImg;
    @BindView(R.id.ll_text_parent_bg)
    LinearLayout llTextParentBg;
    @BindView(R.id.ll_text_parent)
    LinearLayout llTextParent;
    @BindView(R.id.ll_text_bg_parent)
    LinearLayout llTextBgParent;
    @BindView(R.id.ll_click_layout)
    LinearLayout llClickLayout;
    @BindView(R.id.cb)
    CircleBarTime cb;
    @BindView(R.id.fL_big_pic)
    FrameLayout fLBigPic;
    private MediaPlayer player;
    private MediaPlayer playerDoWhatThing;

    private int currentSize = 2;
    private View rightChildTextTwo;
    private View leftChildTextOne;

    private boolean isTwoMove = false;
    private boolean isOneMove = false;
    private TextView tv_content1;
    private TextView tv_content2;
    private AnimationDrawable frameAnim1;
    private AnimationDrawable frameAnim2 = new AnimationDrawable();

    private JuZiChengZu juzibean;
    private MediaPlayer mediaPlayer;
    private boolean playNextCardVoice = false;
    private boolean isQuitActivity = false;
    private ScreenListener screenListener;
    private boolean isFirstInto = true;
    private boolean isTwoInto = true;
    private String[] split;
    private boolean isBackgroundVoiceFinished = false;
    private boolean isAnimationFinished = false;

    @Override
    protected void onDestroy() {
        Log.e("aaa", "onDestroy: ");
        EventBusUtil.unregister(this);

        if (null != foregroundCallbacks) {
            ForegroundCallbacks.get().removeListener(foregroundCallbacks);
            foregroundCallbacks = null;

            screenListener.unregisterListener();
        }
        handler.removeCallbacksAndMessages(null);

        ReleasePlayer();
//        应该是做题的时候，中途退出，20分钟内，进来的时候，显示的刚才的做题位置，如果超过20分钟就从第一个课件开始做
        if (null != bitmap1) {
            bitmap1.recycle();
            bitmap1 = null;
        }
        if (null != bitmap2) {
            bitmap2.recycle();
            bitmap2 = null;
        }
        if (null != bitmap) {
            bitmap.recycle();
            bitmap = null;
        }

        isQuitActivity = true;
        OkHttpUtils.getInstance().cancelTag(this);

        super.onDestroy();
    }

    /**
     * 释放播放器资源
     */
    private void ReleasePlayer(MediaPlayer player) {
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
            }
            //关键语句
            player.reset();
            player.release();
            player = null;
        }
    }

    /**
     * 释放播放器资源
     */
    private void ReleasePlayer() {
//        BitmapUtils.tryRecycleAnimationDrawable(frameAnim1);
//        BitmapUtils.tryRecycleAnimationDrawable(frameAnim2);
//        if (player != null) {
//            player.stop();
//            //关键语句
//            player.reset();
//            player.release();
//            player = null;
//        }
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            //关键语句
//            mediaPlayer.reset();
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//        if (playerDoWhatThing != null) {
//            playerDoWhatThing.stop();
//            //关键语句
//            playerDoWhatThing.reset();
//            playerDoWhatThing.release();
//            playerDoWhatThing = null;
//        }

    }

    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap bitmap;
    private Handler handler = new Handler() {
        /**
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    byte[] bytes = (byte[]) msg.obj;
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    Drawable drawable = new BitmapDrawable(bitmap);

                    // 为AnimationDrawable添加动画帧
                    frameAnim1.addFrame(drawable, 500);

                    if (frameAnim1.getNumberOfFrames() == split.length) {
                        frameAnim1.setOneShot(false);
                        ivImg.setBackground(frameAnim1);

                        doAnim();
                    }
                    break;
                case 2:
                    byte[] bytes1 = (byte[]) msg.obj;
                    bitmap1 = BitmapFactory.decodeByteArray(bytes1, 0, bytes1.length);
                    Drawable drawable1 = new BitmapDrawable(bitmap1);
                    // 为AnimationDrawable添加动画帧
                    frameAnim1.addFrame(drawable1, 500);
                    if (frameAnim1.getNumberOfFrames() == split.length) {
                        frameAnim1.setOneShot(false);
                        ivImg.setBackground(frameAnim1);
                        doAnim();
                    }
                    break;
                case 3://
                    byte[] bytes2 = (byte[]) msg.obj;
                    bitmap2 = BitmapFactory.decodeByteArray(bytes2, 0, bytes2.length);
                    Drawable drawable2 = new BitmapDrawable(bitmap2);

                    frameAnim1.addFrame(drawable2, 500);

                    if (frameAnim1.getNumberOfFrames() == split.length) {
                        frameAnim1.setOneShot(false);
                        ivImg.setBackground(frameAnim1);

                        doAnim();
                    }

                    break;
                default:
                    break;
            }
        }
    };
    private int position = 0;
    private JuZiChengZu.SentenceGroupTrainingBean sentenceGroupTrainingBean;
    private boolean isOrder;
    private Timer timer;
    private int currentLoopTime;
    private TimerTask timerTask;
    private int executeInterval = 100;
    private int loopTimeOne;
    private double loopRateOne;
    private int loopTimeTwo;
    private double loopRateTwo;
    private int currentFirstPotision = 0;
    private int currentTwoPotision;
    private ImageView currentIv_click_pic;
    private int length;
    private String scene;

    /**
     * 异步get,直接调用
     */
    private void asyncGet(String IMAGE_URL, final int i) {
        Log.i("liubiao", "asyncGet: "+IMAGE_URL);
        if (TextUtils.isEmpty(IMAGE_URL)) return;

        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().get().url(IMAGE_URL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = handler.obtainMessage();
                if (response.isSuccessful()) {
                    if (i == 0) {
                        message.what = 1;
                        message.obj = response.body().bytes();
                        handler.sendMessage(message);
                    }
                    if (i == 1) {
                        message.what = 2;
                        message.obj = response.body().bytes();
                        handler.sendMessage(message);
                    }
                    if (i == 2) {
                        message.what = 3;
                        message.obj = response.body().bytes();
                        handler.sendMessage(message);
                    }
                } else {
                    handler.sendEmptyMessage(3);
                }
            }
        });
    }

    /**
     * 名词
     */
    private void getVerb() {
        String url = Setting.getSentencegroup();
        OkHttpUtils
                .post()
                .url(url)//接口地址
                .id(1)//XX接口的标识
                .tag(JuZiChengZuXunLianActivity.this)
                .build()
                .execute(new BaseStringCallback_Host(JuZiChengZuXunLianActivity.this, this));
    }


    /**
     * 成功回调
     *
     * @param object XX接口
     * @param data   字符串数据。用  new JSONObject(result);
     */
    @Override
    public void RequestComplete(Object object, Object data) {
        if (object.equals(1)) {//标记那个接口

            String result = (String) data;
            (JuZiChengZuXunLianActivity.this).runOnUiThread(new Runnable() {//在UI线程处理逻辑，当操作控件的时候
                @Override
                public void run() {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        String code = jsonObject.getString("code");
                        String msg1 = (String) jsonObject.getString("msg");

                        if (isFinish) {
                            return;
                        }
                        if (code.equals("200")) {
                            if (null != foregroundCallbacks) {
                                ForegroundCallbacks.get().removeListener(foregroundCallbacks);
                                foregroundCallbacks = null;

                                screenListener.unregisterListener();
                            }

                            Gson gson = new Gson();
                            juzibean = gson.fromJson(result,
                                    new TypeToken<JuZiChengZu>() {
                                    }.getType());

                            if (length != -1 && !TextUtils.isEmpty(scene)) {
//                                scene	是	string	学习场景 1训练 2测试 3意义
                                if (scene.equals("1")) {
                                    position = length;
                                    setDataIntoView();
                                } else {
                                    Intent intent = new Intent(JuZiChengZuXunLianActivity.this, JuZiChengZuCiShiLianActivity.class);
                                    intent.putExtra("position", length);
                                    intent.putExtra("model", juzibean);
                                    intent.putExtra("groupId", groupId);
                                    startActivity(intent);
                                    ReleasePlayer();
                                    finish();
                                }
                                return;
                            }

                            setDataIntoView();

                        } else {
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            });
        }
        if (object.equals(2)) {//标记那个接口

            String result = (String) data;
            (JuZiChengZuXunLianActivity.this).runOnUiThread(new Runnable() {//在UI线程处理逻辑，当操作控件的时候
                @Override
                public void run() {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        String code = jsonObject.getString("code");
                        String msg1 = (String) jsonObject.getString("msg");

                        if (isFinish) {
                            return;
                        }

                        if (code.equals("200")) {
                            if (null != foregroundCallbacks) {
                                ForegroundCallbacks.get().removeListener(foregroundCallbacks);
                                foregroundCallbacks = null;

                                screenListener.unregisterListener();
                            }

                            ++position;

                            if (position < juzibean.getSentenceGroupTraining().size()) {

                                groupId = (String) jsonObject.getString("groupId");

                                Intent intent = new Intent(JuZiChengZuXunLianActivity.this,
                                        JuZiChengZuXunLianActivity.class);
                                intent.putExtra("groupId", groupId);

                                intent.putExtra("position", position);
                                intent.putExtra("model", juzibean);
                                startActivity(intent);
                                ReleasePlayer();
                                finish();
                            } else {//从新开始

                                Intent intent = new Intent(JuZiChengZuXunLianActivity.this, LetsTestActivity.class);
                                intent.putExtra("type", "juzi");
                                intent.putExtra("model", juzibean);

                                startActivity(intent);
                                ReleasePlayer();
                                finish();
                            }

                        } else {
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }


    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                double obj = (double) msg.obj;

                cb.setProgress((float) obj * currentLoopTime);
                currentLoopTime++;
            } else if (msg.what == 2) {

                View childAt = llClickLayout.getChildAt((int) msg.obj);

                String tagPosition = (String) childAt.getTag();
                if (tagPosition.equals("1")) {
                    if (childAt.isClickable()) {
                        final RelativeLayout rl_hand = childAt.findViewById(R.id.rl_hand);
                        final WaveCircleView wave_cirlce_view = childAt.findViewById(R.id.wave_cirlce_view);

                        rl_hand.setVisibility(View.VISIBLE);
                        wave_cirlce_view.startWave();
                    }

                } else {
                    if (childAt.isClickable()) {
                        final RelativeLayout rl_hand = childAt.findViewById(R.id.rl_hand);
                        final WaveCircleView wave_cirlce_view = childAt.findViewById(R.id.wave_cirlce_view);

                        rl_hand.setVisibility(View.VISIBLE);
                        wave_cirlce_view.startWave();
                    }
                }
            }
        }
    };

    /**
     * 开始自动减时
     *
     * @param currentClick
     * @param loopTime     循环次数
     */
    private void startTime(final int currentClick, int loopTime, double loopRate) {
        if (timer == null) {
            timer = new Timer();
        }
        currentLoopTime = 0;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (currentLoopTime <= loopTime) {

                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = loopRate;
                    if (mHandler != null) {
                        mHandler.sendMessage(message);//发送消息
                    }
                } else {

                    Message message = Message.obtain();
                    message.what = 2;
                    message.obj = currentClick;
                    if (mHandler != null) {
                        mHandler.sendMessage(message);//发送消息
                    }
                    stopTime();
                }

            }
        };
        timer.schedule(timerTask, 0, executeInterval);//1000ms执行一次
    }


    private void stopTime() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    //    名词 训练time字段里的helpTime 测试fristAssistTime,secondAssistTime
//    动词 训练helptime里的helptime 测试cardOneTime,cardTwoTime
//    句子成组 训练helptime里的helptime 测试cardOneTime,cardTwoTime
//    句子分解 训练helptime里的helptime 测试cardOneTime,cardTwoTime,cardThreeTime,cardFourTime
    private void setDataIntoView() {

        frameAnim1 = new AnimationDrawable();
        List<JuZiChengZu.HelptimeBean> helptime = juzibean.getHelptime();
        for (int i = 0; i < helptime.size(); i++) {
            int helpTime = helptime.get(i).getHelpTime();
            int sort = helptime.get(i).getSort();

            if (sort == 1) {
                loopTimeOne = helpTime * 1000 / executeInterval;//循环次数
                loopRateOne = 100.00 / loopTimeOne;//每次循环，圆环走的度数

                loopTimeTwo = helpTime * 1000 / executeInterval;//循环次数
                loopRateTwo = 100.00 / loopTimeTwo;//每次循环，圆环走的度数
            }

            if (sort == 2) {
                loopTimeTwo = helpTime * 1000 / executeInterval;//循环次数
                loopRateTwo = 100.00 / loopTimeTwo;//每次循环，圆环走的度数
            }

        }

        sentenceGroupTrainingBean = juzibean.getSentenceGroupTraining().get(position);//每条数据

        coursewareId = sentenceGroupTrainingBean.getId();
        name = sentenceGroupTrainingBean.getGroupChar();

        String startSlideshow = sentenceGroupTrainingBean.getStartSlideshow();
        split = startSlideshow.split(",");
        for (int i = 0; i < split.length; i++) {
            asyncGet(split[i], i);
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                playLocalVoice("男-谁在干什么.MP3", true);

            }
        }, 200);

        int grapWidth = MyUtils.dip2px(JuZiChengZuXunLianActivity.this, 26);
        for (int i = 0; i < currentSize; i++) {
            View inflate = LayoutInflater.from(JuZiChengZuXunLianActivity.this).inflate(R.layout.text_bg_big, null);
            if (i == 0) {
                llTextBgParent.addView(inflate);//第一个view不用设置间隔
            } else {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(grapWidth, 0, 0, 0);
                inflate.setLayoutParams(lp);
                llTextBgParent.addView(inflate);
            }
        }
        for (int i = 0; i < llTextBgParent.getChildCount(); i++) {
            View childAt = llTextBgParent.getChildAt(i);
            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pass = "0";

                    View childAt = llClickLayout.getChildAt(currentFirstPotision);
                    final RelativeLayout rl_hand = childAt.findViewById(R.id.rl_hand);
                    if (childAt.isClickable()) {
                        if (rl_hand.getVisibility() == View.VISIBLE) {
                        } else {
                            rl_hand.setVisibility(View.VISIBLE);
                            final WaveCircleView wave_cirlce_view = childAt.findViewById(R.id.wave_cirlce_view);
                            wave_cirlce_view.startWave();
                        }
                        return;
                    }

                    View childAt1 = llClickLayout.getChildAt(currentTwoPotision);
                    final RelativeLayout rl_hand1 = childAt1.findViewById(R.id.rl_hand);
                    if (childAt1.isClickable()) {
                        if (rl_hand1.getVisibility() == View.VISIBLE) {
                        } else {
                            rl_hand1.setVisibility(View.VISIBLE);
                            final WaveCircleView wave_cirlce_view = childAt1.findViewById(R.id.wave_cirlce_view);
                            wave_cirlce_view.startWave();
                        }
                    }

                }
            });
        }

        fLBigPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOneMove) {
                    //    把小手的布局隐藏掉
                    View childAt = llClickLayout.getChildAt(currentFirstPotision);
                    final RelativeLayout rl_hand = childAt.findViewById(R.id.rl_hand);
                    if (childAt.isClickable()) {
                        if (rl_hand.getVisibility() == View.VISIBLE) {
                        } else {
                            rl_hand.setVisibility(View.VISIBLE);
                            final WaveCircleView wave_cirlce_view = childAt.findViewById(R.id.wave_cirlce_view);
                            wave_cirlce_view.startWave();
                        }
                    }
                } else {
                    View childAt = llClickLayout.getChildAt(currentTwoPotision);
                    final RelativeLayout rl_hand = childAt.findViewById(R.id.rl_hand);
                    if (childAt.isClickable()) {
                        if (rl_hand.getVisibility() == View.VISIBLE) {
                        } else {
                            rl_hand.setVisibility(View.VISIBLE);
                            final WaveCircleView wave_cirlce_view = childAt.findViewById(R.id.wave_cirlce_view);
                            wave_cirlce_view.startWave();
                        }
                    }
                }
            }
        });


        for (int i = 0; i < currentSize; i++) {
            View inflate = LayoutInflater.from(JuZiChengZuXunLianActivity.this).inflate(R.layout.text_layout_big, null);
            if (i == 0) {
                llTextParent.addView(inflate);//第一个view不用设置间隔
            } else {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(grapWidth, 0, 0, 0);
                inflate.setLayoutParams(lp);
                llTextParent.addView(inflate);
            }
        }//for结束
        leftChildTextOne = llTextParent.getChildAt(0);
        rightChildTextTwo = llTextParent.getChildAt(1);

        tv_content1 = (TextView) leftChildTextOne.findViewById(R.id.tv_content);
        tv_content2 = (TextView) rightChildTextTwo.findViewById(R.id.tv_content);
        tv_content1.setText(sentenceGroupTrainingBean.getCardOneChar());
        tv_content2.setText(sentenceGroupTrainingBean.getCardTwoChar());

        for (int i = 0; i < currentSize; i++) {
            View inflate = LayoutInflater.from(this).inflate(R.layout.click_layout_two_click_pic_mingci_ceshi_xiugai, null);
//            View inflate = LayoutInflater.from(JuZiChengZuXunLianActivity.this).inflate(R.layout.click_layout_two_click_pic, null);
            if (i == 0) {
                llClickLayout.addView(inflate);//第一个view不用设置间隔
            } else {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                if (currentSize != 4) {
                    lp.setMargins(25, 0, 0, 0);
                }
                inflate.setLayoutParams(lp);
                llClickLayout.addView(inflate);
            }
            ImageView iv_click_pic = (ImageView) inflate.findViewById(R.id.iv_click_pic);

            String cardImage = sentenceGroupTrainingBean.getList().get(i).getCardImage();
            boolean contains = cardImage.contains(",");
            if (!contains) {
                GlideUtil.display(JuZiChengZuXunLianActivity.this, cardImage, iv_click_pic);
            } else {
                String[] split1 = cardImage.split(",");
                getNetClickImage(iv_click_pic, split1);
            }

        }//for结束
        TextView tv_content11 = (TextView) llClickLayout.getChildAt(0).findViewById(R.id.tv_choose2);
        TextView tv_content22 = (TextView) llClickLayout.getChildAt(1).findViewById(R.id.tv_choose2);

        tv_content11.setText(sentenceGroupTrainingBean.getList().get(0).getCardChar());
        tv_content22.setText(sentenceGroupTrainingBean.getList().get(1).getCardChar());

        String firstCardColorChar = sentenceGroupTrainingBean.getCardOneChar();
        String currentClickCardColorChar = sentenceGroupTrainingBean.getList().get(0).getCardChar();
        if (currentClickCardColorChar.equals(firstCardColorChar)) {
            isOrder = true;
        }

        if (firstCardColorChar.equals(currentClickCardColorChar)) {
            llClickLayout.getChildAt(0).setTag("0");
            llClickLayout.getChildAt(1).setTag("1");
            currentFirstPotision = 0;
            currentTwoPotision = 1;

        } else {
            llClickLayout.getChildAt(1).setTag("0");
            llClickLayout.getChildAt(0).setTag("1");
            currentFirstPotision = 1;
            currentTwoPotision = 0;

        }

        final View childAt = llClickLayout.getChildAt(0);//第一个
        childAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOrder) {
                    long l = -(currentClickOneStartTime - System.currentTimeMillis()) / 1000;
                    if (l >= 50) {
                        l = 1;
                    }
                    stayTimeList = l + "";
                } else {
                    if (!isTwoMove) {
                        //正确小手辅助出现
                        View childAt = llClickLayout.getChildAt(1);
                        final RelativeLayout rl_hand = childAt.findViewById(R.id.rl_hand);
                        if (rl_hand.getVisibility() == View.GONE) {//是否通过 1 是 0 否
                            rl_hand.setVisibility(View.VISIBLE);
                            pass = "0";
                            final WaveCircleView wave_cirlce_view = childAt.findViewById(R.id.wave_cirlce_view);
                            wave_cirlce_view.startWave();
                        }
                        return;
                    }
                    long l = -(currentClickTwoStartTime - System.currentTimeMillis()) / 1000;
                    if (l >= 50) {
                        l = 1;
                    }
                    stayTimeList = stayTimeList + "," + l + "";
                }

                String tagPosition = (String) childAt.getTag();
                if (tagPosition.equals("0")) {
                    stopTime();
                    currentClickTwoStartTime = System.currentTimeMillis();
                }

                isOneMove = true;

                voiceListData.add(sentenceGroupTrainingBean.getList().get(0).getCardRecord());
                Log.e("voiceList", "onClick: " + sentenceGroupTrainingBean.getList().get(0).getCardRecord());

//                把小手的布局隐藏掉
                View childAt = llClickLayout.getChildAt(0);
                final RelativeLayout rl_hand = childAt.findViewById(R.id.rl_hand);

                if (rl_hand.getVisibility() == View.VISIBLE) {
                    pass = "0";
                }
                rl_hand.setVisibility(View.GONE);

                childAt.setClickable(false);
                AnimationHelper.startScaleAnimation(mContext, childAt);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ObjectAnimator sax = ObjectAnimator.ofFloat(childAt, "scaleX", 1f, 0.8f);
                        ObjectAnimator say = ObjectAnimator.ofFloat(childAt, "scaleY", 1f, 0.5f);

                        int height = (int) (childAt.getHeight() * 0.25);
                        int width = (int) (childAt.getWidth() * 0.1);
                        int left = childAt.getLeft();

                        int distance_x;
                        int distance_y;
                        int currentPosition;

                        if (isOrder) {
                            int topLeft = leftChildTextOne.getLeft();
                            distance_x = childAt.getTop();
                            distance_y = topLeft - left;
                            currentPosition = 0;
                        } else {
                            int topLeft = rightChildTextTwo.getLeft();
                            distance_x = childAt.getTop();
                            distance_y = topLeft - left;
                            currentPosition = 1;

                        }

                        ObjectAnimator obx = ObjectAnimator.ofFloat(childAt, "translationX", distance_y - width);
                        ObjectAnimator oby = ObjectAnimator.ofFloat(childAt, "translationY", -distance_x - height);

                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(sax, say, obx, oby);
                        set.setDuration(2000);
                        set.start();

                        set.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if (tagPosition.equals("0")) {
                                    cb.setProgress(0);
                                    startTime(currentTwoPotision, loopTimeTwo, loopRateTwo);
                                }
                                llTextParent.setVisibility(View.VISIBLE);//文字的父布局

                                childAt.setVisibility(View.INVISIBLE);//移动的view


                                View text_bg = llTextBgParent.getChildAt(currentPosition);//自己的背景
                                text_bg.setVisibility(View.INVISIBLE);

                                View tv_content1 = llTextParent.getChildAt(currentPosition);//自己文本
                                tv_content1.setVisibility(View.VISIBLE);
                                //透明度渐变显示
                                ObjectAnimator animator = ObjectAnimator.ofFloat(tv_content1, "alpha", 0.5f, 1f);
                                animator.setDuration(1000);
                                animator.start();

                                if (isOrder) {
                                    View tv_content11 = llTextParent.getChildAt(1);

                                    if (tv_content11.getVisibility() == View.VISIBLE) {
                                        tv_content11.setVisibility(View.INVISIBLE);
                                    }
                                } else {
                                }

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isOrder) {
//                                            if (!noPlayTwoCardVoice) {
//                                                mergeText();
//                                            }
                                            if (!isShouldMergeText) {
                                                isShouldMergeText = true;
                                                if (voiceListData.size() == 0) {
                                                    if (!isPerformOverMergeText) {
                                                        mergeText();
                                                    }
                                                }
                                            }
//                                            doAnim();
                                        }

                                    }
                                }, 1000);
                            }
                        });
                    }
                }, 1000);
            }
        });

        final View childAt1 = llClickLayout.getChildAt(1);//第二个
        childAt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isOrder) {
                    if (!isOneMove) {
                        //正确小手辅助出现
                        View childAt = llClickLayout.getChildAt(0);
                        final RelativeLayout rl_hand = childAt.findViewById(R.id.rl_hand);
                        if (rl_hand.getVisibility() == View.GONE) {//是否通过 1 是 0 否
                            rl_hand.setVisibility(View.VISIBLE);
                            pass = "0";

                            final WaveCircleView wave_cirlce_view = childAt.findViewById(R.id.wave_cirlce_view);
                            wave_cirlce_view.startWave();
                        }

                        return;
                    }
                    long l = -(currentClickTwoStartTime - System.currentTimeMillis()) / 1000;
                    if (l >= 50) {
                        l = 1;
                    }
                    stayTimeList = stayTimeList + "," + l + "";
                } else {
                    long l = -(currentClickOneStartTime - System.currentTimeMillis()) / 1000;
                    if (l >= 50) {
                        l = 1;
                    }
                    stayTimeList = l + "";
                }

                String tagPosition = (String) childAt1.getTag();
                if (tagPosition.equals("0")) {
                    stopTime();
                    currentClickTwoStartTime = System.currentTimeMillis();
                }
                isTwoMove = true;

                voiceListData.add(sentenceGroupTrainingBean.getList().get(1).getCardRecord());
                Log.e("voiceList", "onClick: " + sentenceGroupTrainingBean.getList().get(1).getCardRecord());

                //  把小手的布局隐藏掉
                View childAt = llClickLayout.getChildAt(1);
                final RelativeLayout rl_hand = childAt.findViewById(R.id.rl_hand);
                if (rl_hand.getVisibility() == View.VISIBLE) {
                    pass = "0";
                } else {
                    pass = "1";
                }
                rl_hand.setVisibility(View.GONE);

                childAt1.setClickable(false);
                AnimationHelper.startScaleAnimation(mContext, childAt1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ObjectAnimator sax = ObjectAnimator.ofFloat(childAt1, "scaleX", 1f, 0.8f);
                        ObjectAnimator say = ObjectAnimator.ofFloat(childAt1, "scaleY", 1f, 0.5f);

                        int height = (int) (childAt1.getHeight() * 0.25);
                        int width = (int) (childAt1.getWidth() * 0.1);

                        int left = childAt1.getLeft();

                        int distance_x = childAt1.getTop();
                        int distance_y;
                        int currentPosition;//上面融合文本对应的地方

                        if (isOrder) {
                            int topLeft = rightChildTextTwo.getLeft();
                            distance_y = topLeft - left;//负数
                            currentPosition = 1;
                        } else {
                            int topLeft = leftChildTextOne.getLeft();
                            distance_y = topLeft - left;//负数
                            currentPosition = 0;
                        }


                        float curTranslationX = childAt1.getTranslationX();
                        float curTranslationY = childAt1.getTranslationY();


                        ObjectAnimator obx = ObjectAnimator.ofFloat(childAt1, "translationX", curTranslationX, distance_y - width);
                        ObjectAnimator oby = ObjectAnimator.ofFloat(childAt1, "translationY", curTranslationY, -distance_x - height);

                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(sax, say, obx, oby);
                        set.setDuration(2000);
                        set.start();

                        set.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if (tagPosition.equals("0")) {
                                    cb.setProgress(0);
                                    startTime(currentTwoPotision, loopTimeTwo, loopRateTwo);
                                }
                                llTextParent.setVisibility(View.VISIBLE);//文字的父布局，展示出来

                                childAt1.setVisibility(View.INVISIBLE);//点击的移动布局

                                View text_bg = llTextBgParent.getChildAt(currentPosition);//自己的背景
                                text_bg.setVisibility(View.INVISIBLE);//自己的背景展示

                                View tv_content0 = llTextParent.getChildAt(currentPosition);
                                tv_content0.setVisibility(View.VISIBLE);
                                //透明度渐变显示
                                ObjectAnimator animator = ObjectAnimator.ofFloat(tv_content0, "alpha", 0.5f, 1f);
                                animator.setDuration(1000);
                                animator.start();

                                if (isOrder) {
                                } else {
                                    View tv_content1 = llTextParent.getChildAt(1);

                                    if (tv_content1.getVisibility() == View.VISIBLE) {
                                        tv_content1.setVisibility(View.INVISIBLE);
                                    }
                                }

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (isOrder) {
//                                            if (!noPlayTwoCardVoice) {
//                                                mergeText();
//                                            }
                                            if (!isShouldMergeText) {
                                                isShouldMergeText = true;
                                                if (voiceListData.size() == 0) {
                                                    if (!isPerformOverMergeText) {
                                                        mergeText();
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }, 1000);

                            }
                        });
                    }
                }, 1000);
            }
        });
    }

    private Handler handler1 = new Handler() {
        /**
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    byte[] bytes = (byte[]) msg.obj;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    Drawable drawable = new BitmapDrawable(bitmap);

                    // 为AnimationDrawable添加动画帧
                    frameAnim2.addFrame(drawable, 500);

                    if (frameAnim2.getNumberOfFrames() == 3) {
                        frameAnim2.setOneShot(false);
                        currentIv_click_pic.setBackgroundDrawable(frameAnim2);

                        frameAnim2.start();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 得到网络的图片
     *
     * @param iv_click_pic
     * @param split1
     */
    private void getNetClickImage(ImageView iv_click_pic, String[] split1) {
        AnimationDrawable frameAnim1 = new AnimationDrawable();

        currentIv_click_pic = iv_click_pic;
        for (int j = 0; j < split1.length; j++) {
            OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder().get().url(split1[j]).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    Message message = handler1.obtainMessage();
                    if (response.isSuccessful()) {
                        message.what = 1;
                        message.obj = response.body().bytes();

                        handler1.sendMessage(message);

                    }

                }
            });
        }//for结束

        frameAnim1.setOneShot(false);
        iv_click_pic.setBackground(frameAnim1);
        frameAnim1.start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ju_zi_chengzu_xunlian);
        ButterKnife.bind(this);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        // 获取当前时间
        Date date = new Date();
        startTime = simpleDateFormat.format(date);
        startTimeMillis = System.currentTimeMillis();

        position = getIntent().getIntExtra("position", 0);
        juzibean = (JuZiChengZu) getIntent().getSerializableExtra("model");

        length = getIntent().getIntExtra("length", -1);
        scene = getIntent().getStringExtra("scene");
        groupId = getIntent().getStringExtra("groupId");

        ForegroundCallbacks.get().addListener(foregroundCallbacks);
        setScreenLock();

        if (length != -1 && !TextUtils.isEmpty(scene)) {
            position = length;
            getVerb();
            llIndicator.setSelectedPosition(position);
            return;
        }
        llIndicator.setSelectedPosition(position);

        if (null == juzibean) {
            getVerb();
        } else {
            setDataIntoView();
        }


        EventBusUtil.register(this);
    }

    private boolean isPlayingVoice = false;
    private boolean FristVoiceFinished = false;
    private boolean isShouldMergeText = false;
    private boolean isPerformOverMergeText = false;

    private Handler voiceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {

                if (FristVoiceFinished) {
                    if (voiceListData.size() != 0) {
                        if (!isPlayingVoice && !isQuitActivity) {
                            playLocalVoiceOnLineNew(voiceListData.get(0));
                        }

                    }
                }

                voiceHandler.sendEmptyMessage(1);
            }

        }
    };

    /**
     * 在线
     *
     * @param videoName
     */
    private void playLocalVoiceOnLineNew(String videoName) {
        Log.e("录音", "playLocalVoiceOnLineNew: " + videoName);
        isPlayingVoice = true;

        if (TextUtils.isEmpty(videoName)) {
            if (voiceListData.size() != 0) {
                voiceListData.remove(0);
            }

            if (voiceListData.size() == 0) {
//                        课件做完，上方动画放大缩小，并且所有音频播放完了，没有立即进入下一课件
                if (isShouldMergeText) {
                    if (!isPerformOverMergeText) {
                        mergeText();
                    }
                }
            }
            isPlayingVoice = false;
            return;
        }
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            //关键语句
//            mediaPlayer.reset();
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }

        //1 初始化mediaplayer
        mediaPlayer = new MediaPlayer();
        //2 设置到播放的资源位置 path 可以是网络 路径 也可以是本地路径
        try {
            mediaPlayer.setDataSource(videoName);
            //3 准备播放
            mediaPlayer.prepareAsync();
            //3.1 设置一个准备完成的监听
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (isQuitActivity) {
                        isPlayingVoice = false;
                        return;
                    }

                    // 4 开始播放
                    mediaPlayer.start();
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer1) {
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                        //关键语句
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    isPlayingVoice = false;

                    if (voiceListData.size() != 0) {
                        voiceListData.remove(0);
                    }

                    if (voiceListData.size() == 0) {
//                        课件做完，上方动画放大缩小，并且所有音频播放完了，没有立即进入下一课件
                        if (isShouldMergeText) {
                            if (!isPerformOverMergeText) {
                                mergeText();
                            }
                        }
                    }
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer1, int i, int i1) {
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                        //关键语句
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    Log.e("bofang", "onError: i" + i + "i1" + i1);
                    return false;
                }
            });
        } catch (Exception e) {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                //关键语句
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            Log.e("bofang", "playLocalVoiceOnLineGroupRecord: IOException" + e.toString());
            e.printStackTrace();
        }

    }

    private boolean isExecuteThe = false;
    private boolean isYetUploadData = false;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BreakNetBean(BreakNetBean event) {//断网
        ReleasePlayer();
        finish();
    }

    ForegroundCallbacks.Listener foregroundCallbacks = new ForegroundCallbacks.Listener() {
        @Override
        public void onBecameForeground() {
//                  preferencesHelper.saveIsBackground(false);
            isQuitActivity = false;

            if (isFirstInto) {
                isFirstInto = false;
            } else {
                if (isExecuteThe && !isYetUploadData) {
                    stayTime = (System.currentTimeMillis() - startTimeMillis) / 1000 + "";
                    addTrainingResult(coursewareId + "", startTime, name, pass, stayTimeList, "", ""
                            , stayTime, groupId);
                }
            }
        }

        @Override
        public void onBecameBackground() {
//                   preferencesHelper.saveIsBackground(true);
            isQuitActivity = true;
            if (null != playerDoWhatThing && playerDoWhatThing.isPlaying()) {

                playerDoWhatThing.stop();
                isPlayingVoice = false;
                FristVoiceFinished = true;
            }

            if (null != player && player.isPlaying()) {
                if (voiceListData.size() > 0) {
                    voiceListData.remove(0);
                }
                player.stop();
                isPlayingVoice = false;
            }

            if (null != mediaPlayer && mediaPlayer.isPlaying()) {
                if (voiceListData.size() > 0) {
                    voiceListData.remove(0);
                }
                mediaPlayer.stop();
                isPlayingVoice = false;
            }


        }
    };

    private void setScreenLock() {
        screenListener = new ScreenListener(this);
        screenListener.begin(new ScreenListener.ScreenStateListener() {

            @Override
            public void onUserPresent() {
                Log.e("onUserPresent", "onUserPresent");
            }

            @Override
            public void onScreenOn() {
                Log.e("onScreenOn", "onScreenOn");
                isQuitActivity = false;

                if (isTwoInto) {//第一次进入activity就会进入这个方法
                    isTwoInto = false;
                } else {
//
                    if (isExecuteThe && !isYetUploadData) {
                        stayTime = (System.currentTimeMillis() - startTimeMillis) / 1000 + "";
                        addTrainingResult(coursewareId + "", startTime, name, pass, stayTimeList, "", ""
                                , stayTime, groupId);
                    }

                }
            }

            @Override
            public void onScreenOff() {
                Log.e("onScreenOff", "onScreenOff");
                isQuitActivity = true;

                if (null != playerDoWhatThing && playerDoWhatThing.isPlaying()) {

                    playerDoWhatThing.stop();
                    isPlayingVoice = false;
                    FristVoiceFinished = true;
                }

                if (null != player && player.isPlaying()) {
                    if (voiceListData.size() > 0) {
                        voiceListData.remove(0);
                    }
                    player.stop();
                    isPlayingVoice = false;
                }

                if (null != mediaPlayer && mediaPlayer.isPlaying()) {
                    if (voiceListData.size() > 0) {
                        voiceListData.remove(0);
                    }
                    mediaPlayer.stop();
                    isPlayingVoice = false;
                }


            }
        });
    }

    private void mergeText() {
        isPerformOverMergeText = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                playLocalVoiceOnLine(sentenceGroupTrainingBean.getGroupRecord());
                playBgVoice();
                String tv_content1Str = tv_content1.getText().toString();
                String tv_content2Str = tv_content2.getText().toString();


                int width = tv_content1.getWidth();

                TextPaint paint = tv_content1.getPaint();
                paint.setTextSize(tv_content1.getTextSize());
                float textWidth = paint.measureText(tv_content1.getText().toString());//这个方法能把文本所占宽度衡量出来.
                int i1 = width - (int) textWidth;

                paint = tv_content2.getPaint();
                paint.setTextSize(tv_content2.getTextSize());
                float textWidth2 = paint.measureText(tv_content2.getText().toString());//这个方法能把文本所占宽度衡量出来.
                int i2 = width - (int) textWidth2;

                int i3 = (rightChildTextTwo.getLeft() - leftChildTextOne.getLeft() - leftChildTextOne.getWidth());
                int distance_x = (i3 + i1 / 2 + i2 / 2) / 2 - 38;//15是调节用的

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) leftChildTextOne.getLayoutParams();
                layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                leftChildTextOne.setLayoutParams(layoutParams);
                leftChildTextOne.setBackground(null);

                LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) rightChildTextTwo.getLayoutParams();
                layoutParams2.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParams2.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParams2.leftMargin = 0;
                rightChildTextTwo.setLayoutParams(layoutParams2);
                rightChildTextTwo.setBackground(null);

                RelativeLayout.LayoutParams ll_text_parentLayoutParams = (RelativeLayout.LayoutParams) llTextParent.getLayoutParams();
                ll_text_parentLayoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                ll_text_parentLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                llTextParent.setLayoutParams(ll_text_parentLayoutParams);
                llTextParent.setBackgroundColor(JuZiChengZuXunLianActivity.this.getResources().getColor(R.color.white));

                ObjectAnimator obx1 = null;
                if (tv_content1Str.length() == tv_content2Str.length()) {
                    ObjectAnimator obx = ObjectAnimator.ofFloat(leftChildTextOne, "translationX",
                            distance_x);
                    obx.setDuration(1000);
                    obx.start();

                    obx1 = ObjectAnimator.ofFloat(rightChildTextTwo, "translationX",
                            -distance_x);
                    obx1.setDuration(1000);
                    obx1.start();
                }
                if (tv_content1Str.length() < tv_content2Str.length()) {

                    int i = tv_content2Str.length() - tv_content1Str.length();

                    ObjectAnimator obx = ObjectAnimator.ofFloat(leftChildTextOne, "translationX",
                            distance_x - i * 12);
                    obx.setDuration(1000);
                    obx.start();

                    obx1 = ObjectAnimator.ofFloat(rightChildTextTwo, "translationX",
                            -distance_x - i * 12);
                    obx1.setDuration(1000);
                    obx1.start();
                }

                obx1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        llTextParent.setBackground(null);

                        int width = llTextParent.getWidth();
                        int height = llTextParent.getHeight();
                        RelativeLayout.LayoutParams ll_text_parentLayoutParams = (RelativeLayout.LayoutParams) llTextParentBg.getLayoutParams();
                        ll_text_parentLayoutParams.width = width;
                        ll_text_parentLayoutParams.height = height;
                        llTextParentBg.setLayoutParams(ll_text_parentLayoutParams);

                        llTextParentBg.setVisibility(View.VISIBLE);

                        llTextParentBg.setBackgroundResource(R.drawable.flash_png);

                        ObjectAnimator sax = ObjectAnimator.ofFloat(llTextParentBg, "scaleX", 1f, 0.7f);
                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(sax);
                        set.setDuration(800);
                        set.start();

                        int duration = 0;
                        for (int i = 0; i < frameAnim1.getNumberOfFrames(); i++) {
                            duration += frameAnim1.getDuration(i);
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                frameAnim1.stop();
                                frameAnim1.start();
                            }
                        }, duration);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                frameAnim1.selectDrawable(1);      //选择当前动画的第一帧，然后停止
                                frameAnim1.stop();
                                isAnimationFinished = true;

                                if (isBackgroundVoiceFinished && !isQuitActivity && !isYetUploadData) {
                                    stayTime = (System.currentTimeMillis() - startTimeMillis) / 1000 + "";
                                    addTrainingResult(coursewareId + "", startTime, name, pass, stayTimeList, "", ""
                                            , stayTime, groupId);
                                }
                            }
                        }, duration * 2);

                        frameAnim1.start();
                        isExecuteThe = true;
                    }
                });

            }
        }, 1000);
    }

    private void playBgVoice() {
        int max = 8;
        Random random = new Random();
        int i1 = random.nextInt(max) + 1;
        if (i1 == 1) {
            playLocalVoiceBg("22729087_christmas-piano-o-holy-night_by_prostorecords_preview.mp3");
        } else if (i1 == 2) {
            playLocalVoiceBg("22729087_christmas-piano-o-holy-night_by_prostorecords_preview_1.mp3");
        } else if (i1 == 3) {
            playLocalVoiceBg("22729087_christmas-piano-o-holy-night_by_prostorecords_preview_2.mp3");
        } else if (i1 == 4) {
            playLocalVoiceBg("22729087_christmas-piano-o-holy-night_by_prostorecords_preview_3.mp3");
        } else if (i1 == 5) {
            playLocalVoiceBg("22729161_beautiful-christmas-advertising-background_by_ikoliks_previessw_4.mp3");
        } else if (i1 == 6) {
            playLocalVoiceBg("22729161_beautiful-christmas-advertising-background_by_ikoliks_preview_1.mp3");
        } else if (i1 == 7) {
            playLocalVoiceBg("22729161_beautiful-christmas-advertising-background_by_ikoliks_preview_2.mp3");
        } else if (i1 == 8) {
            playLocalVoiceBg("22729161_beautiful-christmas-advertising-background_by_ikoliks_preview_4.mp3");
        } else if (i1 == 9) {
            playLocalVoiceBg("22729161_beautiful-christmas-advertising-background_by_ikoliks_preview_5.mp3");
        }
    }

    private void playLocalVoiceBg(String videoName) {
        if (isQuitActivity) {
            return;
        }
//        if (player != null) {
//            player.stop();
//            //关键语句
//            player.reset();
//            player.release();
//            player = null;
//        }
        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor afd = assetManager.openFd("boy/" + videoName);
            player = new MediaPlayer();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.setLooping(false);//循环播放
//            player.prepare();
//            player.start();

            //3 准备播放
            player.prepareAsync();
            //3.1 设置一个准备完成的监听
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (isQuitActivity) {
                        return;
                    }
                    // 4 开始播放
                    player.start();


                }
            });
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (player != null) {
                        if (player.isPlaying()) {
                            player.stop();
                        }
                        //关键语句
                        player.reset();
                        player.release();
                        player = null;
                    }
                    isBackgroundVoiceFinished = true;

                    if (isAnimationFinished && !isQuitActivity && !isYetUploadData) {
                        stayTime = (System.currentTimeMillis() - startTimeMillis) / 1000 + "";
                        addTrainingResult(coursewareId + "", startTime, name, pass, stayTimeList, "", ""
                                , stayTime, groupId);
                    }
                }
            });
            player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    if (player != null) {
                        if (player.isPlaying()) {
                            player.stop();
                        }
                        //关键语句
                        player.reset();
                        player.release();
                        player = null;
                    }
                    Log.e("bofang", "onError: i" + i + "i1" + i1);
                    return false;
                }
            });
        } catch (Exception e) {
            if (player != null) {
                if (player.isPlaying()) {
                    player.stop();
                }
                //关键语句
                player.reset();
                player.release();
                player = null;
            }
            Log.e("bofang", "playLocalVoiceOnLineGroupRecord: IOException" + e.toString());
            e.printStackTrace();
        }
    }

    private int coursewareId;//课件id
    private String startTime;
    private String name;
    private String pass = "1";//是否通过 1 是 0 否
    private String stayTimeList = "";
    private String stayTime;
    private String groupId = "";
    private long startTimeMillis;
    private long currentClickOneStartTime;
    private long currentClickTwoStartTime;

    /**
     * 添加儿童训练测试课件结果
     * * @return token    是	string	用户标识
     * coursewareId	是	int	课件id
     * scene	是	string	学习场景 1训练 2测试 3意义
     * module	是	string	训练测试模块 1名词 2动词 3句子组成 4句子分解
     * startTime	是	data	开始时间
     * name	是	string	课件名字 例：男孩吃苹果
     * pass	是	string	是否通过 1 是 0 否
     * stayTimeList	否	string	停留时间 逗号分隔 例”5,5,6,7”
     * disTurbName	否	string	干扰课件名称 只有名词意义才统计
     * errorType	否	string	错误类型 1干扰形容词 2干扰名词 3预选形容词 4预选名词 只有名词意义级别才统计错误类型
     * stayTime	是	int	总停留时间
     * groupId	是	int	当前组课件记录表id
     */
    private void addTrainingResult(String coursewareId, String startTime
            , String name, String pass, String stayTimeList, String disTurbName, String errorType
            , String stayTime, String groupId) {
        PreferencesHelper helper = new PreferencesHelper(JuZiChengZuXunLianActivity.this);
        String token = helper.getToken();
        if (TextUtils.isEmpty(groupId)) {
            groupId = "";
        }
        String url = Setting.addTrainingResult();
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("token", token);
        stringStringHashMap.put("coursewareId", coursewareId);
        stringStringHashMap.put("scene", "1");// 1训练 2测试 3意义
        stringStringHashMap.put("module", "3");//1名词 2动词 3句子组成 4句子分解
        stringStringHashMap.put("startTime", startTime);
        stringStringHashMap.put("name", name);
        stringStringHashMap.put("pass", pass);

        stringStringHashMap.put("stayTimeList", stayTimeList);
        stringStringHashMap.put("stayTime", stayTime);
        stringStringHashMap.put("groupId", groupId);
        Log.e("数据", "stringStringHashMap:" + stringStringHashMap.toString());

        if (isFinish) {
            return;
        }
        OkHttpUtils
                .post().params(stringStringHashMap)
                .url(url)//接口地址
                .id(2)//XX接口的标识
                .tag("addTrainingResult")
                .build()
                .execute(new BaseStringCallback_Host(JuZiChengZuXunLianActivity.this, this));
        isYetUploadData = true;
    }


    @Override
    public void RequestError(Object var1, int var2, String var3) {

    }


    private boolean isFinish = false;

    @Override
    public void onBackPressed() {

        isQuitActivity = true;
        isFinish = true;
        OkHttpUtils.getInstance().cancelTag("addTrainingResult");

        if (null != foregroundCallbacks) {
            ForegroundCallbacks.get().removeListener(foregroundCallbacks);
            foregroundCallbacks = null;

            screenListener.unregisterListener();
        }
        handler.removeCallbacksAndMessages(null);
        ReleasePlayer();
        super.onBackPressed();
    }

    @OnClick({R.id.iv_home})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_home:
                isQuitActivity = true;
                isFinish = true;
                OkHttpUtils.getInstance().cancelTag("addTrainingResult");

                if (null != foregroundCallbacks) {
                    ForegroundCallbacks.get().removeListener(foregroundCallbacks);
                    foregroundCallbacks = null;

                    screenListener.unregisterListener();
                }
                handler.removeCallbacksAndMessages(null);
                ReleasePlayer();
                finish();
                break;
            case R.id.fl_choose1:
                break;
        }
    }


    private void doAnim() {
        int duration = 0;
        for (int i = 0; i < frameAnim1.getNumberOfFrames(); i++) {
            duration += frameAnim1.getDuration(i);
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                frameAnim1.stop();
                frameAnim1.start();
            }
        }, duration);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                frameAnim1.selectDrawable(1);      //选择当前动画的第一帧，然后停止
                frameAnim1.stop();

                currentClickOneStartTime = System.currentTimeMillis();
                startTime(currentFirstPotision, loopTimeOne, loopRateOne);
            }
        }, duration * 2);

        frameAnim1.start();
    }

    List<String> voiceListData = new ArrayList<>();

    private void playLocalVoice(String videoName, boolean isFirst) {
        voiceHandler.sendEmptyMessage(1);
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 1000);

        if (isQuitActivity) {
            return;
        }
//        if (playerDoWhatThing != null) {
//            playerDoWhatThing.stop();
//            //关键语句
//            playerDoWhatThing.reset();
//            playerDoWhatThing.release();
//            playerDoWhatThing = null;
//        }
        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor afd = assetManager.openFd("boy/" + videoName);
            playerDoWhatThing = new MediaPlayer();
            playerDoWhatThing.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            playerDoWhatThing.setLooping(false);//循环播放

            //3 准备播放
            playerDoWhatThing.prepareAsync();
            //3.1 设置一个准备完成的监听
            playerDoWhatThing.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (isQuitActivity) {
                        return;
                    }
                    // 4 开始播放
                    playerDoWhatThing.start();
                }
            });

            playerDoWhatThing.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (playerDoWhatThing != null) {
                        if (playerDoWhatThing.isPlaying()) {
                            playerDoWhatThing.stop();
                        }
                        //关键语句
                        playerDoWhatThing.reset();
                        playerDoWhatThing.release();
                        playerDoWhatThing = null;
                    }
                    FristVoiceFinished = true;
                    frameAnim1.stop();
                }
            });

            playerDoWhatThing.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    if (playerDoWhatThing != null) {
                        if (playerDoWhatThing.isPlaying()) {
                            playerDoWhatThing.stop();
                        }
                        //关键语句
                        playerDoWhatThing.reset();
                        playerDoWhatThing.release();
                        playerDoWhatThing = null;
                    }
                    Log.e("bofang", "onError: i" + i + "i1" + i1);
                    return false;
                }
            });
        } catch (Exception e) {
            if (playerDoWhatThing != null) {
                if (playerDoWhatThing.isPlaying()) {
                    playerDoWhatThing.stop();
                }
                //关键语句
                playerDoWhatThing.reset();
                playerDoWhatThing.release();
                playerDoWhatThing = null;
            }
            Log.e("bofang", "playLocalVoiceOnLineGroupRecord: IOException" + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 在线
     *
     * @param videoName
     */
    private void playLocalVoiceOnLine(String videoName) {
        if (isQuitActivity) {
            return;
        }

        if (TextUtils.isEmpty(videoName)) {
            if (voiceListData.size() == 2) {//第二个语音说完了
                voiceListData.remove(0);

                if (playNextCardVoice) {//已经点击了，就播放第三个。
                    playLocalVoiceOnLine(voiceListData.get(0));
                }
            } else if (voiceListData.size() == 1) {//第二个语音说完了
                voiceListData.remove(0);
            }
            return;
        }
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            //关键语句
//            mediaPlayer.reset();
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
        //1 初始化mediaplayer
        mediaPlayer = new MediaPlayer();
        //2 设置到播放的资源位置 path 可以是网络 路径 也可以是本地路径
        try {
            mediaPlayer.setDataSource(videoName);
            //3 准备播放
            mediaPlayer.prepareAsync();
            //3.1 设置一个准备完成的监听
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (isQuitActivity) {
                        return;
                    }
                    // 4 开始播放
                    mediaPlayer.start();
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer1) {
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                        //关键语句
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    if (voiceListData.size() == 2) {//第二个语音说完了
                        voiceListData.remove(0);

                        if (playNextCardVoice) {//已经点击了，就播放第三个。
                            playLocalVoiceOnLine(voiceListData.get(0));
                        }
                    } else if (voiceListData.size() == 1) {//第二个语音说完了
                        voiceListData.remove(0);
                    }

                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer1, int i, int i1) {
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                        //关键语句
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    Log.e("bofang", "onError: i" + i + "i1" + i1);
                    return false;
                }
            });
        } catch (Exception e) {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                //关键语句
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            Log.e("bofang", "playLocalVoiceOnLineGroupRecord: IOException" + e.toString());
            e.printStackTrace();
        }

    }
}
