package net.betterlights.patterns;

import java.util.Random;

import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;

/** A light pattern that fills the segment with randomized colors. */
public class RandomLightPattern extends LightPattern
{
    private Color shadeA, shadeB;
    private boolean allShades = true;

    private int refreshEvery = 1;
    private Random randCur = new Random(),
                   randNext = new Random();

    private boolean interpolateSmooth = false;

    public RandomLightPattern() {}

    @Override public RandomLightPattern withGamma(double gamma) { super.withGamma(gamma); return this; }
    /** Make this pattern set its LEDs to a random shade between black and this color. */
    public RandomLightPattern withShadesOfColor(Color color)
    {
        allShades = false;
        shadeA = Color.kBlack;
        shadeB = color;
        return this;
    }
    /** Make this pattern set its LEDs to a random gradient between these two colors. */
    public RandomLightPattern withGradient(Color colorA, Color colorB)
    {
        allShades = false;
        shadeA = colorA;
        shadeB = colorB;
        return this;
    }
    /** Make the pattern re-randomize the LEDs every given amount of ticks. */
    public RandomLightPattern withRefreshEvery(int ticks)
    {
        refreshEvery = ticks;
        return this;
    }
    /** Make the pattern snap from one randomization to the next immediately. Has no effect unless withRefreshEvery() was called with a number greater than one tick. */
    public RandomLightPattern sharp()
    {
        interpolateSmooth = false;
        return this;
    }
    /** Make the pattern fade from one randomization to the next smoothly. Has no effect unless withRefreshEvery() was called with a number greater than one tick. */
    public RandomLightPattern smooth()
    {
        interpolateSmooth = true;
        return this;
    }

    @Override
    public void applyTo(LEDReader reader, LEDWriter writer)
    {
        int seed;
        if (refreshEvery <= 1) seed = getTick();
        else seed = getTick() / refreshEvery;
        randCur.setSeed(seed);
        randNext.setSeed(seed + 1);

        int length = reader.getLength();
        for (int i = 0; i < length; i++)
        {
            if (refreshEvery <= 1 || !interpolateSmooth) writer.setLED(i, get());
            else
            {
                Color curColor = get(randCur),
                      nextColor = get(randNext);
                int leftover = getTick() % refreshEvery;
                double time = (double)leftover / refreshEvery;
                writer.setLED(i, colorLerp(curColor, nextColor, time));
            }
        }
    }

    public Color get()
    {
        return get(randCur);
    }
    private Color get(Random rand)
    {
        double invGamma = gamma;
        if (allShades) return new Color(
            Math.pow(rand.nextDouble(), invGamma),
            Math.pow(rand.nextDouble(), invGamma),
            Math.pow(rand.nextDouble(), invGamma)
        );
        else return colorLerp(shadeA, shadeB, rand.nextDouble());
    }
}
