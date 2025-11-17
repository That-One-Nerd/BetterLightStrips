package betterlights.patterns.wrappers;

import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import betterlights.patterns.LightPattern;

/** A plain simple wrapper for a light pattern. Intended to be used as a base class. */
public class LightWrapper extends LightPattern
{
    protected final LightPattern underlying;

    public LightWrapper(LightPattern pattern)
    {
        underlying = pattern;
    }

    /** Count how many wrappers are being applied over the base pattern. */
    public int getWrapperDepth()
    {
        // A recursive function would be even cleaner, but no need to waste resources.
        LightPattern focus = underlying;
        int depth = 1;
        while (focus instanceof LightWrapper wrap)
        {
            depth++;
            focus = wrap.underlying;
        }
        return depth;
    }

    @Override public int getTick() { return underlying.getTick(); }
    @Override public void incrementTick() { underlying.incrementTick(); }
    @Override public void setStartTick(int newStart) { underlying.setStartTick(newStart); }
    @Override public void setCurrentTick(int newTick) { underlying.setCurrentTick(newTick); }

    @Override public boolean useAbsoluteTicks() { return underlying.useAbsoluteTicks(); }
    @Override public void onEnabled() { underlying.onEnabled(); }
    @Override public void onDisabled() { underlying.onDisabled(); }
    @Override public boolean isComplete() { return underlying.isComplete(); }

    @Override public void applyTo(LEDReader reader, LEDWriter writer) { underlying.applyTo(reader, writer); }
}
