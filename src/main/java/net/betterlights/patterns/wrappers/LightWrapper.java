package net.betterlights.patterns.wrappers;

import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import net.betterlights.patterns.LightPattern;

/** A plain simple wrapper for a light pattern. Intended to be used as a base class. */
public class LightWrapper extends LightPattern
{
    protected final LightPattern underlying;

    public LightWrapper(LightPattern pattern)
    {
        underlying = pattern;
    }

    @Override public int getTick() { return underlying.getTick(); }
    @Override public void incrementTick() { underlying.incrementTick(); }
    @Override public void setStartTick(int newStart) { underlying.setStartTick(newStart); }
    @Override public void setCurrentTick(int newTick) { underlying.setCurrentTick(newTick); }

    @Override public LightPattern reversed() { return underlying.reversed(); }

    @Override public boolean useAbsoluteTicks() { return underlying.useAbsoluteTicks(); }
    @Override public void onEnabled() { underlying.onEnabled(); }
    @Override public void onDisabled() { underlying.onDisabled(); }
    @Override public boolean isComplete() { return super.isComplete(); }

    @Override public void applyTo(LEDReader reader, LEDWriter writer) { underlying.applyTo(reader, writer); }
}
