package sr.ice.server;

import Demo.IRadiator;
import com.zeroc.Ice.Current;

public class Heater implements IRadiator {

    private long temp;

    @Override
    public long adjustTemp(long temp, Current current) {
        if( temp < 0){
            this.temp -= temp;
        }else{
            this.temp += temp;
        }
        return this.temp;
    }
}
