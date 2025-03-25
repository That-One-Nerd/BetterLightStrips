package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class LightScheduler extends Command
{
    private static final LightScheduler kInstance = new LightScheduler();

    /** Returns the configuration class for the light scheduler. Use this before calling the start method. */
    public static LightSchedulerConfig configure() { return kInstance.config; }

    /** Adds a light strip to the scheduler. */
    public static void withStrip(LightStripProperties strip)
    {
        kInstance.log(0, "Adding new strip: Port %d, Length %d", strip.port, strip.length);
        kInstance.stripProperties.add(strip);
    }
    /** Adds a collection of light strips to the scheduler. */
    public static void withStrips(LightStripProperties... strips)
    {
        for (int i = 0; i < strips.length; i++) withStrip(strips[i]);
    }

    /** Refreshes the light scheduler. Required if adding strips or configuring the scheduler while already begun. */
    public static void refresh() { kInstance.refreshStrips(); }

    /** Schedules the light management command. */
    public static void start()
    {
        kInstance.log(1, "Starting light scheduler.");
        CommandScheduler.getInstance().schedule(kInstance);
    }

    private LightSchedulerConfig config;
    private ArrayList<LightStripProperties> stripProperties;

    private AddressableLED[] strips;
    private AddressableLEDBuffer[] buffers;

    private LightScheduler()
    {
        config = LightSchedulerConfig.kDefault;
        stripProperties = new ArrayList<>();
    }

    private void freeStrips()
    {
        log(0, "Freeing strip data.");
        if (strips != null)
        {
            for (int i = 0; i < strips.length; i++)
            {
                strips[i].close();
                log(0, "Strip %d closed.", i);
            }
        }
    }
    private void refreshStrips()
    {
        freeStrips();
        if (stripProperties.size() != 1) throw new Error("Bruh momento only one at a time so far.");

        int stripCount = stripProperties.size();
        log(0, "Starting %d strips...", stripCount);
        strips = new AddressableLED[stripCount];
        buffers = new AddressableLEDBuffer[stripCount];
        for (int i = 0; i < stripCount; i++)
        {
            LightStripProperties properties = stripProperties.get(i);

            AddressableLED strip = new AddressableLED(properties.port);
            strip.setLength(properties.length);

            AddressableLEDBuffer buffer = new AddressableLEDBuffer(properties.length);
            strip.setData(buffer);

            strips[i] = strip;
            buffers[i] = buffer;
            log(0, "Strip %d started.", i);
        }
    }

    @Override
    public void initialize()
    {
        log(0, "Initializing scheduler command.");
        refreshStrips();
    }

    @Override
    public void execute()
    {
        // TODO
        log(0, "bruh");
    }

    private void log(int level, String message, Object... args)
    {
        if (level < config.logLevel) return;
        System.out.printf("[LIGHTS] %s\n", String.format(message, args));
    }
}
