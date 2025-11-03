package net.betterlights.patterns.wrappers.implementations;

import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;
import net.betterlights.patterns.LightPattern;
import net.betterlights.patterns.wrappers.LightWrapper;

/** A wrapper that fades its underlying pattern in and out according to a cosine wave. */
public class BreathingLightWrapper extends LightWrapper
{
    protected final int period;
    protected boolean startEnabled;

    public BreathingLightWrapper(LightPattern pattern, int period)
    {
        super(pattern);
        this.period = period;
        startEnabled = true;
    }

    /** The pattern will be enabled when it first starts. */
    public BreathingLightWrapper startOn()
    {
        startEnabled = true;
        return this;
    }
    /** The pattern will be disabled when it first starts. This is the default behavior. */
    public BreathingLightWrapper startOff()
    {
        startEnabled = false;
        return this;
    }

    @Override
    public void applyTo(LEDReader reader, LEDWriter writer)
    {
        // Determine intensity.
        int tick = getTick();
        double t = 2 * Math.PI * tick / period;
        if (startEnabled) t += 0.5 * Math.PI;
        
        double intensity = 0.5 * (Math.sin(t) + 1);

        // Interpolate!
        int length = reader.getLength();
        Color[] buf = new Color[length];
        LEDWriter bufWriter = (i, r, g, b) -> buf[i] = new Color(r, g, b);
        underlying.applyTo(reader, bufWriter);

        for (int i = 0; i < length; i++)
        {
            Color c = colorLerp(Color.kBlack, buf[i], intensity);
            writer.setLED(i, c);
        }
    }
}
