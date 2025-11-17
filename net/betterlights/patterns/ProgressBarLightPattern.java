package net.betterlights.patterns;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;
import net.betterlights.Gradient;

/** Takes a double supplier and maps it to a progress bar-styled pattern. */
public class ProgressBarLightPattern extends LightPattern
{
    private final DoubleSupplier supplier;

    private Gradient offGradient, onGradient;
    private boolean relativeOn, relativeOff;

    private int refreshEvery;
    private boolean interpolateRefresh;
    private boolean smooth = true;

    public ProgressBarLightPattern(DoubleSupplier supplier)
    {
        offGradient = new Gradient(Color.kBlack);
        onGradient = new Gradient(Color.kGreen);
        this.supplier = supplier;
    }
    protected ProgressBarLightPattern()
    {
        // I've used this system in the UnaryLightWrapper and BinaryLightWrapper classes.
        // If you want to extend the ProgressBarLightPattern, you can implement the get()
        // method and call this protected constructor instead.
        offGradient = new Gradient(Color.kBlack);
        onGradient = new Gradient(Color.kGreen);
        supplier = this::get;
    }

    protected double get() { return 0; }

    @Override public ProgressBarLightPattern withGamma(double gamma)
    {
        super.withGamma(gamma);
        return this;
    }

    /** Set the color for the active part of the progress bar. Can also be a gradient. */
    public ProgressBarLightPattern withOnColor(Color color)
    {
        onGradient = new Gradient(color);
        return this;
    }
    /** Set the color for the active part of the progress bar. */
    public ProgressBarLightPattern withOnColor(Gradient gradient)
    {
        onGradient = gradient;
        return this;
    }

    /** Set the color for the inactive part of the progress bar. Can also be a gradient. */
    public ProgressBarLightPattern withOffColor(Color color)
    {
        offGradient = new Gradient(color);
        return this;
    }
    /** Set the color for the inactive part of the progress bar. */
    public ProgressBarLightPattern withOffColor(Gradient gradient)
    {
        offGradient = gradient;
        return this;
    }

    /**
     * Instead of mapping gradients over the entire segment, both gradients are always
     * fully shown and scale with the size of the bar.
     */
    public ProgressBarLightPattern withRelativeBoth()
    {
        relativeOn = true;
        relativeOff = true;
        return this;
    }
    /**
     * Instead of mapping gradients over the entire segment, the 'on' gradient is always
     * fully shown and scale with the size of the bar.
     */
    public ProgressBarLightPattern withRelativeOn()
    {
        relativeOn = true;
        return this;
    }
    /**
     * Instead of mapping gradients over the entire segment, the 'off' gradient is always
     * fully shown and scale with the size of the bar.
     */
    public ProgressBarLightPattern withRelativeOff()
    {
        relativeOff = true;
        return this;
    }

    /** Make the pattern only call its supplier every given number of ticks. Can help reduce noise. */
    public ProgressBarLightPattern withRefreshEvery(int ticks, boolean interpolate)
    {
        refreshEvery = ticks;
        interpolateRefresh = interpolate;
        return this;
    }

    /** Don't smooth the pixel between the active and inactive parts of the progress bar. */
    public ProgressBarLightPattern sharp() { smooth = false; return this; }
    /**
     * Smooth the pixel between the active and inactive parts of the progress bar.
     * This is the default behavior.
     */
    public ProgressBarLightPattern smooth() { smooth = true; return this; }

    @Override
    public void applyTo(LEDReader reader, LEDWriter writer)
    {
        int length = reader.getLength();

        // Figure out how many pixels to consider "on."        
        double percent = getPercent();
        double pixelsTrue = percent * length;
        int pixelsOn = (int)pixelsTrue;

        // Color 'em!
        for (int i = 0; i < length; i++)
        {
            if (i < pixelsOn)
            {
                // Active part of the bar.
                double location = mapOnGradient(i, length, pixelsTrue);
                writer.setLED(i, onGradient.getColor(location, gamma));
            }
            else if (smooth && i < pixelsTrue)
            {
                // Interpolate this one pixel.
                double part = pixelsTrue % 1;
                double locationOn = mapOnGradient(i, length, pixelsTrue),
                       locationOff = mapOffGradient(i, length, pixelsTrue);
                Color onColor = onGradient.getColor(locationOn, gamma),
                      offColor = offGradient.getColor(locationOff, gamma);
                writer.setLED(i, colorLerp(offColor, onColor, part));
            }
            else
            {
                // Inactive part of the bar.
                double location = mapOffGradient(i, length, pixelsTrue);
                writer.setLED(i, offGradient.getColor(location, gamma));
            }

        }
    }

    protected double mapOnGradient(int i, int length, double pixelsTrue)
    {
        if (relativeOn) return i / (double)(pixelsTrue - 1);
        else return i / (double)(length - 1);
    }
    protected double mapOffGradient(int i, int length, double pixelsTrue)
    {
        if (relativeOff) return (i - pixelsTrue) / (double)(length - pixelsTrue - 1);
        else return i / (double)(length - 1);
    }

    private double lastPercent, nextPercent;
    protected double getPercent()
    {
        if (refreshEvery > 1)
        {
            // Cache value and maybe interpolate.
            int tick = getTick() % refreshEvery;
            if (tick == 0)
            {
                // Update value.
                lastPercent = nextPercent;
                nextPercent = supplier.getAsDouble();
                return lastPercent;
            }
            else if (!interpolateRefresh) return lastPercent;
            else return lastPercent + (nextPercent - lastPercent) * (tick / (double)refreshEvery);
        }
        else return supplier.getAsDouble();
    }
    @Override public void onEnabled()
    {
        if (refreshEvery > 1)
        {
            lastPercent = supplier.getAsDouble();
        }
    }
}
