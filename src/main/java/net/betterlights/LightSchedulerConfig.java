package net.betterlights;

import java.util.ArrayList;
import java.util.function.Consumer;

import edu.wpi.first.wpilibj.util.Color;
import net.betterlights.patterns.LightPattern;
import net.betterlights.patterns.SolidLightPattern;

/**
 * The configuration class for the light scheduler. Can be used like a builder.
 * You should always call refresh directly after changing these values, assuming
 * the scheduler is already running. Failing to call refresh after the config has
 * changed will result in undefined behavior!
 */
public class LightSchedulerConfig
{
    public static final LightSchedulerConfig kDefault = new LightSchedulerConfig();

    /**
     * The log level for the LED scheduler. Messages with this level or higher are printed.
     * 0 = debug, 1 = info, 2 = warning, 3 = error.
     */
    public int logLevel;

    /**
     * A collection of named light segments to use in the scheduler. Each named segment can
     * have its own state, or have its own pattern according to a global state.
     */
    public ArrayList<NamedLightSegment> segments;

    /**
     * A collection of states and their respective outcomes, on a per-segment basis.
     * This allows for two different segments to do different things for a given state,
     * and it also accounts for allowing those two segments to have different states.
     */
    public ArrayList<LightStatusConfig> states;

    /**
     * The default pattern to switch to when the given state does not match any of
     * the possible options.
     */
    public LightPattern unknownBehavior;

    private LightSchedulerConfig()
    {
        logLevel = 1;
        segments = new ArrayList<>();
        states = new ArrayList<>();
        unknownBehavior = new SolidLightPattern(Color.kBlack);
    }

    /**
     * Sets the log level for the light scheduler. Messages with this level or higher are printed.
     * 0 = debug, 1 = info, 2 = warning, 3 = error. Returns this config instance.
     */
    public LightSchedulerConfig withLogLevel(int logLevel)
    {
        this.logLevel = logLevel;
        return this;
    }

    /** Adds an existing named light segment to the scheduler. Returns this config instance. */
    public LightSchedulerConfig withNamedLightSegment(NamedLightSegment segment)
    {
        if (!hasNamedLightSegment(segment.name)) segments.add(segment);
        return this;
    }
    /** Constructs a new named light segment and adds it to the scheduler. Returns this config instance. */
    public LightSchedulerConfig withNamedLightSegment(String name, int port, int startIndex, int endIndex)
    {
        if (!hasNamedLightSegment(name)) segments.add(new NamedLightSegment(name, port, startIndex, endIndex));
        return this;
    }
    /** Adds a collection of existing named light segments to the scheduler. Returns this config instance. */
    public LightSchedulerConfig withNamedLightSegments(NamedLightSegment... segments)
    {
        for (int i = 0; i < segments.length; i++) withNamedLightSegment(segments[i]);
        return this;
    }
    /** Applies a configuration consumer to a named light segment with a given name. Returns this config instance. */
    public LightSchedulerConfig configureNamedLightSegment(String name, Consumer<NamedLightSegment> action)
    {
        for (int i = 0; i < segments.size(); i++)
        {
            NamedLightSegment segment = segments.get(i);
            if (segment.name.equals(name)) action.accept(segment);
        }
        return this;
    }

    /** Adds existing information about a given state to the scheduler. */
    public LightSchedulerConfig withNamedState(LightStatusConfig request)
    {
        if (!hasNamedState(request.state, request.appliesTo)) states.add(request);
        return this;
    }
    /** Adds a collection of existing information about given states to the scheduler. */
    public LightSchedulerConfig withNamedStates(LightStatusConfig... requests)
    {
        for (int i = 0; i < requests.length; i++) withNamedState(requests[i]);
        return this;
    }
    /** Adds new information about a given state to the scheduler. */
    public LightSchedulerConfig withNamedState(String appliesTo, Object state, int priority, LightPattern pattern)
    {
        if (!hasNamedState(state, appliesTo)) states.add(new LightStatusConfig(appliesTo, priority, state, pattern));
        return this;
    }
    /** Adds new information about a given state to the scheduler. Applies to ALL CURRENTLY ADDED named segments. */
    public LightSchedulerConfig withStateAll(Object state, int priority, LightPattern pattern)
    {
        for (int i = 0; i < segments.size(); i++) withNamedState(segments.get(i).name, state, priority, pattern);
        return this;
    }
    /** Applies a configuration consumer to a particular state-name pair. */
    public LightSchedulerConfig configureNamedState(Object state, String name, Consumer<LightStatusConfig> action)
    {
        for (int i = 0; i < states.size(); i++)
        {
            LightStatusConfig request = states.get(i);
            if (request.state == state && request.appliesTo.equals(name)) action.accept(request);
        }
        return this;
    }

    /** Sets the pattern to display when the scheduler is in a state that does not match any of the available options. */
    public LightSchedulerConfig withUnknownBehavior(LightPattern pattern)
    {
        unknownBehavior = pattern;
        return this;
    }

    private boolean hasNamedLightSegment(String name)
    {
        for (int i = 0; i < segments.size(); i++) if (segments.get(i).name.equals(name)) return true;
        return false;
    }
    private boolean hasNamedState(Object state, String name)
    {
        for (int i = 0; i < states.size(); i++)
        {
            LightStatusConfig request = states.get(i);
            if (request.state == state && request.appliesTo.equals(name)) return true;
        }
        return false;
    }
}
