package com.ashokkumarshrestha.sortinghat2;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.AdView;


public class Level8Activity extends AppCompatActivity implements View.OnClickListener, RewardedVideoAdListener {

    private RewardedVideoAd mAd;
    private AdView mAdView;
    private PrefManager prefManager;
    private GameSound gameSound;
    private boolean showHint, isSound, isVidAds;
    private TextView txtHint, txtPoints;
    private Button btnHorcrux, btnNoHorcrux, btnSaveLife;
    private ImageButton btnInfo, btnHome;
    private LinearLayout btnLayout;
    private int currButton = 0, prevButton = 0, points = 10, life = 1;
    private Dialog mDialog;
    private Button[] buttons;
    private int[] arrId;
    private String arrQues[][] = {
            {"No Horcrux here! If there is a horcrux in second row, it is not adjacent to me.", "N", "O"},
            {"No Horcrux here! I share a diagonal with only one horcrux", "N", "C"},
            {"No Horcrux here! The first horcrux that was destroyed can be found in second column.", "N", "C"},
            {"Rowena Ravenclaw's Diadem", "H", "C"},
            {"No Horcrux here!", "N", "C"},
            {"No Horcrux here! Only one column contains three Horcruxes.", "N", "C"},
            {"No Horcrux here! The two horcruxes that are both living creatures in their own right share a column with each other.", "N", "C"},
            {"No Horcrux here! I am adjacent to exactly one horcrux and I do not share a diagonal with any horcruxes.", "N", "C"},
            {"No Horcrux here!", "N", "C"},
            {"No Horcrux here!", "N", "C"},
            {"No Horcrux here!", "N", "C"},
            {"No Horcrux here! I am not adjacent to any horcruxes.", "N", "C"},
            {"No Horcrux here! There are no horcruxes in either A26 or A46.", "N", "C"},
            {"Marvolo gaunt's Ring", "H", "C"},
            {"No Horcrux here!", "N", "C"},
            {"Salazar Slytherin's Locket", "H", "C"},
            {"No Horcrux here! I am adjacent to exactly one horcrux.", "N", "C"},
            {"No Horcrux here! There are two horcruxes in this row.", "N", "C"},
            {"Harry potter", "H", "C"},
            {"No horcruxes here! Nagini is adjacent to exactly two horcruxes.", "N", "C"},
            {"No Horcrux here!", "N", "C"},
            {"No Horcrux here! The two horcruxes that Voldemort entrusted to Death Eaters for safekeeping can be found in the same row as each other.", "N", "C"},
            {"No Horcrux here!", "N", "C"},
            {"No Horcrux here!", "N", "C"},
            {"Nagini", "H", "C"},
            {"Tom Riddle's diary", "H", "C"},
            {"No Horcrux here!", "N", "C"},
            {"Helga Hufflepuff's cup", "H", "C"},
            {"No Horcrux here! All horcruxes that once belonged to Hogwarts founders can be found in fourth column.", "N", "C"},
            {"No Horcrux here! I share a diagonal with a horcrux that once belonged to Voldemort's mother.", "N", "C"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level8);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        prefManager = new PrefManager(this);
        gameSound = new GameSound(this);
        isSound = prefManager.getPoints("GameSound") == 0 ? false : true;
        points = prefManager.getPoints("total_points");
        initId();
        displayUIs();

        updateBtn(prevButton);
        buttons[prevButton].setBackgroundResource(R.drawable.bg_btn_correct);

        /*-----------------Setup Ads------------------*/
        MobileAds.initialize(getApplicationContext(),"ca-app-pub-7250925653938754/1906064888");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        /*---------Use an activity context to get the rewarded video instance.-------*/
        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
    }

    private void loadRewardedVideoAd() {
        mAd.loadAd("ca-app-pub-7250925653938754/1825162070", new AdRequest.Builder().addTestDevice("62157022C502ECE4B82BE08B1F2CE1EE").build());
    }

