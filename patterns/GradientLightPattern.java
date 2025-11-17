package betterlights.patterns;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;
import betterlights.Gradient;

/** Displays a gradient of multiple colors on the segment. The gradient can be animated over time using keyframes. */
public class GradientLightPattern extends LightPattern
{
    private ArrayList<Entry> animation;
    private int duration;
    private boolean endAsBeginning;
    private boolean sharp;

    public GradientLightPattern()
    {
        gamma = 1.0;
        animation = new ArrayList<>();
        duration = 0;
    }

    /** Adds a gradient at a given time interval. Time should be between 0.0 and 1.0. */
    public GradientLightPattern withGradient(double time, Gradient gradient)
    {
        insertEntry(new Entry(time, gradient));
        return this;
    }
    /** Adds a single color animation keyframe. Useful for fades. Time should be 0.0 and 1.0. */
    public GradientLightPattern withSolidGradient(double time, Color color)
    {
        insertEntry(new Entry(time, new Gradient(color)));
        return this;
    }
    /** Adds a gradient between two colors at the given time interval. Time should be between 0.0 and 1.0. */
    public GradientLightPattern withTwoColorGradient(double time, Color left, Color right)
    {
        insertEntry(new Entry(time, new Gradient()
            .withColorEntry(0.0, left)
            .withColorEntry(1.0, right)
        ));
        return this;
    }
    /** Adds a gradient between three colors at the given time interval. Time should be between 0.0 and 1.0. */
    public GradientLightPattern withThreeColorGradient(double time, Color left, Color middle, Color right)
    {
        insertEntry(new Entry(time, new Gradient()
            .withColorEntry(0.0, left)
            .withColorEntry(0.5, middle)
            .withColorEntry(1.0, right)
        ));
        return this;
    }

    @Override public GradientLightPattern withGamma(double gamma)
    {
        super.withGamma(gamma);
        return this;
    }
    /** Sets the duration of one cycle of animation in ticks. This will be the amount of time it takes to go through the entire list of gradients. */
    public GradientLightPattern withDuration(int duration)
    {
        this.duration = duration;
        return this;
    }

    /** Automatically set the gradient at time 1.0 to be identical to the first gradient. */
    public GradientLightPattern withEndAsBeginning()
    {
        endAsBeginning = true;
        return this;
    }

    /** Do not interpolate between gradients over time. The gradients themselves may or may not still be interpolated. */
    public GradientLightPattern sharp() { sharp = true; return this; }
    /** Interpolate between gradients over time. This is the default behavior. The gradients themselves may or may not be interpolated. */
    public GradientLightPattern smooth() { sharp = false; return this; }

    @Override
    public void applyTo(LEDReader reader, LEDWriter writer)
    {
        // Showtime.
        int length = reader.getLength();
        if (animation.size() == 0)
        {
            // No animation at all! Fill all colors black and call it a day.
            for (int i = 0; i < length; i++) writer.setLED(i, Color.kBlack);
        }
        else if (duration == 0 || animation.size() == 1)
        {
            // One frame animation. Just do the simple gradient.
            Gradient grad = animation.get(0).gradient;
            for (int i = 0; i < length; i++)
            {
                double pos = i / (double)(length - 1);
                writer.setLED(i, grad.getColor(pos, gamma));
            }
        }
        else
        {
            // Interpolate between the gradients, and also the colors in those gradients.
            double time = (getTick() % duration) / (double)duration;
            Entry left = null, right = null;

            // Because of our earlier check, left will never be null. But right may be.
            for (Entry e : animation)
            {
                if (e.time > time)
                {
                    right = e;
                    break;
                }
                else left = e; // Also covers the case where e.time == time, that's okay.
            }

            // If the right side is null, we might be able to get away with setting it to the first element.
            if (right == null && endAsBeginning) right = new Entry(1, animation.get(0).gradient);

            // Calculate the "t" value between the two animations. Then lerp between them.
            double t;
            if (sharp || left.time == time || right == null) t = 0;
            else t = (time - left.time) / (right.time - left.time);

            for (int i = 0; i < length; i++)
            {
                double pos = i / (double)(length - 1);
                if (t == 0 || right == null) writer.setLED(i, left.gradient.getColor(pos, gamma));
                else
                {
                    // It's lerpin' time.
                    Color leftCol = left.gradient.getColor(pos, gamma),
                          rightCol = right.gradient.getColor(pos, gamma);

                    writer.setLED(i, colorLerp(leftCol, rightCol, t));
                }
            }
        }
    }

    private void insertEntry(Entry entry)
    {
        // Works very similar to Gradient.insertEntry().
        // Find the left and right nodes that sandwhich it, and insert there.
        int index = 0;
        while (index < animation.size())
        {
            Entry curEntry = animation.get(index);
            if (curEntry.time > entry.time) break;
            else if (curEntry.time == entry.time)
            {
                // Same time! Just replace it.
                animation.set(index, entry);
                return;
            }
            else index++;
        }

        // Put the entry in.
        animation.add(index, entry);
    }
    private static class Entry
    {
        public final double time;
        public final Gradient gradient;

        public Entry(double time, Gradient gradient)
        {
            this.time = time;
            this.gradient = gradient;
        }
    }
}
