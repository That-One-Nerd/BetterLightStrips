package betterlights.patterns.wrappers;

import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;
import betterlights.patterns.LightPattern;

public class BinaryLightWrapper extends LightWrapper
{
    protected final LightPattern patternA, patternB;
    protected final Operator operator;

    protected BinaryLightWrapper(LightPattern patternA, LightPattern patternB)
    {
        super(patternA);
        this.patternA = patternA;
        this.patternB = patternB;
        operator = this::mix;
    }
    public BinaryLightWrapper(LightPattern patternA, LightPattern patternB, Operator operator)
    {
        super(patternA);
        this.patternA = patternA;
        this.patternB = patternB;
        this.operator = operator;
    }

    @Override
    public void incrementTick()
    {
        patternA.incrementTick();
        patternB.incrementTick();
    }
    @Override
    public void setStartTick(int newStart)
    {
        patternA.setStartTick(newStart);
        patternB.setStartTick(newStart);
    }
    @Override
    public void setCurrentTick(int newTick)
    {
        patternA.setCurrentTick(newTick);
        patternB.setCurrentTick(newTick);
    }

    protected Color mix(Color inputA, Color inputB) { return inputA; }

    @Override
    public void applyTo(LEDReader reader, LEDWriter writer)
    {
        // Compute the two patterns.
        int length = reader.getLength();
        Color[] bufA = new Color[length],
                bufB = new Color[length];
        patternA.applyTo(reader, (i, r, g, b) -> bufA[i] = new Color(r, g, b));
        patternB.applyTo(reader, (i, r, g, b) -> bufB[i] = new Color(r, g, b));

        // Then lerp between them.
        for (int i = 0; i < length; i++)
        {
            Color mixed = operator.mix(bufA[i], bufB[i]);
            writer.setLED(i, mixed);
        }
    }

    public interface Operator
    {
        public Color mix(Color inputA, Color inputB);
    }
}
