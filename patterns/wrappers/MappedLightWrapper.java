package betterlights.patterns.wrappers;

import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import betterlights.patterns.LightPattern;

/** A wrapper class that maps an index in the light strip to a new index. */
public class MappedLightWrapper extends LightWrapper
{
    protected final IndexMapper mapper;

    /** Uses the protected map() method as the mapper. */
    protected MappedLightWrapper(LightPattern pattern)
    {
        super(pattern);
        mapper = this::map;
    }
    /** Uses the given IndexMapper functional interface as the mapper. The map() method is ignored. */
    public MappedLightWrapper(LightPattern pattern, IndexMapper mapper)
    {
        super(pattern);
        this.mapper = mapper;
    }

    protected int map(int length, int index) { return index; }

    @Override
    public void applyTo(LEDReader reader, LEDWriter writer)
    {
        // Define a new reader and writer that maps to the new index.
        int length = reader.getLength();
        LEDReader mappedReader = new LEDReader()
        {
            @Override public int getLength() { return length; }
            @Override public int getRed(int index) { return reader.getRed(mapper.apply(length, index)); }
            @Override public int getGreen(int index) { return reader.getGreen(mapper.apply(length, index)); }
            @Override public int getBlue(int index) { return reader.getBlue(mapper.apply(length, index)); }
        };
        LEDWriter mappedWriter = (i, r, g, b) -> writer.setRGB(mapper.apply(length, i), r, g, b);
        underlying.applyTo(mappedReader, mappedWriter);
    }
}