    private void displayUIs() {
        LinearLayout playout = (LinearLayout) findViewById(R.id.layoutBox);
        final int row = 5, col = 6;
        for (int i = 0; i < row; i++) {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < col; j++) {
                final int x = i * col + j;
                buttons[x] = new Button(this);
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lparams.weight = 1;
                lparams.setMargins(1, 1, 1, 1);
                buttons[x].setLayoutParams(lparams);
                buttons[x].setBackgroundResource(R.drawable.bg_btn_normal);
                buttons[x].setText("A" + (i+1) + "" + (j+1));
                buttons[x].setTextSize(14);

                buttons[x].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateBtn(x);
                    }
                });
                layout.addView(buttons[x]);
            }
            playout.addView(layout);
        }
    }

    private void updateBtn(int index) {

        if (showHint) {
            saveLife();

        } else {
            if (index >= 0 && index < arrQues.length) {

                if (arrQues[prevButton][2] == "O") {
                    buttons[prevButton].setBackgroundResource(R.drawable.bg_btn_correct);
                } else {
                    buttons[prevButton].setBackgroundResource(R.drawable.bg_btn_normal);
                }

                buttons[index].setBackgroundResource(R.drawable.bg_btn_pressed);
                prevButton = index;
                currButton = index;
                if (arrQues[index][2] == "O") {
                    btnHorcrux.setVisibility(View.INVISIBLE);
                    btnNoHorcrux.setVisibility(View.INVISIBLE);
                    txtHint.setText(arrQues[index][0]);
                } else {
                    btnHorcrux.setVisibility(View.VISIBLE);
                    btnNoHorcrux.setVisibility(View.VISIBLE);
                    txtHint.setText("Is it a horcrux?");
                }
            }
        }
    }

    private void displayUI() {
        LinearLayout playout = (LinearLayout) findViewById(R.id.layoutBox);
        final int row = 5, col = 6;
        for (int i = 0; i < row; i++) {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < col; j++) {
                final Button btn = new Button(this);
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lparams.weight = 1;
                lparams.setMargins(1, 1, 1, 1);
                btn.setLayoutParams(lparams);
                btn.setBackgroundResource(R.drawable.bg_btn_normal);
                btn.setText("A" + i + "" + j);
                btn.setTextSize(14);
                //btn.setTag("aaa");
                int inn = i * col + j;
                btn.setId(arrId[inn]);

                final int finalI = i;
                final int finalJ = j;
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(Level8Activity.this,"A"+ finalI +""+ finalJ, Toast.LENGTH_SHORT).show();
                        int index = finalI * col + finalJ;
                        if (index >= 0 && index < arrQues.length) {
                            btn.setBackgroundResource(R.drawable.bg_btn_pressed);
                            currButton = index;
                            if (arrQues[index][2] == "O") {
                                btnHorcrux.setVisibility(View.INVISIBLE);
                                btnNoHorcrux.setVisibility(View.INVISIBLE);
                                txtHint.setText(arrQues[index][0]);
                            } else {
                                btnHorcrux.setVisibility(View.VISIBLE);
                                btnNoHorcrux.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                });
                layout.addView(btn);
            }
            playout.addView(layout);
        }
    }

    private void initId() {
        txtPoints = (TextView) findViewById(R.id.txtScore);
        txtHint = (TextView) findViewById(R.id.txtHint);
        btnHorcrux = (Button) findViewById(R.id.btnHorcrux);
        btnNoHorcrux = (Button) findViewById(R.id.btnNoHorcrux);
        btnInfo = (ImageButton) findViewById(R.id.btnInfo);
        btnHome = (ImageButton) findViewById(R.id.btnHome);
        btnSaveLife = (Button) findViewById(R.id.btnSavelife);
        btnLayout = (LinearLayout) findViewById(R.id.layoutBtns);
        txtPoints.setText("Points: "+points);

        btnSaveLife.setOnClickListener(this);
        btnHorcrux.setOnClickListener(this);
        btnNoHorcrux.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
        btnHome.setOnClickListener(this);

        buttons = new Button[30];
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnHome:
                onBackPressed();
                break;
            case R.id.btnInfo:
                displayInfo();
                break;
            case R.id.btnSavelife:
                //pop up saving life options
                saveLife();
                break;
            case R.id.btnHorcrux:
                if (arrQues[currButton][1] == "H") {
                    btnHorcrux.setVisibility(View.INVISIBLE);
                    btnNoHorcrux.setVisibility(View.INVISIBLE);
                    arrQues[currButton][2] = "O";
                    txtHint.setText(arrQues[currButton][0]);
                    buttons[currButton].setBackgroundResource(R.drawable.bg_btn_correct);
                    buttons[currButton].setText("H");
                    updatePoints(5);
                } else {
                    //game over diplay points
                    prepareExit();
                }
                break;
            case R.id.btnNoHorcrux:
                if (arrQues[currButton][1] == "N") {
                    btnHorcrux.setVisibility(View.INVISIBLE);
                    btnNoHorcrux.setVisibility(View.INVISIBLE);
                    arrQues[currButton][2] = "O";
                    txtHint.setText(arrQues[currButton][0]);
                    buttons[currButton].setBackgroundResource(R.drawable.bg_btn_correct);
                    updatePoints(5);
                } else {
                    //game over diplay points
                    prepareExit();
                }
                break;

        }

    }

    private void saveLife() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.activity_savelife);

        // set the custom dialog components - text, image and button
        switch (life - 1) {
            case 3:
                ((ImageView) dialog.findViewById(R.id.imgStar3)).setImageResource(R.drawable.ic_heartd);
            case 2:
                ((ImageView) dialog.findViewById(R.id.imgStar2)).setImageResource(R.drawable.ic_heartd);
            case 1:
                ((ImageView) dialog.findViewById(R.id.imgStar1)).setImageResource(R.drawable.ic_heartd);
                break;
        }

        Button dialogButtonPoints = (Button) dialog.findViewById(R.id.btnCoins);
        Button dialogButtonAds = (Button) dialog.findViewById(R.id.btnAds);
        mDialog = dialog;
        // if button is clicked, close the custom dialog
        dialogButtonPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVidAds = true;
                updateSavePoints(dialog, "Sorry! Could not revive. Need 50 points for charm to work.");
            }
        });
        dialogButtonAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //increase points after ads is displayed and call the hint dialogue
                if (mAd.isLoaded()) {
                    mAd.show();
                } else {
                    //updateSavePoints(dialog, "Sorry! Could not revive. Need to watch ads for charm to work");
                    loadRewardedVideoAd();
                    if (mAd.isLoaded()) {
                        mAd.show();
                    }
                }
            }
        });

        dialog.show();


        //deduce points or show ads

    }

    private void updateSavePoints(final Dialog dialog, String failureMsg) {
        TextView textTitle = (TextView) dialog.findViewById(R.id.txtSaveResult);
        if (life > 0 && life < 4 && points >= 50 && isVidAds) {
            life++;
            isVidAds = false;            CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
                @Override
                public void onTick(long leftTimeInMilliseconds) {
                }

                @Override
                public void onFinish() {
                    dialog.dismiss();
                }
            }.start();
            //points -= 50;
            updatePoints(-50);
            /*if (isSound) {
                gameSound.playWin();
            }*/
            switch (life - 1) {
                case 3:
                    ((ImageView) dialog.findViewById(R.id.imgStar3)).setImageResource(R.drawable.ic_heartd);
                case 2:
                    ((ImageView) dialog.findViewById(R.id.imgStar2)).setImageResource(R.drawable.ic_heartd);
                case 1:
                    ((ImageView) dialog.findViewById(R.id.imgStar1)).setImageResource(R.drawable.ic_heartd);
                    break;
            }

            textTitle.setText("Congratulations! Life saved.");
            showHint = false;
            btnLayout.setVisibility(View.VISIBLE);
            btnSaveLife.setVisibility(View.GONE);
            txtHint.setText("Is it a horcrux?");
        } else {
            CountDownTimer countDownTimer = new CountDownTimer(7000, 1000) {
                @Override
                public void onTick(long leftTimeInMilliseconds) {
                }

                @Override
                public void onFinish() {
                    dialog.dismiss();
                }
            }.start();
            textTitle.setText(failureMsg);
        }
    }

    private void updatePoints(int point) {
        points += point;
        txtPoints.setText("Points: " + points);
        //ring the sound
        if (isSound) {
            gameSound.playWin();
        }
    }

    private void prepareExit() {
        //display dialogue to cntinue game at the expense of 50 coins or ads for 3 times
        showHint = true;
        if (isSound) {
            gameSound.playLoose();
        }
        txtHint.setText("Merlin's beard! You missed the Horcrux. You know, you can still save a life!");
        btnLayout.setVisibility(View.GONE);
        if (life <= 3) {
            btnSaveLife.setVisibility(View.VISIBLE);
        } else {
            //wait for 10 sec and exit
            txtHint.setText("You have lost all your lives. Voldemort wins! Better luck next time!");
            if (isSound) {
                gameSound.playLoose();
            }
            CountDownTimer countDownTimer = new CountDownTimer(4000, 1000) {
                // 500 means, onTick function will be called at every 500 milliseconds

                @Override
                public void onTick(long leftTimeInMilliseconds) {

                }

                @Override
                public void onFinish() {
                    prefManager.setPoints("Level 8", points);
                    onBackPressed();
                }
            }.start();
        }
    }

    private void displayInfo() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.activity_levelsinfo);


        // set the custom dialog components - text, image and button

        TextView textInfo = (TextView) dialog.findViewById(R.id.txtInstructions);
        textInfo.setText("Find the horcrux to defeat you-know-who." +
                " Use clues to find if the box contains horcrux or not." +
                "\n5 Points will be awarded for correct answer." +
                "\nYou can revive the life at the expense of 50 points or by watching ads." +
                "\n\nLet the magic begin!");


        ImageButton dialogButtonClose = (ImageButton) dialog.findViewById(R.id.btnClose);
        dialogButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        LinearLayout layoutAds = (LinearLayout) dialog.findViewById(R.id.layoutAds);
        layoutAds.setVisibility(View.GONE);

        dialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        prefManager.setPoints("total_points", points);
        prefManager.setPoints("Level 8", points);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAd.pause(this);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onResume() {
        mAd.resume(this);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mAd.destroy(this);
        super.onDestroy();
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        isVidAds = false;
        updateSavePoints(mDialog, "Sorry! Could not revive. Need to watch ads for charm to work");
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        isVidAds = true;
        updatePoints(50);
        updateSavePoints(mDialog, "Sorry! Could not revive. Need to watch ads for charm to work");
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }
}
