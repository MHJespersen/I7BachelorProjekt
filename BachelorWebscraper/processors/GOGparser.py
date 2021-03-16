from processors.base.base import IParser
import logging
from processors.util import SALES_ITEM_TEMPLATE, write_to_csv
from bs4 import BeautifulSoup


class GOGParser(IParser):
    def __init__(self):
        super().__init__()

    def parse(self, harvested_items):
        parsed_items = []
        self.logger.info(type(self).__name__ + " started")

        for harvested_html in harvested_items:

            try:
                new_sales_item = dict(SALES_ITEM_TEMPLATE)
                soup = BeautifulSoup(harvested_html.decode('utf-8'), "html.parser")
                rows_data_table = soup.find(
                    'div', attrs={'class': '_3hASC02sd0cES_efOA6g8e _13One9bx_EDOPSf3nXim0R _2ePvufJ0RStyCR8EAhyJ7k'}
                )
                if not rows_data_table:
                    continue
                rows_data_table = rows_data_table.findAll(lambda tag: tag.name == 'dt' or tag.name == 'dd')
                new_sales_item['Overskrift'] = soup.find('h1').text.replace('"', '')
                pris_dict = soup.find(
                    'div', attrs={'class': '_1fR4KkNLWJ7OOZU9yP9H3m'}).text.replace('.', '').split(' ')

                table_dict = self.extract_from_table(rows_data_table)
                if table_dict.get('Str.', None) is None:
                    continue
                new_sales_item['Pris'] = [item for item in pris_dict if item.isdigit()][0]
                new_sales_item['Mærke'] = table_dict.get('Mærke', None)
                new_sales_item['Stand'] = table_dict.get('Varens stand', 'Ukendt')
                tommer_dict = table_dict.get('Str.', None).split(' ')
                new_sales_item['Tommer'] = [item for item in tommer_dict if item.isdigit()][0]
                parsed_items.append(new_sales_item)

            except Exception as e:
                self.logger.error("Harvesting stopped with error: " + str(e))
                # Evt skriv en state til en .txt fil og send data vi allerede har hentet til write_to_csv

        write_to_csv(parsed_items, "Predictering_Data_TV_GOG.csv", True)

    def extract_from_table(self, section):
        values = [str(items.string).replace('"', '') for items in section]
        table_dict = {item: values[index + 1] for index, item in enumerate(values) if index % 2 == 0}
        return table_dict
