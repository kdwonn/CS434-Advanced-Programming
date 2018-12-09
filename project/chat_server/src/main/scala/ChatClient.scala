import java.io.{ObjectInputStream, ObjectOutputStream, PrintStream}
import java.net.Socket

import scala.concurrent.Future
import scala.io.StdIn.readLine
import scala.concurrent.ExecutionContext.Implicits.global

object ChatClient extends App {
  val port = 1026
  val socket = new Socket("localhost", 1026)
  val is = socket.getInputStream
  val os = socket.getOutputStream

  lazy val inStream = new ObjectInputStream(is)
  lazy val outStream = new ObjectOutputStream(os)

  print("Enter username : ")
  val name = readLine()
  sendPacket(TextPacket(name, name))

  //Future{new MicInputLine(this).listenThenSend()}

  Future{decodeIncomingPacket}

  while(true){
    print(">>")
    sendPacket(TextPacket(readLine(), name))
  }

  def sendPacket(packet: Packet): Unit = this.synchronized {
    IoCommon.writeStream(outStream, packet)
  }

  def decodeIncomingPacket: Unit = {
    println("Welcome! Chatting Started")
    while(true){
      IoCommon.readStream(inStream) match{
        case TextPacket(m, f) => println(f + " : " + m)
        case SoundPacket(m, f) => ???
        case EmptyPacket(f) => ???
      }
      Thread.sleep(10)
    }
  }
}
