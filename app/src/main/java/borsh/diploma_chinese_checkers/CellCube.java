package borsh.diploma_chinese_checkers;

/**
 * Created by Borsh on 04.07.2016.
 */
public class CellCube
{

    // ------------ Constants -------------
    public final static String TAG = "===CellCube===";

    // ------------ Fields ----------------
    protected int     _x;
    protected int     _y;
    protected int     _z;

    // ------------ Constructor ------------
    public CellCube(int x, int y, int z)
    {
        this._x = x;
        this._y = y;
        this._z = z;
    }

    public CellCube(int x, int y)
    {
        Cell cell = main._gameTableCell[x][y];
        this._x = cell.getCellCube().getX();
        this._y = cell.getCellCube().getY();
        this._z = cell.getCellCube().getZ();
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
    public int getZ()
    {
        return _z;
    }

    public Cell getOddRCell()
    {
        int x = _x + (_z - (_z & 1))/ 2;                                                            // x = x + (z - (z&1)) / 2
        int y = _z;                                                                                 // y = z

        x += 6;
        y += 8;

        return main._gameTableCell[x][y];
    }

    // ------------ Setters ------------
    @Override
    public String toString()
    {
        return "Cell (" + _x + "," + _y + "," + _z + ")";
    }

}
