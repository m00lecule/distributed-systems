package messanger

import java.net.DatagramSocket

class ClientReadUDP(override val socket: DatagramSocket) extends UDPEndPoint(socket) with UDPRead with Displayable {}
