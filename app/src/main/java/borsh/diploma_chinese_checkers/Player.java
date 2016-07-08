package borsh.diploma_chinese_checkers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

/**
 * Created by Borsh on 29.05.2016.
 */
public class Player
{
    //==================== Constats ================================================================
    public final static String TAG = "===Player===";

    //==================== Fields ==================================================================
    private Checker[] _checker = new Checker[10];
    private int     _pickedCheckerIndex;
    private Cell    _startCell;
    private Cell    _curCell;
    private boolean _isJumping;
    private boolean _isMoving;
    private Bitmap  _bmpChecker;
    private AnimationDrawable _animation;
    private int     _color;

    //========== Constructor ==========
    public Player(Checker[] checkers, Bitmap bmpChecker, int color)
    {
        this._checker = checkers;
        this._bmpChecker = bmpChecker;
        _animation = new AnimationDrawable();
        _animation.setOneShot(false);
        for(int i = 0; i <= 20; i++)
        {
            Bitmap transBitmap = Bitmap.createBitmap(_bmpChecker.getWidth(), _bmpChecker.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas transCanvas = new Canvas(transBitmap);
            transCanvas.drawARGB(0, 0, 0, 0);

            //--- Calculate the opacity of checker ---
            Paint paint        = new Paint();
            int   alphaChannel = Math.abs(255 - (i * 255) / 10);
            paint.setAlpha(alphaChannel);

            transCanvas.drawBitmap(_bmpChecker, 0, 0, paint);
            BitmapDrawable frame = new BitmapDrawable(transBitmap);
            _animation.addFrame(frame, 75);
        }
        this._color = color;
        this._pickedCheckerIndex = -1;
        this._startCell = null;
        this._curCell = null;
        this._isJumping = false;
        this._isMoving = false;
    }

    //========== Getters ==========
    public Bitmap get_bmpChecker()
    {
        return _bmpChecker;
    }
    public AnimationDrawable get_animation(){return _animation;}
    public int get_color(){return _color;}
    public int get_pickedCheckerIndex()
    {
        return _pickedCheckerIndex;
    }
    public Cell get_startCell()
    {
        return _startCell;
    }
    public Cell get_curCell()
    {
        return this._curCell;
    }
    public Checker get_checker(int checkerIndex) throws IndexOutOfBoundsException
    {
        if(checkerIndex > this._checker.length || checkerIndex < 0)
        {
            throw new IndexOutOfBoundsException("Invalid argument. Checker index is out of bounds.");
        }
        return _checker[checkerIndex];
    }

    //========== Setters ==========
    public void set_pickedCheckerIndex(int _pickedCheckerIndex)
    {
        this._pickedCheckerIndex = _pickedCheckerIndex;
    }
    public void set_startCell(Cell _startCell)
    {
        this._startCell = _startCell;
        Log.d(TAG, "_startCell is (" + main._Player[main._currentPlayer]._startCell.getX() + "," + main._Player[main._currentPlayer]._startCell.getY() + ")");
    }
    public void set_curCell(Cell _curCell)
    {
        this._curCell = _curCell;
        Log.d(TAG, "_curCell is (" + main._Player[main._currentPlayer]._curCell.getX() + "," + main._Player[main._currentPlayer]._curCell.getY() + ")");
    }

    //========== Methods ===========
    public void pickUp_checker(int checkerIndex)
    {
        Log.d(TAG, "------PickUp _checker------");
        this.set_pickedCheckerIndex(checkerIndex);
        int x = this.get_checker(checkerIndex).getCell().getX();
        int y = this.get_checker(checkerIndex).getCell().getY();
        main._Player[main._currentPlayer].set_startCell(main._gameTableCell[x][y]);
        main._Player[main._currentPlayer].set_curCell(_startCell);
        main._Player[main._currentPlayer]._curCell.setOccupiedStatus(false);
        main._Player[main._currentPlayer]._curCell.setPreOccupiedStatus(true);
        main._Player[main._currentPlayer].get_checker(checkerIndex).setCell(null);
    }

    public void release_checker(Cell destinationCell)
    {
        Log.d(TAG, "------Release _checker------");
        this.get_checker(_pickedCheckerIndex).setCell(destinationCell);
        destinationCell.setOccupiedStatus(true);
        this._pickedCheckerIndex = -1;
        this._isMoving = false;
        this._isJumping = false;
        this._startCell = null;
        this._curCell = null;
        main.playSound(main.SOUND_RELEASE_CHECKER);
    }

    /**
     * Returns true if the player finished his turn
     *
     * @param destinationCell
     * @return true if the player finished his turn
     * @throws IllegalArgumentException
     */
    public boolean move(Cell destinationCell) throws IllegalArgumentException
    {
        /*
        int fromX = _curCell.getX();
        int fromY = _curCell.getY();
        int toX = destinationCell.getX();
        int toY = destinationCell.getY();
        */

        if (destinationCell == get_startCell())
        {
            Log.d(TAG, "------ Move _checker to _startCell ------");
            this._curCell.setPreOccupiedStatus(false);
            main._Player[main._currentPlayer].release_checker(_startCell);
            return false;
        }

        if(destinationCell == _curCell)
        {
            Log.d(TAG, "------ Finish turn. Release _checker into Finish Cell ------");
            main._Player[main._currentPlayer].release_checker(destinationCell);
            return true;
        }

        // Illegal move. Destination cell is occupied
        if(destinationCell.getOccupiedStatus())
        {
            throw new IllegalArgumentException("Illegal move. Destination cell occupied");
        }


        // ------ Move ------
        if (!_isJumping && distance(_startCell, destinationCell) == 1)
        {
            Log.d(TAG, "------ Move _checker ------");
            Log.d(TAG, "distance = " + distance(_startCell, destinationCell));
            Log.d(TAG, "Move _checker from (" + _curCell.getX() + "," + _curCell.getY() + ") -> (" + destinationCell.getX() + "," + destinationCell.getY() + ")");
            this._isMoving = true;
            this._curCell.setPreOccupiedStatus(false);
            this._curCell = destinationCell;
            this._curCell.setPreOccupiedStatus(true);
            main.playSound(main.SOUND_MOVE);
        }

        // ------ Jump ------
        if (!_isMoving && isJumpAllowed(_curCell, destinationCell))
        {
            Log.d(TAG, "------ Jump _checker ------");
            Log.d(TAG, "Jump! (" + _curCell.getX() + "," + _curCell.getY() + ") -> (" + destinationCell.getX() + "," + destinationCell.getY() + ")");
            this._isJumping = true;
            this._curCell.setPreOccupiedStatus(false);
            this._curCell = destinationCell;
            this._curCell.setPreOccupiedStatus(true);
            main.playSound(main.SOUND_MOVE);
        }

        return false;
    }

    private int distance(Cell startCell, Cell destinationCell)
    {
        CellCube startCellCube = startCell.getCellCube();
        CellCube destinationCellCube = destinationCell.getCellCube();

        int distance = Math.abs(startCellCube.getX() - destinationCellCube.getX()) +
                Math.abs(startCellCube.getY() - destinationCellCube.getY()) +
                Math.abs(startCellCube.getZ() - destinationCellCube.getZ());
        distance /= 2;
        return distance;
    }

    private boolean isJumpAllowed(Cell startCell, Cell destinationCell)
    {
        if(distance(startCell, destinationCell) != 2)
        {
            return false;
        }
        if(destinationCell._isOccupied)
        {
            return false;
        }

        CellCube startCellCube = startCell.getCellCube();
        CellCube destinationCellCube = destinationCell.getCellCube();

        int middleX = (startCellCube.getX() + destinationCellCube.getX()) / 2;
        int middleY = (startCellCube.getY() + destinationCellCube.getY()) / 2;
        int middleZ = (startCellCube.getZ() + destinationCellCube.getZ()) / 2;

        CellCube middleCellCube = new CellCube(middleX, middleY, middleZ);
        Cell middleCell = middleCellCube.getOddRCell();

        return middleCell._isOccupied;
    }

    //TODO Doesn't work from outside yet
    public void dispose()
    {
        this._checker = null;
        this._bmpChecker = null;
        this._pickedCheckerIndex = -1;
        this._startCell = null;
        this._curCell = null;
        this._isJumping = false;
        this._isMoving = false;
    }
}
