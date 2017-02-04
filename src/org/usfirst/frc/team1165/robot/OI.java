package org.usfirst.frc.team1165.robot;

import org.usfirst.frc.team1165.robot.commands.DriveShooter;
import org.usfirst.frc.team1165.robot.commands.EnableUltrasonicSetpoint;
import org.usfirst.frc.team1165.robot.commands.RotateToHeading;
import org.usfirst.frc.team1165.robot.commands.ToggleServo;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI
{
    //// CREATING BUTTONS
    // One type of button is a joystick button which is any button on a
    //// joystick.
    // You create one by telling it which joystick it's on and which button
    // number it is.
    public Joystick stick = new Joystick(RobotMap.joystickPort);
    
     JoystickButton shooterRPMButton = new JoystickButton(stick, RobotMap.shooterRPMPortNumber);

    JoystickButton ultrasonicPIDButton = new JoystickButton(stick, RobotMap.enableUltrasonicPID);
    
    JoystickButton servoButton = new JoystickButton(stick, RobotMap.rightServoButtonNumber);
    JoystickButton rotateN180 = new JoystickButton(stick, RobotMap.rotateRobotN180);
    JoystickButton rotate180 = new JoystickButton(stick, RobotMap.rotateRobot180);
    JoystickButton rotateN90 = new JoystickButton(stick, RobotMap.rotateRobotN90);
    JoystickButton rotate90 = new JoystickButton(stick, RobotMap.rotateRobot90);
    // Button button = new JoystickButton(stick, buttonNumber);

    // There are a few additional built in buttons you can use. Additionally,
    // by subclassing Button you can create custom triggers and bind those to
    // commands the same as any other Button.

    //// TRIGGERING COMMANDS WITH BUTTONS
    // Once you have a button, it's trivial to bind it to a button in one of
    // three ways:
    public OI()
    {
	shooterRPMButton.whenPressed(new DriveShooter());
	ultrasonicPIDButton.whenPressed(new EnableUltrasonicSetpoint());
	
	servoButton.whenPressed(new ToggleServo());
	// Rotate To Heading
	rotateN180.whenPressed(new RotateToHeading(RobotMap.rotateRobotN180));
	rotate180.whenPressed(new RotateToHeading(RobotMap.rotateRobot180));
	rotateN90.whenPressed(new RotateToHeading(RobotMap.rotateRobotN90));
	rotate90.whenPressed(new RotateToHeading(RobotMap.rotateRobot90));
    }
}