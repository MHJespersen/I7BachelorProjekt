import requests
from processors.util import DATE_TIME_FORMAT, GOG_GENERAL_LINK, GOG_TV_LINK, GOG_FJERNSYN_LINK_WPAGES
from processors.base.base import IHarvester
from bs4 import BeautifulSoup


class GOGHarvester(IHarvester):
    def __init__(self):
        IHarvester.__init__(self)
        super().__init__()

    def harvest(self):
        self.logger.info(type(self).__name__ + " started")
        session = requests.session()
        try:
            response = session.get(GOG_GENERAL_LINK)
            response_search = session.get(GOG_TV_LINK)
            soup = BeautifulSoup(response_search.content, "html.parser")
            number_of_pages = int(soup.find('span', attrs={"class": "_1F206US_ZOjnNOHRLFHIn8"}).text.split(" ")[1])

            for page in range(1, number_of_pages+1):
                self.logger.info("Scraping page: " + str(page) + " with link: " +
                                 GOG_FJERNSYN_LINK_WPAGES.format(page))
                subpage_response = session.get(GOG_FJERNSYN_LINK_WPAGES.format(page))
                soup = BeautifulSoup(subpage_response.content, "html.parser")
                announcelinks = soup.find_all('a', attrs={"class": "_1ZRGYCl9RQwYwmK8nrf10i"})

                for i, announce in enumerate(announcelinks):
                    link = announce.get('href')
                    announce_response = session.get(GOG_GENERAL_LINK + link)

                    if announce_response.status_code is not 200:
                        self.logger.warning("Harvesting failed %s, response status code contained",
                                            announce_response.status_code)
                        continue
                    self.harvested_items.append(announce_response.content)

            return self.harvested_items

        except Exception as e:
            self.logger.error("Harvesting stopped with error: " + str(e))
            # Evt skriv en state til en .txt fil og send data vi allerede har hentet til write_to_csv

        finally:
            session.close()
