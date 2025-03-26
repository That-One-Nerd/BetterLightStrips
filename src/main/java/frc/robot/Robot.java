// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import javax.lang.model.util.ElementScanner14;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import net.betterlights.LightScheduler;
import net.betterlights.LightStatusRequest;
import net.betterlights.patterns.SolidLightPattern;

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
      .withNamedLightSegment("demo1", 0, 0, 6)
      .withNamedLightSegment("demo2", 0, 7, 19)
      .withNamedState("demo1", DemoState.State1, 10, new SolidLightPattern(Color.kRed))
      .withNamedState("demo1", DemoState.State2, 20, new SolidLightPattern(Color.kGreen))
      .withNamedState("demo1", DemoState.State3, 30, new SolidLightPattern(Color.kBlue))
      .withNamedState("demo2", DemoState.State1, 10, new SolidLightPattern(Color.kRed))
      .withNamedState("demo2", DemoState.State2, 50, new SolidLightPattern(Color.kGreen))
      .withNamedState("demo2", DemoState.State3, 30, new SolidLightPattern(Color.kBlue))
      .withUnknownBehavior(new SolidLightPattern(Color.kWhite));

    LightScheduler.start();
    request1 = LightScheduler.requestState(DemoState.State1);
  }
  private static LightStatusRequest request1;
  private static LightStatusRequest request2;

  private static enum DemoState
  {
    State1,
    State2,
    State3
  }

  private int tick, index;
  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();

    tick++;
    if (tick >= 50)
    {
      tick = 0;
      index++;
      if (index >= 3) index = 0;
    }

    switch (index)
    {
      case 0: request1.state = DemoState.State1; break;
      case 1: request1.state = DemoState.State2; break;
      case 2: request1.state = DemoState.State3; break;
      default: request1.state = null; break;
    }

    if (tick == 0 && index == 2)
    {
      if (request2 == null)
      {
        request2 = LightScheduler.requestState("demo2", DemoState.State2);
      }
      else
      {
        request2.dispose();
        request2 = null;
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
