package net.betterlights.patterns.wrappers;

import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;
import net.betterlights.patterns.LightPattern;

public class UnaryLightWrapper extends LightWrapper
{
    protected final Operator operator;

    protected UnaryLightWrapper(LightPattern underlying)
    {
        super(underlying);
        operator = this::mix;
    }
    public UnaryLightWrapper(LightPattern underlying, Operator operator)
    {
        super(underlying);
        this.operator = operator;
    }

    protected Color mix(Color input) { return input; }

    @Override
    public void applyTo(LEDReader reader, LEDWriter writer)
    {
        // Define a writer than applies the mix.
        underlying.applyTo(reader, (i, r, g, b) ->
        {
            Color raw = new Color(r, g, b);
            writer.setLED(i, operator.mix(raw));
        });
    }

    public interface Operator
    {
        public Color mix(Color input);
    }
}
