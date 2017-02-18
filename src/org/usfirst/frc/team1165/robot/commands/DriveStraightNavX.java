package org.usfirst.frc.team1165.robot.commands;

import org.usfirst.frc.team1165.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveStraightNavX extends Command
{
	private double targetHeading;
	private double forwardSpeed = -0.5;
	private double initialAngle;
	private double distanceToWall = -1;
	private boolean enableDistanceToWall = false;
	public DriveStraightNavX(double timeout)
	{
		// Use requires() here to declare subsystem dependencies
		requires(Robot.driveTrain);
		requires(Robot.navXSource);
		targetHeading = Robot.navXSource.getHeading();
		super.setTimeout(timeout);
	}
	
	public DriveStraightNavX(double forwardSpeed, double timeout)
	{
		this(timeout);
		//Allows User to pass in positive values for forward
		this.forwardSpeed = forwardSpeed;
	}
	
	public DriveStraightNavX(double forwardSpeed, boolean enableDistanceToWall)
	{
		this.forwardSpeed = forwardSpeed;
		this.enableDistanceToWall = enableDistanceToWall;
		if(enableDistanceToWall)
			distanceToWall = 10;
	}

	// Called just before this Command runs the first time
	protected void initialize()
	{
		//Robot.navXSource.reset();
		initialAngle = Robot.navXSource.getHeading();
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute()
	{
		double twistCorrection = Robot.navXSource.getTwistCorrection(initialAngle);
		double powerCorrection = Robot.ultrasonicSensorSource.distancePower(distanceToWall, forwardSpeed);
		Robot.driveTrain.driveCartesian(0, powerCorrection, twistCorrection, 0);
		
		SmartDashboard.putNumber("Initial Angle", initialAngle);
		SmartDashboard.putNumber("Twist Correction", twistCorrection);
		
		Robot.navXSource.report();
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished()
	{
		return isTimedOut() || Robot.ultrasonicSensorSource.atDistance(distanceToWall);
	}

	// Called once after isFinished returns true
	protected void end()
	{
		Robot.navX.navXController.disable();
		Robot.driveTrain.driveCartesian(0, 0, 0, 0);
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted()
	{
	}
}