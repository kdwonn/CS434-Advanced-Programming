import java.io._

object IoCommon {
  def readStream(ois: ObjectInputStream): Packet = {
    try{
      ois.readObject().asInstanceOf[Packet]
    }catch{
      case e: ClassCastException => {println(e); Thread.sleep(10); readStream(ois)}
    }
  }
}
