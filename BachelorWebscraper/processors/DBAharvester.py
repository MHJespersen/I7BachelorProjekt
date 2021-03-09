import requests
import logging
from processors.util import DBA_TV_LINK_M_SIDE_P_LOW, DBA_TV_LINK_M_SIDE_P_HIGH,\
    SALES_ITEM_TEMPLATE, CSV_COLUMNS, FILE_NAME_DBA, FILE_DIR, DATE_TIME_FORMAT, write_to_csv
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
            for i in range(pages_to_scrape[0], pages_to_scrape[1]+1):
                response = session.get(DBA_TV_LINK_M_SIDE_P_HIGH.format(str(i)), headers=payload)
                response.raise_for_status()
                soup = BeautifulSoup(response.content, "html.parser")
                announcelinks = soup.find_all('a', attrs={"class": "link-to-listing"})
                print("Changing to " + DBA_TV_LINK_M_SIDE_P_HIGH.format(str(i)))

                for announce in announcelinks:
                    time.sleep(0.2) # Tilføjet for at sætte farten ned efter DBA begyndte at lukke forbindelsen.
                    new_sales_item = dict(SALES_ITEM_TEMPLATE)
                    link = announce.get('href')

                    announce_response = session.get(link, headers=payload)
                    if announce_response.status_code is not 200:
                        logging.info("Harvesting failed %s, response status code contained",
                                     announce_response.status_code)
                        continue

                    ann_soup = BeautifulSoup(announce_response.content, "html.parser")
                    rows_data_table = ann_soup.find('table', attrs={'class': 'table'}).\
                        findAll(lambda tag: tag.name == 'tr')
                    table_dict = self.extract_from_table(rows_data_table)

                    if table_dict.get('Str. (tommer)', None) is None:
                        continue

                    new_sales_item['Overskrift'] = ann_soup.find('h1').text
                    new_sales_item['Pris'] = int(ann_soup.find('span', attrs={'class': 'price-tag'}).
                                                 text.split(' ')[0].replace('.', ''))
                    new_sales_item['Model'] = table_dict.get('Model', None)
                    new_sales_item['Type'] = table_dict.get('Type', None)
                    new_sales_item['Mærke'] = table_dict.get('Mærke', None)
                    new_sales_item['Stand'] = table_dict.get('Stand', 'Ukendt')
                    new_sales_item['Tommer'] = table_dict.get('Str. (tommer)', None)
                    new_sales_item['Tommer'] = new_sales_item['Tommer'].strip('"').split(' ')
                    new_sales_item['Tommer'] = [item for item in new_sales_item['Tommer'] if item.isdigit()][0]

                    harvested_items.append(new_sales_item)

            write_to_csv(harvested_items, FILE_NAME_DBA, newFile=False)

        except Exception as e:
            logging.warning("Harvesting stopped with error: " + str(e))
            # Evt skriv en state til en .txt fil og send data vi allerede har hentet til write_to_csv
        finally:
            session.close()

    @staticmethod
    def extract_from_table(rows):
        logging.info("Extracting info from table")
        tds = BeautifulSoup(str(rows), "html.parser")
        values = [tds.string for tds in tds.find_all('td')]
        values = [i for i in values if i is not None]
        table_dict = {item: values[index + 1] for index, item in enumerate(values) if index % 2 == 0}
        return table_dict
