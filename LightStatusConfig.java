package betterlights;

import betterlights.patterns.LightPattern;

/**
 * Represents the behavior of the light scheduler when in a particular state.
 */
public class LightStatusConfig
{
    public String appliesTo;
    public int priority;
    public Object state;
    public LightPattern pattern;

    public LightStatusConfig(String appliesTo, int priority, Object state, LightPattern pattern)
    {
        this.appliesTo = appliesTo;
        this.priority = priority;
        this.state = state;
        this.pattern = pattern;
    }
}
