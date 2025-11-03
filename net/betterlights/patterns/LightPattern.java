package net.betterlights.patterns;

import java.util.function.BooleanSupplier;

import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Dimensionless;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.Frequency;
import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;
import net.betterlights.ColorHelper;
import net.betterlights.patterns.wrappers.*;
import net.betterlights.patterns.wrappers.implementations.*;

/** Represents a light pattern used by the scheduler. Can be applied to any named light segment. */
public abstract class LightPattern implements LEDPattern
{
    public static final LightPattern kOff = new SolidLightPattern(Color.kBlack);

    /** From a base-layer LED pattern, construct a light pattern compatible with the scheduler. */
    public static LightPattern from(LEDPattern basePattern)
    {
        if (basePattern instanceof LightPattern light) return light; // No compatibiltiy wrapper needed.
        else return new CompatibleLightWrapper(basePattern);
    }

    private int curTick;
    private int startTick;

    protected double gamma = 1.0;

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

    /** Sets the gamma value for color interpolation. If you don't know a good value, leave this unset. */
    public LightPattern withGamma(double gamma)
    {
        this.gamma = gamma;
        return this;
    }

    // #region Things to override.
    public boolean useAbsoluteTicks() { return false; }

    public void onEnabled() { }
    public void onDisabled() { }

    public boolean isComplete() { return false; }
    // #endregion

    // #region Helper functions.
    protected Color colorLerp(Color a, Color b, double t) { return ColorHelper.lerp(a, b, t, gamma); }
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
    
    public LightPattern blend(LightPattern other, double amount) { return new BlendedLightWrapper(this, other, amount); }
    public LightPattern blend(LightPattern other) { return blend(other, 0.5); }
    @Override public LightPattern blend(LEDPattern other) { return blend(from(other)); }
    
    @Override public LightPattern mapIndex(IndexMapper indexMapper) { return new MappedLightWrapper(this, indexMapper); }
    
    public LightPattern mask(LightPattern mask) { return new MaskedLightWrapper(this, mask); }
    @Override public LightPattern mask(LEDPattern mask) { return new MaskedLightWrapper(this, from(mask)); }

    public BlinkedLightWrapper blink(int ticksOn) { return blink(ticksOn, ticksOn); }
    public BlinkedLightWrapper blink(int ticksOn, int ticksOff) { return new BlinkedLightWrapper(this, ticksOn, ticksOff); }
    @Override public BlinkedLightWrapper blink(Time onTime) { return blink((int)(onTime.in(Units.Seconds) * 50)); }
    @Override public BlinkedLightWrapper blink(Time onTime, Time offTime) { return blink((int)(onTime.in(Units.Seconds) * 50), (int)(offTime.in(Units.Seconds) * 50)); }
    @Override public LightPattern synchronizedBlink(BooleanSupplier signal) { return new BlinkedLightWrapper(this, signal); }
    // #endregion
}
