package frc.robot.subsystems.vision;

import frc.robot.lib.LerpTable;
import frc.robot.subsystems.vision.Camera.CameraIntrinsics;

import java.util.HashMap;

import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;

public class VisionConstants {
    public record CameraConfig(String name, double trustScalar, Transform3d transform, CameraIntrinsics intrinsics) {
    }

    public static final CameraConfig[] CONFIGS = {
            new CameraConfig(
                    "Left",
                    1.0,
                    new Transform3d(
                            new Translation3d(
                                    Units.inchesToMeters(-12.767),
                                    Units.inchesToMeters(-12.327),
                                    Units.inchesToMeters(7.588)),
                            new Rotation3d(0, Math.toRadians(-15), Math.toRadians(135))),
                    new CameraIntrinsics(
                            800.0,
                            600.0,
                            685.13,
                            690.95,
                            453.90,
                            237.267,
                            new double[] { 0.105, 0.275, -0.028, 0.014, -0.047, 0.062, 0.176, 0.197 })),
            new CameraConfig(
                    "Right",
                    1.0,
                    new Transform3d(
                            new Translation3d(Units.inchesToMeters(-12.754),
                                    Units.inchesToMeters(12.326),
                                    Units.inchesToMeters(7.588)),
                            new Rotation3d(0, Math.toRadians(-15), Math.toRadians(-135))),
                    new CameraIntrinsics(
                            800.0,
                            600.0,
                            690.51,
                            687.34,
                            358.37,
                            332.82,
                            new double[] { 0.051, -0.09, 0.004, -0.014, 0.044, -0.005, 0.007, 0 }))
    };

    public static final class Filtering {
        public static final LerpTable HEIGHT_WIDTH_PROPORTION_WEIGHT_COEFFICIENT = new LerpTable(
                new LerpTable.LerpTableEntry(0.25, 0.0),
                new LerpTable.LerpTableEntry(0.7, 0.9),
                new LerpTable.LerpTableEntry(1.0, 1.0));

        public static final LerpTable AREA_WEIGHT_COEFFICIENT = new LerpTable(
                new LerpTable.LerpTableEntry(0.0, 0.0),
                new LerpTable.LerpTableEntry(0.2, 0.35),
                new LerpTable.LerpTableEntry(1.0, 0.45),
                new LerpTable.LerpTableEntry(4.0, 0.70),
                new LerpTable.LerpTableEntry(7.5, 1.0));

        public static final LerpTable PIXEL_OFFSET_WEIGHT_COEFFICIENT = new LerpTable(
                new LerpTable.LerpTableEntry(0.0, 1.0),
                new LerpTable.LerpTableEntry(0.2, 1.0),
                new LerpTable.LerpTableEntry(0.65, 0.75),
                new LerpTable.LerpTableEntry(1.0, 0.35));

        public static final LerpTable LINEAR_VELOCITY_WEIGHT_COEFFICIENT = new LerpTable(
                new LerpTable.LerpTableEntry(0.0, 1.0),
                new LerpTable.LerpTableEntry(2.5, 0.8),
                new LerpTable.LerpTableEntry(5.0, 0.1));

        public static final LerpTable ANGULAR_VELOCITY_WEIGHT_COEFFICIENT = new LerpTable(
                new LerpTable.LerpTableEntry(0.0, 1.0),
                new LerpTable.LerpTableEntry(7.0, 0.65),
                new LerpTable.LerpTableEntry(12.0, 0.0));

        public static final HashMap<Integer, Double> TAG_RANKINGS = new HashMap<>() {
            {
                put(1, 0.0); // CORAL STATION
                put(2, 0.0); // CORAL STATION
                put(3, 0.0); // PROCESSOR
                put(4, 0.0); // BARGE
                put(5, 0.0); // BARGE
                put(6, 1.0); // REEF
                put(7, 1.0); // REEF
                put(8, 1.0); // REEF
                put(9, 1.0); // REEF
                put(10, 1.0); // REEF
                put(11, 1.0); // REEF
                put(12, 0.0); // CORAL STATION
                put(13, 0.0); // CORAL STATION
                put(14, 0.0); // BARGE
                put(15, 0.0); // BARGE
                put(16, 0.0); // PROCESSOR
                put(17, 1.0); // REEF
                put(18, 1.0); // REEF
                put(19, 1.0); // REEF
                put(20, 1.0); // REEF
                put(21, 1.0); // REEF
                put(22, 1.0); // REEF
            }
        };
    }
}
