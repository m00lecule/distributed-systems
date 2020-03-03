package messanger.socket

import java.net.DatagramSocket

import messanger.Displayable

class DatagramSocketRead extends DatagramObjectOperations[DatagramSocket] with Displayable {
  override val socket = new DatagramSocket
}
