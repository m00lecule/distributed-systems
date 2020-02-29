package messanger.multicast

import java.net.MulticastSocket

import messanger.Displayable
import messanger.udp.{DatagramRead}

class ClientReadMulticast(override val socket: MulticastSocket) extends DatagramRead with Displayable
