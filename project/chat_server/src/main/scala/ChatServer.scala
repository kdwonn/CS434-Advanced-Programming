import java.io._
import java.net.{ServerSocket, Socket}
import java.util

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object ChatServer extends App{
  var noNameCount = 0
  val users = new TrieMap[String, User]()
  val packetToBroadcast = new util.ArrayList[Packet]()

  Future{launchServer}
  while(true){
    println("...")
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
    lazy val ois = new ObjectInputStream(socketAccept.getInputStream)
    lazy val oos = new ObjectOutputStream(socketAccept.getOutputStream)
    val name = Future {
      IoCommon.readStream(ois).from
    } foreach { name => {
        println("[SERVER] new user : " + name)
        users += name -> new User(name, socketAccept, ois, oos)
      }
    }
  }

  def broadcast(user: User): Unit ={
    val newPacket = user.readPacket
    println("[SERVER] Broadcasting new packet : " + newPacket + " from " + user.name)
    users foreach (p => p._2.outStream.writeObject(newPacket))
  }
}
