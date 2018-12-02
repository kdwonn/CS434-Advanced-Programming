import java.io.{BufferedReader, InputStream, InputStreamReader, PrintStream}
import java.net.{ServerSocket, Socket}

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future


object ChatServer extends App{
  case class User(name: String, socket: Socket, inStream: InputStream, outStream: PrintStream)
  val users = new TrieMap[String, User]()
  Future{launchServer}

  def launchServer = {
    val ss = new ServerSocket(1026)
    while(true) {
      val socketAccept = ss.accept
      newUser(socketAccept)
    }
  }
  def newUser(socketAccept: Socket) = {
    val textInStream = new BufferedReader(new InputStreamReader(socketAccept.getInputStream))
    val textoutStream = new PrintStream(socketAccept.getOutputStream)
  }
}
