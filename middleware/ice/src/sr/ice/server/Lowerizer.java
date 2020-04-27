package sr.ice.server;

import Demo.IVoiceControl;
import com.zeroc.Ice.Current;

import java.util.List;
import java.util.stream.Collectors;

public class Lowerizer implements IVoiceControl {
    @Override
    public List<String> process(List<String> words, Current current) {
        return words.stream().map(name -> Character.toLowerCase(name.charAt(0)) + name.substring(1)).collect(Collectors.toList());
    }
}
