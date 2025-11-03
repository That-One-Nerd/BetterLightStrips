package net.betterlights.patterns.wrappers;

import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import net.betterlights.patterns.LightPattern;

/**
 * A class that supports backwards-compatibility with a more basic LEDPattern object.
 * Note that because we're being compatible here, this class CANNOT automatically animate
 * the underlying pattern. It must have its own animation clock.
 */
public class CompatibleLightWrapper extends LightPattern
{
    protected final LEDPattern pattern;

    public CompatibleLightWrapper(LEDPattern pattern)
    {
        this.pattern = pattern;
    }

    @Override
    public void applyTo(LEDReader reader, LEDWriter writer)
    {
        // Just call the pattern.
        pattern.applyTo(reader, writer);
    }
}
