package net.betterlights.transitions;

import java.util.Random;

import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;

/** Transition between two patterns by randomly changing a certain number of pixels per tick. */
public class RandomLightTransition extends LightTransition
{
    private Random rand = new Random();

    private int ppt = 1;
    private Color middleColor;
    private int middleTime;
    private boolean smoothFade;

    /** Sets the amount of pixels to swap during every tick. Higher numbers complete the transition faster. */
    public RandomLightTransition withPixelsPerTick(int ppt) { this.ppt = ppt; return this; }

    /**
     * Makes the randomizer jump to this color as an intermediate before switching to the second pattern.
     * A custom duration can be set with `withMiddleTime()`, and fading can be set with `withSmoothFade()`.
     */
    public RandomLightTransition withColor(Color intermediate)
    {
        middleColor = intermediate;
        if (middleTime == 0) middleTime = 5; // Default.
        return this;
    }
    /**
     * Sets a duration to wait during pixel transition. If a color is provided, it will wait that amount of
     * ticks before swapping to the second option. Alternatively, if `withSmoothFade()` is active, it will
     * fade either from the color to the final pattern or between the two patterns over "ticks."
     */
    public RandomLightTransition withMiddleTime(int ticks)
    {
        middleTime = ticks;
        return this;
    }
    /**
     * Either smoothly fades the pixels towards the new pattern, or fades the pixels from the intermediate
     * color to the new pattern if `withColor()` is set.
     */
    public RandomLightTransition withSmoothFade()
    {
        smoothFade = true;
        if (middleTime == 0) middleTime = 5; // Default.
        return this;
    }

    @Override
    public void onEnabled()
    {
        // This transition is being reused. Let's reset it.
        initialized = false;
    }

    // We track the states of each pixel independently. Each one falls into three categories:
    // 1. Untransitioned, represented with a value of -1.
    // 2. Transitioning, represented with a tick value that will decrement over time.
    // 3. Transitioned, represented with a value of 0.
    private int[] pixelStates;
    private int done, unset;
    private boolean initialized;

    @Override
    public void applyTransition(int length, Color[] startBuffer, Color[] endBuffer, LEDWriter writer)
    {
        if (!initialized)
        {
            // Populate the state list with all possible indices.
            pixelStates = new int[length];
            for (int i = 0; i < length; i++) pixelStates[i] = -1;
            unset = length;
            done = 0;
            initialized = true;
        }
        else if (done == pixelStates.length) return; // Done, will be completed by the end of this tick.

        // Pick a certain number of pixels to swap.
        for (int i = 0; i < ppt && unset > 0; i++)
        {
            // Calculate a random index out of the unset pixels, then find that corresponding index.
            int relativeIndex = rand.nextInt(unset);
            int index = 0;
            while (index < length)
            {
                if (pixelStates[index] == -1)
                {
                    // Found an unset pixel.
                    relativeIndex--;
                    if (relativeIndex < 0) break; // Found THE unset pixel.
                }
                index++;
            }

            // Update its state.
            pixelStates[index] = middleTime;
            if (middleTime == 0) done++;
            unset--;
        }

        // Now properly set each pixel.
        for (int i = 0; i < length; i++)
        {
            int state = pixelStates[i];
            Color outputColor;

            if (state == -1) outputColor = startBuffer[i];
            else if (state == 0) outputColor = endBuffer[i];
            else
            {
                // Interpolate, perchance?
                if (smoothFade) outputColor = colorLerp(endBuffer[i], middleColor, (double)state / middleTime);
                else outputColor = middleColor;

                state--;
                pixelStates[i] = state;
                if (state == 0) done++;
            }

            writer.setLED(i, outputColor);
        }
    }

    @Override
    public boolean isComplete()
    {
        return initialized && done == pixelStates.length;
    }
}
