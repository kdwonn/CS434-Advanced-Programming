import javax.sound.sampled._
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectOutputStream
import java.util.zip.GZIPOutputStream


trait SoundInputLine {
  def chatClient: ChatClient.type
  def audioSource: TargetDataLine
}

class MicInputLine(val chatClient: ChatClient.type) extends SoundInputLine  {
  val threshold: Double = 0.5

  val audioSource: TargetDataLine = {
    //println("MIC initializing")
    val info = new DataLine.Info(classOf[TargetDataLine], null)
    AudioSystem.getLine(info).asInstanceOf[TargetDataLine]
  }

  def listenThenSend(): Unit = {
    val buffer = new Array[Byte](Sound.defaultLength)
    micStart()
    //println("MIC OPENED")
    while(true){
      if (audioSource.available() >= Sound.defaultLength){
        while(audioSource.available() >= Sound.defaultLength){
          audioSource.read(buffer, 0, Sound.defaultLength)
        }
        //println("MIC READ BUFFER " + buffer.mkString(" "))
        chatClient.sendPacket(compressedSoundPacket(buffer))
      } else Thread.sleep(10)
    }
  }

  private def micStart(): Unit = {
    try {
      audioSource.open(Sound.defaultFormat)
      audioSource.start()
    } catch {
      case e: Exception => println("CANNOT OPEN MIC " + e);e.printStackTrace()
    }
  }

  private def checkVolume(buffer: Array[Byte]): Boolean ={
    buffer.foldLeft(0)(_ +math.abs(_))/buffer.length >= threshold
  }

  private def compressedSoundPacket(buffer: Array[Byte]): Packet = {
    val from = chatClient.name
    if (checkVolume(buffer)) {
      val b = new ByteArrayOutputStream(buffer.length)
      val zipped = new GZIPOutputStream(b)
      zipped.write(buffer)
      zipped.close()
      val zippedBytes = b.toByteArray
      b.close()
      //println("MIC compressed array " + zippedBytes.mkString(" "))
      SoundPacket(Sound(zippedBytes), from)
    } else EmptyPacket(from)
  }
}
