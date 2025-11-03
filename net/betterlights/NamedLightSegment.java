package net.betterlights;

/** Represents a segment of lights, determined by its port number and start and end indices. */
public class NamedLightSegment
{
    /** The ID of the light segment. */
    public String name;

    /** The RIO port of the light strip this segment is a part of. */
    public int port;

    /** The starting LED index of this segment. Inclusive. */
    public int startIndex;

    /** The ending LED index of this segment. Inclusive. */
    public int endIndex;

    /** Whether the segment is enabled or not. */
    public boolean enabled;

    public NamedLightSegment(String name, int port, int length)
    {
        this.name = name;
        this.port = port;
        startIndex = 0;
        endIndex = length - 1;
        enabled = true;
    }
    public NamedLightSegment(String name, int port, int startIndex, int endIndex)
    {
        this.name = name;
        this.port = port;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        enabled = true;
    }

    /** Sets the name of this light segment. Returns this config instance. */
    public NamedLightSegment withName(String name)
    {
        this.name = name;
        return this;
    }
    /** Sets the port of this light segment. Returns this config instance. */
    public NamedLightSegment withPort(int port)
    {
        this.port = port;
        return this;
    }
    /** Sets the start and end indices of this segment. Returns this config instance. */
    public NamedLightSegment withIndices(int startIndex, int endIndex)
    {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        return this;
    }
    /** Enables this named light segment. Remember to call LightScheduler.refresh()! Returns this config instance. */
    public NamedLightSegment enable()
    {
        enabled = true;
        return this;
    }
    /** Disables this named light segment. Remember to call LightScheduler.refresh()! Returns this config instance. */
    public NamedLightSegment disable()
    {
        enabled = false;
        return this;
    }
}
