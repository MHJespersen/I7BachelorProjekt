from processors.base.base import IParser
import logging
from processors.util import SALES_ITEM_TEMPLATE, write_to_csv
from bs4 import BeautifulSoup


class DBAParser(IParser):
    def __init__(self):
        super().__init__()

    def parse(self, harvested_items):
        parsed_items = []
        logging.info("Parsing stared")

        for harvested_html in harvested_items:
            new_sales_item = dict(SALES_ITEM_TEMPLATE)
            soup = BeautifulSoup(harvested_html.decode('utf-8'), "html.parser")

            rows_data_table = soup.find('table', attrs={'class': 'table'}). \
                findAll(lambda tag: tag.name == 'tr')
            table_dict = self.extract_from_table(rows_data_table)

            if table_dict.get('Str. (tommer)', None) is None:
                continue

            new_sales_item['Overskrift'] = soup.find('h1').text
            new_sales_item['Pris'] = int(soup.find('span', attrs={'class': 'price-tag'}).
                                         text.split(' ')[0].replace('.', ''))
            new_sales_item['Model'] = table_dict.get('Model', None)
            new_sales_item['Type'] = table_dict.get('Type', None)
            new_sales_item['Mærke'] = table_dict.get('Mærke', None)
            new_sales_item['Stand'] = table_dict.get('Stand', 'Ukendt')
            new_sales_item['Tommer'] = table_dict.get('Str. (tommer)', None)
            new_sales_item['Tommer'] = new_sales_item['Tommer'].strip('"').split(' ')
            new_sales_item['Tommer'] = [item for item in new_sales_item['Tommer'] if item.isdigit()][0]
            parsed_items.append(new_sales_item)

        write_to_csv(parsed_items, "test.csv", True)

    @staticmethod
    def extract_from_table(rows):
        logging.info("Extracting info from table")
        tds = BeautifulSoup(str(rows), "html.parser")
        values = [tds.string for tds in tds.find_all('td')]
        values = [i for i in values if i is not None]
        table_dict = {item: values[index + 1] for index, item in enumerate(values) if index % 2 == 0}
        return table_dict
