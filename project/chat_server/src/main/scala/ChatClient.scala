import java.io.{ObjectInputStream, ObjectOutputStream, PrintStream}
import java.net.Socket

import scala.concurrent.Future
import scala.io.StdIn.readLine
import scala.concurrent.ExecutionContext.Implicits.global

object ChatClient extends App {
  val port = 1026
  val socket = new Socket("localhost", 1026)
  val inputStream = socket.getInputStream
  val outputStream = socket.getOutputStream

  lazy val ois = new ObjectInputStream(inputStream)
  lazy val oos = new ObjectOutputStream(outputStream)

  print("Enter username : ")
  val name = readLine()
  oos.writeObject(TextPacket(name, name))

  println("Welcome! Chatting Started")
  Future{
    while(true){
      IoCommon.readStream(ois) match{
        case TextPacket(m, f) => println(f + " : " + m)
        case SoundPacket(m, f) => ???
      }
    }
  }
  while(true){
    print(">>")
    oos.writeObject(TextPacket(readLine(), name))
  }
}
