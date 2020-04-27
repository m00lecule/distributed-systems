
#ifndef CALC_ICE
#define CALC_ICE

module Demo
{
  ["java:type:java.util.LinkedList<String>"]
  sequence<string> names;

  interface INames
  {
     names getNames();
  };

  struct Position{
    long x;
    long y;
  };

  interface IMoving
  {
    Position move(Position vector);
    Position getPosition();
  };

  interface IRadiator{
    long adjustTemp(long temp);
  };

  interface IVoiceControl{
    names process(names words);
  };

};

#endif
