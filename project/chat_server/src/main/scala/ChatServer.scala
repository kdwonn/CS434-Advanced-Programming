import java.io.{BufferedReader, InputStream, InputStreamReader, PrintStream}
import java.net.{ServerSocket, Socket}
import scala.collection.concurrent.TrieMap


object ChatServer extends App{
  case class User(name: String, socket: Socket, inStream: InputStream, outStream: PrintStream)
  val users = new TrieMap[String, User]()

  def launchServer = {
    val ss = new ServerSocket(1026)
    val socketAccept = ss.accept
    val textInStream = new BufferedReader(new InputStreamReader(socketAccept.getInputStream))
    val textoutStream = new PrintStream(socketAccept.getOutputStream)
  }
}
