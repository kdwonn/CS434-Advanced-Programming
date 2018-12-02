import java.io.{BufferedReader, InputStream, InputStreamReader, PrintStream}
import java.net.{ServerSocket, Socket}
import scala.collection.concurrent.TrieMap
import scala.concurrent.Future


object ChatServer extends App{
  case class User(name: String, socket: Socket, textInStream: InputStream, textOutStream: PrintStream)
  val users = new TrieMap[String, User]()
  Future{launchServer}
  while(true){
    users foreach(p => broadcast(p._2))
  }

  def launchServer: Unit = {
    val ss = new ServerSocket(1026)
    while(true) {
      val socketAccept = ss.accept
      newUser(socketAccept)
    }
  }
  def newUser(socketAccept: Socket): Unit = {
    val textInStream = socketAccept.getInputStream
    val textoutStream = new PrintStream(socketAccept.getOutputStream)
    val name = Future {
      readOrWaitStream(textInStream)
    }
    name foreach { name =>
      users += name -> User(name, socketAccept, socketAccept.getInputStream, textoutStream)
    }
  }
  def readOrNotStream(textInStream: InputStream): Option[String] = {
    val br = new BufferedReader(new InputStreamReader(textInStream))
    if(br.ready()) Some(br.readLine()) else None
  }
  def readOrWaitStream(textInStream: InputStream): String = {
    val br = new BufferedReader(new InputStreamReader(textInStream))
    while(!br.ready()) Thread.sleep(10)
    br.readLine()
  }
  def broadcast(user: User): Unit ={
    readOrNotStream(user.textInStream) foreach { i =>
      users foreach (p => p._2.textOutStream.println(user.name + " : " + i))
    }
  }
}
