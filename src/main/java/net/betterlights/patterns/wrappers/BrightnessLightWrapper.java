package net.betterlights.patterns.wrappers;

import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import net.betterlights.patterns.LightPattern;

public class BrightnessLightWrapper extends LightWrapper
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
    public void applyTo(LEDReader reader, LEDWriter writer)
    {
        underlying.applyTo(reader, (i, r, g, b) ->
        {
            int newR = (int)clamp(r * factor, 0, 255),
                newG = (int)clamp(g * factor, 0, 255),
                newB = (int)clamp(b * factor, 0, 255);
            writer.setRGB(i, newR, newG, newB);
        });
    }

    private static double clamp(double n, double min, double max)
    {
        if (n <= min) return min;
        else if (n >= max) return max;
        else return n;
    }
}
