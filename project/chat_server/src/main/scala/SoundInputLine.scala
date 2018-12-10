import javax.sound.sampled._
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectOutputStream
import java.util.zip.GZIPOutputStream


trait SoundInputLine {
  def chatClient: ChatClient.type
  def audioSource: TargetDataLine
}

class MicInputLine(val chatClient: ChatClient.type) extends SoundInputLine {
  val threshold: Int = 0

  val audioSource: TargetDataLine = {
    val info = new DataLine.Info(classOf[TargetDataLine], null)
    AudioSystem.getLine(info).asInstanceOf[TargetDataLine]
  }

  def listenThenSend(): Unit = {
    val buffer = new Array[Byte](Sound.defaultLength)
    micStart()
    while(true){
      if (audioSource.available() >= Sound.defaultLength){
        audioSource.read(buffer, 0, Sound.defaultLength)
        chatClient.sendPacket(compressedSoundPacket(buffer))
      } else Thread.sleep(10)
    }
  }

  private def micStart(): Unit = {
    audioSource.open(Sound.defaultFormat)
    audioSource.start()
  }

  private def checkVolume(buffer: Array[Byte]): Boolean ={
    buffer.foldLeft(0)(_ +math.abs(_))/buffer.length >= threshold
  }

  private def compressedSoundPacket(buffer: Array[Byte]): Packet = {
    val from = chatClient.name
    if (checkVolume(buffer)) {
      val b = new ByteArrayOutputStream()
      new GZIPOutputStream(b).write(buffer)
      SoundPacket(Sound(b.toByteArray), from)
    } else EmptyPacket(from)
  }
}
