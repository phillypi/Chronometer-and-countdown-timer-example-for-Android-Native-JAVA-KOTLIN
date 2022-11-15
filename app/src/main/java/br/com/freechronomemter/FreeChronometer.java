package br.com.freechronomemter;
/*
 *  THIS HELPED YOU? Follow me!!! ->
 *
 *  DEVELOPED BY Phillypi Fernandes
 *  LinkedIn: https://www.linkedin.com/in/phillypi-vieira-469232121/
 *  GitHub: https://github.com/phillypi
 *  Facebook: https://www.facebook.com/Phillypi.F.Vieira
 *  Contact: fernandesphillypi323@gmail.com
 *
 * */

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class FreeChronometer extends AppCompatActivity implements NumberPicker.OnValueChangeListener {
    public static final String PAUSE_KEY = "pause";
    public static final String PLAY_KEY = "play";
    public static final String TEXT_VIEW_INDICATOR_ALERT =
            "Click here to define a countdown value";
    public static final String CHOOSE_THE_TIME_TO_COUNT = "Choose the time to count";
    private static final String RESTART_PRESS_PLAY = "Restart? press play!";
    private static final String MESSAGE_TIMER_IS_OFF =
            "To use the timer, click on the left side buttom of Play/Pause buttom";
    private static final String MESSAGE_TIMER_IS_EMPTY =
            "No value to count, please, click and define a value to count";
    NumberPicker timerNumberPickerHour;
    NumberPicker timerNumberPickerMinute;
    NumberPicker timerNumberPickerSecond;
    private Chronometer chronometer;
    private CountDownTimer counterTimer;
    private ImageButton buttonStart;
    private ImageButton buttonReset;
    private ImageButton buttonTimer;
    private ImageButton buttonExitChronometer;
    private Button chronometerAlertDialogButtonOK;
    private TextView TextViewChronometerAndTimer;
    private TextView chronometerMainNameMode;
    private TextView chronometerMainTextViewIndicator;
    private ProgressBar progressAnimation;
    private ProgressBar progressBarTimer;
    private boolean isResume = false;
    private boolean isTimerOn = false;
    private Handler handler;
    private long tMilliSec, tStart, tBuff, tUpdate = 0L;

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tMilliSec = SystemClock.uptimeMillis() - tStart;
            tUpdate = tBuff + tMilliSec;
            second = (int) (tUpdate / 1000);
            minute = second / 60;
            second = second % 60;
            milliSecond = (int) (tUpdate % 100);
            setValueOnTextViewClock(minute, second, milliSecond);
            handler.postDelayed(this, 60);

        }
    };

    private int hour, second, minute, milliSecond, userHour, userMinute, userSecond;

    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
        clearAllFields();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_freechronometer);
        findIds();
        chronometer = new Chronometer(this);
        handler = new Handler();
        startPauseButton();
        exitButton();
        timerOnOffButton();
        resetButtonClick();
        progressBarClickAction();
        setValueOnTextViewClock(0, 0, 0);
    }

    private void progressBarClickAction() {
        progressAnimation.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                if (isTimerOn) {
                    showDialogTimerIsOn();
                } else {
                    showDialogMessage(MESSAGE_TIMER_IS_OFF);
                }
            }
        });
    }

    private void progressBarRotation() {
        if ((second % 2) != 0) {
            progressAnimation.setLayoutDirection(second); // rotation layout
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void countDownTimerFunction() {
        catchUserValues();

        if (getMilliseconds() == 0) {
            showDialogMessage(MESSAGE_TIMER_IS_EMPTY);
            return;
        }

        setConfigOnProgressTimer();

        if (!isResume) {
            isResume = true;
            /* the adition of 999 is to increase a delay to start */
            counterTimer = new CountDownTimer(getMilliseconds() + 999,
                    1) {

                public void onTick(long millisUntilFinished) {
                    setValueOnTimer(millisUntilFinished);
                    setImageButtonPlayPause(PAUSE_KEY);
                    setProgressOnTimer(millisUntilFinished);

                    progressAnimation.setClickable(false);

                    if (!isTimerOn) { //keep this order execution
                        onFinish();
                        clearAllFields();
                    }
                }

                @SuppressLint("SetTextI18n")
                public void onFinish() {
                    cancel();
                    progressAnimation.setClickable(true);
                    isResume = false;
                    setValueOnTimer(getMilliseconds());
                    setImageButtonPlayPause(PLAY_KEY);
                    chronometerMainTextViewIndicator.setText(RESTART_PRESS_PLAY);
                }
            }.start();
        }
    }

    private void setProgressOnTimer(long progress) {
        progress = (getMilliseconds() - progress);
        progressBarTimer.setProgress((int) progress);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setConfigOnProgressTimer() {
        progressBarTimer.setMax((int) getMilliseconds() - 20);
        progressBarTimer.setMin(0);
    }

    private void catchUserValues() {
        userHour = hour;
        userMinute = minute;
        userSecond = second;
    }

    @SuppressLint("SetTextI18n")
    private void setValueOnTimer(long millis) {
        setTimeFormatted(millis);
        progressBarRotation();

        chronometerMainTextViewIndicator
                .setText("remaining of: " + getFormattedTimeString(userHour,
                        userMinute, userSecond));

        TextViewChronometerAndTimer
                .setText(getFormattedTimeString(hour,
                        minute, second));
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    private String getFormattedTimeString(int hour, int minute, int second) {
        return String.format("%02d", hour) + ":"
                + String.format("%02d", minute) + ":"
                + String.format("%02d", second);
    }

    private void setTimeFormatted(long millis) {
        hour = (int) millis / (1000 * 60 * 60) % 24;
        minute = (int) millis / (1000 * 60) % 60;
        second = (int) (millis / 1000) % 60;
    }

    private void setValueOnTextViewClock(int minute, int second, int milliSecond) {
        progressBarRotation();
        TextViewChronometerAndTimer
                .setText(getFormattedTimeString(minute, second, milliSecond));
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        hour = timerNumberPickerHour.getValue();
        minute = timerNumberPickerMinute.getValue();
        second = timerNumberPickerSecond.getValue();
        setValueOnTextViewClock(hour, minute, second);
    }

    private void showDialogMessage(String message) {
        AlertDialog.Builder alert =
                new AlertDialog.Builder(FreeChronometer.this);
        alert.setMessage(message);
        alert.show();
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.Q)

    private void numberPickerConfig(Dialog dialogNumberPicker) {
        dialogNumberPicker.setContentView(R.layout.dialog_timer_custom_timer);
        dialogNumberPicker.setTitle(CHOOSE_THE_TIME_TO_COUNT);

        timerNumberPickerHour = dialogNumberPicker
                .findViewById(R.id.timer_number_picker_hour);
        timerNumberPickerMinute = dialogNumberPicker

                .findViewById(R.id.timer_number_picker_minute);
        timerNumberPickerSecond = dialogNumberPicker

                .findViewById(R.id.timer_number_picker_second);

        numberPickerConfig();
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void numberPickerConfig() {
        timerNumberPickerHour.setMaxValue(23);
        timerNumberPickerHour.setMinValue(0);
        timerNumberPickerHour.setValue(0);
        timerNumberPickerHour.setTextColor(R.color.Gray_mod);
        timerNumberPickerHour.setOnValueChangedListener
                (this);

        timerNumberPickerMinute.setMaxValue(59);
        timerNumberPickerMinute.setMinValue(0);
        timerNumberPickerMinute.setValue(0);
        timerNumberPickerMinute.setTextColor(R.color.Gray_mod);
        timerNumberPickerMinute.setOnValueChangedListener
                (this);

        timerNumberPickerSecond.setMaxValue(59);
        timerNumberPickerSecond.setMinValue(0);
        timerNumberPickerSecond.setValue(0);
        timerNumberPickerSecond.setTextColor(R.color.Gray_mod);
        timerNumberPickerSecond.setOnValueChangedListener
                (this);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void showDialogTimerIsOn() {
        //function to pick time values to count
        final Dialog dialogNumberPicker = new Dialog(FreeChronometer.this);
        numberPickerConfig(dialogNumberPicker);

        chronometerAlertDialogButtonOK = dialogNumberPicker
                .findViewById(R.id.chronometer_alert_dialog_button_ok);

        chronometerAlertDialogButtonOK.setOnClickListener(v -> dialogNumberPicker.dismiss());
        dialogNumberPicker.show();
    }

    private void timerOnOffButton() {
        buttonTimer.setOnClickListener(view -> {
            if (!isTimerOn) {
                timerIsOnAction();
                progressBarTimer.setProgress(100);
            } else {
                progressBarTimer.setProgress(0);
                timerIsOffAction();
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void timerIsOffAction() {
        buttonTimer.setImageDrawable(getResources().getDrawable(
                R.drawable.ic_chronometer_main_timer_off));
        isTimerOn = false;
        chronometerMainNameMode.setText("C H R O N O M E T E R");
        chronometerMainTextViewIndicator.setVisibility(View.GONE);
        stopChronometerAction();
        clearAllFields();
    }

    private void timerIsOnAction() {
        buttonTimer.setImageDrawable(getResources().getDrawable(
                R.drawable.ic_chronometer_main_timer_on));
        isTimerOn = true;
        chronometerMainNameMode.setText("T I M E R");
        chronometerMainTextViewIndicator.setVisibility(View.VISIBLE);
        chronometerMainTextViewIndicator
                .setText(TEXT_VIEW_INDICATOR_ALERT);
        stopChronometerAction();
        clearAllFields();
    }

    private void exitButton() {
        buttonExitChronometer.setOnClickListener(view -> finish());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void findIds() {
        progressAnimation = findViewById(R.id.chronometer_main_progress_bar_chronometer);
        buttonStart = findViewById(R.id.chronometer_main_play_pause_buttom);
        buttonReset = findViewById(R.id.chronometer_main_restart_buttom);
        TextViewChronometerAndTimer = findViewById(R.id.chronometer_main_text_view);
        buttonExitChronometer = findViewById(R.id.chronometer_main_exit_buttom);
        chronometerMainNameMode = findViewById(R.id.chronometer_main_name);
        buttonTimer = findViewById(R.id.chronometer_main_timer_on_off);
        chronometerMainTextViewIndicator = findViewById(R.id.chronometer_main_text_view_indicator);
        chronometerAlertDialogButtonOK = findViewById(R.id.chronometer_alert_dialog_button_ok);
        progressBarTimer = findViewById(R.id.chronometer_main_progress_bar_timer);
    }

    private void startPauseButton() {
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (isTimerOn) {
                    timerClick();
                } else {
                    chronometerClick();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void timerClick() { //picks values of NumberPickers to do this

        if (!isResume) {
            countDownTimerFunction();
        } else if (counterTimer != null) {///CHECAR ISSO, TA ESTRANHO SAPORRA
            counterTimer.onFinish();
        }
    }

    private long getMilliseconds() {
        final long timeInMillis;

        timeInMillis = (userSecond * 1000L) +
                (userMinute * 60000L) +
                (userHour * 3600000L);
        return timeInMillis;
    }

    private void chronometerClick() {
        if (!isResume) {
            resumeChronometerAction();
            setImageButtonPlayPause(PAUSE_KEY);

        } else {
            stopChronometerAction();//"pause" doenst exists, so we use "stop() to do this"
            setImageButtonPlayPause(PLAY_KEY);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setImageButtonPlayPause(String key) {
        if (Objects.equals(key, PAUSE_KEY)) {
            buttonStart.setImageDrawable(getResources().getDrawable(
                    R.drawable.ic_chronometer_main_pause));
        } else if (Objects.equals(key, PLAY_KEY)) {
            buttonStart.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_chronometer_main_play));
        }
    }

    private void resetButtonClick() {
        buttonReset.setOnClickListener(view -> {
            if ((isTimerOn) && (isResume)) {
                counterTimer.onFinish();
            }
            isResume = false;
            clearAllFields();
        });
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void clearAllFields() {
        if (chronometer != null) {
            chronometer.stop();
            tMilliSec = 0L;
            tStart = 0L;
            tBuff = 0L;
            tUpdate = 0L;
            handler.removeCallbacks(runnable);
        }

        buttonStart.setImageDrawable(getResources()
                .getDrawable(R.drawable.ic_chronometer_main_play));

        if (isTimerOn) {
            chronometerMainTextViewIndicator.setText(TEXT_VIEW_INDICATOR_ALERT);
        } else {
            chronometerMainTextViewIndicator.setVisibility(View.GONE);
        }

        progressBarTimer.setMax(10); //to keep progress full
        progressBarTimer.setProgress(11); //to keep progress full

        second = 0;
        minute = 0;
        milliSecond = 0;

        TextViewChronometerAndTimer.setText("00:00:00");
    }

    private void stopChronometerAction() {
        tBuff += tMilliSec;
        handler.removeCallbacks(runnable);
        chronometer.stop();
        isResume = false;
    }

    private void resumeChronometerAction() {
        tStart = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
        chronometer.start();
        isResume = true;
    }
}