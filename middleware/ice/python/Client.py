import sys, Ice
import pygen.Demo

with Ice.initialize(sys.argv) as communicator:
    base = communicator.stringToProxy("cart/michal :tcp localhost -p 10000")
    printer = Demo.IMovingPrx.checkedCast(base)
    if not printer:
        raise RuntimeError("Invalid proxy")

    printer.getPosition()