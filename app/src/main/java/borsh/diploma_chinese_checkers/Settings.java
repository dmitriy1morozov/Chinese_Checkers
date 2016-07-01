package borsh.diploma_chinese_checkers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Borsh on 22.06.2016.
 */
public class Settings implements Serializable
{
    // ============================== Constants ====================================================
    public final static String TAG = "=Settings=";
    public final static String SETTINGS_FILE_NAME = "settings.dat";

    // ============================== Fields =======================================================
    private transient Context _context;
    private boolean           _isScreenAlwaysOn;
    private int               _musicVolume;
    private int               _soundVolume;

    // AlertDialog fields
    private transient LinearLayout        _llAlertDialogSettings;
    private transient AlertDialog.Builder _settingsBuilder;

    // ============================== Constructor ==================================================
    public Settings(Context context)
    {
        this._context = context;
        _isScreenAlwaysOn = false;
        _musicVolume = 100;
        _soundVolume = 100;
    }

    // ============================== Setters ======================================================
    public void setAlwaysScreenOn(boolean ScreenAlwaysOn)
    {
        this._isScreenAlwaysOn = ScreenAlwaysOn;
        if(this._isScreenAlwaysOn)
        {
            ((Activity) _context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        else
        {
            ((Activity) _context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        ToggleButton tbScreenAlwaysOn = (ToggleButton)this._llAlertDialogSettings.findViewById(R.id.toggle_screen_on);
        tbScreenAlwaysOn.setChecked(main._settings.get_IsAlwaysScreenOnStatus());
    }
    public void set_musicVolume(int volume)
    {
        ImageButton ibMusicVolume = (ImageButton) this._llAlertDialogSettings.findViewById(R.id.ib_music_volume);
        if(volume == 0 )
        {
            ibMusicVolume.setBackground(_context.getResources().getDrawable(R.drawable.music_volume_0));
        }
        if(volume > 0  && volume <= 33)
        {
            ibMusicVolume.setBackground(_context.getResources().getDrawable(R.drawable.music_volume_33));
        }
        if(volume > 33 && volume <= 67)
        {
            ibMusicVolume.setBackground(_context.getResources().getDrawable(R.drawable.music_volume_67));
        }
        if(volume > 67)
        {
            ibMusicVolume.setBackground(_context.getResources().getDrawable(R.drawable.music_volume_100));
        }

        this._musicVolume = volume;
        main._backgroundMusic.changeVolume((float)this._musicVolume / 100);

        SeekBar sbMusicVolume = (SeekBar)this._llAlertDialogSettings.findViewById(R.id.sb_music_volume);
        sbMusicVolume.setProgress(main._settings.get_musicVolume());
    }

    public void set_soundVolume(int volume)
    {
        ImageButton  ibSoundVolume    = (ImageButton) this._llAlertDialogSettings.findViewById(R.id.ib_sound_volume);
        if(volume == 0 )
        {
            ibSoundVolume.setBackground(_context.getResources().getDrawable(R.drawable.sound_volume_0));
        }
        if(volume > 0  && volume <= 33)
        {
            ibSoundVolume.setBackground(_context.getResources().getDrawable(R.drawable.sound_volume_33));
        }
        if(volume > 33 && volume <= 67)
        {
            ibSoundVolume.setBackground(_context.getResources().getDrawable(R.drawable.sound_volume_67));
        }
        if(volume > 67)
        {
            ibSoundVolume.setBackground(_context.getResources().getDrawable(R.drawable.sound_volume_100));
        }

        this._soundVolume = volume;

        SeekBar sbSoundVolume = (SeekBar) this._llAlertDialogSettings.findViewById(R.id.sb_sound_volume);
        sbSoundVolume.setProgress(main._settings.get_soundVolume());
    }


    // ============================== Getters ======================================================
    public int get_musicVolume()
    {
        return this._musicVolume;
    }
    public int get_soundVolume()
    {
        return this._soundVolume;
    }
    public boolean get_IsAlwaysScreenOnStatus()
    {
        return this._isScreenAlwaysOn;
    }
    public AlertDialog.Builder get_settingsBuilder()
    {
        return this._settingsBuilder;
    }
    public LinearLayout get_llAlertDialogSettings()
    {
        return this._llAlertDialogSettings;
    }

    //================================= Methods ====================================================
    public void createSettingsAlertDialogBuilder()
    {
        LayoutInflater inflater = ((Activity) _context).getLayoutInflater();
        this._llAlertDialogSettings = (LinearLayout) inflater.inflate(R.layout.alert_dialog_settings, null);

        this._settingsBuilder = new AlertDialog.Builder(_context);
        this._settingsBuilder.setView(this._llAlertDialogSettings);
        this._settingsBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });
        this._settingsBuilder.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                if((Settings.this._llAlertDialogSettings.getParent()) != null)
                {
                    ((ViewGroup) Settings.this._llAlertDialogSettings.getParent()).removeAllViews();
                }
            }
        });

