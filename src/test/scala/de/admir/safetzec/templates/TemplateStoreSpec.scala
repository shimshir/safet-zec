package de.admir.safetzec.templates

import com.github.simplyscala.{MongoEmbedDatabase, MongodProps}
import de.admir.safetzec.models.EngineEnum._
import de.admir.safetzec.models.TemplateModel
import org.kohsuke.github.GitHub
import org.scalatest._
import reactivemongo.api.{MongoConnection, MongoDriver}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class TemplateStoreSpec extends FlatSpec with Matchers with MongoEmbedDatabase {
  var mongoProps: MongodProps = _
  val mongoPort = 12345

  "InMemoryTemplateStore" should "save and find templates" in {
    val inMemoryTemplateStore = new InMemoryTemplateStore()
    testTemplateStore(inMemoryTemplateStore)
  }

  "MongoTemplateStore" should "save and find templates" in {
    mongoProps = mongoStart(mongoPort)
    val driver = MongoDriver()
    val parsedUri = MongoConnection.parseURI(s"mongodb://localhost:$mongoPort")
    val connectionTry = parsedUri.map(driver.connection)

    val mongoTemplateStore = new MongoTemplateStore(connectionTry.get)
    testTemplateStore(mongoTemplateStore)
    mongoStop(mongoProps)
  }

  "GithubTemplateStore" should "save and find templates" in pendingUntilFixed {
    val anonGithub = GitHub.connectAnonymously()
    val githubTemplateStore = new GithubTemplateStore(
      github = anonGithub,
      repoName = "shimshir/safet-zec",
      templateFolder = "templates"
    )
    testTemplateStore(githubTemplateStore)
  }

  private def testTemplateStore(templateStore: TemplateStore) = {
    val templateModel1 = TemplateModel("testTemplate1.ftl", "<p>some string 1 ${whatever}</p>", FREEMARKER)
    val templateModel2 = TemplateModel("testTemplate2.ftl", "<p>some string 2</p>", FREEMARKER)
    Await.result(templateStore.saveTemplate(templateModel1), 5.seconds) shouldBe 'right
    Await.result(templateStore.saveTemplate(templateModel2), 5.seconds) shouldBe 'right
    Await.result(templateStore.findTemplate("testTemplate1.ftl"), 5.seconds) shouldBe Some(templateModel1)
    Await.result(templateStore.findTemplate("testTemplate2.ftl"), 5.seconds) shouldBe Some(templateModel2)
    Await.result(templateStore.findTemplate("someNoneExistingTemplateName"), 5.seconds) shouldBe None
    Await.result(templateStore.templates, 5.seconds) should contain only(templateModel1, templateModel2)
  }

}