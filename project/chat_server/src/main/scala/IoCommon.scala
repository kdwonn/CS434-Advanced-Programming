import java.io.{BufferedReader, InputStream, InputStreamReader}

object IoCommon {
  def readOrNotStream(textInStream: InputStream): Option[String] = {
    val br = new BufferedReader(new InputStreamReader(textInStream))
    if(br.ready()) Some(br.readLine()) else None
  }
  def readOrWaitStream(textInStream: InputStream): String = {
    val br = new BufferedReader(new InputStreamReader(textInStream))
    while(!br.ready()) Thread.sleep(10)
    br.readLine()
  }
}
