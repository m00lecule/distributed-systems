package messanger

import java.io.{DataInputStream, DataOutputStream, InputStream, ObjectInputStream, ObjectOutputStream}
import java.util.Scanner

import messanger.messages.Message

class ClientReadTCP(override val inputSocketStream: InputStream) extends TCPRead with Displayable {}
