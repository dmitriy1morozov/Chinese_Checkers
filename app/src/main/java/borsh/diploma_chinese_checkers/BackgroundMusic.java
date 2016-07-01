package borsh.diploma_chinese_checkers;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Borsh on 21.06.2016.
 */
public class BackgroundMusic extends Thread
{
    //============================== Constants =====================================================
    public final static String TAG = "===BackgroundMusic===";

    //============================== Fields ========================================================
    public Context     _context;
    private MediaPlayer _mediaPlayer;
    private boolean     _isRunning = false;
    private boolean _isPause = false;

    //============================== Constructor ===================================================
    public BackgroundMusic(Context context)
    {
        super();
        this._context = context;
        _mediaPlayer = MediaPlayer.create(context, R.raw.music1);
    }

    public void changeVolume(float volume)
    {
        _mediaPlayer.setVolume(volume, volume);
    }

    @Override
    public void run()
    {
        _isRunning = true;
        _mediaPlayer.setLooping(true);
        _mediaPlayer.setVolume(main._settings.get_musicVolume(), main._settings.get_musicVolume());

        if (!_mediaPlayer.isPlaying())
        {
            _mediaPlayer.start();
            Log.d(TAG, "backgroundMusic started");
        }
        else
        {
            Log.d(TAG, "backgroundMusic is already playing");
        }
    }

    public synchronized void pauseMusic()
    {
        /*
        try
        {
            _isPause = true;
            this.wait();
        }
        catch (InterruptedException ie)
        {
            Log.d(TAG, "backgroundMusic InterruptedException: " + ie.getMessage());
        }
        */

        _mediaPlayer.pause();
    }

    public void startMusic()
    {
        /*
        _isPause = false;
        if(!_isRunning)
        {
            _mediaPlayer.start();
        }
        else
        {
            this.notify();
            //this.start();
        }
        */

        _mediaPlayer.start();
    }

    public void nextTrack()
    {
        //TODO make next Track
        Toast.makeText(_context, "Next track", Toast.LENGTH_SHORT).show();
    }

}
