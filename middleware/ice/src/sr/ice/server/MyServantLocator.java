package sr.ice.server;
import Demo.IMoving;
import Demo.IRadiator;
import Demo.IVoiceControl;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ServantLocator;

import java.util.*;

public class MyServantLocator implements ServantLocator
{
    private ObjectAdapter adapter;
    private LinkedList<String> allocatedNames = new LinkedList<>();

    public MyServantLocator(ObjectAdapter adapter){
        this.adapter = adapter;
    }

    public ServantLocator.LocateResult locate(com.zeroc.Ice.Current current)
    {
        String name = current.id.name;

        System.out.println("[ser]" + name);

        switch (current.id.category){
            case "cart":
                putName(current);
                IMoving c = new Cart();
                return bindToAdapter(c, current.id);
            case "rcart":
                putName(current);
                IMoving c2 = new ReverseCart();
                return bindToAdapter(c2, current.id);
            case "mcart":
                putName(current);
                IMoving c3 = new MagnifiCart();
                return bindToAdapter(c3, current.id);
            case "cool":
                putName(current);
                IRadiator i1 = new Cooler();
                return bindToAdapter(i1, current.id);
            case "heat":
                putName(current);
                IRadiator i2 = new Heater();
                return bindToAdapter(i2, current.id);
            case "spec":
                putName(current);
                IRadiator i3 = new HighSpecificHeat();
                return bindToAdapter(i3, current.id);
            case "cap":
                putName(current);
                IVoiceControl v1 = new Capitalizer();
                return bindToAdapter(v1, current.id);
            case "low":
                putName(current);
                IVoiceControl v2 = new Lowerizer();
                return bindToAdapter(v2, current.id);
            case "silly":
                putName(current);
                IVoiceControl v3 = new ScatterBrainer();
                return bindToAdapter(v3, current.id);
            case "names":
                if(current.id.name.equals("all")) {
                    System.out.println(allocatedNames);
                    NameHolder nh = new NameHolder(allocatedNames);
                    return bindToAdapter(nh, current.id);
                }
            default:
                return new ServantLocator.LocateResult();
        }
    }

    private void putName(com.zeroc.Ice.Current current){
        allocatedNames.add(current.id.name);
    }

    private ServantLocator.LocateResult bindToAdapter(com.zeroc.Ice.Object obj, Identity id){
        adapter.add(obj,id);
        return  new ServantLocator.LocateResult(obj, null);
    }

    public void finished(com.zeroc.Ice.Current current, com.zeroc.Ice.Object servant, java.lang.Object cookie)
    {
    }

    public void deactivate(String category)
    {
    }
}