package org.usfirst.frc.team1165.robot.commands;

import java.util.Timer;
import java.util.TimerTask;

import org.usfirst.frc.team1165.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RunAgitator extends Command
{

	Timer timer;
	public static double power = -0.5;

	public RunAgitator()
	{
		// Use requires() here to declare subsystem dependencies
		requires(Robot.agitator);
		timer = new Timer();
	}

	// Called just before this Command runs the first time
	protected void initialize()
	{
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute()
	{
		if (Robot.shooter.isRunning() && Robot.servo.isServoUp())
			Robot.agitator.set(power);
		else
			Robot.agitator.set(0);
		SmartDashboard.putNumber("Agitator Power", Robot.agitator.getSpeed());
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished()
	{
		return false;
	}

	// Called once after isFinished returns true
	protected void end()
	{
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted()
	{
	}
}

class ReverseMotor extends TimerTask
{
	public void run()
	{
		RunAgitator.power *= -1;
	}
}