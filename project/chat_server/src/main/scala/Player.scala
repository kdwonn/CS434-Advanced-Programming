import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.util
import java.util.zip.GZIPInputStream
import javax.sound.sampled.{AudioSystem, DataLine, SourceDataLine}
import org.apache.commons.io.IOUtils
import scala.util.Try

class Player(val name: String) {
  val packetsToPlay = new util.ArrayList[Packet]()
  val audioOutLine: SourceDataLine = {
    val info = new DataLine.Info(classOf[SourceDataLine], Sound.defaultFormat)
    AudioSystem.getLine(info).asInstanceOf[SourceDataLine]
  }
  val emptyBytes: Array[Byte] = Array.fill(Sound.defaultLength)(0)

  def playerStart(): Unit = {
    audioOutLine.open(Sound.defaultFormat)
    audioOutLine.start()
  }

  def checkThenPlay() = {
    while(true){
      if (packetsToPlay.isEmpty) () else play()
      Thread.sleep(10)
    }
  }

  def play(): Unit = {
    val packet = packetsToPlay.get(0)
    packetsToPlay.remove(0)

    val bytesToPlay = packet match {
      case SoundPacket(m, f) => {
        Try {
          val zipStream = new GZIPInputStream (new ByteArrayInputStream (m.bytes) )
          IOUtils.toByteArray (zipStream)
        }.toOption.getOrElse(emptyBytes)
      }
      case EmptyPacket(_) => emptyBytes
    }

    audioOutLine.write(bytesToPlay, 0, Sound.defaultLength)
  }
}
