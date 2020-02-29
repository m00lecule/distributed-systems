package messanger.udp

import java.net.DatagramSocket

import messanger.Displayable

class ClientReadUDP(override val socket: DatagramSocket) extends DatagramRead with Displayable {}
