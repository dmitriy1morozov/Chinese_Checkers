package borsh.diploma_chinese_checkers;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;

/**
 * Created by Borsh on 08.07.2016.
 */
public class GameTable
{
    // ------------ Constants ------------
    private final static String TAG = "===GameTable===";
    public final static int GAME_TABLE_WIDTH  = 13;
    public final static int GAME_TABLE_HEIGHT = 17;

    // ------------ Fields ---------------
    private Activity _activity;
    /**
     * Matrix of cells
     */
    public static Cell[][] _gameTableCell = new Cell[GAME_TABLE_WIDTH][GAME_TABLE_HEIGHT];
    /**
     * Array of Homes
     */
    public static Home[] _homes = new Home[6];

    public static TableLayout _TLgameTable;
    /**
     * TextView Matrix of game table visible on the screen
     */
    public static HexagonButton[][] _HBgameTableArray = new HexagonButton[GAME_TABLE_WIDTH][GAME_TABLE_HEIGHT];

    // ------------ Constructors ---------
    public GameTable(Activity activity)
    {
        _activity = activity;
        GameTable._TLgameTable = (TableLayout) _activity.findViewById(R.id.tlGameTable);
        GameTable.this.addTiles();

        _homes[Home.RED] = new Home(_activity, _activity.getResources().getColor(R.color.colorRed));
        _homes[Home.PURPLE] = new Home(_activity, _activity.getResources().getColor(R.color.colorPurple));
        _homes[Home.BLUE] = new Home(_activity, _activity.getResources().getColor(R.color.colorBlue));
        _homes[Home.GREEN] = new Home(_activity, _activity.getResources().getColor(R.color.colorGreen));
        _homes[Home.YELLOW] = new Home(_activity, _activity.getResources().getColor(R.color.colorYellow));
        _homes[Home.ORANGE] = new Home(_activity, _activity.getResources().getColor(R.color.colorOrange));
        for(int i = 0; i < _homes.length; i++)
        {
            _homes[i].colorHome();
        }
    }

    // ------------ Mehtods --------------
    private void addTiles()
    {
        //Adding Rows
        TableRow TRrow[] = new TableRow[GAME_TABLE_HEIGHT];

        int displayWidth     = _activity.getWindowManager().getDefaultDisplay().getWidth();
        int horizontalMargin = (int) _activity.getResources().getDimension(R.dimen.activity_horizontal_margin);
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

        //LayoutParams for all the middle rows
        TableLayout.LayoutParams TLrowLP = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TLrowLP.gravity = Gravity.CENTER_HORIZONTAL;
        TLrowLP.weight = 1;
        //Overlap tiles! THE DIFFERENCE IN ROWS IS HERE
        TLrowLP.setMargins(0, overlapMargin, 0, overlapMargin);
        for (int j = 0; j < GAME_TABLE_HEIGHT; j++)
        {
            TRrow[j] = new TableRow(_activity);
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
                HexagonButton tvTile = new HexagonButton(_activity);
                tvTile.setVisibility(View.INVISIBLE);

                if (!(j % 2 == 1 && i == GAME_TABLE_WIDTH - 1))                                     // не заполняем виджетами последний столбце нечетных строк (там будет пробед)
                {
                    TRrow[j].addView(tvTile, TRfieldLP);

                    main._HBgameTableArray[i][j] = tvTile;
                    // Первый видимый треугольник
                    int remainder  = j % 2;
                    int jOptimized = (j + remainder) / 2;
                    if (j < (GAME_TABLE_HEIGHT - 4) &&
                            (i + jOptimized) >= 6 &&
                            (i - j / 2) <= 6)
                    {
                        tvTile.setVisibility(View.VISIBLE);
                        main._gameTableCell[i][j] = new Cell(i, j);                                      //initialize free cells
                        main._HBgameTableArray[i][j].setTag(main._gameTableCell[i][j]);                   //link TextView of GameTable with corresponding cell object
                    }
                    // Второй видимый треугольник (перевернутый)
                    if ((j >= 4) &&
                            (i + jOptimized) <= 14 &&
                            (j / 2 - i) <= 2)
                    {
                        tvTile.setVisibility(View.VISIBLE);
                        main._gameTableCell[i][j] = new Cell(i, j);                                      //initialize free cells
                        main._HBgameTableArray[i][j].setTag(main._gameTableCell[i][j]);                   //link TextView of GameTable with corresponding cell object
                    }
                }

                //Добавляем пробелы слева и справа (по половине ячейки) для того, чтобы сместить ячейки и сделать сетку диагональной
                if ((i == 0 || i == GAME_TABLE_WIDTH - 1) &&
                        (j % 2 == 1))
                {
                    Space space         = new Space(_activity);
                    int   displayMargin = (int) _activity.getResources().getDimension(R.dimen.activity_horizontal_margin);
                    int   displaySize   = _activity.getResources().getDisplayMetrics().widthPixels;
                    int   spaceSize     = (displaySize - displayMargin * 2) / 13;
                    spaceSize /= 2;
                    TableRow.LayoutParams TRLP = new TableRow.LayoutParams(spaceSize, ViewGroup.LayoutParams.MATCH_PARENT);
                    if(i == 0)
                    {
                        TRrow[j].addView(space, 0, TRLP);
                    }
                    else
                    {
                        TRrow[j].addView(space, TRLP);
                    }
                }
            }
        }
    }


}
