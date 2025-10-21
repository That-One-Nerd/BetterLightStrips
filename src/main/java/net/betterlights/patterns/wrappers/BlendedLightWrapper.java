package net.betterlights.patterns.wrappers;

import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;
import net.betterlights.patterns.LightPattern;

/** A light wrapper than blends between two underlying light patterns by a given amount. */
public class BlendedLightWrapper extends LightWrapper
{
    protected final LightPattern toMix;
    protected final double amount;

    public BlendedLightWrapper(LightPattern a, LightPattern b, double t)
    {
        super(a);
        toMix = b;
        amount = t;
    }

    @Override
    public void incrementTick()
    {
        underlying.incrementTick();
        toMix.incrementTick();
    }
    @Override
    public void setStartTick(int newStart)
    {
        underlying.setStartTick(newStart);
        toMix.setStartTick(newStart);
    }
    @Override
    public void setCurrentTick(int newTick)
    {
        underlying.setCurrentTick(newTick);
        toMix.setCurrentTick(newTick);
    }

    @Override
    public void applyTo(LEDReader reader, LEDWriter writer)
    {
        // Compute the two patterns.
        int length = reader.getLength();
        Color[] bufA = new Color[length],
                bufB = new Color[length];
        underlying.applyTo(reader, (i, r, g, b) -> bufA[i] = new Color(r, g, b));
        toMix.applyTo(reader, (i, r, g, b) -> bufB[i] = new Color(r, g, b));

        // Then lerp between them.
        for (int i = 0; i < length; i++)
        {
            Color mixed = colorLerp(bufA[i], bufB[i], amount);
            writer.setLED(i, mixed);
        }
    }
}
