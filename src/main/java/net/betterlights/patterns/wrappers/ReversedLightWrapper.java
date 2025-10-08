package net.betterlights.patterns.wrappers;

import net.betterlights.patterns.LightPattern;

/** An internal wrapper that reverses the direction of the pattern. */
public class ReversedLightWrapper extends MappedLightWrapper
{
    public ReversedLightWrapper(LightPattern pattern) { super(pattern); }

    @Override
    protected int map(int length, int index)
    {
        // Reverse the index
        return length - index - 1;
    }

    @Override
    public LightPattern reversed()
    {
        // Un-reverse the pattern. No need to construct a double-wrapper.
        return underlying;
    }
}
