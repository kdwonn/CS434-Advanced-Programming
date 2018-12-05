import java.io._

object IoCommon {
  def readOrNotStream(textInStream: InputStream): Option[Packet] = {
    val ois = new ObjectInputStream(textInStream)
    try{
      val packet = ois.readObject().asInstanceOf[Packet]
      Some(packet)
    }catch{
      case e: ClassCastException | IOException => None
    }
  }
  def readOrWaitStream(textInStream: InputStream): Packet = {
    val ois = new ObjectInputStream(textInStream)
    def waitTilGet(ois: ObjectInputStream): Packet = {
      val packet = ois.readObject()
      try{
        if(packet == null) {Thread.sleep(100);waitTilGet(ois)} else packet.asInstanceOf[Packet]
      }
      catch{
        case e: ClassCastException => waitTilGet(ois)
      }
    }
    waitTilGet(ois)
  }
}
