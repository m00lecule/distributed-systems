package sr.ice.server;

import Demo.IRadiator;
import com.zeroc.Ice.Current;

import java.util.Random;

public class HighSpecificHeat implements IRadiator {

    private long temp = new Random().nextLong();

    @Override
    public long adjustTemp(long temp, Current current) {
        return this.temp;
    }
}
