import requests
import logging
from processors.util import DBA_TV_LINK_M_SIDE_P_LOW, DBA_TV_LINK_M_SIDE_P_HIGH, SALES_ITEM_TEMPLATE, DATE_TIME_FORMAT
from processors.base.base import IHarvester
from bs4 import BeautifulSoup
from datetime import datetime
import time


class DBAHarvester(IHarvester):
    def __init__(self):
        IHarvester.__init__(self)

    def harvest(self):
        harvested_items = [] # Flyt til interface
        logging.info("Harvesting started: " + datetime.strftime(datetime.now(), DATE_TIME_FORMAT))
        session = requests.session()
        pages_to_scrape = [1, 41]

        try:
            payload = {
                "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                              "(KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36",
                "Accept": "*/*",
                "Accept-Encoding": "gzip, deflate, br",
                "Connection": "keep-alive"
            }
            for page in range(pages_to_scrape[0], pages_to_scrape[1]+1):
                print("Scraping page: " + str(page))

                response = session.get(DBA_TV_LINK_M_SIDE_P_HIGH.format(str(page)), headers=payload)
                response.raise_for_status()
                soup = BeautifulSoup(response.content, "html.parser")
                announcelinks = soup.find_all('a', attrs={"class": "link-to-listing"})
                print("Changing to " + DBA_TV_LINK_M_SIDE_P_HIGH.format(str(page)))

                for announce in announcelinks:
                    time.sleep(0.2) # Tilføjet for at sætte farten ned efter DBA begyndte at lukke forbindelsen.
                    new_sales_item = dict(SALES_ITEM_TEMPLATE)
                    link = announce.get('href')

                    announce_response = session.get(link, headers=payload)
                    if announce_response.status_code is not 200:
                        logging.info("Harvesting failed %s, response status code contained",
                                     announce_response.status_code)
                        continue
                    harvested_items.append(announce_response.content)
                    if page > 2:
                        break
                break

            return harvested_items

        except Exception as e:
            logging.warning("Harvesting stopped with error: " + str(e))
            # Evt skriv en state til en .txt fil og send data vi allerede har hentet til write_to_csv
        finally:
            session.close()
