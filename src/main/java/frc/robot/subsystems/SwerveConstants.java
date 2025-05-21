package frc.robot.subsystems;

import edu.wpi.first.math.util.Units;
import frc.robot.Constants;

public final class SwerveConstants 
{
    public final static int DRIVEFRONTLEFT = Constants.CanBus.DRIVEFRONTLEFT;
    public final static int DRIVEFRONTRIGHT = Constants.CanBus.DRIVEFRONTRIGHT;
    public final static int DRIVEBACKLEFT = Constants.CanBus.DRIVEBACKLEFT;
    public final static int DRIVEBACKRIGHT = Constants.CanBus.DRIVEBACKRIGHT;

    public final static int ROTATIONFRONTLEFT = Constants.CanBus.ROTATIONFRONTLEFT;
    public final static int ROTATIONFRONTRIGHT = Constants.CanBus.ROTATIONFRONTRIGHT;
    public final static int ROTATIONBACKLEFT = Constants.CanBus.ROTATIONBACKLEFT;
    public final static int ROTATIONBACKRIGHT = Constants.CanBus.ROTATIONBACKRIGHT;

    public final static int ENCODERFRONTLEFT = Constants.CanBus.ENCODERFRONTLEFT;
    public final static int ENCODERFRONTRIGHT = Constants.CanBus.ENCODERFRONTRIGHT;
    public final static int ENCODERBACKLEFT = Constants.CanBus.ENCODERBACKLEFT;
    public final static int ENCODERBACKRIGHT = Constants.CanBus.ENCODERBACKRIGHT;

    public final static double WHEELRADIUS = 2; // inches
    public final static double WHEEL_CIRCUMFERENCE = Units.inchesToMeters(WHEELRADIUS * 2 * Math.PI);

    public static final double GEAR_RATIO_WCP_BELTED = 6.55; 
    public static final double GEAR_RATIO_WCP_GEARED = 6.55; 
    public static final double GEAR_RATIO_WCP_UPRIGHT = 7.42;

}