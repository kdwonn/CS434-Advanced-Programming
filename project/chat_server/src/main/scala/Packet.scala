import javax.sound.sampled.AudioFormat

trait Packet{
  def from : String
}

class Sound(val bytes: Array[Byte]) extends Serializable
object Sound{
  def apply(bytes: Array[Byte]): Sound = new Sound(bytes)
  val defaultFormat = new AudioFormat(11025f, 8, 1, true, true);
  val defaultLength = 1200
}

case class TextPacket(message: String, from: String) extends Packet with Serializable{
  override def toString: String = "[TEXT] : " + message
}

case class SoundPacket(message: Sound, from: String) extends Packet with Serializable{
  override def toString: String = "[SOUND]" + message.bytes.mkString(" ")
}

case class EmptyPacket(from: String) extends Packet with Serializable{
  override def toString: String = "[EMPTY]"
}
