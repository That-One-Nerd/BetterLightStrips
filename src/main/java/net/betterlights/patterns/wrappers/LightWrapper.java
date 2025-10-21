package net.betterlights.patterns.wrappers;

import edu.wpi.first.units.measure.Dimensionless;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.Frequency;
import edu.wpi.first.units.measure.LinearVelocity;
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

    @Override public LightPattern offsetBy(int offset) { return underlying.offsetBy(offset); }
    @Override public LightPattern reversed() { return underlying.reversed(); }
    @Override public LightPattern scroll(double pixelsPerTick) { return underlying.scroll(pixelsPerTick); }
    @Override public LightPattern scrollAtAbsoluteSpeed(LinearVelocity velocity, Distance ledSpacing) { return underlying.scrollAtAbsoluteSpeed(velocity, ledSpacing); }
    @Override public LightPattern scrollAtRelativeSpeed(Frequency velocity) { return underlying.scrollAtRelativeSpeed(velocity); }
    @Override public LightPattern atBrightness(double brightness) { return underlying.atBrightness(brightness); }
    @Override public LightPattern atBrightness(Dimensionless relativeBrightness) { return underlying.atBrightness(relativeBrightness); }

    @Override public boolean useAbsoluteTicks() { return underlying.useAbsoluteTicks(); }
    @Override public void onEnabled() { underlying.onEnabled(); }
    @Override public void onDisabled() { underlying.onDisabled(); }
    @Override public boolean isComplete() { return super.isComplete(); }

    @Override public void applyTo(LEDReader reader, LEDWriter writer) { underlying.applyTo(reader, writer); }
}
