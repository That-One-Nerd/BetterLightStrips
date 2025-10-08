package net.betterlights.patterns;

import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;
import net.betterlights.patterns.wrappers.ReversedLightWrapper;

/** Represents a light pattern used by the scheduler. Can be applied to any named light segment. */
public abstract class LightPattern implements LEDPattern
{
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

    // Override the older reversed() LEDPattern method.
    @Override public LightPattern reversed() { return new ReversedLightWrapper(this); }

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
}
