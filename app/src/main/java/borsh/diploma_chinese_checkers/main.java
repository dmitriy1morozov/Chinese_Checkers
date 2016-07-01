package borsh.diploma_chinese_checkers;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.Serializable;

public class main extends AppCompatActivity implements SoundPool.OnLoadCompleteListener, Serializable
{
    // ------------ Constants ------------
    private final static String TAG = "===main===";
    public final static int GAME_TABLE_WIDTH  = 25;
    public final static int GAME_TABLE_HEIGHT = 17;

    // ------------ Sound Constants --------------
    public final static int SOUND_START           = 0;
    public final static int SOUND_FINISH          = 1;
    public final static int SOUND_MOVE            = 2;
    public final static int SOUND_RELEASE_CHECKER = 3;

    // ------------ Vars ------------
    /**
     * TextViews of highlighted cur player status (above gameTable on screen)
     */
    public static HexagonButton _HBcurrentPlayer;
    public static TableLayout   _TLgameTable;
    /**
     * TextView Matrix of game table visible on the screen
     */
    public static HexagonButton[][] _HBgameTableArray = new HexagonButton[GAME_TABLE_WIDTH][GAME_TABLE_HEIGHT];
    /**
     * Matrix of cells
     */
    public static Cell[][] _gameTableCell = new Cell[GAME_TABLE_WIDTH][GAME_TABLE_HEIGHT];

    public static Home[] _homes = new Home[6];

    /**
     * Array of Players
     */
    public static Player _Player[];

    // ------------ Current game Variables ------------
    public static int _currentPlayer   = 1;
    public static int _numberOfPlayers = 2;                                                         // 2 players by default

    // ------------ Sounds -----------------------------
    public static SoundPool _soundPool;
    public static int     _soundId[]         = new int[4];
    public static int     _streamId[]        = new int[4];
    public static boolean _isSoundIdLoaded[] = new boolean[4];
    public static BackgroundMusic _backgroundMusic;
    public static Settings        _settings;

    // ------------ Methods ------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main._TLgameTable = (TableLayout) findViewById(R.id.tlGameTable);
        main._HBcurrentPlayer = (HexagonButton) findViewById(R.id.hbCurrentPlayer);
        main.this.addTiles();
        _homes[Home.RED] = new Home(this, getResources().getColor(R.color.colorRed));
        _homes[Home.PURPLE] = new Home(this, getResources().getColor(R.color.colorPurple));
        _homes[Home.BLUE] = new Home(this, getResources().getColor(R.color.colorBlue));
        _homes[Home.GREEN] = new Home(this, getResources().getColor(R.color.colorGreen));
        _homes[Home.YELLOW] = new Home(this, getResources().getColor(R.color.colorYellow));
        _homes[Home.ORANGE] = new Home(this, getResources().getColor(R.color.colorOrange));
        for(int i = 0; i < _homes.length; i++)
        {
            _homes[i].colorHome();
        }

        for (int i = 0; i < _isSoundIdLoaded.length; i++)
        {
            main._isSoundIdLoaded[i] = false;
        }
        main.this.loadSounds();
        main._backgroundMusic = new BackgroundMusic(this);

