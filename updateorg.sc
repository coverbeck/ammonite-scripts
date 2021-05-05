import ammonite.ops._
import ammonite.ops.ImplicitWd._

// Does a git pull on each direct child directory of the current directory
@main
def main() = {
    ls! pwd filter(_.isDir) foreach(f => {
         println(s"Processing $f")
         scala.util.Try(%.git("pull", "--prune")(f))
         })
}
