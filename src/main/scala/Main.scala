import javax.swing.{JFileChooser, JOptionPane}
import java.io.File
import java.nio.file.{Paths, Files}
import javax.imageio.ImageIO

object Main extends App {
  def askUseClass(): Boolean = {
    val options = Array[Object]("class", "height")
    JOptionPane.showOptionDialog(
      null,
      "Как обрабатывать вертикальные?",
      "Выбор всегда есть",
      JOptionPane.DEFAULT_OPTION,
      JOptionPane.QUESTION_MESSAGE,
      null,
      options,
      null) == 0
  }

  def getDirJpegs(dir: File): List[File] =
    dir.listFiles.filter(_.isFile).filter { f =>
      val name = f.getName.toLowerCase
      name.endsWith(".jpg") || name.endsWith(".jpeg")
    }.toList

  def getImageParams(imgFile: File): (Int, Int) = {
    val img = ImageIO.read(imgFile)
    (img.getWidth, img.getHeight)
  }

  def makeHtml(useClass: Boolean)(imgFile: File): String = {
    val name = imgFile.getName.split('.').dropRight(1).mkString(".")
    val (w, h) = getImageParams(imgFile)
    val appendClass = if (h > w && useClass) " vertical" else ""
    val appendHeight = if (useClass) "" else  s"height='${h}'"
    s"""<a href="photos/otchet/${name}.jpg" class="img-wrap" data-imagelightbox="f">
     |  <div class="grid__item${appendClass}">
     |    <img src="photos/otchet/thumb/${name}.bpg" alt="img${name}" ${appendHeight} />
     |  </div>
     |</a>""".stripMargin
  }

  val chooser = new JFileChooser
  chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
  if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
    val dir = chooser.getSelectedFile
    val html = getDirJpegs(dir).map(makeHtml(askUseClass())).mkString("\n")
    val htmlFile = dir.toPath.resolve("listing.html")
    Files.write(htmlFile, html.getBytes)

    ()
  } else {
    ()
  }
}
