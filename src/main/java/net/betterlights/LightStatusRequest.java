package net.betterlights;

public class LightStatusRequest
{
    public Object state;

    private boolean enabled;
    private boolean disposed;

    LightStatusRequest(Object state)
    {
        this.state = state;
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
