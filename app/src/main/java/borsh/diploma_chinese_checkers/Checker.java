package borsh.diploma_chinese_checkers;

/**
 * Created by Borsh on 25.05.2016.
 */
public class Checker
{
    protected Cell    _cell;

    // ------------ Constructor ------------
    public Checker(Cell cell)
    {
        this._cell = cell;
    }

    // ------------ Getters ------------
    public Cell getCell()
    {
        return this._cell;
    }

    // ------------ Setters ------------
    public void setCell(Cell cell)
    {
        this._cell = cell;
    }

    @Override
    public String toString()
    {
        return "(" + this._cell.getX() + "," + this._cell.getY() + ")";
    }
}
