package betterlights.patterns.wrappers.implementations;

import edu.wpi.first.wpilibj.util.Color;
import betterlights.patterns.LightPattern;
import betterlights.patterns.wrappers.BinaryLightWrapper;

public class MaskedLightWrapper extends BinaryLightWrapper
{
    public MaskedLightWrapper(LightPattern a, LightPattern b) { super(a, b); }

    @Override
    protected Color mix(Color inputA, Color inputB)
    {
        double newR = inputA.red * inputB.red,
               newG = inputA.green * inputB.green,
               newB = inputA.blue * inputB.blue;
        return new Color(newR, newG, newB);
    }
}
