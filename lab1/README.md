# Simple TCP/UDP/Multicast chat 
So the chat is example of one to many comunication, every participant will receive your message

## Architecture
For every client there are 4 additional threads created (reading from tcp, udp and multicast socket and another to process user input and send it)

On the server side, there is only one thread that manages UDP traffic - all clients communicates with this endpoint, but for n clients there is pool of n socket and that indicates n thread that must process those endpoints.

The mutlticast communication doesnt require server service to handle the traffic - group is defined by 224.168.1.124 multicast IP address

### build
go to /lab1 directory and type sbt, then run runMain messanger.client.Client to run client side 

### How to send message

The input linke has regex format M|U|A|'' {.*} - the message content part

The prefix part indicates with protocol will be used
- M stands for multicast socket
- U stands for UDP
- if there is no prefix specified then TCP is used
- to send ASCII ART message just type A


### Code review
I have introduced two traits that are frequently used Displayable and Processable

##### Displayable
Its job is to read object from something and display it - this trait was introduced to DRY code on client side 


##### Processable
No doubt, that this trait encapsulates behavior of class that reads and provisions data - mostly used on server side to handle multiple TCP connections


### ASCII Art
Here is example of A kolanko message:
```
 ***    ****      ****      ***           *****     ***     ***  ***    ****      ****     
 ***   ****     ********    ***           *****     ****    ***  ***   ****     ********   
 ***   ***     **********   ***           *****     *****   ***  ***   ***     **********  
 ***  ***      ***    ***   ***          *** ***    *****   ***  ***  ***      ***    ***  
 *** ***      ***      ***  ***          *** ***    ******  ***  *** ***      ***      *** 
 *******      ***      ***  ***          *** ***    *** **  ***  *******      ***      *** 
 ********     ***      ***  ***         ***   ***   *** *** ***  ********     ***      *** 
 **** ***     ***      ***  ***         *********   ***  ** ***  **** ***     ***      *** 
 ***   ***    ***      ***  ***         *********   ***  ******  ***   ***    ***      *** 
 ***   ***     ***    ***   ***        ***********  ***   *****  ***   ***     ***    ***  
 ***    ***    **********   *********  ***     ***  ***   *****  ***    ***    **********  
 ***     ***    ********    *********  ***     ***  ***    ****  ***     ***    ********   
 ***     ***      ****      ********* ***       *** ***     ***  ***     ***      ****     
```
