// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.util.Units;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
    public static final int kOperatorPort = 1;   
  }

  public static class ElavationConstants{
    public static final double POSITIONACCURACY = 0.25;
    public static final double CURRENTLIMIT = 70.0;
  }

  public static class CanID {
  }
    public static final double POSITION_ERROR_DELTA = 0.25;

    public static final SwerveDriveKinematics kinematics = new SwerveDriveKinematics(
        new Translation2d(Units.inchesToMeters(11), Units.inchesToMeters(11)), // Front Left
        new Translation2d(Units.inchesToMeters(11), Units.inchesToMeters(-11)), // Front Right
        new Translation2d(Units.inchesToMeters(-11), Units.inchesToMeters(11)), // Back Left
        new Translation2d(Units.inchesToMeters(-11), Units.inchesToMeters(-11))); // Back Right
  
    public static final PPHolonomicDriveController SwerveDriveController = new PPHolonomicDriveController(
      new PIDConstants(2.5, .75, 0.0), // Translation PID constants
      //new PIDConstants(0.0, 0.0, 0.0), // Translation PID constants
      new PIDConstants(10, 0.0, 0.0) // Rotation PID constants.
    );

    public static class CanBus {
      public final static int DRIVEFRONTLEFT = 1;
      public final static int DRIVEFRONTRIGHT = 2;
      public final static int DRIVEBACKLEFT = 3;
      public final static int DRIVEBACKRIGHT = 4;
  
      public final static int ROTATIONFRONTLEFT = 21;
      public final static int ROTATIONFRONTRIGHT = 22;
      public final static int ROTATIONBACKLEFT = 23;
      public final static int ROTATIONBACKRIGHT = 24;
  
      public final static int ENCODERFRONTLEFT = 31;
      public final static int ENCODERFRONTRIGHT = 32;
      public final static int ENCODERBACKLEFT = 33;
      public final static int ENCODERBACKRIGHT = 34;

      public final static int ClimberPrimary = 5;
      public final static int ClimberSecondary = 6;

      public static final int ElevatorPrimaryID = 7;
      public static final int ElevatorSecondaryID = 8;
      public static final int ArmMotorPrimaryID = 9;
        public final static int Intake = 16;

      public final static int IntakeSensor = 17;
    }

    public static class DigitalIO {
      public static final int ElevatorForwardLimit = 0;
      public static final int ElevatorReverseLimit = 1;
    }
}