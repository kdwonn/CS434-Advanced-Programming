import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.Socket
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class User(val name: String, val socket: Socket, val inStream: ObjectInputStream, val outStream: ObjectOutputStream){
  def writePacket(packet: Packet): Unit = {
    try {
      outStream.writeObject(packet)
    }catch {
      case _ => {Thread.sleep(10); writePacket(packet)}
    }
  }
  def readPacket: Packet = {
    try {
      inStream.readObject().asInstanceOf[Packet]
    }catch {
      case e: ClassCastException => {println(e); Thread.sleep(10); readPacket}
    }
  }
  def checkIncomingPacket(server: ChatServer.type): Unit = {
    while (true) {
      println("[SERVER] [" + name + "] reading incoming packet")
      server.addBroadcastPacket(readPacket)
      println("[SERVER] [" + name + "] new message added to broadcast array")
    }
  }
}
