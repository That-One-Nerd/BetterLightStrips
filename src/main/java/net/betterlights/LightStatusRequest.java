package net.betterlights;

import net.betterlights.patterns.LightPattern;

/**
 * Represents the behavior of the light scheduler when in a particular state.
 */
public class LightStatusRequest
{
    public String appliesTo;
    public int priority;
    public Object state;
    public LightPattern pattern;

    public LightStatusRequest(String appliesTo, int priority, Object state, LightPattern pattern)
    {
        this.appliesTo = appliesTo;
        this.priority = priority;
        this.state = state;
        this.pattern = pattern;
    }
}
