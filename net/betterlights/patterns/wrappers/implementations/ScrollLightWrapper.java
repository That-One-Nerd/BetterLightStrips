package net.betterlights.patterns.wrappers.implementations;

import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;
import net.betterlights.ColorHelper;
import net.betterlights.patterns.LightPattern;
import net.betterlights.patterns.wrappers.LightWrapper;

/** An internal wrapper that scrolls the pattern at a given rate. */
public class ScrollLightWrapper extends LightWrapper
{
    private final double speed;

    public ScrollLightWrapper(LightPattern pattern, double speed)
    {
        super(pattern);
        this.speed = speed;
    }

    @Override
    public void applyTo(LEDReader reader, LEDWriter writer)
    {
        // First, apply the buffer before we do anything else.
        int length = reader.getLength();
        Color[] buffer = new Color[length];
        underlying.applyTo(reader, (i, r, g, b) -> buffer[i] = new Color(r, g, b));

        // Then work on the offset.
        for (int i = 0; i < length; i++)
        {
            // Get the two indices we're currently interpolating between.
            double offset = getTick() * -speed;
            int minIndex = (int)ColorHelper.absMod(Math.floor(i + offset), length),
                maxIndex = (int)ColorHelper.absMod(Math.ceil(i + offset), length);
            double t = ColorHelper.absMod(offset, 1);

            if (minIndex == maxIndex) writer.setLED(i, buffer[minIndex]); // Easy, they're the same!
            else writer.setLED(i, colorLerp(buffer[minIndex], buffer[maxIndex], t)); // Whelp, let's interpolate.
        }
    }
}
