package sr.ice.server;

import Demo.IVoiceControl;
import com.zeroc.Ice.Current;

import java.util.Collections;
import java.util.List;

public class ScatterBrainer implements IVoiceControl {
    @Override
    public List<String> process(List<String> words, Current current) {
        Collections.shuffle(words);
        return words;
    }
}
