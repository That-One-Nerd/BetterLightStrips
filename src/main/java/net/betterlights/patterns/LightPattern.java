package net.betterlights.patterns;

import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;

/** Represents a light pattern used by the scheduler. Can be applied to any named light segment. */
public abstract class LightPattern implements LEDPattern
{
    private int curTick;
    private int startTick;

    public int getTick()
    {
        if (useAbsoluteTicks()) return curTick;
        else return curTick - startTick;
    }
    public void setStartTick(int newStart)
    {
        startTick = newStart;
    }
    public void setCurrentTick(int newTick)
    {
        curTick = newTick;
    }
    public abstract void applyTo(LEDReader reader, LEDWriter writer);

    public boolean useAbsoluteTicks() { return false; }

    public void onEnabled() { }
    public void onDisabled() { }
}
