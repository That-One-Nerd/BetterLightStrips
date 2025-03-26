package net.betterlights;

import static edu.wpi.first.units.Units.Newton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.math.Pair;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.AddressableLEDBufferView;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import net.betterlights.patterns.LightPattern;

public class LightScheduler extends Command
{
    private static final LightScheduler kInstance = new LightScheduler();

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

    /**
     * Returns the current state of the light segment with the given name.
     */
    public static Object getState(String name)
    {
        return kInstance.mGetState(name);
    }

    /**
     * Returns the current pattern of the light segment with the given name.
     */
    public static LightPattern getPattern(String name)
    {
        return kInstance.mGetPattern(name);
    }

    /**
     * Request that a particular named segment change its state to the given one.
     * If the priority if the current state is above the new one, this request is ignored.
     */
    public static LightStatusRequest requestState(String name, Object state)
    {
        LightStatusRequest request = new LightStatusRequest(state);
        kInstance.mRequestState(name, request);
        return request;
    }
    /**
     * Requests that a specific group of segments change their states to another.
     * If the priority of the current state of any named segments are above the new one, those
     * named segments will not be updated.
     */
    public static Map<String, LightStatusRequest> requestStates(Map<String, Object> segmentStates)
    {
        HashMap<String, LightStatusRequest> results = new HashMap<>();
        segmentStates.forEach((name, state) -> results.put(name, requestState(name, state)));
        return results;
    }
    /**
     * Request that every currently active named segment change its state to the given one.
     * If the priority of the current state of any named segments are above the new one, those
     * named segments will not be updated.
     */
    public static LightStatusRequest requestState(Object state)
    {
        LightStatusRequest request = new LightStatusRequest(state);
        for (int i = 0; i < kInstance.config.segments.size(); i++)
            kInstance.mRequestState(kInstance.config.segments.get(i).name, request);
        return request;
    }

    private LightSchedulerConfig config;

    private AddressableLED[] strips;
    private AddressableLEDBuffer[] buffers;
    private HashMap<String, AddressableLEDBufferView> namedSegmentToView;

    private HashMap<String, ArrayList<LightStatusRequest>> segmentRequests;
    private HashMap<String, Object> chosenStates;

    private boolean initialized;

    private LightScheduler()
    {
        config = LightSchedulerConfig.kDefault;
        initialized = false;
    }

