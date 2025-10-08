package net.betterlights.transitions;

import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;
import net.betterlights.patterns.LightPattern;

public class SwipeTransition extends LightTransition
{
    private Color middleColor;
    private int middleLength;
    private double speed;

    private boolean reversed;

    private int length = Integer.MAX_VALUE;

    public SwipeTransition()
    {
        middleLength = 0;
        speed = 1;
    }

    public SwipeTransition withStartPattern(LightPattern pattern) { super.withStartPattern(pattern); return this; }
    public SwipeTransition withEndPattern(LightPattern pattern) { super.withEndPattern(pattern); return this; }

    /** Defines an intermediate color of length `middleLength` LEDs to wipe in between the two patterns. */
    public SwipeTransition withIntermediate(Color middleColor, int middleLength)
    {
        this.middleColor = middleColor;
        this.middleLength = middleLength;
        return this;
    }
    /** Sets the speed at which this transition takes place. A speed of 1 means it covers one LED per tick. */
    public SwipeTransition withSpeed(double speed)
    {
        this.speed = speed;
        return this;
    }
    /** Flip the direction of the wipe. */
    public SwipeTransition reversed()
    {
        reversed = !reversed;
        return this;
    }

    @Override
    public void applyTransition(int length, Color[] startBuffer, Color[] endBuffer, LEDWriter writer)
    {
        this.length = length;

        if (reversed)
        {
            int oldIndex = length - (int)(getTick() * speed);
            int newIndex = oldIndex + middleLength;

            for (int i = 0; i < length; i++)
            {
                Color outputColor;
                if (i < oldIndex) outputColor = startBuffer[i];
                else if (i >= newIndex) outputColor = endBuffer[i];
                else outputColor = middleColor;
                writer.setLED(i, outputColor);
            }
        }
        else
        {
            int newIndex = (int)(getTick() * speed) - middleLength;
            int oldIndex = newIndex + middleLength;

            for (int i = 0; i < length; i++)
            {
                Color outputColor;
                if (i < newIndex) outputColor = endBuffer[i];
                else if (i >= oldIndex) outputColor = startBuffer[i];
                else outputColor = middleColor;
                writer.setLED(i, outputColor);
            }
        }
    }

    @Override
    public boolean isComplete()
    {
        return getTick() * speed >= length + middleLength;
    }
}
