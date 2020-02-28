package messanger.tcp

import java.io.InputStream

import messanger.Displayable

class ClientReadTCP(override val inputSocketStream: InputStream) extends TCPRead with Displayable {}
