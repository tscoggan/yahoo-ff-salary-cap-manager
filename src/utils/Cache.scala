package utils

import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger
import scala.collection.mutable

class Cache[T] {
  val log = Logger.getLogger(this.getClass)

  val cache: mutable.ListBuffer[T] = mutable.ListBuffer.empty

  def add(t: T): Unit = synchronized {
    cache += t
    log.info(s"Added to cache (new size = ${cache.size}): " + t)
  }

  def remove(t: T): Unit = synchronized {
    if (cache.contains(t)) {
      cache -= t
      log.info(s"Removed from cache (new size = ${cache.size}): " + t)
    } else log.info(s"WARNING: Cannot remove from cache, not found: " + t)
  }
}

object Cache {
  val oAuthTokens = new Cache[OAuthToken]
  val yqlQueries = new Cache[YQLQuery]
}