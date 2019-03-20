package com.easychange.admin.smallrain.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easychange.admin.smallrain.MyApplication;
import com.easychange.admin.smallrain.R;
import com.easychange.admin.smallrain.base.BaseActivity;
import com.easychange.admin.smallrain.utils.AnimationHelper;
import com.easychange.admin.smallrain.utils.MyUtils;
import com.easychange.admin.smallrain.views.CompletedView;
import com.easychange.admin.smallrain.views.IndicatorView;
import com.easychange.admin.smallrain.views.WaveCircleView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MingciTestActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.ll_indicator)
    IndicatorView llIndicator;
    @BindView(R.id.iv_home)
    ImageView ivHome;
    @BindView(R.id.rl_top)
    RelativeLayout rlTop;
    @BindView(R.id.iv_img)
    ImageView ivImg;
    @BindView(R.id.iv_content1)
    ImageView ivContent1;
    @BindView(R.id.iv_content2)
    ImageView ivContent2;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.iv_choose1)
    ImageView ivChoose1;
    @BindView(R.id.tv_choose1)
    TextView tvChoose1;
    @BindView(R.id.iv_choose2)
    ImageView ivChoose2;
    @BindView(R.id.tv_choose2)
    TextView tvChoose2;
    @BindView(R.id.ll_choose1)
    LinearLayout ll_choose1;
    @BindView(R.id.ll_choose2)
    LinearLayout ll_choose2;
    @BindView(R.id.tv_paint)
    TextView tvPaint;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_money)
    TextView tv_money;
    @BindView(R.id.rl_root)
    RelativeLayout rl_root;
    @BindView(R.id.fl_root)
    FrameLayout flRoot;
    @BindView(R.id.tasks_view)
    CompletedView mTasksView;
    @BindView(R.id.waveCirlceView)
    WaveCircleView waveCirlceView;
    @BindView(R.id.waveCirlceView2)
    WaveCircleView waveCirlceView2;
    @BindView(R.id.fl_choose1)
    FrameLayout fl_choose1;
    @BindView(R.id.fl_choose2)
    FrameLayout fl_choose2;
    @BindView(R.id.iv_point)
    ImageView iv_point;
    @BindView(R.id.iv_point2)
    ImageView iv_point2;
    @BindView(R.id.ll_money)
    LinearLayout ll_money;
    private int mTotalProgress = 100;
    private int mCurrentProgress = 0;
    private boolean isCorrect;
    private MediaPlayer player;

    private Boolean isFirstVoiceFinished=false;
    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            //进度开启
            new Thread(new ProgressRunable()).start();

            isFirstVoiceFinished=true;
        }
    };
    private Handler handler = new Handler();
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mingci_test);
        ButterKnife.bind(this);
