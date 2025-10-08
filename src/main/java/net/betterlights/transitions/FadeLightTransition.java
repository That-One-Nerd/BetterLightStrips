package net.betterlights.transitions;

import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;
import net.betterlights.patterns.LightPattern;

/** A pattern that transitions between two other internal light patterns, while continuing the animation of each. */
public class FadeLightTransition extends LightTransition
{
    private double gamma;
    private boolean useFadeColor;
    private Color fadeColor;

    private int duration;

    public FadeLightTransition()
    {
        super();
        gamma = 1.0;
        useFadeColor = false;
    }

    /**
     * Sets the interpolating gamma value for this pattern.
     * Changing this value results in changing the transition colors.
     * A value closer to one prefers lighter in-betweens.
     * Typically, 2.2 is used for simulation and a number close to 1 is used with actual strips.
     */
    public FadeLightTransition withGamma(double gamma)
    {
        this.gamma = gamma;
        return this;
    }

    public FadeLightTransition withStartPattern(LightPattern pattern) { super.withStartPattern(pattern); return this; }
    public FadeLightTransition withEndPattern(LightPattern pattern) { super.withEndPattern(pattern); return this; }

    /** Sets the total time in ticks for this transition to complete. */
    public FadeLightTransition withDuration(int duration)
    {
        this.duration = duration;
        return this;
    }

    /** Fade into the given color before fading into the next pattern. */
    public FadeLightTransition withColor(Color fadeColor)
    {
        useFadeColor = true;
        this.fadeColor = fadeColor;
        return this;
    }
    /** Fade directly from one pattern to the next. This is the default behavior. */
    public FadeLightTransition withNoColor()
    {
        useFadeColor = false;
        return this;
    }

    @Override
    public void applyTransition(int length, Color[] startBuffer, Color[] endBuffer, LEDWriter writer)
    {
        // Interpolate between the two buffers.
        double time = (double)getTick() / duration;

        for (int i = 0; i < length; i++)
        {
            Color outputColor;
            if (useFadeColor)
            {
                if (time <= 0.5) outputColor = colorLerp(startBuffer[i], fadeColor, 2 * time);
                else outputColor = colorLerp(fadeColor, endBuffer[i], 2 * (time - 0.5));
            }
            else outputColor = colorLerp(startBuffer[i], endBuffer[i], time, gamma);

            writer.setLED(i, outputColor);
        }
    }

    @Override
    public boolean isComplete()
    {
        return getTick() >= duration;
    }
}
