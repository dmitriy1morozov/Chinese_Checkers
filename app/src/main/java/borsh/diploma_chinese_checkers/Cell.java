package borsh.diploma_chinese_checkers;

import android.util.Log;

/**
 * Created by Borsh on 26.05.2016.
 */
public class Cell
{
    // ------------ Constants -------------
    public final static String TAG = "===cell===";

    // ------------ Fields ----------------
    protected int     _x;
    protected int     _y;
    protected boolean _isOccupied;

    // ------------ Constructor ------------
    public Cell(int x, int y)
    {
        this._x = x;
        this._y = y;
        this._isOccupied = false;
    }


    // ------------ Getters ------------
    public int getX()
    {
        return _x;
    }
    public int getY()
    {
        return _y;
    }
    public boolean getOccupiedStatus()
    {
        return this._isOccupied;
    }


    // ------------ Setters ------------
    public void setOccupiedStatus(boolean isOccupied)
    {
        this._isOccupied = isOccupied;
        Log.d(TAG, "(" + _x + "," + _y + ") is occupied = " + isOccupied);
        if (isOccupied)
        {
            main._HBgameTableArray[_x][_y].setImageBitmap(main._Player[main._currentPlayer].get_bmpChecker());
        }
        else
        {
            main._HBgameTableArray[_x][_y].setImageResource(android.R.color.transparent);
        }
    }

    public void setPreOccupiedStatus(boolean isPreOccupied)
    {
        Log.d(TAG, "(" + _x + "," + _y + ") is PreOccupied = " + isPreOccupied);
        if (isPreOccupied)
        {
            main._HBgameTableArray[_x][_y].startCheckerAnimation(main._Player[main._currentPlayer].get_animation());
        }
        else
        {
            if(main._HBgameTableArray != null && main._HBgameTableArray[_x][_y] != null && main._Player != null && main._Player[main._currentPlayer] != null)
            {
                main._HBgameTableArray[_x][_y].stopCheckerAnimation(main._Player[main._currentPlayer].get_animation());
            }
        }
    }


    //========== Methods ==========
    public int getPlayerIndex()
    {
        int index = -1;
        for(int i = 1; i <= main._numberOfPlayers; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                if (this == main._Player[i].get_checker(j).getCell())
                {
                    index = i;
                    return index;
                }
            }
        }

        return index;
    }

    public int getCheckerIndex()
    {
        int index = -1;
        for(int i = 1; i <= main._numberOfPlayers; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                if (this == main._Player[i].get_checker(j).getCell())
                {
                    index = j;
                    return index;
                }
            }
        }

        return index;
    }


    @Override
    public String toString()
    {
        return "Cell (" + _x + "," + _y + "); Occupied = " + this._isOccupied;
    }
}