//        AnimationHelper.startScaleAnimation(this, ivImg);
        fl_choose1.setOnClickListener(this);
        fl_choose2.setOnClickListener(this);
        ivHome.setOnClickListener(this);
        position = getIntent().getIntExtra("position", 0);
        initView();

    }

    private void initView() {
        if (position == 0) {
            ll_money.setVisibility(View.GONE);
        } else if (position == 1) {
            ll_money.setVisibility(View.VISIBLE);
            tv_money.setText("x 1");
            llIndicator.setSelectedPosition(1);
            ivImg.setImageResource(R.drawable.back_hou);
            ivChoose1.setImageResource(R.drawable.hou_bg);
            ivChoose2.setImageResource(R.drawable.black_img);
            tvPaint.setText("黑");
            tvContent.setText("猴");
            tvChoose1.setText("猴");
            tvChoose2.setText("黑");
        } else if (position == 2) {
            ll_money.setVisibility(View.VISIBLE);
            tv_money.setText("x 2");
            llIndicator.setSelectedPosition(2);
            ivImg.setImageResource(R.drawable.huitu);
            ivChoose1.setImageResource(R.drawable.huitu);
            ivChoose2.setImageResource(R.drawable.gray_img);
            tvPaint.setText("灰");
            tvContent.setText("兔");
            tvChoose1.setText("兔");
            tvChoose2.setText("灰");
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                playLocalVoice("男-这是什么样的东西.MP3");
                //缩放
                Animation animation = android.view.animation.AnimationUtils.loadAnimation(MingciTestActivity.this, R.anim.anim_scale_pic);
                ivImg.startAnimation(animation);

            }
        }, 2000);

    }

    private void playLocalVoice(String videoName) {
        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor afd = assetManager.openFd("boy/" + videoName);
            player = new MediaPlayer();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.setLooping(false);//循环播放
            player.prepare();
            player.start();
            player.setOnCompletionListener(completionListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playLocalVoice2(String videoName) {
        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor afd = assetManager.openFd("boy/" + videoName);
            player = new MediaPlayer();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.setLooping(false);//循环播放
            player.prepare();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ProgressRunable implements Runnable {
        @Override
        public void run() {
            while (mCurrentProgress < mTotalProgress) {
                mCurrentProgress += 1;
                mTasksView.setProgress(mCurrentProgress);
                if (mCurrentProgress == mTotalProgress) {
                    //小手辅助--别忘记切换到主线程更新UI
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            iv_point2.setVisibility(View.VISIBLE);
                            waveCirlceView2.setVisibility(View.VISIBLE);
                            waveCirlceView2.startWave();
                        }
                    }, 500);
                }
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_home:
                finish();
                break;
            case R.id.fl_choose1:
                if (!isCorrect) {
                    return;
                }
                if (!isFirstVoiceFinished) {
                    return;
                }

                fl_choose1.setClickable(false);
                AnimationHelper.startScaleAnimation(mContext, fl_choose1);
                if (position == 0) {
                    playLocalVoice("男-猫.MP3");
                } else if (position == 1) {
                    playLocalVoice("男-猴.MP3");
                } else if (position == 2) {
                    playLocalVoice("男-兔.MP3");
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int screenWidth = MyUtils.getScreenWidth(mContext);
                        int screenHeight = MyUtils.getScreenHeight(mContext);
                        ObjectAnimator sax = ObjectAnimator.ofFloat(fl_choose1, "scaleX", 1f, 0.6f);
                        ObjectAnimator say = ObjectAnimator.ofFloat(fl_choose1, "scaleY", 1f, 0.4f);
                        int marginleft = (screenWidth - MyUtils.dip2px(MyApplication.getGloableContext(), 200)) / 2;
                        int x = screenWidth - marginleft - MyUtils.dip2px(MyApplication.getGloableContext(), 40 + 140 + 90 - (70 + 45));
                        ObjectAnimator obx = ObjectAnimator.ofFloat(fl_choose1, "translationX", x);
                        //因为图片透明边距的问题微调
                        int y = screenHeight - MyUtils.dip2px(MyApplication.getGloableContext(), 350 + 20 + 140 + 55 - (70 + 27.5f))
                                - MyUtils.getStatusBarHeight(MyApplication.getGloableContext());
                        ObjectAnimator oby = ObjectAnimator.ofFloat(fl_choose1, "translationY", -y);
                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(sax, say, obx, oby);
                        set.setDuration(2000);
                        set.start();
                        set.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tvContent.setVisibility(View.VISIBLE);
                                //透明度渐变显示
                                ObjectAnimator animator = ObjectAnimator.ofFloat(tvContent, "alpha", 0.5f, 1f);
                                animator.setDuration(1000);
                                animator.start();
                                fl_choose1.setVisibility(View.GONE);
                                ivContent2.setVisibility(View.INVISIBLE);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mergeText();
                                    }
                                }, 1000);
                            }
                        });
                    }
                }, 1000);
                break;
            case R.id.fl_choose2:
                if (!isFirstVoiceFinished) {
                    return;
                }

                //小手指示隐藏掉
                iv_point2.setVisibility(View.GONE);
                waveCirlceView2.setVisibility(View.GONE);
                isCorrect = true;
                fl_choose2.setClickable(false);
                AnimationHelper.startScaleAnimation(mContext, fl_choose2);
                if (position == 0) {
                    playLocalVoice("男-黄.MP3");
                } else if (position == 1) {
                    playLocalVoice("男-黑.MP3");
                } else if (position == 2) {
                    playLocalVoice("男-灰.MP3");
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int screenWidth = MyUtils.getScreenWidth(mContext);
                        int screenHeight = MyUtils.getScreenHeight(mContext);
                        ObjectAnimator sax = ObjectAnimator.ofFloat(fl_choose2, "scaleX", 1f, 0.6f);
                        ObjectAnimator say = ObjectAnimator.ofFloat(fl_choose2, "scaleY", 1f, 0.4f);
                        //因为图片透明边距的问题微调
                        int y = screenHeight - MyUtils.dip2px(MyApplication.getGloableContext(), 350 + 20 + 140 + 55 - (70 + 27.5f))
                                - MyUtils.getStatusBarHeight(MyApplication.getGloableContext());
                        int marginleft = (screenWidth - MyUtils.dip2px(MyApplication.getGloableContext(), 200)) / 2;
                        int x = screenWidth - marginleft - MyUtils.dip2px(MyApplication.getGloableContext(), 40 + 140 + 90 - (70 + 45));
                        ObjectAnimator obx = ObjectAnimator.ofFloat(fl_choose2, "translationX", -x);
                        ObjectAnimator oby = ObjectAnimator.ofFloat(fl_choose2, "translationY", -y);
                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(sax, say, obx, oby);
                        set.setDuration(2000);
                        set.start();
                        set.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tvPaint.setVisibility(View.VISIBLE);
                                //透明度渐变显示
                                ObjectAnimator animator = ObjectAnimator.ofFloat(tvPaint, "alpha", 0.5f, 1f);
                                animator.setDuration(1000);
                                animator.start();
                                fl_choose2.setVisibility(View.GONE);
                                ivContent1.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }, 1000);
                break;
        }
    }

    private void mergeText() {
        if (position == 0) {
            playLocalVoice("男-黄猫.MP3");
        } else if (position == 1) {
            playLocalVoice("男-黑猴.MP3");
        } else if (position == 2) {
            playLocalVoice("男-灰兔.MP3");
        }
        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvPaint.getLayoutParams();
        layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        tvPaint.setLayoutParams(layoutParams);
        tvPaint.setBackground(null);

        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) tvContent.getLayoutParams();
        layoutParams2.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        layoutParams2.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        layoutParams2.leftMargin = 0;
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        tvContent.setLayoutParams(layoutParams2);
        tvContent.setBackground(null);

        tvPaint.post(new Runnable() {
            @Override
            public void run() {
                rl_root.setBackgroundResource(R.drawable.painttext_bg);
                int i = (MyUtils.dip2px(MingciTestActivity.this, 190) - (tvPaint.getWidth() + tvContent.getWidth())) / 2;
                int paintX = MyUtils.dip2px(MingciTestActivity.this, 85) - tvPaint.getWidth();
                int contentX = MyUtils.dip2px(MingciTestActivity.this, 85 - 20) - tvContent.getWidth();
                TranslateAnimation tr = new TranslateAnimation(-contentX, -i, 0, 0);
                tr.setDuration(1000);
                tr.setFillAfter(true);
                tvContent.startAnimation(tr);

                TranslateAnimation tr2 = new TranslateAnimation(paintX, i, 0, 0);
                tr2.setDuration(1000);
                tr2.setFillAfter(true);
                tvPaint.startAnimation(tr2);
                tr2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        AnimationHelper.startScaleAnimation(MingciTestActivity.this, ivImg);
                        rl_root.setBackground(null);
                        flRoot.setBackgroundResource(R.drawable.faguang_bg);

                        //发光背景缩小一些
                        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) flRoot.getLayoutParams();
                        layoutParams1.width = MyUtils.dip2px(MyApplication.getGloableContext(), 150);
                        flRoot.setLayoutParams(layoutParams1);


                        //最后放大一下
                        Animation aa = android.view.animation.AnimationUtils.loadAnimation(MingciTestActivity.this, R.anim.anim_scale_pic);
                        ivImg.startAnimation(aa);
                        aa.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                if (position == 0) {
                                    Intent intent = new Intent(MingciTestActivity.this, MingciTestActivity.class);
                                    intent.putExtra("position", 1);
                                    startActivity(intent);
                                } else if (position == 1) {
                                    Intent intent = new Intent(MingciTestActivity.this, MingciTestActivity.class);
                                    intent.putExtra("position", 2);
                                    startActivity(intent);
                                } else if (position == 2) {
                                    Intent intent = new Intent(MingciTestActivity.this, MingciTest2Activity.class);
                                    startActivity(intent);
                                }
                                finish();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (player != null)
            player.stop();
    }
}