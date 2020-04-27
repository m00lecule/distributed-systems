package sr.ice.server;

import Demo.INames;
import com.zeroc.Ice.Current;

import java.util.LinkedList;
import java.util.List;


public class NameHolder implements INames {

    private LinkedList<String> names;

    public NameHolder(LinkedList<String> names){
        this.names = names;
    }

    @Override
    public LinkedList<String> getNames(Current current) {
        return names;
    }

}
