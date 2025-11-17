package betterlights;

public class LightStatusRequest
{
    public Object state;
    public boolean temporary;

    private boolean enabled;
    private boolean disposed;

    LightStatusRequest(Object state)
    {
        this.state = state;
        temporary = false;
        enabled = true;
        disposed = false;
    }
    LightStatusRequest(Object state, boolean temporary)
    {
        this.state = state;
        this.temporary = temporary;
        enabled = true;
        disposed = false;
    }

    public boolean isEnabled()
    {
        return enabled;
    }
    public void enable()
    {
        enabled = true;
    }
    public void disable()
    {
        enabled = false;
    }

    public boolean isDisposed()
    {
        return disposed;
    }
    public void dispose()
    {
        disposed = true;
    }
}
