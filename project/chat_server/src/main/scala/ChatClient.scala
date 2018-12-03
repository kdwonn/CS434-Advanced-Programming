import java.io.PrintStream
import java.net.Socket

import scala.concurrent.Future
import scala.io.StdIn.readLine
import scala.concurrent.ExecutionContext.Implicits.global

object ChatClient extends App {
  val port = 1026
  val socket = new Socket("localhost", 1026)
  val textInputStream = socket.getInputStream
  val textOutputStream = new PrintStream(socket.getOutputStream)

  print("Enter username : ")
  val name = readLine()
  textOutputStream.println(name)

  println("Welcome! Chatting Started")
  Future{
    while(true){
      println(IoCommon.readOrWaitStream(textInputStream))
    }
  }
  while(true){
    print(">>")
    textOutputStream.println(readLine())
  }
}
