package betterlights.patterns.wrappers.implementations;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;
import betterlights.patterns.LightPattern;
import betterlights.patterns.wrappers.LightWrapper;

/** A wrapper that displays its underlying pattern only if a given condition is true, or alternatively during a given interval. */
public class BlinkedLightWrapper extends LightWrapper
{
    protected final BooleanSupplier condition;
    protected final int timeOn, timeOff;

    protected double easeInPerTick, easeOutPerTick;
    protected boolean startEnabled;
    
    protected double showLerp;

    public BlinkedLightWrapper(LightPattern pattern, BooleanSupplier condition)
    {
        super(pattern);
        this.condition = condition;
        this.timeOn = 0;
        this.timeOff = 0;

        easeInPerTick = 1;
        easeOutPerTick = 1;

        startEnabled = false;
    }
    public BlinkedLightWrapper(LightPattern pattern, int timeOn, int timeOff)
    {
        super(pattern);
        this.condition = null;
        this.timeOn = timeOn;
        this.timeOff = timeOff;

        easeInPerTick = 1;
        easeOutPerTick = 1;

        startEnabled = false;
    }

    @Override
    public void onEnabled()
    {
        super.onEnabled();

        // We've just started. If we're using a supplied condition,
        // don't bother with animations for the start.
        if (condition != null)
        {
            if (condition.getAsBoolean()) showLerp = 1;
            else showLerp = 0;
        }
    }

    /** Gives a time for the pattern to fade in when it decides to show. */
    public BlinkedLightWrapper withEaseIn(int ticksToShow)
    {
        if (ticksToShow == 0) easeInPerTick = 1;
        else easeInPerTick = 1.0 / (ticksToShow + 1);
        return this;
    }
    /** Gives a time for the pattern to fade out when it decides to hide. */
    public BlinkedLightWrapper withEaseOut(int ticksToHide)
    {
        if (ticksToHide == 0) easeOutPerTick = 1;
        else easeOutPerTick = 1.0 / (ticksToHide + 1);
        return this;
    }

    /** The pattern will be enabled when it first starts. */
    public BlinkedLightWrapper startOn()
    {
        startEnabled = true;
        return this;
    }
    /** The pattern will be disabled when it first starts. This is the default behavior. */
    public BlinkedLightWrapper startOff()
    {
        startEnabled = false;
        return this;
    }

    @Override
    public void applyTo(LEDReader reader, LEDWriter writer)
    {
        // First, determine if we should be showing the pattern or not.
        int tick = getTick();
        boolean show;
        if (condition != null) show = condition.getAsBoolean();
        else
        {
            if (startEnabled) show = (tick % (timeOn + timeOff)) < timeOn;
            else show = (tick % (timeOn + timeOff)) >= timeOff;
        }

        // If we can show, then update our fade in/out timer.
        if (show) showLerp += easeInPerTick;
        else showLerp -= easeOutPerTick;

        // Then set the stuff. Combine clamping to do this in a single step.
        if (showLerp <= 0)
        {
            showLerp = 0;
            kOff.applyTo(reader, writer);
        }
        else if (showLerp >= 1)
        {
            showLerp = 1;
            underlying.applyTo(reader, writer);
        }
        else
        {
            // Interpolate!
            int length = reader.getLength();
            Color[] buf = new Color[length];
            LEDWriter bufWriter = (i, r, g, b) -> buf[i] = new Color(r, g, b);
            underlying.applyTo(reader, bufWriter);

            for (int i = 0; i < length; i++)
            {
                Color c = colorLerp(Color.kBlack, buf[i], showLerp);
                writer.setLED(i, c);
            }
        }
    }
}
