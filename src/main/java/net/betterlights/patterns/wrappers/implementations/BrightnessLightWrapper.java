package net.betterlights.patterns.wrappers.implementations;

import edu.wpi.first.wpilibj.util.Color;
import net.betterlights.ColorHelper;
import net.betterlights.patterns.LightPattern;
import net.betterlights.patterns.wrappers.UnaryLightWrapper;

/** A light wrapper that modifies the brightness of its underlying pattern by a specific factor. */
public class BrightnessLightWrapper extends UnaryLightWrapper
{
    protected final double factor;

    public BrightnessLightWrapper(LightPattern pattern, double brightness)
    {
        super(pattern);
        factor = brightness;
    }

    @Override
    public LightPattern atBrightness(double brightness)
    {
        // Prevent redundant wrappers.
        if (brightness * factor == 1) return underlying;
        else return new BrightnessLightWrapper(underlying, brightness * factor);
    }

    @Override
    protected Color mix(Color color)
    {
        // Multiply the color by a given brightness value.
        double newR = ColorHelper.clamp(color.red * Math.abs(factor), 0, 1),
               newG = ColorHelper.clamp(color.green * Math.abs(factor), 0, 1),
               newB = ColorHelper.clamp(color.blue * Math.abs(factor), 0, 1);
        
        if (factor < 0)
        {
            // Invert the colors.
            newR = 1 - newR;
            newG = 1 - newG;
            newB = 1 - newB;
        }
        
        return new Color(newR, newG, newB);
    }
}
