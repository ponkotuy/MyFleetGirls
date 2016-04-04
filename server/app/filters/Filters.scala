package filters

import javax.inject.Inject

import akka.stream.Materializer
import play.api.http.HttpFilters
import play.api.mvc.{EssentialFilter, Filter, RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

class Filters @Inject()(cors: CorsFilter) extends HttpFilters {
  override def filters: Seq[EssentialFilter] = Seq(cors)
}

class CorsFilter @Inject()(implicit val mat: Materializer, ex: ExecutionContext) extends Filter {
  def apply(f: (RequestHeader) => Future[Result])(rh: RequestHeader): Future[Result] = {
    val result = f(rh)
    result.map { r =>
      r.withHeaders("Access-Control-Allow-Origin" -> "http://ponkotuy.github.io")
    }
  }
}

