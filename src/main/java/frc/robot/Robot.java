// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

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
      .withNamedLightSegment("full", 0, 0, 19)
      .withStateAll("randomlights", 10,
        new RandomLightPattern()
          .withGamma(2.2)
          .withShadesOfColor(Color.kPurple)
          .withRefreshEvery(5)
          .withSmoothInterpolation())
      .withStateAll("bouncer", 20,
        new BounceLightPattern(Color.kPurple)
          .withMoveSpeed(0.75)
          .withLength(5)
          .withWaveBounce()
          .withFade())
      .withTransitionAll("randomlights", "bouncer",
        new SwipeTransition()
          .withIntermediate(Color.kRed, 20)
          .withSpeed(2))
      .withTransitionAll("bouncer", "randomlights",
        new SwipeTransition()
          .withIntermediate(Color.kWhite, 5)
          .withSpeed(1))
      .withUnknownBehavior(new SolidLightPattern(Color.kWhite));

    LightScheduler.start();
    LightScheduler.requestState("randomlights");
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

    request = LightScheduler.requestState("bouncer");
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
