package net.betterlights.patterns.wrappers.implementations;

import edu.wpi.first.wpilibj.util.Color;
import net.betterlights.patterns.LightPattern;
import net.betterlights.patterns.wrappers.BinaryLightWrapper;

/** A light wrapper than blends between two underlying light patterns by a given amount. */
public class BlendedLightWrapper extends BinaryLightWrapper
{
    protected final double amount;

    public BlendedLightWrapper(LightPattern a, LightPattern b, double t)
    {
        super(a, b);
        amount = t;
    }

    @Override
    protected Color mix(Color inputA, Color inputB)
    {
        return colorLerp(inputA, inputB, amount);
    }
}
