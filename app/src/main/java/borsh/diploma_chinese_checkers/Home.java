package borsh.diploma_chinese_checkers;

import android.content.Context;

/**
 * Class for homes for tiles to reach
 * Created by Borsh on 29.06.2016.
 */
public class Home
{
    //=============================== Constants ====================================================
    private final static String TAG = "===Home===";

    public final static int RED    = 0;
    public final static int PURPLE = 1;
    public final static int BLUE   = 2;
    public final static int GREEN  = 3;
    public final static int YELLOW = 4;
    public final static int ORANGE = 5;

    //=============================== Fields =======================================================
    private Context _context;
    private int _color;
    private Cell _cell[] = new Cell[10];

    //=============================== Constructor ==================================================
    public Home(Context context, int color)
    {
        this._context = context;
        this._color = color;

        int red = context.getResources().getColor(R.color.colorRed);
        int purple = context.getResources().getColor(R.color.colorPurple);
        int blue = context.getResources().getColor(R.color.colorBlue);
        int green = context.getResources().getColor(R.color.colorGreen);
        int yellow = context.getResources().getColor(R.color.colorYellow);
        int orange = context.getResources().getColor(R.color.colorOrange);

        if(color == red)
        {
            _cell[0] = main._gameTableCell[4][3];
            _cell[1] = main._gameTableCell[5][3];
            _cell[2] = main._gameTableCell[6][3];
            _cell[3] = main._gameTableCell[7][3];
            _cell[4] = main._gameTableCell[5][2];
            _cell[5] = main._gameTableCell[6][2];
            _cell[6] = main._gameTableCell[7][2];
            _cell[7] = main._gameTableCell[5][1];
            _cell[8] = main._gameTableCell[6][1];
            _cell[9] = main._gameTableCell[6][0];
        }
        if(color == purple)
        {
            _cell[0] = main._gameTableCell[9][4];
            _cell[1] = main._gameTableCell[9][5];
            _cell[2] = main._gameTableCell[10][6];
            _cell[3] = main._gameTableCell[10][7];
            _cell[4] = main._gameTableCell[10][4];
            _cell[5] = main._gameTableCell[10][5];
            _cell[6] = main._gameTableCell[11][6];
            _cell[7] = main._gameTableCell[11][4];
            _cell[8] = main._gameTableCell[11][5];
            _cell[9] = main._gameTableCell[12][4];
        }
        if(color == blue)
        {
            _cell[0] = main._gameTableCell[10][9];
            _cell[1] = main._gameTableCell[10][10];
            _cell[2] = main._gameTableCell[9][11];
            _cell[3] = main._gameTableCell[9][12];
            _cell[4] = main._gameTableCell[11][10];
            _cell[5] = main._gameTableCell[10][11];
            _cell[6] = main._gameTableCell[10][12];
            _cell[7] = main._gameTableCell[11][11];
            _cell[8] = main._gameTableCell[11][12];
            _cell[9] = main._gameTableCell[12][12];
        }
        if(color == green)
        {
            _cell[0] = main._gameTableCell[4][13];
            _cell[1] = main._gameTableCell[5][13];
            _cell[2] = main._gameTableCell[6][13];
            _cell[3] = main._gameTableCell[7][13];
            _cell[4] = main._gameTableCell[5][14];
            _cell[5] = main._gameTableCell[6][14];
            _cell[6] = main._gameTableCell[7][14];
            _cell[7] = main._gameTableCell[5][15];
            _cell[8] = main._gameTableCell[6][15];
            _cell[9] = main._gameTableCell[6][16];
        }
        if(color == yellow)
        {
            _cell[0] = main._gameTableCell[3][12];
            _cell[1] = main._gameTableCell[2][11];
            _cell[2] = main._gameTableCell[2][10];
            _cell[3] = main._gameTableCell[1][9];
            _cell[4] = main._gameTableCell[2][12];
            _cell[5] = main._gameTableCell[1][11];
            _cell[6] = main._gameTableCell[1][10];
            _cell[7] = main._gameTableCell[1][12];
            _cell[8] = main._gameTableCell[0][11];
            _cell[9] = main._gameTableCell[0][12];
        }
        if(color == orange)
        {
            _cell[0] = main._gameTableCell[1][7];
            _cell[1] = main._gameTableCell[2][6];
            _cell[2] = main._gameTableCell[2][5];
            _cell[3] = main._gameTableCell[3][4];
            _cell[4] = main._gameTableCell[1][6];
            _cell[5] = main._gameTableCell[1][5];
            _cell[6] = main._gameTableCell[2][4];
            _cell[7] = main._gameTableCell[0][5];
            _cell[8] = main._gameTableCell[1][4];
            _cell[9] = main._gameTableCell[0][4];
        }
    }

    //=============================== Getters ======================================================
    public int get_color()
    {
        return _color;
    }
    public Cell[] get_cells()
    {
        return _cell;
    }

    //=============================== Methods ======================================================
    public void colorHome()
    {
        for(int i = 0; i < _cell.length; i++)
        {
            int x = _cell[i].getX();
            int y =_cell[i].getY();
            main._HBgameTableArray[x][y].setHexBackgroundColor(_color);
        }
    }
}