    private void freeStrips()
    {
        initialized = false;
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
        if (segmentRequests != null) segmentRequests.clear();
        if (chosenStates != null) chosenStates.clear();
        if (warningCooldownTimer != null) warningCooldownTimer.stop();
    }
    private void refreshStrips()
    {
        freeStrips();
        initialized = false;

        // STEP 1: Compress named segments into strips.
        // The left side of the tuple represents the port, the right represents the length.
        ArrayList<Pair<Integer, Integer>> stripInfo = new ArrayList<>();
        HashMap<Integer, Integer> portToIndex = new HashMap<>();

        final int segmentCount = config.segments.size();
        for (int i = 0; i < segmentCount; i++)
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
        log(0, "Initializing %d light strips for %d named segments.", stripCount, segmentCount);

        if (stripCount > 1)
        {
            log(3, "Hello! Sorry to inconvenience you, but at the moment only a single LED strip port is supported. The scheduler will be disabled until the configuration is changed.");
            return;
        }

        strips = new AddressableLED[stripCount];
        buffers = new AddressableLEDBuffer[stripCount];
        for (int i = 0; i < stripCount; i++)
        {
            Pair<Integer, Integer> info = stripInfo.get(i);
            int port = info.getFirst(), length = info.getSecond();

            @SuppressWarnings("resource")
            AddressableLED strip = new AddressableLED(port);
            strip.setLength(length);

            AddressableLEDBuffer buffer = new AddressableLEDBuffer(length);

            strips[i] = strip;
            buffers[i] = buffer;

            strip.start();
            strip.setData(buffer);
        }

        // STEP 2: Create views by name and add them to the hash map. Also start the state machine.
        namedSegmentToView = new HashMap<>();
        segmentRequests = new HashMap<>();
        chosenStates = new HashMap<>();
        for (int i = 0; i < segmentCount; i++)
        {
            NamedLightSegment segment = config.segments.get(i);
            AddressableLEDBufferView view = buffers[portToIndex.get(segment.port)].createView(segment.startIndex, segment.endIndex);
            namedSegmentToView.put(segment.name, view);
            segmentRequests.put(segment.name, new ArrayList<>());
            chosenStates.put(segment.name, null);
        }

        // Other stuff        
        warningCooldownTimer = new Timer();
        warningCooldownTimer.start();
        initialized = true;
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

    private int absoluteTicks;
    @Override
    public void execute()
    {
        absoluteTicks++;
        if (!initialized) return;

        // Check for problems.
        if (warningCooldownTimer.hasElapsed(1.0))
        {
            checkOverlap();
            checkSamePriority();
            warningCooldownTimer.restart();
        }

        // Apply patterns to views depending on state.
        for (int i = 0; i < config.segments.size(); i++)
        {
            NamedLightSegment segment = config.segments.get(i);
            if (!segment.enabled) continue;

            LightPattern pattern = mGetPattern(segment.name);
            pattern.setCurrentTick(absoluteTicks);
            pattern.applyTo(namedSegmentToView.get(segment.name));
        }

        // Apply buffers.
        for (int i = 0; i < strips.length; i++) strips[i].setData(buffers[i]);
    }

    private Object mGetState(String name)
    {
        if (!initialized) return null;
        ArrayList<LightStatusRequest> requests = segmentRequests.get(name);
        Object state = null;
        int priority = Integer.MIN_VALUE;
        for (int i = 0; i < requests.size(); i++)
        {
            LightStatusRequest request = requests.get(i);
            if (request.isDisposed())
            {
                requests.remove(i);
                i--;
                continue;
            }
            LightStatusConfig stateConfig = getStatusConfig(name, request.state);
            if (stateConfig == null) continue;
            else if (stateConfig.priority > priority)
            {
                state = request.state;
                priority = stateConfig.priority;
            }
        }

        Object prevState = chosenStates.get(name);
        if (prevState != state)
        {
            LightPattern prevPattern = getPatternByState(name, prevState),
                         newPattern = getPatternByState(name, state);
            prevPattern.onDisabled();

            newPattern.setStartTick(absoluteTicks);
            newPattern.onEnabled();

            log(1, "Light segment \"%s\" has changed state: %s -> %s",
                name,
                prevState == null ? "null" : prevState.toString(),
                state == null ? "null" : state.toString());
        }
        chosenStates.put(name, state);
        return state;
    }
    private LightPattern mGetPattern(String name)
    {
        // Do not call this method when inside the mGetState method, as that
        // will create a permanent recursive loop. Use getStatusConfig() instead.
        Object curState = mGetState(name);
        LightPattern pattern = config.unknownBehavior;
        for (int j = 0; j < config.states.size(); j++)
        {
            LightStatusConfig request = config.states.get(j);
            if (!request.appliesTo.equals(name) || curState != request.state) continue;
            pattern = request.pattern;
        }
        return pattern;
    }
    private void mRequestState(String name, LightStatusRequest state)
    {
        if (!initialized) return;
        segmentRequests.get(name).add(state);
    }

    @Override
    public boolean runsWhenDisabled() { return true; }

    private Timer warningCooldownTimer;
    private void checkOverlap()
    {
        // Check if any segment overlaps any other segment.
        // Ignore if they are on different ports.
        int segmentCount = config.segments.size();
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
                    log(2, "Named light segment \"%s\" overlaps segment \"%s\"",
                        segmentA.name,
                        segmentB.name);
                }
            }
        }
    }
    private void checkSamePriority()
    {
        // Check if two states have the same priority for a given named segment.
        for (int i = 0; i < config.segments.size(); i++)
        {
            NamedLightSegment segment = config.segments.get(i);
            for (int j = 0; j < config.states.size(); j++)
            {
                LightStatusConfig requestA = config.states.get(j);
                if (!segment.name.equals(requestA.appliesTo)) continue;

                for (int k = j + 1; k < config.states.size(); k++)
                {
                    LightStatusConfig requestB = config.states.get(k);
                    if (!segment.name.equals(requestB.appliesTo)) continue;

                    if (requestA.priority == requestB.priority)
                    {
                        log(2, "Named light segment \"%s\" has two states with the same priority: %s and %s (priority %d).",
                            segment.name,
                            requestA.state == null ? "null" : requestA.state.toString(),
                            requestB.state == null ? "null" : requestB.state.toString(),
                            requestA.priority);
                    }
                }
            }
        }
    }
    private LightStatusConfig getStatusConfig(String name, Object state)
    {
        for (int i = 0; i < config.states.size(); i++)
        {
            LightStatusConfig request = config.states.get(i);
            if (request.appliesTo.equals(name) && request.state == state) return request;
        }
        return null;
    }
    private LightPattern getPatternByState(String name, Object state)
    {
        LightStatusConfig config = getStatusConfig(name, state);
        if (config == null) return this.config.unknownBehavior;
        else return config.pattern;
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
