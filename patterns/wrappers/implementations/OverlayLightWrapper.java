package betterlights.patterns.wrappers.implementations;

import edu.wpi.first.wpilibj.util.Color;
import betterlights.patterns.LightPattern;
import betterlights.patterns.wrappers.BinaryLightWrapper;

/** A light wrapper that overlays one pattern on another. */
public class OverlayLightWrapper extends BinaryLightWrapper
{
    public OverlayLightWrapper(LightPattern patternA, LightPattern patternB)
    {
        super(patternA, patternB);
    }
    
    @Override
    protected Color mix(Color inputA, Color inputB)
    {
        // Use the r, g, and b values to interpolate the final color.
        // We need to basically re-implement the lerp function here since
        // it's per-channel.

        return new Color(
            lerpGamma(inputA.red, 1, inputB.red),
            lerpGamma(inputA.green, 1, inputB.green),
            lerpGamma(inputA.blue, 1, inputB.blue)
        );
    }

    protected double lerpGamma(double a, double b, double t)
    {
        if (t <= 0) return a;
        else if (t >= 1) return b;
        
        double invGamma = 1 / gamma;
        double aGamma = Math.pow(a, gamma), bGamma = Math.pow(b, gamma);
        double cGamma = aGamma + t * (bGamma - aGamma);

        return Math.pow(cGamma, invGamma);
    }
}
