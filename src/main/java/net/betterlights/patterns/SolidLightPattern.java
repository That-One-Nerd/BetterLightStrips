package net.betterlights.patterns;

import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;

/** A light pattern that is a single unchanging color. */
public class SolidLightPattern extends LightPattern
{
    /** The color for the lights. */
    public Color color;

    /** Creates a light pattern that displays a given solid color. */
    public SolidLightPattern(Color color)
    {
        this.color = color;
    }

    @Override
    public void applyTo(LEDReader reader, LEDWriter writer)
    {
        int length = reader.getLength();
        for (int i = 0; i < length; i++) writer.setLED(i, color);
    }
}
