// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file iAhrsn the root directory of this project.

package frc.robot.subsystems;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.config.RobotConfig;
//Studica Labs Dependencies
import com.studica.frc.AHRS;
import com.studica.frc.AHRS.NavXComType;

//WPILIB Dependencies
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DriverStation;

//Class containing all functions and variables pertaining to the SwerveDrive
public class SwerveDrive extends SubsystemBase 
{ 
  double maxVelocity; //The maximum linear speed of the robot in meters per second
  double maxAngularSpeed; //The maximum angular speed of the robot in radians per second
  double headingAdjustment = 180.0; //An adjustment to be applied to the gyro sensor if needed
  SwerveDriveKinematics kinematics; //A kinematics object used by the odometry object to determine wheel locations
  String moduleType; //The type of Swerve Module being utilized
  boolean debugMode = false; //Whether or not to enable debug features (DISABLE FOR COMPETITIONS)
  private boolean useStopAngle = false; //Command the wheels to a stop angle

  //Instantiate four Swerve Modules according to the class SwerveModule constructor
  private final SwerveModule frontLeft  = new SwerveModule(SwerveConstants.DRIVEFRONTLEFT, SwerveConstants.ROTATIONFRONTLEFT, SwerveConstants.ENCODERFRONTLEFT, 45); 
  private final SwerveModule frontRight =  new SwerveModule(SwerveConstants.DRIVEFRONTRIGHT, SwerveConstants.ROTATIONFRONTRIGHT, SwerveConstants.ENCODERFRONTRIGHT, -45); 
  private final SwerveModule backLeft   =  new SwerveModule(SwerveConstants.DRIVEBACKLEFT, SwerveConstants.ROTATIONBACKLEFT, SwerveConstants.ENCODERBACKLEFT, -45); 
  private final SwerveModule backRight  = new SwerveModule(SwerveConstants.DRIVEBACKRIGHT, SwerveConstants.ROTATIONBACKRIGHT, SwerveConstants.ENCODERBACKRIGHT, 45); 
  
  //The gyro used to determine the robot heading is a Kauli Labs NavX plugged into the MXP port on the roborio
  private final AHRS gyro = new AHRS(NavXComType.kMXP_SPI);

  //Odometry determines the robots position on the field
  //private final SwerveDriveOdometry odometry; 

  //Pose estimator to update odometry
  private final SwerveDrivePoseEstimator estimator; 

  //This switch is used as an external input to tell the SwerveDrive to reset the odometry
  private Command normalize;
  private Command resetOdometry;

  private Field2d field = new Field2d();

