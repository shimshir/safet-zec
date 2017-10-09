package de.admir.safetzec.templates

import com.github.simplyscala.{MongoEmbedDatabase, MongodProps}
import de.admir.safetzec.models.EngineEnum._
import de.admir.safetzec.models.TemplateModel
import org.kohsuke.github.extras.OkHttpConnector
import org.kohsuke.github.{GitHub, GitHubBuilder, HttpConnector}
import org.scalatest._
import reactivemongo.api.{MongoConnection, MongoDriver}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

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
    val parsedUri = MongoConnection.parseURI(s"mongodb://localhost:$mongoPort")
    val connectionTry = parsedUri.map(driver.connection)

    val mongoTemplateStore = new MongoTemplateStore(connectionTry.get)
    testTemplateStore(mongoTemplateStore)
  }

  "GithubTemplateStore" should "save and find templates" in {
    val github = GitHub.connectUsingOAuth("c58daa849575c3aa9a431ddb73893cfd7d2991f2")
    val githubTemplateStore = new GithubTemplateStore(
      github,
      "shimshir/safet-zec",
      "templates"
    )
    testTemplateStore(githubTemplateStore)
  }

  private def testTemplateStore(templateStore: TemplateStore) = {
    val templateModel1 = TemplateModel("testTemplate1.ftl", "<p>some string 1 ${whatever}</p>", FREEMARKER)
    val templateModel2 = TemplateModel("testTemplate2.ftl", "<p>some string 2</p>", FREEMARKER)
    Await.result(templateStore.saveTemplate(templateModel1), 5.seconds)
    Await.result(templateStore.saveTemplate(templateModel2), 5.seconds)

    val assertionNoneFut = templateStore.findTemplate("nonExistingName").map { templateDocOpt =>
      templateDocOpt shouldBe None
    }

    val assertion1Fut = templateStore.findTemplate("testTemplate1.ftl").map { templateDocOpt =>
      templateDocOpt shouldBe Some(templateModel1)
    }

    val assertion2Fut = templateStore.findTemplate("testTemplate2.ftl").map { templateDocOpt =>
      templateDocOpt shouldBe Some(templateModel2)
    }

    Await.result(assertionNoneFut, 5.seconds)
    Await.result(assertion1Fut, 5.seconds)
    Await.result(assertion2Fut, 5.seconds)
  }

}