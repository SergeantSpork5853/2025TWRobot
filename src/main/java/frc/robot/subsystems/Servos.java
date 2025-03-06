// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.servohub.ServoChannel;
import com.revrobotics.servohub.ServoChannel.ChannelId;
import com.revrobotics.servohub.ServoHub;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Servos extends SubsystemBase {
  /** Creates a new Servos. */
  int pulse_0 = 1500; 
  int pulse_1 = 1500; 
  // Initialize the servo hub
  ServoHub m_servoHub = new ServoHub(2);
  // Obtain a servo channel controller
  ServoChannel m_channel0 = m_servoHub.getServoChannel(ChannelId.kChannelId0);
  ServoChannel m_channel1 = m_servoHub.getServoChannel(ChannelId.kChannelId1);
  ServoChannel m_channel2 = m_servoHub.getServoChannel(ChannelId.kChannelId2);
  ServoChannel m_channel3 = m_servoHub.getServoChannel(ChannelId.kChannelId3);

  public Servos() {
    m_servoHub.setBankPulsePeriod(ServoHub.Bank.kBank0_2, 4000);
    m_servoHub.setBankPulsePeriod(ServoHub.Bank.kBank3_5, 4000);
    m_channel0.setPowered(true);
    m_channel1.setPowered(true);
    m_channel2.setPowered(true);
    m_channel3.setPowered(true);

    m_channel0.setEnabled(true);
    m_channel1.setEnabled(true);
    m_channel2.setEnabled(true);
    m_channel3.setEnabled(true);
  }

  @Override
  public void periodic() {
    //SmartDashboard.putNumber("Servo 0 Angle", 1500); 
    //SmartDashboard.putNumber("Servo 1 Angle", 1500); 
   
  }

  public void openCageGuide(){
    m_channel0.setPulseWidth(1450); 
    m_channel1.setPulseWidth(1550);
  }

  public void closeCageGuide(){
    m_channel0.setPulseWidth(2100); 
    m_channel1.setPulseWidth(850);
  }

  public void openCageGrip(){
    m_channel2.setPulseWidth(1600); 
    m_channel3.setPulseWidth(1250);
  }

  public void closeCageGrip(){
    m_channel2.setPulseWidth(900); 
    m_channel3.setPulseWidth(2000);
  }
}
