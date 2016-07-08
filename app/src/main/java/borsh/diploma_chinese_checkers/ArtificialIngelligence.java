package borsh.diploma_chinese_checkers;

import java.util.ArrayList;

/**
 * Created by Borsh on 26.06.2016.
 */
public class ArtificialIngelligence
{
    //=============================== Constants ====================================================
    private final static String TAG = "===AI===";

    //=============================== Fields =======================================================
    private Player _player;
    private CellCube _homeCell;
    private ArrayList<CellCube> _path;
    private int       _evaluation;
    //=============================== Constructor ==================================================
    public ArtificialIngelligence()
    {
    }

    //=============================== Getters ======================================================
    public Player get_player()
    {
        return _player;
    }
    public ArrayList<CellCube> get_path()
    {
        return _path;
    }
    public int get_evaluation()
    {
        return _evaluation;
    }

    //=============================== Setters ======================================================
    public void set_player(Player _player)
    {
        this._player = _player;
        this._homeCell = (main._homes[main._currentPlayer].get_cells()[9]).getCellCube();
    }

    //=============================== Methods ======================================================



    public void dispose()
    {
        this._player = null;
        this._homeCell = null;
        this._path.clear();
        this._evaluation = 0;
    }
}
