import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.Socket

class User(val name: String, val socket: Socket, val inStream: ObjectInputStream, val outStream: ObjectOutputStream){
  def writePacket(packet: Packet): Unit = {
    IoCommon.writeStream(outStream, packet)
  }
  def readPacket: Packet = {
    IoCommon.readStream(inStream)
  }
  def checkIncomingPacket(server: ChatServer.type): Unit = {
    while (true) {
      println("[SERVER] [" + name + "] reading incoming packet")
      server.addBroadcastPacket(readPacket)
      println("[SERVER] [" + name + "] new message added to broadcast array")
    }
  }
}
