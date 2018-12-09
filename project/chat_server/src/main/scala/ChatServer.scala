import java.io._
import java.net.{ServerSocket, Socket}
import java.util

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object ChatServer extends App{
  var noNameCount = 0
  val users = new TrieMap[String, User]()
  val packetsToBroadcast = new util.ArrayList[Packet]()

  Future{launchServer}
  Future{checkThenBroadcast()}
  while(true){
    Thread.sleep(10)
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
    Future {
      IoCommon.readStream(ois).from
    } foreach { name => {
        println("[SERVER] new user : " + name)
        val newUser = new User(name, socketAccept, ois, oos)
        users += name -> newUser
        Future{newUser.checkIncomingPacket(this)}
      }
    }
  }

  def addBroadcastPacket(packet: Packet): Unit = this.synchronized{
    packetsToBroadcast.add(packet)
  }

  def checkThenBroadcast(): Unit ={
    while(true){
      println("... checking ..." + System.currentTimeMillis())
      if (packetsToBroadcast.isEmpty) () else broadcast()
      Thread.sleep(100)
    }
  }

  def broadcast(): Unit ={
    val packet = packetsToBroadcast.get(0)
    for((name, user) <- users; if packet.from != name){
      user.writePacket(packet)
    }
    packetsToBroadcast.remove(0)
    println("[SERVER] Broadcast new packet : " + packet + " from " + packet.from)
  }
}
