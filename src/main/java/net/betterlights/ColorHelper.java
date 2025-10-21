package net.betterlights;

import edu.wpi.first.wpilibj.util.Color;

public final class ColorHelper
{
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
