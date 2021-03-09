import requests
import logging
from processors.util import DATE_TIME_FORMAT, GOG_GENERAL_LINK, GOG_TV_LINK, SALES_ITEM_TEMPLATE, GOG_TV_LINK_WPAGES,\
    write_to_csv, FILE_NAME_GOG, GOG_FJERNSYN_LINK_WPAGES
from processors.base.base import IHarvester
from bs4 import BeautifulSoup
from datetime import datetime


class GOGHarvester(IHarvester):
    def __init__(self):
        IHarvester.__init__(self)

    def harvest(self):
        harvested_items = []  # Flyt til interface
        logging.info("Harvesting started: " + datetime.strftime(datetime.now(), DATE_TIME_FORMAT))
        session = requests.session()

        try:
            return "test data"
            response = session.get(GOG_GENERAL_LINK)
            response_search = session.get(GOG_TV_LINK)
            soup = BeautifulSoup(response_search.content, "html.parser")
            number_of_pages = int(soup.find('span', attrs={"class": "_1F206US_ZOjnNOHRLFHIn8"}).text.split(" ")[1])

            for page in range(3, number_of_pages+1):
                print("Scraping page: " + str(page))
                subpage_response = session.get(GOG_FJERNSYN_LINK_WPAGES.format(page))
                soup = BeautifulSoup(subpage_response.content, "html.parser")
                announcelinks = soup.find_all('a', attrs={"class": "_1ZRGYCl9RQwYwmK8nrf10i"})

                for i, announce in enumerate(announcelinks):
                    new_sales_item = dict(SALES_ITEM_TEMPLATE)
                    link = announce.get('href')
                    announce_response = session.get(GOG_GENERAL_LINK + link)

                    if announce_response.status_code is not 200:
                        logging.info("Harvesting failed %s, response status code contained",
                                     announce_response.status_code)
                        continue

                    soup = BeautifulSoup(announce_response.content, "html.parser")
                    rows_data_table = soup.find(
                        'div', attrs={'class': '_3hASC02sd0cES_efOA6g8e _13One9bx_EDOPSf3nXim0R _2ePvufJ0RStyCR8EAhyJ7k'}
                    )
                    if not rows_data_table:
                        continue
                    rows_data_table = rows_data_table.findAll(lambda tag: tag.name == 'dt' or tag.name == 'dd')

                    table_dict = self.extract_from_table(rows_data_table)
                    if table_dict.get('Str.', None) is None:
                        continue
                    new_sales_item['Overskrift'] = soup.find('h1').text
                    pris_dict = soup.find(
                        'div', attrs={'class': '_1fR4KkNLWJ7OOZU9yP9H3m'}).text.replace('.', '').split(' ')
                    new_sales_item['Pris'] = [item for item in pris_dict if item.isdigit()][0]
                    new_sales_item['Mærke'] = table_dict.get('Mærke', None)
                    new_sales_item['Stand'] = table_dict.get('Varens stand', 'Ukendt')
                    tommer_dict = table_dict.get('Str.', None).strip('"').split(' ')
                    new_sales_item['Tommer'] = [item for item in tommer_dict if item.isdigit()][0]
                    harvested_items.append(new_sales_item)

            # Kan rykkes en indeksering til højre, hvis vi vil appende til csv-filen hver gang vi har scrapet en subpage
            # I så fald, husk at tømme harvested_iitems efter.
            write_to_csv(harvested_items, FILE_NAME_GOG, newFile=False)

        except Exception as e:
            logging.warning("Harvesting stopped with error: " + str(e))
            # Evt skriv en state til en .txt fil og send data vi allerede har hentet til write_to_csv

        finally:
            session.close()

    @staticmethod
    def extract_from_table(section):
        logging.info("Extracting info from table")
        values = [items.string for items in section]
        table_dict = {item: values[index + 1] for index, item in enumerate(values) if index % 2 == 0}
        return table_dict
