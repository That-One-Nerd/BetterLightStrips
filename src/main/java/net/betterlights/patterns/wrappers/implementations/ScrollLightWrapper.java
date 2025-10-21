package net.betterlights.patterns.wrappers.implementations;

import net.betterlights.patterns.LightPattern;
import net.betterlights.patterns.wrappers.MappedLightWrapper;

/** An internal wrapper that scrolls the pattern at a given rate. */
public class ScrollLightWrapper extends MappedLightWrapper
{
    // TODO: Maybe interpolation between pixels could be useful? Idk.
    private final double speed;

    public ScrollLightWrapper(LightPattern pattern, double speed)
    {
        super(pattern);
        this.speed = speed;
    }
    
    @Override
    protected int map(int length, int index)
    {
        int offset = (int)(getTick() * speed);
        return Math.floorMod(index + offset, length);
    }
}
