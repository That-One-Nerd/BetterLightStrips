package net.betterlights.patterns;

import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Dimensionless;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.Frequency;
import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;
import net.betterlights.ColorHelper;
import net.betterlights.patterns.wrappers.*;

/** Represents a light pattern used by the scheduler. Can be applied to any named light segment. */
public abstract class LightPattern implements LEDPattern
{
    public static final LightPattern kOff = new SolidLightPattern(Color.kBlack);

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
    protected Color colorLerp(Color a, Color b, double t) { return ColorHelper.lerp(a, b, t, 1.0); }
    protected Color colorLerp(Color a, Color b, double t, double gamma) { return ColorHelper.lerp(a, b, t, gamma); }
    // #endregion

    // #region Base LEDPattern methods.
    @Override public LightPattern offsetBy(int offset) { return new OffsetLightWrapper(this, offset); }
    @Override public LightPattern reversed() { return new ReversedLightWrapper(this); }

    public LightPattern scroll(double pixelsPerTick) { return new ScrollLightWrapper(this, pixelsPerTick); }
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
    
    public LightPattern atBrightness(double brightness) { return new BrightnessLightWrapper(this, brightness); }
    @Override public LightPattern atBrightness(Dimensionless relativeBrightness) { return atBrightness(relativeBrightness.in(Units.Value)); }
    // #endregion
}
