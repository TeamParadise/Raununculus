package org.usfirst.frc.team1165.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.UsbCameraInfo;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team1165.robot.commands.AutoCrossBaseline;
import org.usfirst.frc.team1165.robot.commands.AutoGearAwayBoilerBlueOrRightGearOnly;
import org.usfirst.frc.team1165.robot.commands.AutoGearAwayBoilerRedOrLeftGearOnly;
import org.usfirst.frc.team1165.robot.commands.AutoPlaceGearCenter;
import org.usfirst.frc.team1165.robot.commands.AutoPlaceGearCenterAndLeave;
import org.usfirst.frc.team1165.robot.commands.AutoPlaceGearCenterAndShootBlue;
import org.usfirst.frc.team1165.robot.commands.AutoPlaceGearCenterAndShootRed;
import org.usfirst.frc.team1165.robot.commands.AutoPlaceGearCenterNoVision;
import org.usfirst.frc.team1165.robot.commands.AutoShootAndGearBlue;
import org.usfirst.frc.team1165.robot.commands.AutoShootAndGearRed;
import org.usfirst.frc.team1165.robot.subsystems.Agitator;
import org.usfirst.frc.team1165.robot.subsystems.Climber;
import org.usfirst.frc.team1165.robot.subsystems.DriveTrain;
import org.usfirst.frc.team1165.robot.subsystems.EncoderPID;
import org.usfirst.frc.team1165.robot.subsystems.NavX_MXP_PID;
import org.usfirst.frc.team1165.robot.subsystems.NavX_MXP_Source;
import org.usfirst.frc.team1165.robot.subsystems.Pickup;
import org.usfirst.frc.team1165.robot.subsystems.ServoSystem;
import org.usfirst.frc.team1165.robot.subsystems.Shooter;
import org.usfirst.frc.team1165.robot.subsystems.UltrasonicPID;
import org.usfirst.frc.team1165.robot.subsystems.UltrasonicSensorSource;
import org.usfirst.frc.team1165.robot.subsystems.VisionGRIPSource;
import org.usfirst.frc.team1165.robot.subsystems.VisionPID;

import Util.GripContoursPipeline;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot
{
	public static final DriveTrain driveTrain = new DriveTrain();
	public static final UltrasonicSensorSource ultrasonicSensorSource = new UltrasonicSensorSource();
	public static final UltrasonicPID ultrasonicPID = new UltrasonicPID();
	public static final NavX_MXP_Source navXSource = new NavX_MXP_Source();
	public static final ServoSystem servo = new ServoSystem();
	public static final Shooter shooter = new Shooter();
	public static final NavX_MXP_PID navX = new NavX_MXP_PID();
	public static final Climber climber = new Climber();
	public static final EncoderPID encoder = new EncoderPID();
	public static final GripContoursPipeline pipeline = new GripContoursPipeline();
	public static final VisionPID visionPID = new VisionPID();
	
	public static final Pickup pickup = new Pickup();
	public static final Agitator agitator = new Agitator();

	public static int usbCameraImageWidth = 640;
	public static int usbCameraImageHeight = 480;
	private final int usbCameraFrameRate = 10;
	public static UsbCamera usbCameras[];

	public static VisionGRIPSource visionGRIP = null;
	public static OI oi;

	Command autonomousCommand;
	SendableChooser autoChooser;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit()
	{
		oi = new OI();
		SmartDashboard.putBoolean("Blue Alliance" , DriverStation.getInstance().getAlliance() == Alliance.Red);
		autoChooser = new SendableChooser();
		autoChooser.addDefault("Place Gear On Center(Forwards)", new AutoPlaceGearCenter());
		autoChooser.addDefault("Place Gear On Center No Vision(Forwards)", new AutoPlaceGearCenterNoVision());
		autoChooser.addObject("Cross Baseline (Forwards)", new AutoCrossBaseline());
		autoChooser.addObject("Place Gear Only Left Side (Forwards)", new AutoGearAwayBoilerRedOrLeftGearOnly());
		autoChooser.addObject("Place Gear Only Right Side (Backwards)", new AutoGearAwayBoilerBlueOrRightGearOnly());
		/*autoChooser.addDefault("Place Gear On Center(Forwards) and leave", new AutoPlaceGearCenterAndLeave());
		autoChooser.addDefault("Place Gear On Center(Forwards) and shoot Blue", new AutoPlaceGearCenterAndShootBlue());
		autoChooser.addDefault("Place Gear On Center(Forwards) and shoot Red", new AutoPlaceGearCenterAndShootRed());
		autoChooser.addObject("Shoot and Gear Autonomous Red(Backwards)", new AutoShootAndGearRed());
		autoChooser.addObject("Shoot and Gear Autonomous Blue(Backwards)", new AutoShootAndGearBlue());
		autoChooser.addObject("Place Gear Only Red(Forwards)", new AutoGearAwayBoilerRedOrLeftGearOnly());
		autoChooser.addObject("Place Gear Only Blue(Backwards)", new AutoGearAwayBoilerBlueOrRightGearOnly());*/
		// chooser.addObject("My Auto", new MyAutoCommand());

		SmartDashboard.putNumber(RobotMap.getShooterWheelString, 3100); //3500);
		SmartDashboard.putNumber(RobotMap.getFeederWheelString, 2600); //2400);
		SmartDashboard.putNumber(RobotMap.getShooterWheelValue, 0.5);
		SmartDashboard.putNumber(RobotMap.getFeederWheelValue, 0.5);
		
		SmartDashboard.putData("Auto mode", autoChooser);
		SmartDashboard.putBoolean("Camera Crashed", false);

		// Do not delete this line
		try
		{
			CameraServer.getInstance();
			initializeUsbCameras();
		} catch (Exception e)
		{
			SmartDashboard.putBoolean("Camera Crashed", true);
		}
	}

	private void initializeUsbCameras()
	{
		UsbCameraInfo infos[] = UsbCamera.enumerateUsbCameras();
		usbCameras = new UsbCamera[infos.length];

		SmartDashboard.putNumber("No. Of cameras", infos.length);
		for (int i = 0; i < usbCameras.length; i++)
		{
			usbCameras[i] = new UsbCamera("USB" + i, infos[i].path);
			usbCameras[i].setResolution(usbCameraImageWidth, usbCameraImageHeight);
			usbCameras[i].setFPS(usbCameraFrameRate);
			System.out.println("Created USB camera " + i + ": " + usbCameras[i].getPath());
			CameraServer.getInstance().startAutomaticCapture(usbCameras[i]);
		}
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit()
	{

	}

	@Override
	public void disabledPeriodic()
	{
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit()
	{
		autonomousCommand = (Command)autoChooser.getSelected();
		//final String autoCommandPlusAlliance = autoChooser.getSelected().toString()+DriverStation.getInstance().getAlliance();
		//autonomousCommand = (Command)autoCommandPlusAlliance.;
		
		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		// schedule the autonomous command (example)
		visionGRIP = new VisionGRIPSource();
		if (autonomousCommand != null)
			autonomousCommand.start();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic()
	{
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit()
	{
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (autonomousCommand != null)
			autonomousCommand.cancel();
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic()
	{
		if (visionGRIP != null) visionGRIP.visionThreadStop(); //we are done with vision and try catch will prevent exceptions
		//kill the thread to drop roborio cpu usage
		Scheduler.getInstance().run();
		Timer.delay(0.005);
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic()
	{
		LiveWindow.run();
	}
}