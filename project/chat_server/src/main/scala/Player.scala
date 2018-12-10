import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.util
import java.util.zip.GZIPInputStream
import javax.sound.sampled.{AudioSystem, DataLine, SourceDataLine}
import org.apache.commons.io.IOUtils
import scala.util.Try

class Player {
  val packetsToPlay = new util.ArrayList[Packet]()
  val audioOutLine: SourceDataLine = {
    val info = new DataLine.Info(classOf[SourceDataLine], Sound.defaultFormat)
    AudioSystem.getLine(info).asInstanceOf[SourceDataLine]
  }
  val emptyBytes: Array[Byte] = Array.fill(Sound.defaultLength)(0)

  private def playerStart(): Unit = {
    try{
      audioOutLine.open(Sound.defaultFormat)
      audioOutLine.start()
    } catch{
      case e: Exception => println("CANNOT OPEN SPEAKER")
    }
  }

  def checkThenPlay() = {
    playerStart()
    while(true){
      if (packetsToPlay.isEmpty) () else play()
      Thread.sleep(10)
    }
  }

  private def play(): Unit = {
    val packet = packetsToPlay.get(0)
    packetsToPlay.remove(0)

    val bytesToPlay = packet match {
      case SoundPacket(m, f) => {
        // println("PLAYER soundpacket received")
        Try {
          val zipStream = new GZIPInputStream (new ByteArrayInputStream (m.bytes) )
          IOUtils.toByteArray (zipStream)
        }.toOption.getOrElse(emptyBytes)
      }
      case EmptyPacket(_) => emptyBytes
    }

    // println("PLAYER writing bytes to speaker" + bytesToPlay.mkString(" "))
    audioOutLine.write(bytesToPlay, 0, Sound.defaultLength)
  }
}
