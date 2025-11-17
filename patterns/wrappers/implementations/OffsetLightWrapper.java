package betterlights.patterns.wrappers.implementations;

import betterlights.patterns.LightPattern;
import betterlights.patterns.wrappers.MappedLightWrapper;

public class OffsetLightWrapper extends MappedLightWrapper
{
    private final int offset;

    public OffsetLightWrapper(LightPattern pattern, int offset)
    {
        super(pattern);
        this.offset = offset;
    }

    @Override
    public LightPattern offsetBy(int offset)
    {
        // Prevent extra wrappers if not needed.
        if (offset == -this.offset) return underlying;
        else return new OffsetLightWrapper(underlying, this.offset + offset);
    }

    @Override
    protected int map(int length, int index)
    {
        // Map the new index.
        return Math.floorMod(index + offset, length);
    }
}
