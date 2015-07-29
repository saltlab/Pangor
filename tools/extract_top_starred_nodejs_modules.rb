
require 'rubygems'
require 'nokogiri'
require 'open-uri'

# USAGE:
# ruby extract_top_starred_nodejs_modules.rb > output.txt
#

LIMIT = 50

package_links = []

page = Nokogiri::HTML(open("https://www.npmjs.com/browse/star"))

while (package_links.size < LIMIT)
  package_links.concat page.css('.package-details a.name').map{ |e| e.attribute("href").value }.flatten

  next_page_uri = page.css('.pagination .next').attribute("href").value
  page = Nokogiri::HTML(open("https://www.npmjs.com#{next_page_uri}"))
end

for package_link in package_links[0...LIMIT]
  package_page = Nokogiri::HTML(open("https://www.npmjs.com#{package_link}"))
  git_repository = package_page.css('.sidebar .box a')[1].attribute("href").value + ".git"

  puts git_repository
end