package net.betterlights.patterns.wrappers;

import net.betterlights.patterns.LightPattern;

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
