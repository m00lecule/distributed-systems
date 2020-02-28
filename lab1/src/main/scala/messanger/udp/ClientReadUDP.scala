package messanger.udp

import java.net.DatagramSocket

import messanger.Displayable

class ClientReadUDP(override val socket: DatagramSocket) extends UDPEndPoint(socket) with UDPRead with Displayable {}