        main._settings = new Settings(this);
        main._settings.createSettingsAlertDialogBuilder();
        firstRun();
    }

    private void firstRun()
    {
        SharedPreferences pref = getSharedPreferences("mypref", MODE_PRIVATE);
        if(pref.getBoolean("firststart", true)){
            // update sharedpreference - another start wont be the first
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("firststart", false);
            editor.apply();

            Toast.makeText(main.this, "test first run", Toast.LENGTH_SHORT).show();
            // first start, show your dialog | first-run code goes here
        }
    }

    private void addTiles()
    {
        //Adding Rows
        TableRow TRrow[] = new TableRow[GAME_TABLE_HEIGHT];

        int displayWidth     = getWindowManager().getDefaultDisplay().getWidth();
        int horizontalMargin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
        int tileWidth        = (displayWidth - horizontalMargin) / 13;
        int overlapMargin    = (int) ((tileWidth / 2) * (Math.sqrt(3) / 2));                        //overlapMargin = tileWidth/2 * tg(30deg) //TODO doesn't work with Tangens. Works with Cosinus
        overlapMargin /= 2;                                                                         //tiles Move towards each other, so overLapping automatically increased by 2.
        overlapMargin *= -1;                                                                        //set negative Margin to Overlap

        //LayoutParams for the 1st row
        TableLayout.LayoutParams TLrowOneLP = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TLrowOneLP.gravity = Gravity.CENTER_HORIZONTAL;
        TLrowOneLP.weight = 1;
        //Overlap tiles! THE DIFFERENCE IN ROWS IS HERE
        TLrowOneLP.setMargins(0, 0, 0, overlapMargin);

        //LayoutParams for the last row
        TableLayout.LayoutParams TLrowLastLP = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TLrowLastLP.gravity = Gravity.CENTER_HORIZONTAL;
        TLrowLastLP.weight = 1;
        //Overlap tiles! THE DIFFERENCE IN ROWS IS HERE
        TLrowLastLP.setMargins(0, overlapMargin, 0, 0);

        TableLayout.LayoutParams TLrowLP = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TLrowLP.gravity = Gravity.CENTER_HORIZONTAL;
        TLrowLP.weight = 1;
        //Overlap tiles! THE DIFFERENCE IN ROWS IS HERE
        TLrowLP.setMargins(0, overlapMargin, 0, overlapMargin);
        for (int j = 0; j < GAME_TABLE_HEIGHT; j++)
        {
            TRrow[j] = new TableRow(this);
            //Adding a row depending on its index
            switch (j)
            {
                case 0:
                    _TLgameTable.addView(TRrow[j], TLrowOneLP);
                    break;
                case GAME_TABLE_HEIGHT - 1:
                    _TLgameTable.addView(TRrow[j], TLrowLastLP);
                    break;
                default:
                    _TLgameTable.addView(TRrow[j], TLrowLP);
                    break;
            }

        }

        //----------------------------------------------
        //Adding tiles
        TableRow.LayoutParams TRfieldLP = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        TRfieldLP.weight = 1;
        for (int j = 0; j < GAME_TABLE_HEIGHT; j++)
        {
            for (int i = 0; i < GAME_TABLE_WIDTH; i++)
            {
                HexagonButton tvTile = new HexagonButton(this);

                tvTile.setVisibility(View.INVISIBLE);

                if ((i % 2 == 0 && j % 2 == 0) || ((i + 1) % 2 == 0 && (j + 1) % 2 == 0))           //заполняем сетку в шахматном порядке ноликами 0
                {
                    if (i >= GAME_TABLE_WIDTH || j >= GAME_TABLE_HEIGHT)                            // условие не выхода за пределы массива
                    {
                        continue;
                    }

                    TRrow[j].addView(tvTile, TRfieldLP);

                    main._HBgameTableArray[i][j] = tvTile;
                    // Первый видимый треугольник
                    if (j < (GAME_TABLE_HEIGHT - 4) &&
                            (i - j) < (GAME_TABLE_HEIGHT - 4) &&
                            (i + j) >= (GAME_TABLE_WIDTH - 13))
                    {
                        tvTile.setVisibility(View.VISIBLE);
                        main._gameTableCell[i][j] = new Cell(i, j);                                      //initialize free cells
                        main._HBgameTableArray[i][j].setTag(main._gameTableCell[i][j]);                   //link TextView of GameTable with corresponding cell object
                    }
                    // Второй видимый треугольник (перевернутый)
                    if ((j >= 4) &&
                            (j - i) <= 4 &&
                            (i + j) <= (GAME_TABLE_WIDTH + 4))
                    {
                        tvTile.setVisibility(View.VISIBLE);
                        main._gameTableCell[i][j] = new Cell(i, j);                                      //initialize free cells
                        main._HBgameTableArray[i][j].setTag(main._gameTableCell[i][j]);                   //link TextView of GameTable with corresponding cell object
                    }
                    continue;
                }

                //Добавляем пробелы слева и справа (по половине ячейки) для того, чтобы сместить ячейки и сделать сетку диагональной
                if ((i == 0 || i == GAME_TABLE_WIDTH - 1) &&
                        (j >= 4 || j < GAME_TABLE_HEIGHT - 4) &&
                        (j + 1) % 2 == 0)
                {
                    Space space         = new Space(this);
                    int   displayMargin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
                    int   displaySize   = getResources().getDisplayMetrics().widthPixels;
                    int   spaceSize     = (displaySize - displayMargin * 2) / 13;
                    spaceSize /= 2;
                    TableRow.LayoutParams TRLP = new TableRow.LayoutParams(spaceSize, ViewGroup.LayoutParams.MATCH_PARENT);
                    TRrow[j].addView(space, TRLP);
                }
            }
        }
    }


    private void addPlayers(int numberOfPlayers)
    {
        main._HBcurrentPlayer.setMaxWidth(((ViewGroup) main._HBcurrentPlayer.getParent()).getHeight());

        main._Player = new Player[numberOfPlayers + 1];
        switch (numberOfPlayers)
        {
            case 2:
                // ------ Add _Player 1 checkers ------
                int playerIndex = 1;
                int color = getResources().getColor(R.color.colorGreen);
                Bitmap bmpChecker = BitmapFactory.decodeResource(getResources(), R.drawable.green);
                addOnePlayer(playerIndex, _homes[Home.RED].get_cells() , bmpChecker, color);

                // ------ Add _Player 2 checkers ------
                playerIndex = 2;
                color = getResources().getColor(R.color.colorRed);
                bmpChecker = BitmapFactory.decodeResource(getResources(), R.drawable.red);
                addOnePlayer(playerIndex, _homes[Home.GREEN].get_cells(), bmpChecker, color);
                break;

            case 3:
                // ------ Add _Player 1 checkers ------

                playerIndex = 1;
                color = getResources().getColor(R.color.colorBlue);
                bmpChecker = BitmapFactory.decodeResource(getResources(), R.drawable.blue);
                addOnePlayer(playerIndex, _homes[Home.ORANGE].get_cells(), bmpChecker, color);

                // ------ Add _Player 2 checkers ------
                playerIndex = 2;
                color = getResources().getColor(R.color.colorYellow);
                bmpChecker = BitmapFactory.decodeResource(getResources(), R.drawable.yellow);
                addOnePlayer(playerIndex, _homes[Home.PURPLE].get_cells(), bmpChecker, color);

                // ------ Add _Player 3 checkers ------
                playerIndex = 3;
                color = getResources().getColor(R.color.colorRed);
                bmpChecker = BitmapFactory.decodeResource(getResources(), R.drawable.red);
                addOnePlayer(playerIndex, _homes[Home.GREEN].get_cells(), bmpChecker, color);
                break;
            case 4:
                // ------ Add _Player 1 checkers ------
                playerIndex = 1;
                color = getResources().getColor(R.color.colorBlue);
                bmpChecker = BitmapFactory.decodeResource(getResources(), R.drawable.blue);
                addOnePlayer(playerIndex, _homes[Home.ORANGE].get_cells(), bmpChecker, color);

                // ------ Add _Player 2 checkers ------
                playerIndex = 2;
                color = getResources().getColor(R.color.colorYellow);
                bmpChecker = BitmapFactory.decodeResource(getResources(), R.drawable.yellow);
                addOnePlayer(playerIndex, _homes[Home.PURPLE].get_cells(), bmpChecker, color);

                // ------ Add _Player 3 checkers ------
                playerIndex = 3;
                color = getResources().getColor(R.color.colorOrange);
                bmpChecker = BitmapFactory.decodeResource(getResources(), R.drawable.orange);
                addOnePlayer(playerIndex, _homes[Home.BLUE].get_cells(), bmpChecker, color);

                // ------ Add _Player 4 checkers ------
                playerIndex = 4;
                color = getResources().getColor(R.color.colorPurple);
                bmpChecker = BitmapFactory.decodeResource(getResources(), R.drawable.purple);
                addOnePlayer(playerIndex, _homes[Home.YELLOW].get_cells(), bmpChecker, color);
                break;
            case 6:
                // ------ Add _Player 1 checkers ------
                playerIndex = 1;
                color = getResources().getColor(R.color.colorGreen);
                bmpChecker = BitmapFactory.decodeResource(getResources(), R.drawable.green);
                addOnePlayer(playerIndex, _homes[Home.RED].get_cells(), bmpChecker, color);

                // ------ Add _Player 2 checkers ------
                playerIndex = 2;
                color = getResources().getColor(R.color.colorYellow);
                bmpChecker = BitmapFactory.decodeResource(getResources(), R.drawable.yellow);
                addOnePlayer(playerIndex, _homes[Home.PURPLE].get_cells(), bmpChecker, color);

                // ------ Add _Player 3 checkers ------
                playerIndex = 3;
                color = getResources().getColor(R.color.colorOrange);
                bmpChecker = BitmapFactory.decodeResource(getResources(), R.drawable.orange);
                addOnePlayer(playerIndex, _homes[Home.BLUE].get_cells(), bmpChecker, color);

                // ------ Add _Player 4 checkers ------
                playerIndex = 4;
                color = getResources().getColor(R.color.colorRed);
                bmpChecker = BitmapFactory.decodeResource(getResources(), R.drawable.red);
                addOnePlayer(playerIndex, _homes[Home.GREEN].get_cells(), bmpChecker, color);

                // ------ Add _Player 5 checkers ------
                playerIndex = 5;
                color = getResources().getColor(R.color.colorPurple);
                bmpChecker = BitmapFactory.decodeResource(getResources(), R.drawable.purple);
                addOnePlayer(playerIndex, _homes[Home.YELLOW].get_cells(), bmpChecker, color);

                // ------ Add _Player 6 checkers ------
                playerIndex = 6;
                color = getResources().getColor(R.color.colorBlue);
                bmpChecker = BitmapFactory.decodeResource(getResources(), R.drawable.blue);
                addOnePlayer(playerIndex, _homes[Home.ORANGE].get_cells(), bmpChecker, color);
                break;
        }
    }

    private void addOnePlayer(int playerIndex, Cell[] cell, Bitmap bmpChecker, int color)
    {
        Checker checkers[] = new Checker[10];
        //Add checkers to _Player array
        main._Player[playerIndex] = new Player(checkers, bmpChecker, color);

        //Defining checkers
        int checkerIndex = 0;
        for(int i = 0; i < cell.length; i++)
        {
            int x = cell[i].getX();
            int y = cell[i].getY();

            checkers[checkerIndex] = new Checker(cell[i]);
            Log.d(TAG, "Added checker to _Player" + playerIndex + " : " + checkers[checkerIndex].toString());
            cell[i].setOccupiedStatus(true);
            main._HBgameTableArray[x][y].setImageBitmap(bmpChecker);
            checkerIndex++;
        }
    }

    public void setOnClickListeners()
    {
        for (int j = 0; j < GAME_TABLE_HEIGHT; j++)
        {
            for (int i = 0; i < GAME_TABLE_WIDTH; i++)
            {
                if (main._HBgameTableArray[i][j] == null || main._HBgameTableArray[i][j].getVisibility() != View.VISIBLE)
                {
                    continue;
                }

                final int x = i;
                final int y = j;

                main._HBgameTableArray[i][j].setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Log.d(TAG, "Clicked: x = " + x + " y = " + y);
                        Cell clickedCell = main._gameTableCell[x][y];

                        try
                        {
                            // Initial pick up.
                            if (main._Player[main._currentPlayer].get_pickedCheckerIndex() == -1)
                            {
                                //Check if clicked on free cell or opponent's checker
                                if (!clickedCell._isOccupied || clickedCell.getPlayerIndex() != main._currentPlayer)
                                {
                                    Log.d(TAG, "Nothing picked. Clicked on a free cell or opponent's checker");
                                    return;
                                }

                                // Pick a checker
                                main._Player[main._currentPlayer].pickUp_checker(clickedCell.getCheckerIndex());
                            }
                            // Checker is already picked
                            else
                            {
                                if (main._Player[main._currentPlayer].move((Cell) v.getTag()))
                                {
                                    main.this.nextPlayer();
                                }
                            }
                        }
                        catch (IllegalArgumentException iae)
                        {
                            Log.d(TAG, iae.getMessage());
                        }
                    }
                });
            }
        }
    }

    public void startGame()
    {
        //Erase cells
        for (int j = 0; j < GAME_TABLE_HEIGHT; j++)
        {
            for (int i = 0; i < GAME_TABLE_WIDTH; i++)
            {
                if (main._gameTableCell[i][j] != null)
                {
                    main._gameTableCell[i][j].setPreOccupiedStatus(false);
                    main._gameTableCell[i][j].setOccupiedStatus(false);
                }
            }
        }
        //Free Players resources
        //TODO НАДО ЛИ ВОТ ТАК ОСВОБОЖДАТЬ РЕСУРСЫ???????????????????777
        //TODO Надо чистить поля ручным методом dispose() Пока что не работает
        if (main._Player != null)
        {
            for (int i = 1; i < main._Player.length; i++)
            {
                //Toast.makeText(main.this, String.valueOf(i), Toast.LENGTH_SHORT).show();
                //main._Player[i].dispose();
            }
        }

        //Start a new game
        main._currentPlayer = 1;
        main.this.addPlayers(_numberOfPlayers);
        main.this.setOnClickListeners();
        main._HBcurrentPlayer.setImageBitmap(main._Player[_currentPlayer].get_bmpChecker());

        main.playSound(SOUND_START);
    }


    public void nextPlayer()
    {
        if (checkWin(main._currentPlayer))
        {
            Toast.makeText(main.this, "_Player " + main._currentPlayer + " WIN!!", Toast.LENGTH_SHORT).show();
            gameOver();
            return;
        }

        main._currentPlayer++;
        if (main._currentPlayer > main._numberOfPlayers)
        {
            main._currentPlayer = 1;
        }
        main._HBcurrentPlayer.setImageBitmap(main._Player[_currentPlayer].get_bmpChecker());

        Log.d(TAG, "------ Next _Player turn. _Player " + main._currentPlayer + " ------");
    }


    public boolean checkWin(int playerIndex)
    {
        for (int i = 0; i < 10; i++)
        {
            int x = main._Player[playerIndex].get_checker(i).getCell().getX();
            int y = main._Player[playerIndex].get_checker(i).getCell().getY();

            int tileColor = main._HBgameTableArray[x][y].getHexBackgroundColor();
            Log.d(TAG, "_tileColor = " + tileColor);
            Log.d(TAG, "_playerColor = " + main._Player[playerIndex].get_color());

            if (tileColor == Color.TRANSPARENT ||
                    tileColor != main._Player[playerIndex].get_color())
            {
                return false;
            }
        }

        main.playSound(SOUND_FINISH);
        return true;
    }

    public void gameOver()
    {
        for (int j = 0; j < GAME_TABLE_HEIGHT; j++)
        {
            for (int i = 0; i < GAME_TABLE_WIDTH; i++)
            {
                if (main._HBgameTableArray[i][j] == null || main._HBgameTableArray[i][j].getVisibility() != View.VISIBLE)
                {
                    continue;
                }

                main._HBgameTableArray[i][j].setOnClickListener(null);
            }
        }
    }

    //================================ Sound Methods ===============================================
    /**
     * @param soundPool
     * @param sampleId
     * @param status
     */
    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
    {
        Log.d(TAG, "onLoadComplete," +
                " sampleId = " + sampleId +
                " status = " + status);

        if (status != 0) return;

        for(int i = 0; i < main._isSoundIdLoaded.length; i++)
        {
            if(sampleId == main._soundId[i])
            {
                main._isSoundIdLoaded[i] = true;
            }
        }
    }

    private void loadSounds()
    {
        //---------- Создание объекта SoundPool ----------
        if(Build.VERSION.SDK_INT >= 21)
        {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setAudioAttributes(new AudioAttributes.Builder().
                    setUsage(AudioAttributes.USAGE_MEDIA).
                    setContentType(AudioAttributes.CONTENT_TYPE_MOVIE).
                    build());
            builder.setMaxStreams(6);
            main._soundPool = builder.build();
        }
        else
        {
            main._soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }

        //---------- Назначаем слушателя на загрузку файлов ----------
        main._soundPool.setOnLoadCompleteListener(this);

        //---------- Загружаем файлы ----------
        main._soundId[SOUND_START] = _soundPool.load(this, R.raw.start, 1);
        main._soundId[SOUND_FINISH] = _soundPool.load(this, R.raw.win, 1);
        main._soundId[SOUND_MOVE] = _soundPool.load(this, R.raw.move, 1);
        main._soundId[SOUND_RELEASE_CHECKER] = _soundPool.load(this, R.raw.release_checker, 1);
    }

    public static void playSound(int soundId)
    {
        if (main._isSoundIdLoaded[soundId])
        {
            main._streamId[soundId] = main._soundPool.play(main._soundId[soundId], main._settings.get_soundVolume(), main._settings.get_soundVolume(), 1, 0, 1);
        }
    }


    //======================================== MENU ================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.menu_new_game:
                LayoutInflater inflater = getLayoutInflater();
                LinearLayout llAlertDialogNewGame = (LinearLayout) inflater.inflate(R.layout.alert_dialog_choose_number_of_players, null);
                final NumberPicker npPlayers = (NumberPicker) llAlertDialogNewGame.findViewById(R.id.numberPicker);
                npPlayers.setMinValue(2);
                npPlayers.setMaxValue(5);
                npPlayers.setDisplayedValues(new String[]{"2", "3", "4", "6"});
                npPlayers.setWrapSelectorWheel(false);

                final ToggleButton tbHumanAI[] = new ToggleButton[7];
                for(int i = 1; i <= 6; i++)
                {
                    int resourceId = getResources().getIdentifier("tbPlayer" + i, "id", main.this.getPackageName());
                    tbHumanAI[i] = (ToggleButton) llAlertDialogNewGame.findViewById(resourceId);
                }
                npPlayers.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
                {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal)
                    {
                        int numberOfPlayers = newVal;
                        if(newVal == 5)
                        {
                            numberOfPlayers = 6;
                        }
                        int i = 1;
                        while (i <= numberOfPlayers)
                        {
                            tbHumanAI[i].setVisibility(ToggleButton.VISIBLE);
                            i++;
                        }
                        while (i <=6)
                        {
                            tbHumanAI[i].setVisibility(ToggleButton.INVISIBLE);
                            i++;
                        }
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(main.this);
                builder.setView(llAlertDialogNewGame);
                builder.setPositiveButton("Start game", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        int number = npPlayers.getValue();
                        if(number == 5)
                        {
                            number = 6;
                        }
                        main._numberOfPlayers = number;
                        startGame();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            case R.id.menu_settings:
                Log.d(TAG, "menu_settings clicked");
                AlertDialog settingsDialog = main._settings.get_settingsBuilder().create();
                settingsDialog.show();
                return true;
            case R.id.menu_help:
                Log.d(TAG, "menu_help clicked");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // =============================== Activity LifeCycle methods ==================================
    @Override
    protected void onPause()
    {
        super.onPause();
        main._settings.saveSettingsIntoFile();
        main._backgroundMusic.pauseMusic();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        main._settings.restoresSettingsFromFile();
        main._backgroundMusic.startMusic();
    }


    private long lastPress;
    @Override
    public void onBackPressed()
    {
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastPress > 3000)
        {
            Toast.makeText(this, "" + getResources().getString(R.string.exit_app), Toast.LENGTH_LONG).show();
            lastPress = currentTime;
        }
        else
        {
            super.onBackPressed();
            finish();
        }
    }
}
