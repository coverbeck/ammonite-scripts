import ammonite.ops._
import ammonite.ops.ImplicitWd._

@main
def main(org: String) = {
  val rawRepos = requests.get(s"https://api.github.com/orgs/$org/repos?per_page=200")
  val repos = ujson.read(rawRepos).arr.map(_("ssh_url").str)
  repos.foreach(repo => {
    println(s"Cloning $repo")
    %git("clone", repo)
  })
}
