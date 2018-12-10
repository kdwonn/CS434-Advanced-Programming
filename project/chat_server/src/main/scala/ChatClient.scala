import java.io.{ObjectInputStream, ObjectOutputStream, PrintStream}
import java.net.Socket
import java.util

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future
import scala.io.StdIn.readLine
import scala.concurrent.ExecutionContext.Implicits.global

object ChatClient extends App {
  val port = 1026
  val socket = new Socket("localhost", 1026)
  val players = new TrieMap[String, Player]()

  val is = socket.getInputStream
  val os = socket.getOutputStream
  lazy val inStream = new ObjectInputStream(is)
  lazy val outStream = new ObjectOutputStream(os)

  print("Enter username : ")
  val name = readLine()
  sendPacket(TextPacket(name, name))

  Future{decodeIncomingPacket}
  val mic = new MicInputLine(this)
  Future{mic.listenThenSend()}

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
        case p @ _ => play(p)
      }
      Thread.sleep(10)
    }
  }

  def play(packet: Packet): Unit = {
    val playerName = packet.from
    val playerOrNot = players.get(playerName)
    val player = if (playerOrNot.isEmpty) {
      println("Add new PLAYER for [" + playerName + "]")
      val newPlayer = new Player
      players += playerName -> newPlayer
      Future{newPlayer.checkThenPlay()}
      newPlayer
    } else playerOrNot.get
    player.packetsToPlay.add(packet)
  }
}