  /**
   * The constructor for the swerve drive
   * @param maxVelocity The desired max velocity of the robot in meters per second
   * @param maxAngularSpeed The desired max angular speed of the robot in radians per second 
   * @param moduleType The module tyoe being used. Options: "geared flipped", "belted flipped"
   * @param kinematics A kinematics object containing the locations of each swerve module relative to robot center 
   */
  public SwerveDrive(double maxVelocity, double maxAngularSpeed, String moduleType, SwerveDriveKinematics kinematics) 
    {
      this.maxAngularSpeed = maxAngularSpeed; 
      this.maxVelocity = maxVelocity; 
      this.moduleType = moduleType; 
      this.kinematics = kinematics; 
      
      //Reset the gyro sensor on initialization of the SwerveDrive subsystem
      resetGyro(); 

      //Using the moduleType passed in to the constructor, set the appropriate settings for the modules used.
      frontLeft.setModuleSettings(moduleType);
      frontRight.setModuleSettings(moduleType);
      backLeft.setModuleSettings(moduleType);
      backRight.setModuleSettings(moduleType);  

      /*
      * Initialize the odometry (if this is done outside of the constructor it will pass garbage values 
      * for the distances of the Swerve Modules). 
      */
      estimator = new SwerveDrivePoseEstimator(kinematics, gyro.getRotation2d(), getSwerveModulePositions(), Pose2d.kZero);          
      try{
        RobotConfig config = RobotConfig.fromGUISettings();

        AutoBuilder.configure(
          this::getPose, 
          this::resetPose, 
          this::getChassisSpeeds, 
          (speeds, feedforwards) -> driveRobotOriented(speeds),
          Constants.SwerveDriveController,
          config, 
          () -> {
            // Boolean supplier that controls when the path will be mirrored for the red alliance
            // This will flip the path being followed to the red side of the field.
            // THE ORIGIN WILL REMAIN ON THE BLUE SIDE
            var alliance = DriverStation.getAlliance();
            if (alliance.isPresent()) {
                return alliance.get() == DriverStation.Alliance.Red;
            }
            return false;
          }, 
          this
        );
      } catch (Exception e) {
        // Handle exception as needed
        e.printStackTrace();
      }  

      //This switch is used as an external input to tell the SwerveDrive to normalize the Swerve Modules
      normalize = new InstantCommand( () -> {
        normalizeModules();
      })
        .ignoringDisable(true);
      normalize.setName("Normalize Swerve");
      SmartDashboard.putData(normalize);

      // Reset the odometry readings when reset odometry switch is pressed (DIO switches are ACTIVE LOW)
      resetOdometry = new InstantCommand( () -> {
        zeroPose();
      })
        .ignoringDisable(true);
        resetOdometry.setName("Reset Odometry");
      SmartDashboard.putData(resetOdometry);
  }

  /**
   * The constructor for the swerve drive for pathplanner use
   * @param maxVelocity The desired max velocity of the robot in meters per second
   * @param maxAngularSpeed The desired max angular speed of the robot in radians per second 
   * @param moduleType The module tyoe being used. Options: "geared flipped", "belted flipped"
   * @param kinematics A kinematics object containing the locations of each swerve module relative to robot center 
   * @param config PID constants and other settings for autonomous driving
   */

  /**
   * Reset the gyro using NavX reset function and apply a user-specified adjustment to the angle
   */
  public void resetGyro()
  {
    if (gyro != null)
    {
      gyro.reset();
      gyro.setAngleAdjustment(headingAdjustment);
    }
  }

  @Override 
  public void periodic() 
    {      
      // //Periodically update the swerve odometry
      updateOdometry(); 
      SmartDashboard.putNumber("Raw Gyro Angle", gyro.getAngle());

      if(debugMode)
        {
          
        
          SmartDashboard.putNumber("SwerveModuleAngle/frontLeft", frontLeft.getAngle()); 
          SmartDashboard.putNumber("SwerveModuleAngle/frontRight", frontRight.getAngle()); 
          SmartDashboard.putNumber("SwerveModuleAngle/backLeft", backLeft.getAngle()); 
          SmartDashboard.putNumber("SwerveModuleAngle/backRight", backRight.getAngle()); 
  
          SmartDashboard.putNumber("SwerveModuleDistanceFL", frontLeft.getSwerveModulePosition().distanceMeters);
          SmartDashboard.putNumber("SwerveModuleDistanceFR", frontRight.getSwerveModulePosition().distanceMeters);
          SmartDashboard.putNumber("SwerveModuleDistanceBL", backLeft.getSwerveModulePosition().distanceMeters);
          SmartDashboard.putNumber("SwerveModuleDistanceBR", backRight.getSwerveModulePosition().distanceMeters);

          SmartDashboard.putNumber("SwerveModuleVelocityFL", frontLeft.getSwerveModuleState().speedMetersPerSecond);
          SmartDashboard.putNumber("SwerveModuleVelocityFR", frontRight.getSwerveModuleState().speedMetersPerSecond);
          SmartDashboard.putNumber("SwerveModuleVelocityBL", backLeft.getSwerveModuleState().speedMetersPerSecond);
          SmartDashboard.putNumber("SwerveModuleVelocityBR", backRight.getSwerveModuleState().speedMetersPerSecond);
  
          SmartDashboard.putNumber("SwerveModuleAngleFL", frontLeft.getSwerveModulePosition().angle.getDegrees());
          SmartDashboard.putNumber("SwerveModuleAngleFR", frontRight.getSwerveModulePosition().angle.getDegrees());
          SmartDashboard.putNumber("SwerveModuleAngleBL", backLeft.getSwerveModulePosition().angle.getDegrees());
          SmartDashboard.putNumber("SwerveModuleAngleBR", backRight.getSwerveModulePosition().angle.getDegrees());
  
          SmartDashboard.putNumber("SwerveDrive/Pose/X", estimator.getEstimatedPosition().getX());
          SmartDashboard.putNumber("SwerveDrive/Pose/Y", estimator.getEstimatedPosition().getY());
          SmartDashboard.putNumber("SwerveDrive/Pose/Z", estimator.getEstimatedPosition().getRotation().getDegrees());
        }
    }

