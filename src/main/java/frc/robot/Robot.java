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
      .withNamedLightSegment("full", 0, 0, 99)
      .withStateAll("funnylights", 10,
        new RandomLightPattern()
          .withGamma(2.2)
          .withRefreshEvery(5))
      .withStateAll("lerpfrom", 20,
        new TransitionLightPattern()
          .withStartPattern(
            new RandomLightPattern()
              .withGamma(2.2)
              .withRefreshEvery(5))
          .withEndPattern(new SolidLightPattern(Color.kGreen))
          .withDuration(100))
      .withStateAll("lerpto", 20,
        new TransitionLightPattern()
          .withStartPattern(new SolidLightPattern(Color.kGreen))
          .withEndPattern(
            new RandomLightPattern()
               .withGamma(2.2)
              .withRefreshEvery(5))
          .withDuration(100))
      .withUnknownBehavior(new SolidLightPattern(Color.kWhite));
      
    LightScheduler.start();
    request1 = LightScheduler.requestState("lerpto");
    request2 = LightScheduler.requestState("lerpfrom");
  }

  private boolean first;
  private int tick;
  private LightStatusRequest request1, request2;
  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();

    tick++;
    if (tick % 150 == 0)
    {
      if (request1.isEnabled())
      {
        request1.disable();
        request2.enable();
      }
      else
      {
        request1.enable();
        request2.disable();
      }
    }
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
  }

  @Override
  public void teleopPeriodic() {}

  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}
}
