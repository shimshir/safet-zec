import com.github.simplyscala.{MongoEmbedDatabase, MongodProps}
import de.admir.safetzec.rendering.{MongoTemplateStore, TemplateStore}
import org.scalatest._
import reactivemongo.api.{MongoConnection, MongoDriver}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


class TemplateStoreSpec extends FlatSpec with Matchers with MongoEmbedDatabase with BeforeAndAfter {
  var mongoProps: MongodProps = _
  val mongoPort = 12345

  before {
    mongoProps = mongoStart(mongoPort)
  }

  after {
    mongoStop(mongoProps)
  }

  "MongoTemplateStore" should "save and find templates" in {
    val driver = MongoDriver()
    val parsedUri = MongoConnection.parseURI(s"mongodb://localhost:${mongoPort}")
    val connectionTry = parsedUri.map(driver.connection)

    val mongoTemplateStore = new MongoTemplateStore(connectionTry.get)
    testTemplateStore(mongoTemplateStore)
  }

  private def testTemplateStore(templateStore: TemplateStore) = {
    Await.result(templateStore.saveTemplate("testTemplate1", "<p>some string 1 ${whatever}</p>"), 2.seconds)
    Await.result(templateStore.saveTemplate("testTemplate2", "<p>some string 2</p>"), 2.seconds)

    val assertionNoneFut = templateStore.findTemplate("nonExistingName").map { templateDocOpt =>
      templateDocOpt shouldBe None
    }

    val assertion1Fut = templateStore.findTemplate("testTemplate1").map { templateDocOpt =>
      templateDocOpt shouldBe Some("<p>some string 1 ${whatever}</p>")
    }

    val assertion2Fut = templateStore.findTemplate("testTemplate2").map { templateDocOpt =>
      templateDocOpt shouldBe Some("<p>some string 2</p>")
    }

    Await.result(assertionNoneFut, 2.seconds)
    Await.result(assertion1Fut, 2.seconds)
    Await.result(assertion2Fut, 2.seconds)
  }

}