        final ToggleButton tbScreenAlwaysOn = (ToggleButton) this._llAlertDialogSettings.findViewById(R.id.toggle_screen_on);
        tbScreenAlwaysOn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Settings.this.setAlwaysScreenOn(((ToggleButton) v).isChecked());
            }
        });

        ImageButton ibMusicNextTrack = (ImageButton) this._llAlertDialogSettings.findViewById(R.id.ibNextTrack);
        ibMusicNextTrack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO make this work
                main._backgroundMusic.nextTrack();
            }
        });

        final SeekBar sbMusicVolume = (SeekBar) this._llAlertDialogSettings.findViewById(R.id.sb_music_volume);
        sbMusicVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                Settings.this.set_musicVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                Log.d(TAG, "sbMusicVolume.onStopTrackingTouch. Progress = " + Settings.this.get_musicVolume());
            }
        });

        final SeekBar sbSoundVolume = (SeekBar) this._llAlertDialogSettings.findViewById(R.id.sb_sound_volume);
        sbSoundVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                Settings.this.set_soundVolume(seekBar.getProgress());
                main.playSound(main.SOUND_MOVE);
                Log.d(TAG, "sbSoundVolume.onStopTrackingTouch. Progress = " + Settings.this.get_soundVolume());
            }
        });

        final ImageButton ibMusicVolume = (ImageButton) this._llAlertDialogSettings.findViewById(R.id.ib_music_volume);
        ibMusicVolume.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (Settings.this.get_musicVolume() != 0)
                {
                    Settings.this.set_musicVolume(0);
                    sbMusicVolume.setProgress(0);
                }
            }
        });

        final ImageButton ibSoundVolume = (ImageButton) this._llAlertDialogSettings.findViewById(R.id.ib_sound_volume);
        ibSoundVolume.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (Settings.this.get_soundVolume() != 0)
                {
                    Settings.this.set_soundVolume(0);
                    sbSoundVolume.setProgress(0);
                }
            }
        });

        Settings.this.setAlwaysScreenOn(Settings.this.get_IsAlwaysScreenOnStatus());
        Settings.this.set_soundVolume(Settings.this.get_soundVolume());
        Settings.this.set_musicVolume(Settings.this.get_musicVolume());
    }

    public void saveSettingsIntoFile()
    {
        try
        {
            FileOutputStream   FOS = _context.openFileOutput(SETTINGS_FILE_NAME, _context.MODE_PRIVATE);
            ObjectOutputStream OOS = new ObjectOutputStream(FOS);
            OOS.writeObject(this);
            OOS.close();
            FOS.close();
        }
        catch (IOException ioe)
        {
            Log.d(TAG, "Serialize error: " + ioe.getMessage());
        }
    }

    public void restoresSettingsFromFile()
    {
        Settings newSettings;

        try
        {
            FileInputStream   FIS = _context.openFileInput(SETTINGS_FILE_NAME);
            ObjectInputStream OIS = new ObjectInputStream(FIS);
            newSettings = (Settings) OIS.readObject();
            OIS.close();
            FIS.close();

            this.createSettingsAlertDialogBuilder();
            this.set_soundVolume(newSettings.get_soundVolume());
            this.set_musicVolume(newSettings.get_musicVolume());
            this.setAlwaysScreenOn(newSettings.get_IsAlwaysScreenOnStatus());
        }
        catch (IOException | ClassNotFoundException exception)
        {
            Log.d(TAG, "DeSerialize error: " + exception.getMessage());
        }
    }
}
