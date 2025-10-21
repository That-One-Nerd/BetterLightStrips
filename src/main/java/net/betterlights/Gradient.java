package net.betterlights;

import java.util.List;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.util.Color;

/** Represents a change of color over a position from 0 to 1. */
public class Gradient
{
    private List<Entry> entries;

    /** Creates a blank gradient. */
    public Gradient()
    {
        entries = new ArrayList<>();
    }

    /** Create a new gradient with an initial color at position 0. */
    public Gradient(Color initial)
    {
        entries = new ArrayList<>();
        entries.add(new Entry(0, initial));
    }

    /** Adds a color marker to the gradient at the given position between 0 and 1. */
    public Gradient withColorEntry(double position, Color color)
    {
        insertEntry(new Entry(position, color));
        return this;
    }

    /** Returns the color at the given position between 0 and 1. Will most likely be interpolated. */
    public Color getColor(double position, double gamma)
    {
        // We first need to find the two colors to lerp between.
        Entry left = null, right = null;
        for (Entry e : entries)
        {
            if (e.position < position) left = e;
            else if (e.position > position)
            {
                right = e;
                break;
            }
            else return e.color;
        }

        // Now see if we're at one of the edges.
        if (left == null && right == null) return Color.kBlack; // Empty gradient!
        else if (left == null) return right.color;
        else if (right == null) return left.color;
        else
        {
            // Find the "t" value between left and right. Then lerp it.
            double t = (position - left.position) / (right.position - left.position);
            return ColorHelper.lerp(left.color, right.color, t, gamma);
        }
    }

    private void insertEntry(Entry entry)
    {
        // Find the left and right nodes that sandwhich it, and insert there.
        int index = 0;
        while (index < entries.size())
        {
            Entry curEntry = entries.get(index);
            if (curEntry.position > entry.position) break;
            else if (curEntry.position == entry.position)
            {
                // Same position! Just replace it.
                entries.set(index, entry);
                return;
            }
            else index++;
        }

        // Put the entry in.
        entries.add(index, entry);
    }
    private static class Entry
    {
        public final double position;
        public final Color color;

        public Entry(double position, Color color)
        {
            this.position = position;
            this.color = color;
        }
    }
}
