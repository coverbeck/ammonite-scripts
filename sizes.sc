import ammonite.ops._
import ammonite.ops.ImplicitWd._

@arg(doc = "An optional extension to filter on")
@main
def main(extension: String = "" ) = {
  val files = ls.rec(pwd)
    .filter(_.isFile)
    .filter(extension.isEmpty || _.ext == extension)
    .map(os.size(_))
    .sum
  val size = "%,d".format(files)
  if (extension.isEmpty) println(s"Total files size $size")
  else println(s"Total files size for files with extension $extension: $size")
}
