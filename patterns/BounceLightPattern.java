package betterlights.patterns;

import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;

/**
 * A pattern that displays a bouncing section of color across the segment. 
 * Has the ability to move in square or sine wave format, and the edges
 * can be faded to form more of a moving gradient.
 */
public class BounceLightPattern extends LightPattern
{
    private int radius = 2;
    private boolean fade = false;

    private Color colorFore, colorBack;

    private boolean hardBounce = true;

    private double speed = 1; // def 1

    public BounceLightPattern()
    {
        colorFore = Color.kWhite;
        colorBack = Color.kBlack;
    }
    public BounceLightPattern(Color color)
    {
        colorFore = color;
        colorBack = Color.kBlack;
    }
    public BounceLightPattern(Color colorFore, Color colorBack)
    {
        this.colorFore = colorFore;
        this.colorBack = colorBack;
    }

    /** Sets the foreground color for this pattern. */
    public BounceLightPattern withColorFront(Color color)
    {
        colorFore = color;
        return this;
    }
    /** Sets the background color for this pattern. */
    public BounceLightPattern withColorBack(Color color)
    {
        colorBack = color;
        return this;
    }

    /**
     * Sets the length of the foreground color for this pattern. The given value
     * is interpreted as the distance from the center of the pattern to one of
     * the extents.
     */
    public BounceLightPattern withLength(int radius)
    {
        this.radius = radius;
        return this;
    }
    /**
     * Makes the pattern fade between the foreground color and the background color
     * based on an LED's distance from the center of the wave. See also `sharp()`.
     */
    public BounceLightPattern smooth()
    {
        fade = true;
        return this;
    }
    /**
     * Makes the pattern have no fading between the foreground color and the background
     * color. The opposite of `smooth()`.
     */
    public BounceLightPattern sharp()
    {
        fade = false;
        return this;
    }

    /**
     * Makes this pattern appear to bounce off the sides of the segment, rather
     * than smoothly flowing. By default, this is on. See also `withWaveBounce()`.
     */
    public BounceLightPattern withHardBounce()
    {
        hardBounce = true;
        return this;
    }
    /**
     * Makes this pattern smoothly travel from one side to the next according to
     * a sine wave. By default, this is false. See also `withHardBounce`.
     */
    public BounceLightPattern withWaveBounce()
    {
        hardBounce = false;
        return this;
    }

    /** Sets the speed of this pattern. Units change depending on configuration. */
    public BounceLightPattern withMoveSpeed(double speed)
    {
        this.speed = speed;
        return this;
    }

    @Override
    public void applyTo(LEDReader reader, LEDWriter writer)
    {
        int length = reader.getLength(), tick = getTick();

        double center = getEffectCenter(length, tick);
        for (int i = 0; i < length; i++)
        {
            double absDist = Math.abs((center - 0.5) - i);

            if (fade)
            {
                double dist = (double)absDist / radius;
                // TODO: Gamma
                writer.setLED(i, colorLerp(colorFore, colorBack, dist));
            }
            else
            {
                if (absDist <= radius) writer.setLED(i, colorFore);
                else writer.setLED(i, colorBack);
            }
        }
    }

    private double getEffectCenter(int length, int tick)
    {
        int trueRadius = fade ? radius - 1 : radius;
        int trueLength = length - trueRadius * 2;

        double place = tick * speed;
        if (hardBounce)
        {
            int doubleLength = trueLength * 2;
            place %= doubleLength;
            if (place < trueLength) return place + trueRadius;
            else
            {
                double val = doubleLength - place + trueRadius;
                if (val > length - 1) val = length - 1;
                return val;
            }
        }
        else
        {
            final double oneOverTwoPi = 0.159154943092;
            return (Math.sin(place * oneOverTwoPi) + 1) * 0.5 * trueLength + trueRadius;
        }
    }
}
