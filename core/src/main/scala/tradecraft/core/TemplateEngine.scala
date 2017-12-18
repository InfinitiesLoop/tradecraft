package tradecraft.core

import java.io.{Reader, StringReader, StringWriter}

import freemarker.cache.TemplateLoader
import freemarker.template.{Configuration, TemplateExceptionHandler}

abstract class TemplateEngine(val templates: Map[String, String]) {
  def render(template: String, viewData: AnyRef): String
}

class FreeMarkerTemplateEngine(templates: Map[String, String]) extends TemplateEngine(templates) {
  class SimpleTemplateLoader() extends TemplateLoader {
    override def getLastModified(templateSource: scala.Any): Long = -1L
    override def getReader(templateSource: scala.Any, encoding: String): Reader =
      new StringReader(templateSource.asInstanceOf[String])
    override def closeTemplateSource(templateSource: scala.Any): Unit = {}
    override def findTemplateSource(name: String): AnyRef = templates.getOrElse(name, null)
  }

  val cfg = new Configuration(Configuration.VERSION_2_3_27)
  cfg.setDefaultEncoding("UTF-8")
  cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER)
  cfg.setLogTemplateExceptions(false)
  cfg.setWrapUncheckedExceptions(true)
  cfg.setTemplateLoader(new SimpleTemplateLoader())

  def render(template: String, viewData: AnyRef): String = {
    val writer = new StringWriter()
    try {
      cfg.getTemplate(template).process(viewData, writer)
      writer.flush()
      writer.toString
    } finally {
      writer.close()
    }
  }
}
