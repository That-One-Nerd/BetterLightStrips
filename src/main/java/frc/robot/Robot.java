// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import net.betterlights.Gradient;
import net.betterlights.LightScheduler;
import net.betterlights.LightStatusRequest;
import net.betterlights.patterns.*;
import net.betterlights.transitions.*;

public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private final RobotContainer m_robotContainer;

  public Robot() {
    m_robotContainer = new RobotContainer();
  }

  @Override
  public void robotInit() {
    LightScheduler.configure()
      .withLogLevel(0)
      .withNamedLightSegment("seg1", 0,  0, 39)
      .withStateAll("patternA", 10, () ->
        new GradientLightPattern()
          .withGamma(1.0)
          .withGradient(0, new Gradient()
            .withColorEntry(0.0, Color.kGreen)
            .withColorEntry(0.1, Color.kWhite)
            .withColorEntry(0.2, Color.kWhite)
            .withColorEntry(0.3, Color.kGreen))
          .atBrightness(0.5)
          .scroll(0.5))
      .withStateAll("patternB", 20, () ->
        new GradientLightPattern()
          .withGamma(2.2)
          .withSolidGradient(0.0, Color.kBlack)
          .withGradient(0.25, new Gradient()
            .withColorEntry(0.00, Color.kBlack)
            .withColorEntry(0.15, Color.kRed)
            .withColorEntry(0.30, Color.kRed)
            .withColorEntry(0.45, Color.kBlack))
          .withSolidGradient(0.5, Color.kBlack)
          .withGradient(0.75, new Gradient()
            .withColorEntry(0.55, Color.kBlack)
            .withColorEntry(0.70, Color.kBlue)
            .withColorEntry(0.85, Color.kBlue)
            .withColorEntry(1.00, Color.kBlack))
          .withEndAsBeginning()
          .withDuration(35))
      .withTransitionAll("patternA", "patternB", () ->
        new RandomLightTransition()
          .withColor(Color.kWhite)
          .withPixelsPerTick(2))
      .withTransitionAll("patternB", "patternA", () ->
        new RandomLightTransition()
          .withColor(Color.kWhite)
          .withPixelsPerTick(2))
      .withUnknownBehavior(new SolidLightPattern(Color.kWhite));

    LightScheduler.start();
    LightScheduler.requestState("patternA");

    // Things I need to make wrappers for:
    // LEDPattern pattern = null;
    // pattern.blend(null);
    // pattern.blink(null, null);
    // pattern.synchronizedBlink(null);
    // pattern.breathe(null);
    // pattern.mask(null);
    // pattern.overlayOn(null);
    // also make OffsetLightWrapper lerp between indices.

    // Other debug-purpose patterns to make:
    // - ProgressBarLightPattern
  }

  public LightStatusRequest request;
  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void disabledExit() {}

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void autonomousExit() {}

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }

    request = LightScheduler.requestState("patternB");
  }

  @Override
  public void teleopPeriodic() {}

  @Override
  public void teleopExit() {
    request.dispose();
  }

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}
}
