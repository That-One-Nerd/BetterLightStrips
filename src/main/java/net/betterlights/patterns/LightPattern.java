package net.betterlights.patterns;

import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;

public abstract class LightPattern implements LEDPattern
{
    protected int tick;

    public void setTick(int newTick)
    {
        tick = newTick;
    }
    public abstract void applyTo(LEDReader reader, LEDWriter writer);
}
