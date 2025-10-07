package net.betterlights.patterns;

import edu.wpi.first.wpilibj.util.Color;

public abstract class LightPatternTransition extends LightPattern
{
    protected LightPattern startPattern, endPattern;
    protected int duration;

    public LightPatternTransition()
    {
        startPattern = new SolidLightPattern(Color.kBlack);
        endPattern = new SolidLightPattern(Color.kBlack);
        duration = 50;
    }

    @Override
    public void onEnabled()
    {
        endPattern.onEnabled();
    }
    @Override
    public void onDisabled()
    {
        startPattern.onDisabled();
    }

    /** Sets the starting pattern for this transition. */
    public LightPatternTransition withStartPattern(LightPattern pattern)
    {
        startPattern = pattern;
        return this;
    }
    /** Sets the ending pattern for this transition. */
    public LightPatternTransition withEndPattern(LightPattern pattern)
    {
        endPattern = pattern;
        return this;
    }

    /** Sets the duration of this transition in ticks. */
    public LightPatternTransition withDuration(int duration)
    {
        this.duration = duration;
        return this;
    }

    @Override
    public boolean isComplete()
    {
        return getTick() >= duration;
    }
}
