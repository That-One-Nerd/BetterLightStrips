package net.betterlights.patterns.wrappers;

import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import net.betterlights.patterns.LightPattern;

/** An internal wrapper that reverses the direction of the pattern. */
public class ReversedLightWrapper extends LightPattern
{
    private LightPattern underlying;

    public ReversedLightWrapper(LightPattern pattern)
    {
        underlying = pattern;
    }

    @Override public int getTick() { return underlying.getTick(); }
    @Override public void incrementTick() { underlying.incrementTick(); }
    @Override public void setStartTick(int newStart) { underlying.setStartTick(newStart); }
    @Override public void setCurrentTick(int newTick) { underlying.setCurrentTick(newTick); }

    @Override
    public LightPattern reversed()
    {
        // Un-reverse the pattern. No need to construct a double-wrapper.
        return underlying;
    }

    @Override public boolean useAbsoluteTicks() { return underlying.useAbsoluteTicks(); }
    @Override public void onEnabled() { underlying.onEnabled(); }
    @Override public void onDisabled() { underlying.onDisabled(); }
    @Override public boolean isComplete() { return super.isComplete(); }

    @Override
    public void applyTo(LEDReader reader, LEDWriter writer)
    {
        // Write new LED wrappers that automatically flips the index.
        int length = reader.getLength();
        LEDReader flippedReader = new LEDReader() {
            @Override public int getLength() { return length; }
            @Override public int getRed(int index) { return reader.getRed(length - index - 1); }
            @Override public int getGreen(int index) { return reader.getGreen(length - index - 1); }
            @Override public int getBlue(int index) { return reader.getBlue(length - index - 1); }
        };
        LEDWriter flippedWriter = (i, r, g, b) -> writer.setRGB(length - i - 1, r, g, b);
        underlying.applyTo(flippedReader, flippedWriter);
    }
}
