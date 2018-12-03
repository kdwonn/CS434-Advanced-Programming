import java.io.{BufferedReader, InputStream, InputStreamReader, PrintStream}
import java.net.{ServerSocket, Socket}
import scala.collection.concurrent.TrieMap
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object ChatServer extends App{
  case class User(name: String, socket: Socket, textInStream: InputStream, textOutStream: PrintStream)
  val users = new TrieMap[String, User]()
  Future{launchServer}
  while(true){
    users foreach(p => broadcast(p._2))
    Thread.sleep(100)
  }

  def launchServer: Unit = {
    val ss = new ServerSocket(1026)
    println("[SERVER] started")
    while(true) {
      val socketAccept = ss.accept
      newUser(socketAccept)
    }
  }
  def newUser(socketAccept: Socket): Unit = {
    val textInStream = socketAccept.getInputStream
    val textoutStream = new PrintStream(socketAccept.getOutputStream)
    val name = Future {
      IoCommon.readOrWaitStream(textInStream)
    }
    name foreach { name => {
      println("[CLIENT] new user : " + name)
      users += name -> User(name, socketAccept, socketAccept.getInputStream, textoutStream)
    }
    }
  }
  def broadcast(user: User): Unit ={
    IoCommon.readOrNotStream(user.textInStream) foreach { i => {
      println("[SERVER] Broadcasting new message : " + i)
      users foreach (p => p._2.textOutStream.println(user.name + " : " + i))
    }
    }
  }
}