  /**
   * Stop all swerve modules by setting their speeds to 0
   */
  public void stop()
    {
      drive(0.0, 0.0, 0.0, false);
    } 

  //Drive the robot with the robot's front always being forward
  private void driveRobotOriented(ChassisSpeeds robotRelativeSpeeds)
  {
    ChassisSpeeds targetSpeeds = ChassisSpeeds.discretize(robotRelativeSpeeds, .02);

    SwerveModuleState[] targetStates = kinematics.toSwerveModuleStates(new ChassisSpeeds(targetSpeeds.vxMetersPerSecond, targetSpeeds.vyMetersPerSecond, -targetSpeeds.omegaRadiansPerSecond));
    setModuleStates(targetStates);
  }

  private double maxXSpeed = 0.0;
  private double maxYSpeed = 0.0;
  public void drive(double xSpeed, double ySpeed, double rotationSpeed, boolean fieldOriented)
    {
      double tmpXSpeed;
      tmpXSpeed = Math.abs(xSpeed * maxVelocity);
      if (tmpXSpeed > maxXSpeed) {
        maxXSpeed = tmpXSpeed;
      }
      double tmpYSpeed;
      tmpYSpeed = Math.abs(ySpeed * maxVelocity);
      if (tmpYSpeed > maxYSpeed) {
        maxYSpeed = tmpYSpeed;
      }
      SmartDashboard.putNumber("Max Y Speed", maxYSpeed);
      SmartDashboard.putNumber("Max X Speed", maxXSpeed);

      SwerveModuleState[] swerveModuleStates = kinematics.toSwerveModuleStates(
        (fieldOriented && gyro != null)
        ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed * maxVelocity, 
                                              ySpeed * maxVelocity, 
                                              rotationSpeed * maxAngularSpeed, 
                                              gyro.getRotation2d()) 
        : new ChassisSpeeds(xSpeed * maxVelocity, 
                            ySpeed * maxVelocity, 
                            rotationSpeed * maxAngularSpeed)); 
      //This function should limit our speed to the value we set (maxVelocity)
      SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, maxVelocity);
      setModuleStates(swerveModuleStates);
    }

  //Update the odometry values using the latest reported SwerveModule postitions and robot heading
  private void updateOdometry()
    { 
      estimator.update( gyro.getRotation2d(), getSwerveModulePositions());
      field.setRobotPose(getPose());
      SmartDashboard.putData(field);
    }

  /**
   * Set a starting location other than the defualt (x=0,y=0,rotation=0)
   * @param pose a Pose2d with a location (x,y) and a rotation in radians 
   */
  public void setStartLocation(Pose2d pose) 
    {
      gyro.setAngleAdjustment(pose.getRotation().getDegrees() - getGyroAngle());
      estimator.resetPosition(gyro.getRotation2d(), getSwerveModulePositions(), getPose());
    }  

  /**
   * Get the robots current pose
   * @return The pose of the robot as a Pose2d
   */
  public Pose2d getPose()
  { 
    return new Pose2d(estimator.getEstimatedPosition().getX(), estimator.getEstimatedPosition().getY(), estimator.getEstimatedPosition().getRotation());   
  } 

  /* 
   * Command each swerve module to a SwerveModuleState (velocity and angle) based on an array of states passed in.
   * The first state in the array will correspond to the state to be applied to the first module
   * and so on.
   */ 
  private void setModuleStates(SwerveModuleState[] states)
    { 
      frontLeft.setDesiredState(states[0], useStopAngle);
      frontRight.setDesiredState(states[1], useStopAngle); 
      backLeft.setDesiredState(states[2], useStopAngle);
      backRight.setDesiredState(states[3], useStopAngle);
    }

  
  //Get the rotation and distance of each swerve module as an array of SwerveModulePositions
  private SwerveModulePosition[] getSwerveModulePositions() 
    {
      return new SwerveModulePosition[] 
        {
          frontLeft.getSwerveModulePosition(),
          frontRight.getSwerveModulePosition(),
          backLeft.getSwerveModulePosition(),
          backRight.getSwerveModulePosition()
        };
    }

  //Get the rotation and velocity of each swerve module as an array of SwerveModulePositions
  private SwerveModuleState[] getSwerveModuleStates()
  {
    return new SwerveModuleState[] 
        {
          frontLeft.getSwerveModuleState(),
          frontRight.getSwerveModuleState(),
          backLeft.getSwerveModuleState(),
          backRight.getSwerveModuleState()
        };
  }

  //Zero out the pose of the robot to a location of x=0, y=0, and rotation = 0 
  public void zeroPose()
  {
    System.out.println("resetting pose");
    resetGyro();
    estimator.resetPosition(gyro.getRotation2d(), getSwerveModulePositions(), new Pose2d(0.0, 0.0, gyro.getRotation2d()));
  }

  //A Pose2d consumer required for PathPlanner
  public void resetPose(Pose2d pose)
  {
    estimator.resetPosition(gyro.getRotation2d(), getSwerveModulePositions(), pose);
  }

  //A getter of the robot's speed relative to itself
  public ChassisSpeeds getChassisSpeeds() {
    return kinematics.toChassisSpeeds(getSwerveModuleStates());
  }

  /*
   * Get the angle of the gyro. This angle is negated to reflect the fact that 
   * the code is expecting counterclockwise to be positive (critical for odometry).
   */
  private double getGyroAngle()
    {
      if(gyro == null)
        {
          return 0.0;
        }
      return -gyro.getAngle();
    } 
 
  //Put all swerve modules in brakemode
  public void brakeMode(boolean bOn)
    { 
      frontLeft.brakeMode(bOn);
      backLeft.brakeMode(bOn); 
      frontRight.brakeMode(bOn); 
      backRight.brakeMode(bOn);
    }
  
  //Enable the "parking brake" feature of the robot (commanding the modules to an 45 degree angle)
  public void enableStopAngle(boolean enabled)
    { 
      useStopAngle = enabled; 
    }
 
  //Configure the angle offsets for each swerve module  
  private void normalizeModules()
    {
      frontLeft.normalizeModule();
      backLeft.normalizeModule(); 
      frontRight.normalizeModule(); 
      backRight.normalizeModule();
    }

 /**
  * Enable debug settings like extra SmartDashboard data. Do not use this in competitions
  * because it consumes too much RAM!
  */
  public void enableDebugMode()
   {
     System.out.println("DEBUG MODE ENABLED");
     debugMode = true;
   }

  /**
   * Set an offset to the robots heading in degrees. This is useful if the robots physical forward
   * direction is not the direction of forward travel desired. This value will update when the gyro is 
   * reset.
   * @param adjustmentDeg an adjustment, in degrees, of the robots heading
   */
  public void setHeadingAdjustment(double adjustmentDeg)
    {
      headingAdjustment = adjustmentDeg; 
      System.out.printf("Set an angle adjustment of %.2f degrees", adjustmentDeg);
    }

  public static void useStopAngle(boolean b) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'useStopAngle'");
  }
}