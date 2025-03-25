package net.betterlights;

import edu.wpi.first.math.Pair;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.AddressableLEDBufferView;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import java.util.ArrayList;
import java.util.HashMap;

public class LightScheduler extends Command
{
    public static final LightScheduler kInstance = new LightScheduler();

    /** Returns the configuration class for the light scheduler. Use this before calling the start method. */
    public static LightSchedulerConfig configure() { return kInstance.config; }

    /** Refreshes the light scheduler. Required if adding strips or configuring the scheduler while already begun. */
    public static void refresh() { kInstance.refreshStrips(); }

    /** Starts the light scheduler. */
    public static void start()
    {
        kInstance.log(1, "Starting light scheduler.");
        CommandScheduler.getInstance().schedule(kInstance);
    }

    /** Stops the light scheduler. */
    public static void stop()
    {
        CommandScheduler.getInstance().cancel(kInstance);
    }

    private LightSchedulerConfig config;

    private AddressableLED[] strips;
    private AddressableLEDBuffer[] buffers;
    private HashMap<String, AddressableLEDBufferView> namedSegmentToView;

    private LightScheduler()
    {
        config = LightSchedulerConfig.kDefault;
        overlapCooldownTimer = new Timer();
    }

    private void freeStrips()
    {
        if (strips != null)
        {
            log(0, "Freeing strip data due to a refresh or initialization.");
            for (int i = 0; i < strips.length; i++)
            {
                strips[i].close();
                log(0, "Freed strip %d", i);
            }
        }
        if (namedSegmentToView != null) namedSegmentToView.clear();
    }
    private void refreshStrips()
    {
        freeStrips();

        // STEP 1: Compress named segments into strips.
        // The left side of the tuple represents the port, the right represents the length.
        ArrayList<Pair<Integer, Integer>> stripInfo = new ArrayList<>();
        HashMap<Integer, Integer> portToIndex = new HashMap<>();

        for (int i = 0; i < config.segments.size(); i++)
        {
            NamedLightSegment segment = config.segments.get(i);
            int desiredLength = segment.endIndex + 1;
            boolean added = false;
            for (int j = 0; j < stripInfo.size(); j++)
            {
                Pair<Integer, Integer> strip = stripInfo.get(j);
                if (segment.port == strip.getFirst())
                {
                    // This port is already set up to be initialized.
                    // If this placement requires a larger length, update it.
                    if (desiredLength > strip.getSecond()) stripInfo.set(j, Pair.of(segment.port, desiredLength));
                    added = true;
                    break;
                }
            }
            if (!added)
            {
                stripInfo.add(Pair.of(segment.port, desiredLength));
                portToIndex.put(segment.port, i);
            }
        }

        int stripCount = stripInfo.size();
        log(0, "Initializing %d light strips for %d named segments.", stripCount, config.segments.size());

        if (stripCount > 1) throw new Error("Bruh too many for now. Whatever the thingamabob.");

        strips = new AddressableLED[stripCount];
        buffers = new AddressableLEDBuffer[stripCount];
        for (int i = 0; i < stripCount; i++)
        {
            Pair<Integer, Integer> info = stripInfo.get(i);
            int port = info.getFirst(), length = info.getSecond();

            AddressableLED strip = new AddressableLED(port);
            strip.setLength(length);

            AddressableLEDBuffer buffer = new AddressableLEDBuffer(length);

            strips[i] = strip;
            buffers[i] = buffer;

            strip.start();
            strip.setData(buffer);
        }

        // STEP 2: Create views by name and add them to the hash map.
        namedSegmentToView = new HashMap<>();
        for (int i = 0; i < config.segments.size(); i++)
        {
            NamedLightSegment segment = config.segments.get(i);
            AddressableLEDBufferView view = buffers[portToIndex.get(segment.port)].createView(segment.startIndex, segment.endIndex);
            namedSegmentToView.put(segment.name, view);
        }
    }

    @Override
    public void initialize()
    {
        log(0, "Initializing...");
        refreshStrips();
    }

    @Override
    public void end(boolean interrupted)
    {
        log(1, "Stopping light scheduler!");
        freeStrips();
    }

    private int index;
    @Override
    public void execute()
    {
        // TODO
        checkOverlap();

        index = 0;
        namedSegmentToView.forEach((name, view) ->
        {
            Color color;
            switch (index)
            {
                case 0: color = Color.kRed; break;
                case 1: color = Color.kYellow; break;
                case 2: color = Color.kGreen; break;
                case 3: color = Color.kBlue; break;
                case 4: color = Color.kPurple; break;
                default: color = Color.kWhite; break;
            }
            LEDPattern.solid(color).applyTo(view);
            index++;
        });

        for (int i = 0; i < strips.length; i++) strips[i].setData(buffers[i]);
    }

    @Override
    public boolean runsWhenDisabled() { return true; }

    private Timer overlapCooldownTimer;
    private void checkOverlap()
    {
        // Check if any segment overlaps any other segment.
        // Ignore if they are on different ports.
        int segmentCount = config.segments.size();
        ArrayList<Pair<NamedLightSegment, NamedLightSegment>> overlaps = new ArrayList<>();
        for (int i = 0; i < segmentCount; i++)
        {
            NamedLightSegment segmentA = config.segments.get(i);
            for (int j = i + 1; j < segmentCount; j++)
            {
                NamedLightSegment segmentB = config.segments.get(j);
                if (segmentA.port != segmentB.port) continue;

                if ((segmentA.startIndex >= segmentB.startIndex && segmentA.startIndex <= segmentB.endIndex) ||
                    (segmentA.endIndex   >= segmentB.startIndex && segmentA.endIndex   <= segmentB.endIndex) ||
                    (segmentB.startIndex >= segmentA.startIndex && segmentB.startIndex <= segmentA.endIndex) ||
                    (segmentB.endIndex   >= segmentA.startIndex && segmentB.endIndex   <= segmentA.endIndex))
                {
                    overlaps.add(Pair.of(segmentA, segmentB));
                }
            }
        }

        if (overlaps.size() > 0)
        {
            if (!overlapCooldownTimer.isRunning() || overlapCooldownTimer.hasElapsed(1.0))
            {
                for (int i = 0; i < overlaps.size(); i++)
                {
                    Pair<NamedLightSegment, NamedLightSegment> overlap = overlaps.get(i);
                    log(2, "Named light segment \"%s\" overlaps segment \"%s\"",
                        overlap.getFirst().name,
                        overlap.getSecond().name);
                }
                overlapCooldownTimer.restart();
            }
        }
        else
        {
            if (overlapCooldownTimer.isRunning())
            {
                log(1, "Overlap is no longer occurring.");
            }
            overlapCooldownTimer.stop();
            overlapCooldownTimer.reset();
        }
    }

    private void log(int level, String message, Object... args)
    {
        if (level < config.logLevel) return;
        String prefix;
        switch (level)
        {
            case 0: prefix = "DEBUG"; break;
            case 1: prefix = "INFO"; break;
            case 2: prefix = "WARN"; break;
            case 3: prefix = "ERROR"; break;
            default: prefix = "???"; break;
        }
        System.out.printf("[LIGHTS] %s: %s\n", prefix, String.format(message, args));
    }
}
