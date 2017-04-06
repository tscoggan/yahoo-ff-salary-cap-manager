package utils

import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger

object CommonUtils {

  private val log = Logger.getLogger(this.getClass)
  
  def ignoreErrors(code: => Unit) = {
    try {
      code
    } catch {
      case e: Exception => log.error(e)
    }
  }
  
}