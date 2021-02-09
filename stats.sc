import upickle.default.{read, ReadWriter, macroRW}

case class StarredUser(id: Int)
object StarredUser {
    implicit val rw: ReadWriter[StarredUser] = macroRW
}
case class Organization(name: String, id: Int, starredUsers: Seq[StarredUser])
object Organization {
    implicit val rw: ReadWriter[Organization] = macroRW
}

case class Entry(entryPath: String, entryType: String, id: Int, versionName: String)
object Entry {
    implicit val rw: ReadWriter[Entry] = macroRW
}

case class Collection(id: Int, displayName: String, entries: Seq[Entry])
object Collection {
    implicit val rw: ReadWriter[Collection] = macroRW
}

val apiBase = "https://dockstore.org/api"
val orgsApi = s"$apiBase/organizations"

def doOrg(org: Organization) = {
    println(s"${org.name} has  ${org.starredUsers.size} stars")
    val collections = requests.get(s"$orgsApi/${org.id}/collections")
    val cos = read[Seq[Collection]](collections)

    val entries = cos
      .map(_.id)
      .map(id => read[Collection](requests.get(s"$orgsApi/${org.id}/collections/${id}")))
      .flatMap(collection => collection.entries)
      .toSet // Filter out any entries in more than one collection

    println(s"It has ${cos.size} collections with ${entries.size} unique entries")

    val stars = entries
      .toSeq // Otherwise this maps to a Set, which will filter out duplicate numbers of stars
      .map(entry => ujson.read(requests.get(s"$apiBase/workflows/${entry.id}/starredUsers")).arr.size)
      .sum

    println(s"The total number of stars for all entries is ${stars}")
}
@arg(doc = "A star counter for organizations in Dockstore")
@main
def main(org: String = "bdcatalyst") = {
    val orgs = requests.get(orgsApi)
    val organizations = read[Seq[Organization]](orgs)
    doOrg(organizations.find(_.name == org).get)
}
