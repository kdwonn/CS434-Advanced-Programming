import java.io._

object IoCommon {
  def readStream(ois: ObjectInputStream): Packet = {
    try{
      ois.readObject().asInstanceOf[Packet]
    }catch{
      case e: ClassCastException => println(e); Thread.sleep(10); readStream(ois)
    }
  }
  def writeStream(oos: ObjectOutputStream, packet: Packet): Unit = {
    try {
      oos.writeObject(packet)
    }catch {
      case e: IOException => println(e); Thread.sleep(10); writeStream(oos, packet)
    }
  }
}
