package borsh.diploma_chinese_checkers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by Borsh on 08.06.2016.
 */
public class HexagonButton extends ImageView
{
    //==================================== Constants ===============================================
    public final static String TAG = "===HexagonButton===";
    private static final int _strokeWidth = 2;

    //==================================== Fields ==================================================
    private Paint _paintBorder = new Paint();
    private Paint _paintBackground;
    private Path _path = new Path();

    private Context _context;

    /**
     * Required to compute if onTouchEvent is inside bounds
     */
    private Region _region = new Region();
    /**
     * Required to compute if onTouchEvent is inside bounds
     */
    private RectF  _rectF  = new RectF();


    //======================================= Constructors =========================================
    public HexagonButton(Context context)
    {
        super(context);
        this._context = context;
        init(null, 0);
    }

    public HexagonButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this._context = context;
        init(attrs, 0);
    }

    public HexagonButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        this._context = context;
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyles)
    {
        _paintBorder.setColor(Color.GRAY);
        _paintBorder.setAntiAlias(true);
        _paintBorder.setStyle(Paint.Style.STROKE);
        _paintBorder.setStrokeWidth(_strokeWidth);

        this.setSoundEffectsEnabled(false);
    }

    //======================================= Getters ==============================================
    public Path getPath()
    {
        return _path;
    }

    public Region getRegion()
    {
        return _region;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        //int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(parentWidth, parentWidth);


        int w = getWidth();
        _path.moveTo(w / 2, 0);
        _path.lineTo(w - _strokeWidth / 2, w / 4);
        _path.lineTo(w - _strokeWidth / 2, w * 3 / 4);
        _path.lineTo(w / 2, w);
        _path.lineTo(_strokeWidth / 2, w * 3 / 4);
        _path.lineTo(_strokeWidth / 2, w / 4);
        _path.lineTo(w / 2, 0);
        _path.close();

        //Required to compute if onTouchEvent is inside bounds
        _path.computeBounds(_rectF, true);
        _region = new Region((int) _rectF.left, (int) _rectF.top, (int) _rectF.right, (int) _rectF.bottom);
        _region.setPath(_path, _region);
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        float touchX = motionEvent.getX();
        float touchY = motionEvent.getY();

        if (_region.contains((int) touchX, (int) touchY))
        {
            Log.d(TAG, "Touch Inside Hex");
            switch (motionEvent.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return super.onTouchEvent(motionEvent);
        }
        else
        {
            Log.d(TAG, "Touch Ouside Hex");
            return false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawPath(_path, _paintBorder);
    }

    @Override
    public void draw(Canvas canvas)
    {
        if (_paintBackground != null)
        {
            //Log.d(TAG, "setBackGroundColor");
            canvas.drawPath(_path, _paintBackground);
        }
        else
        {
            //Log.d(TAG, "_paintBackground is null");
        }
        super.draw(canvas);
    }

    //================================== MyMethods =================================================
    public void setHexBackgroundColor(int color)
    {
        //Log.d(TAG, "setHexBackgroundColor()");
        _paintBackground = new Paint();
        _paintBackground.setAntiAlias(true);
        _paintBackground.setStyle(Paint.Style.FILL);
        _paintBackground.setColor(color);
        _paintBackground.setAlpha(164);
    }

    public int getHexBackgroundColor()
    {
        if(_paintBackground != null)
        {
            //Disregard alpha channel as it is used only for cell paint.
            int returnColor = Color.rgb(Color.red(_paintBackground.getColor()),
                                         Color.green(_paintBackground.getColor()),
                                         Color.blue(_paintBackground.getColor()));
            return returnColor;
        }
        else
        {
            return Color.TRANSPARENT;
        }
    }

    public void startCheckerAnimation(final AnimationDrawable animation)
    {
        this.setBackgroundDrawable(animation);
        this.post(new Runnable(){
            @Override
            public void run() {
               animation.start();
            }
        });
    }

    public void stopCheckerAnimation(AnimationDrawable animation)
    {
        if(animation != null)
        {
            animation.stop();
            //Set blank drawabla animation frame to erase cell
            animation.selectDrawable(10);
            //Set null drawable for view
            this.setBackgroundDrawable(null);
            //Jump to first frame in animation object. It is required to start this animation object smoothly. Otherwise it will start from blank frame next time.
            animation.selectDrawable(0);
            System.gc();                                                                            //Run garbage collector
        }
    }
}