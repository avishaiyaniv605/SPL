package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private final int _duration;
	private final int _speed;
	private Timer _timer;
	private int _currTick;

	/**
	 * TimeService object constructor
	 * @param speed represents the time to wait between ticks, initializes _speed
	 * @param duration represents the number of ticks in the program initializes _duration
	 */
	public TimeService(int speed, int duration) {
		super("TimeService");
		_speed = speed;
		_duration = duration;

	}

	@Override
	protected void initialize() {
		// subscription to terminate broadcast
		// the TimeService actually sends it so it will invoked itself from its message loop
		subscribeBroadcast(TerminateBroadcast.class, ev ->{
			terminate();
		});

		// timer of java invokes the TimeService so it will send tick and terminate broadcast
		_timer = new Timer();
		_timer.schedule(new TimerTask() {
			@Override
			public void run() {
				_currTick ++;
				if (_currTick == _duration) {
					sendBroadcast(new TerminateBroadcast());
					_timer.cancel();
				}
				else
					sendBroadcast(new TickBroadcast(_currTick));
			}
		}, _speed, _speed);
	}
}
