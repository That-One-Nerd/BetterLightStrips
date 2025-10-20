package net.betterlights.transitions;

import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;
import net.betterlights.LightScheduler;
import net.betterlights.patterns.LightPattern;
import net.betterlights.patterns.SolidLightPattern;

public abstract class LightTransition extends LightPattern
{
    protected LightPattern startPattern, endPattern;
    protected int contTick;

    public LightTransition()
    {
        startPattern = new SolidLightPattern(Color.kBlack);
        endPattern = new SolidLightPattern(Color.kBlack);
    }

    @Override
    public void onEnabled()
    {
        contTick = LightScheduler.getAbsoluteTicks();
        endPattern.onEnabled();
        endPattern.setStartTick(contTick);
    }
    @Override
    public void onDisabled()
    {
        startPattern.onDisabled();
    }

    /** Sets the starting pattern for this transition. */
    public LightTransition withStartPattern(LightPattern pattern)
    {
        startPattern = pattern;
        return this;
    }
    /** Sets the ending pattern for this transition. */
    public LightTransition withEndPattern(LightPattern pattern)
    {
        endPattern = pattern;
        return this;
    }

    public int getContinuationTick() { return contTick; }

    @Override
    public void applyPattern(LEDReader reader, LEDWriter writer)
    {
        int length = reader.getLength();
        Color[] bufferA = new Color[length], bufferB = new Color[length];

        int tick = LightScheduler.getAbsoluteTicks();
        startPattern.setCurrentTick(tick);
        endPattern.setCurrentTick(tick);
        
        // Apply the patterns to the buffers.
        startPattern.applyTo(reader, (i, r, g, b) -> bufferA[i] = new Color(r, g, b));
        endPattern.applyTo(reader, (i, r, g, b) -> bufferB[i] = new Color(r, g, b));

        // Apply the new transition.
        applyTransition(length, bufferA, bufferB, writer);
    }
    public abstract void applyTransition(int length, Color[] startBuffer, Color[] endBuffer, LEDWriter writer);

    public abstract boolean isComplete();
}
