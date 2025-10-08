package net.betterlights.patterns;

import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.Frequency;
import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;
import net.betterlights.patterns.wrappers.*;

/** Represents a light pattern used by the scheduler. Can be applied to any named light segment. */
public abstract class LightPattern implements LEDPattern
{
    /** From a base-layer LED pattern, construct a light pattern compatible with the scheduler. */
    public static LightPattern from(LEDPattern basePattern) { return new CompatibleLightWrapper(basePattern); }

    private int curTick;
    private int startTick;

    public int getTick()
    {
        if (useAbsoluteTicks()) return curTick;
        else return curTick - startTick;
    }
    public void incrementTick()
    {
        curTick++;
    }
    public void setStartTick(int newStart)
    {
        startTick = newStart;
    }
    public void setCurrentTick(int newTick)
    {
        curTick = newTick;
    }
    public abstract void applyTo(LEDReader reader, LEDWriter writer);

    // #region Things to override.
    public boolean useAbsoluteTicks() { return false; }

    public void onEnabled() { }
    public void onDisabled() { }

    public boolean isComplete() { return false; }
    // #endregion

    // #region Helper functions.
    protected Color colorLerp(Color a, Color b, double t)
    {
        return colorLerp(a, b, t, 1.0);
    }
    protected Color colorLerp(Color a, Color b, double t, double gamma)
    {
        if (t < 0) t = 0;
        else if (t > 1) t = 1;

        double invGamma = 1 / gamma;
        double aGammaR = Math.pow(a.red, gamma),   bGammaR = Math.pow(b.red, gamma),
               aGammaG = Math.pow(a.green, gamma), bGammaG = Math.pow(b.green, gamma),
               aGammaB = Math.pow(a.blue, gamma),  bGammaB = Math.pow(b.blue, gamma);

        double cGammaR = aGammaR + t * (bGammaR - aGammaR),
               cGammaG = aGammaG + t * (bGammaG - aGammaG),
               cGammaB = aGammaB + t * (bGammaB - aGammaB);
        return new Color(
            Math.pow(cGammaR, invGamma),
            Math.pow(cGammaG, invGamma),
            Math.pow(cGammaB, invGamma)
        );
    }
    // #endregion

    // #region Base LEDPattern methods.
    @Override public LightPattern offsetBy(int offset) { return new OffsetLightWrapper(this, offset); }
    @Override public LightPattern reversed() { return new ReversedLightWrapper(this); }

    @Override
    public LightPattern scrollAtRelativeSpeed(Frequency velocity)
    {
        // This method assumes 50 ticks per second, hence the 50.
        // pixels   1 second
        // ------ * -------- = answer
        // second   50 ticks
        return scroll(velocity.baseUnitMagnitude() / 50);
    }
    @Override
    public LightPattern scrollAtAbsoluteSpeed(LinearVelocity velocity, Distance ledSpacing)
    {
        // I've always felt like this is a really lame method. I find it much more intuitive to scroll
        // in pixel-based units rather than true measurements. Do the calculations on your own end if
        // you really need to. But I'll keep supporting this method regardless.
        
        double metersPerSecond = velocity.in(Units.Meters.per(Units.Second));
        double metersPerPixel = ledSpacing.in(Units.Meters);

        // meters   pixels   1 second
        // ------ * ------ * -------- = answer
        // second   meters   50 ticks
        return scroll(metersPerSecond / metersPerPixel / 50);
    }

    public LightPattern scroll(double pixelsPerTick) { return new ScrollLightWrapper(this, pixelsPerTick); }
    // #endregion
}
