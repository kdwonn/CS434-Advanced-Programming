import javax.sound.sampled.AudioFormat

trait Packet{
  def message : Any
  def from : String
  def getMessage : Any
}

class Sound(val bytes: List[Byte]) extends Serializable
object Sound{
  def apply(bytes: List[Byte]): Sound = new Sound(bytes)
  val defaultFormat = new AudioFormat(11025f, 8, 1, true, true);
  val defaultLength = 1200
}

case class TextPacket(message: String, from: String) extends Packet with Serializable{
  override def getMessage: String = message
  override def toString: String = "[TEXT] : " + message
}

case class SoundPacket(message: Sound, from: String) extends Packet with Serializable{
  override def getMessage: Sound = message
  override def toString: String = "[SOUND]"
}