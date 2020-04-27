package sr.ice.server;

import Demo.IMoving;
import Demo.Position;
import com.zeroc.Ice.Current;

import java.util.Random;

public class ReverseCart implements IMoving {

    private Position pos = new Position(new Random().nextLong() % 128, new Random().nextLong() % 128);
    @Override
    public Position move(Position vector, Current current) {

        System.out.println(vector.x + " " + vector.y);
        this.pos.x -= vector.x;
        this.pos.y -= vector.y;
        System.out.println("aftermath " + this.pos.x + " " + this.pos.y);
        return this.pos;
    }

    @Override
    public Position getPosition(Current current) {
        return this.pos;
    }
}