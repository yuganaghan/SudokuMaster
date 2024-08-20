package com.example.sudokumaster;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.*;;
import android.view.*;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.sudokumaster.FileManager.*;
import com.example.sudokumaster.SudokuBoardElements.*;
import com.example.sudokumaster.SettingElement.*;

import java.util.Random;


public class Sudoku extends AppCompatActivity {
    GridLayout gridMain;
    Board board;
    TextView mistakeRound, newGame, solve, timeView,hintCount;
    downButtons downbuttons;
    ImageView Undo, Erase, Hint;
    boolean soundToggle, vibrateToggle, timeToggle, mistakeToggle;
    int mistakeCounter;
    LinearLayout numberButton, back;
    MediaPlayer mediaplayer;
    int difficulty ;
    Random random ;
    VibrationHelper vibrates;

    ImageView setting;
    long bestTimeEasy, bestTimeMedium, bestTimeHard, currentTime, startTime, pastTime, bestTimeExpert, bestTimeNightmare,CurrentTime;
    int data,hints;

    savingSudoku Sudoku;
    saveScore Score;
    saveSetting Setting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sudoku_game);
        // INITIALIZE CLASS 'S INSTANCES ===========================================================
        board = new Board();
        vibrates = new VibrationHelper(getApplicationContext());
        downbuttons = new downButtons();
        random = new Random();
        Sudoku = new savingSudoku(getApplicationContext(), "sudoku.txt");
        Score = new saveScore(getApplicationContext(), "time.txt");
        Setting = new saveSetting(getApplicationContext(), "Setting");
        // INITIALIZE VARIABLES ====================================================================
        mistakeCounter = 0;
        bestTimeEasy = 0;
        bestTimeMedium = 0;
        pastTime = 0;
        bestTimeHard = 0;
        currentTime = 0;
        difficulty = 30;
        timeToggle = true;
        soundToggle = true;
        mistakeToggle = true;
        vibrateToggle = true;
        //FINDING VIEW FROM LAYOUT =================================================================
        gridMain = findViewById(R.id.gridmain);
        numberButton = findViewById(R.id.numberbutton);
        timeView = findViewById(R.id.time);
        mistakeRound = findViewById(R.id.mistakes);
        startTime = System.currentTimeMillis();
        newGame = findViewById(R.id.new_game);
        Undo = findViewById(R.id.undo);
        Hint = findViewById(R.id.hint);
        Erase = findViewById(R.id.erace);
        setting = findViewById(R.id.setting);
        solve = findViewById(R.id.solve);
        back = findViewById(R.id.back);
        hintCount = findViewById(R.id.hintCount);
        mediaplayer = MediaPlayer.create(getApplicationContext(), R.raw.sound);
        mediaplayer.setVolume(1.0f, 1.0f);
        ReadSetting();
        ReadScore();

        hintCount.setText(String.valueOf(hints));
        // UPDATING TIME EVERY SECOND ==============================================================
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateTimer();
                handler.postDelayed(this, 1000);
            }
        }, 1000);

        //NEW GAME ONCLICK  EVENT ==================================================================
        newGame.setOnClickListener(v -> {
            newGame.setBackgroundResource(R.drawable.reverse_main_button);
            newGame.setTextColor(Color.rgb(23, 28, 34));
            handler.postDelayed(() -> {
                newGame.setBackgroundResource(R.drawable.main_button);
                newGame.setTextColor(Color.WHITE);
            }, 200);
            ShowOptions(new Random());
        });

        solve.setOnClickListener(v -> {

            solve.setBackgroundResource(R.drawable.reverse_main_button);
            solve.setTextColor(Color.rgb(23, 28, 34));
            handler.postDelayed(() -> {
                solve.setBackgroundResource(R.drawable.main_button);
                solve.setTextColor(Color.WHITE);
            }, 200);
            board.solution(board.sudoku);
            board.changeToSolution();
            board.GameOver = true;
            afterGameActivity(true);

        });

        // UNDO ONCLICK LISTENER====================================================================
        Undo.setOnClickListener(v -> {

            if (board.undoList.size() >= 1) {
                int[] temp = board.undoList.get(board.undoList.size() - 1);
                String x = temp[2] != 0 ? String.valueOf(temp[2]) : "";
                if (x.equals(""))
                    board.board[temp[0] / 3][temp[1] / 3].block[temp[0] % 3][temp[1] % 3].btn.setTextColor(Color.BLUE);
                board.sudoku[temp[0]][temp[1]] = temp[2];
                board.board[temp[0] / 3][temp[1] / 3].block[temp[0] % 3][temp[1] % 3].btn.setText(x);
                board.board[temp[0] / 3][temp[1] / 3].block[temp[0] % 3][temp[1] % 3].number = temp[2];
                board.undoList.remove(board.undoList.size() - 1);

            }
            if (board.sx != -1) {
                board.setSelected();
            }
        });


        // HINT ONCLICK LISTENER ===================================================================
        Hint.setOnClickListener(v -> {
            if(hints>0){
                board.hint();
                hints -=1;
                hintCount.setText(String.valueOf(hints));
                if (board.sx != -1) {
                    board.setSelected();
                }
                board.checkGameOver();
                if (board.GameOver) {
                    afterGameActivity(false);
                    hints +=1;
                }
            }
        });


        // ERASE ONCLICK LISTENER ==================================================================
        Erase.setOnClickListener(v -> {
            if (board.sx != -1 && !board.board[board.sx / 3][board.sy / 3].block[board.sx % 3][board.sy % 3].permanent) {
                board.eraseElement(board.sx / 3, board.sy / 3, board.sx % 3, board.sy % 3);
            }
            if (board.sx != -1) {
                board.setSelected();
            }
        });


        //SETTING ONCLICK LISTENER ===================
        setting.setOnClickListener(v -> {
            showDialog();
        });


        back.setOnClickListener(v -> {
            board.normal();
            downbuttons.normal();
        });

        //  ASSIGNING THE GRID ELEMENT IN SIDE THE SUDOKU ==========================================
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board.board[i][j].miniGrid = new GridLayout(this);
                board.board[i][j].miniGrid.setRowCount(3);
                board.board[i][j].miniGrid.setColumnCount(3);
                board.board[i][j].miniGrid.setPadding(10, 10, 10, 10);
                board.board[i][j].miniGrid.setBackgroundResource(R.drawable.miniblock);
                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.width = GridLayout.LayoutParams.WRAP_CONTENT;
                param.height = GridLayout.LayoutParams.WRAP_CONTENT;
                gridMain.addView(board.board[i][j].miniGrid, param);
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        String x = board.sudoku[i * 3 + k][j * 3 + l] != 0 ? String.valueOf(board.sudoku[i * 3 + k][j * 3 + l]) : "";
                        board.board[i][j].block[k][l].btn = new TextView(this);
                        board.board[i][j].block[k][l].btn.setTextColor(Color.BLUE);
                        board.board[i][j].block[k][l].btn.setText(x);
                        board.board[i][j].block[k][l].btn.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        board.board[i][j].block[k][l].btn.setTextSize(25);
                        board.board[i][j].block[k][l].number = board.sudoku[i * 3 + k][j * 3 + l];
                        board.board[i][j].block[k][l].btn.setPadding(0, 0, 2, 8);
                        board.board[i][j].block[k][l].btn.setBackgroundResource(R.drawable.numberblock);

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = 108;
                        params.height = 108;
                        params.setMargins(1, 1, 1, 1);
                        board.board[i][j].miniGrid.addView(board.board[i][j].block[k][l].btn, params);
                    }
                }
            }
        }

        // ASSIGNING THE GRID ON CLICKING EVENT ====================================================
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        int finalI = i;
                        int finalJ = j;
                        int finalK = k;
                        int finalL = l;
                        board.board[i][j].block[k][l].btn.setOnClickListener(v -> {
                            board.sx = finalI * 3 + finalK;
                            board.sy = finalJ * 3 + finalL;
                            board.setSelected();

                        });
                    }
                }
            }
        }

        //ASSIGNING THE DOWN INSERTING NUMBER ======================================================
        for (int i = 0; i < 9; i++) {
            downbuttons.button[i].btn = new TextView(this);
            downbuttons.button[i].btn.setBackgroundResource(R.drawable.dropdown_norma_button);
            downbuttons.button[i].number = i + 1;
            downbuttons.button[i].btn.setText(String.valueOf(downbuttons.button[i].number));
            downbuttons.button[i].btn.setTextSize(25);
            downbuttons.button[i].btn.setTextColor(Color.WHITE);
            downbuttons.button[i].btn.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(110, 110);
            param.setMargins(0, 7, 8, 0);
            numberButton.addView(downbuttons.button[i].btn, param);
        }

        //ASSIGNING THE ON CLICK ON THE DOWN INSERTING NUMBER ======================================
        for (int i = 0; i < 9; i++) {
            int finalI = i;
            downbuttons.button[i].btn.setOnClickListener(v -> {
                downbuttons.changeSelected(downbuttons.button[finalI].number);
                insertNumber(board.sx, board.sy, downbuttons.selected);
                board.onlySelectNumbers(finalI + 1);
            });
        }

        if (mistakeToggle)
            mistakeRound.setText(R.string.mistake);
        else
            mistakeRound.setText(" ");

        Intent intent = getIntent();
        data = intent.getIntExtra("difficulty", 0);
        int challenge = intent.getIntExtra("challenge",0);
        if (challenge == 1){
            difficulty = random.nextInt(10) + 50;
            restartGame();
        }
        else if (data != 0) {
            difficulty = data;
            restartGame();
        } else
            ContinueGame(board);

    }

    //OUT SIDE OF THE ON CREATE METHOD AFTER INITIALIZE METHOD */*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/**/*/

    // FOR CONTINUING LAST GAME ===================================================================
    public void ContinueGame(Board board) {
        Sudoku.readData();
        if (Sudoku.fileExist) {
            this.currentTime = Sudoku.cTime;
            this.mistakeCounter = Sudoku.mistake;
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    board.sudoku = Sudoku.matrix;
                    board.changeToSolution();
                    board.board[i / 3][j / 3].block[i % 3][j % 3].permanent = Sudoku.ColorsMatrix[i][j];
                    board.ColorMatrix[i][j] = Sudoku.ColorsMatrix[i][j];
                    if (Sudoku.ColorsMatrix[i][j])
                        board.board[i / 3][j / 3].block[i % 3][j % 3].btn.setTextColor(Color.rgb(0, 21, 97));
                    else
                        board.board[i / 3][j / 3].block[i % 3][j % 3].btn.setTextColor(Color.BLUE);
                }
            }
            downbuttons.checkFrequency(board.sudoku);
        } else {
            board.createSudoku(difficulty);
            board.changeToSolution();
        }
    }

    // READING ALL PREVIOUS SETTINGS ===============================================================
    public void ReadSetting() {
        Setting.readData();
        if (Setting.fileExist) {
            soundToggle = Setting.setting[0];
            vibrateToggle = Setting.setting[1];
            timeToggle = Setting.setting[2];
            mistakeToggle = Setting.setting[3];
            hints = Setting.hints;
        } else {
            soundToggle = true;
            vibrateToggle = true;
            timeToggle = true;
            mistakeToggle = true;
            hints = 3;
        }
    }

    //READING ALL PREVIOUS BEST SCORES =============================================================
    public void ReadScore() {
        Score.readData();
        if (Score.fileExist) {
            bestTimeEasy = Score.scores[0];
            bestTimeMedium = Score.scores[1];
            bestTimeHard = Score.scores[2];
            bestTimeExpert = Score.scores[3];
            bestTimeNightmare = Score.scores[4];

        } else {
            bestTimeEasy = 0;
            bestTimeMedium = 0;
            bestTimeHard = 0;
            bestTimeExpert = 0;
            bestTimeNightmare = 0;
        }
    }


    //  INSERTING THE NUMBER INSIDE SUDOKU =========================================================
    public void insertNumber(int sx, int sy, int selected) {
        int temp = mistakeCounter;
        if (sx != -1 && !board.Loose && !board.GameOver) {
            if (soundToggle)
                mediaplayer.start();

            if (board.sudoku[sx][sy] == 0) {
                mistakeCounter += board.changeSudoku(selected, vibrates);
                downbuttons.changeFrequency(mistakeCounter == temp);
                if (mistakeToggle)
                    mistakeRound.setText(getString(R.string.mistakeP1) + mistakeCounter + getString(R.string.mistakeP3));
                else
                    mistakeRound.setText(" ");
                if (mistakeCounter >=3)
                    board.Loose = true;
                board.synchronize();
            }
        }
        board.checkGameOver();
        if (board.GameOver) {
            afterGameActivity(false);
            hints+=1;
            hintCount.setText(String.valueOf(hints));
        }
        if (board.Loose){
            LoosingDialog();
        }
    }


    // UPDATING TIME EVERY SECOND ==================================================================
    private void updateTimer() {
        if (!board.GameOver && !board.Loose)
            CurrentTime = System.currentTimeMillis();
        long elapsedTime = CurrentTime - startTime;
        if (timeToggle)
            timeView.setText(getString(R.string.time) + formatElapsedTime(elapsedTime));
        else
            timeView.setText(" ");
    }

    // FORMATTING THAT UPDATED TIME =================================================================
    private String formatElapsedTime(long elapsedTime) {
        long seconds = elapsedTime / 1000;
        seconds += pastTime;
        long minutes = seconds / 60;
        seconds %= 60;
        currentTime = (minutes * 60) + seconds;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // AFTER WINNING ACTIVITY DIALOG CALLER ========================================================
    private void afterGameActivity(boolean solveIt) {
        Handler handler = new Handler();
        handler.postDelayed(() -> WinningDialog(solveIt), 500);
    }


    //AFTER WINNING ACTIVITY DIALOG ================================================================
    private void WinningDialog(boolean solveIt) {
        Score.saveData(new long[]{bestTimeEasy, bestTimeMedium, bestTimeHard, bestTimeExpert, bestTimeNightmare});
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.game_over_winning);
        dialog.setCanceledOnTouchOutside(false);
        TextView nGame = dialog.findViewById(R.id.newGame);
        TextView cTime = dialog.findViewById(R.id.currentTime);
        TextView bTime = dialog.findViewById(R.id.bestTime);
        TextView dTime = dialog.findViewById(R.id.difficulty);
        TextView homeText = dialog.findViewById(R.id.homeText);
        ImageView homeIcon = dialog.findViewById(R.id.homeIcon);
        LinearLayout home = dialog.findViewById(R.id.home);
        dialog.show();
        if (!solveIt){
            if (30 <= difficulty && difficulty < 40)
                bestTimeEasy = bestTimeEasy == 0 ? currentTime : Math.min(bestTimeEasy, currentTime);
            else if (40 <= difficulty && difficulty < 45)
                bestTimeMedium = bestTimeMedium == 0 ? currentTime : Math.min(bestTimeMedium, currentTime);
            else if (45 <= difficulty && difficulty < 55)
                bestTimeHard = bestTimeHard == 0 ? currentTime : Math.min(bestTimeHard, currentTime);
            else if (55 <= difficulty && difficulty < 60)
                bestTimeExpert = bestTimeExpert == 0 ? currentTime : Math.min(bestTimeExpert, currentTime);
            else
                bestTimeNightmare = bestTimeNightmare == 0 ? currentTime : Math.min(bestTimeNightmare, currentTime);
            cTime.setText(timeView.getText().toString().substring(8));
        }
        else
            cTime.setText("-- : --");

        if (30 <= difficulty && difficulty < 40) {
            dTime.setText(R.string.easy);
            bTime.setText(bestTimeEasy / 60 + ":" + bestTimeEasy % 60);
        } else if (40 <= difficulty && difficulty < 45) {
            bTime.setText(bestTimeMedium / 60 + ":" + bestTimeMedium % 60);
            dTime.setText(R.string.medium);
        } else if (45 <= difficulty && difficulty < 55) {
            bTime.setText(bestTimeHard / 60 + ":" + bestTimeHard % 60);
            dTime.setText(R.string.hard);
        } else if (55 <= difficulty && difficulty < 60) {
            bTime.setText(bestTimeExpert / 60 + ":" + bestTimeExpert % 60);
            dTime.setText(R.string.expert);
        } else {
            bTime.setText(bestTimeNightmare / 60 + ":" + bestTimeNightmare % 60);
            dTime.setText(R.string.nightmare);
        }
        nGame.setOnClickListener(v -> {
            nGame.setBackgroundResource(R.drawable.reverse_main_button);
            nGame.setTextColor(Color.rgb(23, 28, 34));
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                nGame.setBackgroundResource(R.drawable.main_button);
                nGame.setTextColor(Color.WHITE);
            }, 200);
            ShowOptions(new Random());
            dialog.dismiss();
        });
        home.setOnClickListener(v -> {
            home.setBackgroundResource(R.drawable.reverse_main_button);
            homeText.setTextColor(Color.rgb(23, 28, 34));
            Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.home);
            drawable.setTint(ContextCompat.getColor(getApplicationContext(), R.color.newblack));
            homeIcon.setImageDrawable(drawable);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.home);
                    drawable.setTint(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    homeIcon.setImageDrawable(drawable);
                    home.setBackgroundResource(R.drawable.main_button);
                    homeText.setTextColor(Color.WHITE);
                }
            }, 200);
            Intent intent = new Intent(getApplicationContext(), mainSudoku.class);
            startActivity(intent);
        });
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.AnimationWinning;
        dialog.getWindow().setGravity(Gravity.TOP);

    }

    //LOOSING DIALOG ===============================================================================
    private void LoosingDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.game_over_looseing);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView newGame = dialog.findViewById(R.id.newGame);
        newGame.setOnClickListener(v -> {
            restartGame();
            dialog.dismiss();
        });
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.setting_activity);
        dialog.show();
        LinearLayout back = dialog.findViewById(R.id.backOfSetting);
        LinearLayout voice = dialog.findViewById(R.id.voice);
        LinearLayout vibrate = dialog.findViewById(R.id.vibration);
        LinearLayout time = dialog.findViewById(R.id.time);
        LinearLayout mistake = dialog.findViewById(R.id.mistakeShower);
        ImageView settingIcon = dialog.findViewById(R.id.settingIcon);
        back.setOnClickListener(v -> dialog.dismiss());
        onBottomSheetCreated(R.drawable.settings, true, settingIcon);
        onBottomSheetCreated(R.drawable.volume_up, soundToggle, dialog.findViewById(R.id.voiceIcon));
        onBottomSheetCreated(R.drawable.vibration, vibrateToggle, dialog.findViewById(R.id.vibrationIcon));
        onBottomSheetCreated(R.drawable.time, timeToggle, dialog.findViewById(R.id.timeIcon));
        onBottomSheetCreated(R.drawable.disabled, mistakeToggle, dialog.findViewById(R.id.mistakeIcon));
        voice.setOnClickListener(v -> {
            soundToggle = !soundToggle;
            onBottomSheetCreated(R.drawable.volume_up, soundToggle, dialog.findViewById(R.id.voice));
        });
        vibrate.setOnClickListener(v -> {
            vibrateToggle = !vibrateToggle;
            onBottomSheetCreated(R.drawable.vibration, vibrateToggle, dialog.findViewById(R.id.vibrationIcon));
        });
        time.setOnClickListener(v -> {
            timeToggle = !timeToggle;
            onBottomSheetCreated(R.drawable.time, timeToggle, dialog.findViewById(R.id.timeIcon));
        });
        mistake.setOnClickListener(v -> {
            mistakeToggle = !mistakeToggle;
            onBottomSheetCreated(R.drawable.disabled, mistakeToggle, dialog.findViewById(R.id.mistakeIcon));
            if (mistakeToggle)
                mistakeRound.setText(getString(R.string.mistakeP1) + mistakeCounter + getString(R.string.mistakeP3));
            else
                mistakeRound.setText("");
        });
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.AnimationSetting;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void onBottomSheetCreated(int id, boolean toggle, ImageView icons) {
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), id);
        if (toggle) {
            drawable.setTint(ContextCompat.getColor(this, R.color.white));
        } else {
            drawable.setTint(ContextCompat.getColor(this, R.color.gray));
        }
        icons.setImageDrawable(drawable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (board.GameOver)
            Sudoku.emptySave();
        else
            Sudoku.saveData(board.sudoku, currentTime, mistakeCounter, board.ColorMatrix);
        Setting.saveData(new boolean[]{soundToggle, vibrateToggle, timeToggle, mistakeToggle},hints);


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (board.GameOver)
            Sudoku.emptySave();
        else
            Sudoku.saveData(board.sudoku, currentTime, mistakeCounter, board.ColorMatrix);
        Setting.saveData(new boolean[]{soundToggle, vibrateToggle, timeToggle, mistakeToggle},hints);
    }

    public void restartGame(){
        board.createSudoku(difficulty);
        board.changeToSolution();
        startTime = System.currentTimeMillis();
        board.GameOver = false;
        board.Loose = false;
        mistakeCounter = 0;
        if (mistakeToggle)
            mistakeRound.setText(getString(R.string.mistakeP1) + mistakeCounter + getString(R.string.mistakeP3));
        updateTimer();
        downbuttons.checkFrequency(board.sudoku);
    }

    public void ShowOptions(Random random) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.difficlty_select_dialog);
        dialog.show();
        LinearLayout easyMode = (LinearLayout) dialog.findViewById(R.id.easyMode);
        TextView easyText = dialog.findViewById(R.id.easyText);
        LinearLayout mediumMode = (LinearLayout) dialog.findViewById(R.id.mediumMode);
        TextView mediumText = dialog.findViewById(R.id.mediumText);
        LinearLayout hardMode = (LinearLayout) dialog.findViewById(R.id.hardMode);
        TextView hardText = dialog.findViewById(R.id.hardText);
        LinearLayout expertMode = (LinearLayout) dialog.findViewById(R.id.expertMode);
        TextView expertText = dialog.findViewById(R.id.expertText);
        LinearLayout nightmareMode = (LinearLayout) dialog.findViewById(R.id.nightmareMode);
        TextView nightmareText = dialog.findViewById(R.id.nightmareText);
        easyMode.setOnClickListener(v -> {

            easyMode.setBackgroundResource(R.drawable.norml_back);
            easyText.setTextColor(Color.rgb(23, 28, 34));
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                easyMode.setBackgroundResource(0);
                easyText.setTextColor(Color.WHITE);
                difficulty =(random.nextInt(10) + 30);
                dialog.dismiss();
                restartGame();
            }, 100);

        });

        mediumMode.setOnClickListener(v -> {
            mediumMode.setBackgroundResource(R.drawable.norml_back);
            mediumText.setTextColor(Color.rgb(23, 28, 34));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mediumMode.setBackgroundResource(0);
                    mediumText.setTextColor(Color.WHITE);
                    difficulty=random.nextInt(5) + 40;
                    dialog.dismiss();
                    restartGame();
                }
            }, 100);
        });
        hardMode.setOnClickListener(v -> {
            hardMode.setBackgroundResource(R.drawable.norml_back);
            hardText.setTextColor(Color.rgb(23, 28, 34));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hardMode.setBackgroundResource(0);
                    hardText.setTextColor(Color.WHITE);
                    difficulty = random.nextInt(10) + 45;
                    dialog.dismiss();
                    restartGame();
                }
            }, 100);
        });
        expertMode.setOnClickListener(v -> {
            expertMode.setBackgroundResource(R.drawable.norml_back);
            expertText.setTextColor(Color.rgb(23, 28, 34));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    expertMode.setBackgroundResource(0);
                    expertText.setTextColor(Color.WHITE);
                    difficulty = random.nextInt(5) + 55;
                    dialog.dismiss();
                    restartGame();
                }
            }, 100);
        });
        nightmareMode.setOnClickListener(v -> {
            nightmareMode.setBackgroundResource(R.drawable.norml_back);
            nightmareText.setTextColor(Color.rgb(23, 28, 34));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nightmareMode.setBackgroundResource(0);
                    nightmareText.setTextColor(Color.WHITE);
                    difficulty = random.nextInt(6) + 60;
                    dialog.dismiss();
                    restartGame();
                }
            }, 100);
        });
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.AnimationOption;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}


