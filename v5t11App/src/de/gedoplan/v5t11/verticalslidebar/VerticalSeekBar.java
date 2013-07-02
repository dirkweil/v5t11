package de.gedoplan.v5t11.verticalslidebar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;
import de.gedoplan.v5t11.listener.OnSeekBarChangeListener;

/**
 * A SeekBar is an extension of ProgressBar that adds a draggable thumb. The user can touch the thumb and drag left or right to
 * set the current progress level or use the arrow keys. Placing focusable widgets to the left or right of a SeekBar is
 * discouraged.
 * <p>
 * Clients of the SeekBar can attach a {@link SeekBar.OnSeekBarChangeListener} to be notified of the user's actions.
 * 
 * @attr ref android.R.styleable#SeekBar_thumb
 */
public class VerticalSeekBar extends AbsVerticalSeekBar
{

  private OnSeekBarChangeListener mOnSeekBarChangeListener;

  public VerticalSeekBar(Context context)
  {
    this(context, null);
  }

  public VerticalSeekBar(Context context, AttributeSet attrs)
  {
    this(context, attrs, android.R.attr.seekBarStyle);
  }

  public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
  }

  @Override
  void onProgressRefresh(float scale, boolean fromUser)
  {
    super.onProgressRefresh(scale, fromUser);

    if (mOnSeekBarChangeListener != null)
    {
      mOnSeekBarChangeListener.onProgressChanged(this, getProgress(), fromUser);
    }
  }

  /**
   * Sets a listener to receive notifications of changes to the SeekBar's progress level. Also provides notifications of when the
   * user starts and stops a touch gesture within the SeekBar.
   * 
   * @param l The seek bar notification listener
   * 
   * @see SeekBar.OnSeekBarChangeListener
   */
  public void setOnSeekBarChangeListener(OnSeekBarChangeListener l)
  {
    mOnSeekBarChangeListener = l;
  }

  @Override
  void onStartTrackingTouch()
  {
    if (mOnSeekBarChangeListener != null)
    {
      mOnSeekBarChangeListener.onStartTrackingTouch(this);
    }
  }

  @Override
  void onStopTrackingTouch()
  {
    if (mOnSeekBarChangeListener != null)
    {
      mOnSeekBarChangeListener.onStopTrackingTouch(this);
    }
  }

}
