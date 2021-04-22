import requests
from processors.util import DBA_TV_LINK_M_SIDE_P_LOW, DBA_TV_LINK_M_SIDE_P_HIGH, SALES_ITEM_TEMPLATE, DATE_TIME_FORMAT,\
    dba_payload
from processors.base.base import IHarvester
from bs4 import BeautifulSoup
import time


class DBAHarvester(IHarvester):
    def __init__(self):
        IHarvester.__init__(self)
        super().__init__()

    def harvest(self):
        self.logger.info(type(self).__name__ + " started")
        session = requests.session()
        pages_to_scrape = [1, 5]
        try:
            for page in range(pages_to_scrape[0], pages_to_scrape[1] + 1):
                self.logger.info("Scraping page: " + str(page) + " with link: " +
                                 DBA_TV_LINK_M_SIDE_P_HIGH.format(str(page)))

                response = session.get(DBA_TV_LINK_M_SIDE_P_HIGH.format(str(page)), headers=dba_payload)
                response.raise_for_status()
                soup = BeautifulSoup(response.content, "html.parser")
                announcelinks = soup.find_all('a', attrs={"class": "link-to-listing"})

                for announce in announcelinks:
                    time.sleep(0.2)  # Tilføjet for at sætte farten ned efter DBA begyndte at lukke forbindelsen.
                    link = announce.get('href')

                    announce_response = session.get(link, headers=dba_payload)
                    if announce_response.status_code is not 200:
                        self.logger.info("Harvesting failed %s, response status code contained",
                                         announce_response.status_code)
                        continue
                    self.harvested_items.append(announce_response.content)

            return self.harvested_items

        except Exception as e:
            self.logger.error("Harvesting stopped with error: " + str(e))
            # Evt skriv en state til en .txt fil og send data vi allerede har hentet til write_to_csv
        finally:
            session.close()
