package betterlights.transitions;

import edu.wpi.first.math.Pair;

/** Represents a pair of states to transition between. */
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
