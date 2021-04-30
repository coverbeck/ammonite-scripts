def fetchTaggedBuilds(token: String, offset: Int, tag: String): collection.mutable.ArrayBuffer[ujson.Value] = {
  pprint.pprintln(s"Querying with offset $offset")
  val response = requests.get(s"https://circleci.com/api/v1.1/project/github/dockstore/dockstore?limit=100&offset=$offset", headers = Map("Circle-Token" -> token))
  val json = ujson.read(response)
  val tags = json.arr.filter(_("vcs_tag") != ujson.Null).filter(_("vcs_tag").str.startsWith(tag))
  if (tags.size > 0) tags
  else fetchTaggedBuilds(token, offset+ 100, tag)
}

@main
def main(offset: Int = 2000, tag: String = "1.10.2") = {
  val token = sys.env("CIRCLE_CI_TOKEN")
  fetchTaggedBuilds(token, offset, tag)
}
