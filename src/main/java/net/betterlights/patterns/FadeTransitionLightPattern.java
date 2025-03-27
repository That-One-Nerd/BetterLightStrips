package net.betterlights.patterns;

import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;

/** A pattern that transitions between two other internal light patterns, while continuing the animation of each. */
public class FadeTransitionLightPattern extends LightPattern
{
    private double gamma;

    private LightPattern patternA, patternB;

    private int duration;

    public FadeTransitionLightPattern()
    {
        gamma = 1.0;
        patternA = new SolidLightPattern(Color.kBlack);
        patternB = new SolidLightPattern(Color.kBlack);
        duration = 50;
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
        patternA = pattern;
        return this;
    }
    /** Sets the ending pattern for this transition. */
    public FadeTransitionLightPattern withEndPattern(LightPattern pattern)
    {
        patternB = pattern;
        return this;
    }

    /** Sets the duration of this transition in ticks. */
    public FadeTransitionLightPattern withDuration(int duration)
    {
        this.duration = duration;
        return this;
    }

    @Override
    public void applyTo(LEDReader reader, LEDWriter writer)
    {
        int length = reader.getLength();
        Color[] bufferA = new Color[length], bufferB = new Color[length];

        patternA.incrementTick();
        patternB.incrementTick();
        
        // Apply the patterns to the buffers. This will break if the LED values are read from the reader.
        patternA.applyTo(reader, (i, r, g, b) -> bufferA[i] = new Color(r, g, b));
        patternB.applyTo(reader, (i, r, g, b) -> bufferB[i] = new Color(r, g, b));

        // Interpolate between the two buffers.
        double time = (double)getTick() / duration;
        for (int i = 0; i < length; i++) writer.setLED(i, colorLerp(bufferA[i], bufferB[i], time, gamma));
    }

    @Override
    public boolean isComplete()
    {
        return getTick() >= duration;
    }
}
