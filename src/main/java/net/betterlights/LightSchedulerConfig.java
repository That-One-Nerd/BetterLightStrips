package net.betterlights;

import java.util.ArrayList;
import java.util.function.Consumer;

/** The configuration class for the light scheduler. Can be used like a builder. */
public class LightSchedulerConfig
{
    public static final LightSchedulerConfig kDefault = new LightSchedulerConfig();

    /**
     * The log level for the LED scheduler. Messages with this level or higher are printed.
     * 0 = debug, 1 = info, 2 = warning, 3 = error.
     */
    public int logLevel = 1;

    /**
     * A collection of named light segments to use in the scheduler. Each named segment can
     * have its own state, or have its own pattern according to a global state.
     */
    public ArrayList<NamedLightSegment> segments;

    private LightSchedulerConfig()
    {
        segments = new ArrayList<>();
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

    private boolean hasNamedLightSegment(String name)
    {
        for (int i = 0; i < segments.size(); i++) if (segments.get(i).name.equals(name)) return true;
        return false;
    }
}
