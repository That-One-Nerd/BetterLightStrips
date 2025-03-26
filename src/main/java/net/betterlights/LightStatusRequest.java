package net.betterlights;

public class LightStatusRequest
{
    public Object state;
    private boolean disposed;

    LightStatusRequest(Object state)
    {
        this.state = state;
        disposed = false;
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
