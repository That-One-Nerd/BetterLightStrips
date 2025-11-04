package net.betterlights;

import edu.wpi.first.wpilibj.util.Color;

public final class ColorHelper
{
    public static double absMod(double val, double mod)
    {
        while (val < 0) val += mod;
        while (val >= mod) val -= mod;
        return val;
    }
    public static double clamp(double val, double min, double max)
    {
        if (val <= min) return min;
        else if (val >= max) return max;
        else return val;
    }
    public static Color lerp(Color a, Color b, double t, double gamma)
    {
        if (t <= 0) return a;
        else if (t >= 1) return b;

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
}
