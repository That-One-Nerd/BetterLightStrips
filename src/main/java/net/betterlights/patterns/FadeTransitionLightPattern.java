package net.betterlights.patterns;

import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;

/** A pattern that transitions between two other internal light patterns, while continuing the animation of each. */
public class FadeTransitionLightPattern extends LightPatternTransition
{
    private double gamma;

    public FadeTransitionLightPattern()
    {
        super();
        gamma = 1.0;
    }

    /**
     * Sets the interpolating gamma value for this pattern.
     * Changing this value results in changing the transition colors.
     * A value closer to one prefers lighter in-betweens.
     * Typically, 2.2 is used for simulation and a number close to 1 is used with actual strips.
     */
    public FadeTransitionLightPattern withGamma(double gamma)
    {
        this.gamma = gamma;
        return this;
    }

    /** Sets the starting pattern for this transition. */
    public FadeTransitionLightPattern withStartPattern(LightPattern pattern)
    {
        super.withStartPattern(pattern);
        return this;
    }
    /** Sets the ending pattern for this transition. */
    public FadeTransitionLightPattern withEndPattern(LightPattern pattern)
    {
        super.withEndPattern(pattern);
        return this;
    }

    /** Sets the duration of this transition in ticks. */
    public FadeTransitionLightPattern withDuration(int duration)
    {
        super.withDuration(duration);
        return this;
    }

    @Override
    public void applyTo(LEDReader reader, LEDWriter writer)
    {
        int length = reader.getLength();
        Color[] bufferA = new Color[length], bufferB = new Color[length];

        startPattern.incrementTick();
        endPattern.setCurrentTick(getTick());
        
        // Apply the patterns to the buffers. This will break if the LED values are read from the reader.
        startPattern.applyTo(reader, (i, r, g, b) -> bufferA[i] = new Color(r, g, b));
        endPattern.applyTo(reader, (i, r, g, b) -> bufferB[i] = new Color(r, g, b));

        // Interpolate between the two buffers.
        double time = (double)getTick() / duration;
        for (int i = 0; i < length; i++) writer.setLED(i, colorLerp(bufferA[i], bufferB[i], time, gamma));
    }
}
