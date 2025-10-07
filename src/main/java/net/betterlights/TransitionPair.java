package net.betterlights;

import edu.wpi.first.math.Pair;

public class TransitionPair extends Pair<Object, Object>
{
    public TransitionPair(Object startState, Object endState)
    {
        super(startState, endState);
    }

    @Override
    public String toString()
    {
        return String.format("(%s)->(%s)", getFirst(), getSecond());
    }
}
