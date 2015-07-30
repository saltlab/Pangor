
require 'rubygems'
require 'nokogiri'
require 'open-uri'
require 'github_api'

# USAGE:
# ruby extract_top_starred_nodejs_modules.rb > output.txt
#
# Extracts top <LIMIT> packages from most starred and most depended, merge them
# and remove duplicates
#

LIMIT = 50

#
# Classes
#
class Parser
  def self.get_top_packages(page_to_visit, limit)
    output = []

    while (true)
      page = Nokogiri::HTML(open(page_to_visit))
      output += page.css('.package-details a.name').map{ |e| e.attribute("href").value }.flatten

      if (output.size < limit)
        next_page_uri = page.css('.pagination .next').attribute("href").value
        page_to_visit = "https://www.npmjs.com#{next_page_uri}"
      else
        break
      end
    end

    return output[0...limit]
  end
end


class Package
  attr_reader :url, :downloads_month, :stargazers

  def initialize(url, downloads_month)
    @url = url
    @downloads_month = downloads_month
  end

  def to_s
    "#{url},#{@downloads_month}"
  end
end


#
# Main
# 

package_links = []

package_links += Parser.get_top_packages("https://www.npmjs.com/browse/star", LIMIT)
package_links += Parser.get_top_packages("https://www.npmjs.com/browse/depended", LIMIT)

for package_link in package_links.uniq
  package_page = Nokogiri::HTML(open("https://www.npmjs.com#{package_link}"))

  git_url = package_page.css('.sidebar .box a')[1].attribute("href").value + ".git"
  downloads_month = package_page.css('.sidebar .box .monthly-downloads')[0].text

  package = Package.new(git_url, downloads_month)
  puts package

  # be nice to npmjs.com
  sleep(1) 
end