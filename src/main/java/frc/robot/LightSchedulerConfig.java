package frc.robot;

public class LightSchedulerConfig
{
    public static final LightSchedulerConfig kDefault = new LightSchedulerConfig();

    /**
     * The log level for the LED scheduler. Messages with this level or higher are printed.
     * 0 = debug, 1 = info, 2 = warning, 3 = error.
     */
    public int logLevel = 1;

    private LightSchedulerConfig() { }
}
