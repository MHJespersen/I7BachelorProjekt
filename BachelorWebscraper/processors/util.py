import logging
import os
import csv
from datetime import datetime
logging.info = print
# ----------- GENERAL -----------

SALES_ITEM_TEMPLATE = {'Overskrift': '', 'Mærke': '', 'Type': '', 'Model': '', 'Tommer': '', 'Stand': '', 'Pris': ''}
CSV_COLUMNS = ['Overskrift', 'Mærke', 'Type', 'Model', 'Tommer', 'Stand', 'Pris']
FILE_DIR = "C:\\Users\\mathi\\Aarhus Universitet\\Marcus Bech Lorenzen - 6.Semester\\Bachelor\\MachineLearning\\Data"
DATE_TIME_FORMAT = '%d-%m-%Y %H:%M:%S'


# Overskriv til ny fil -> set type til 'w' og sæt writer.writeheader()
# Tilføj til eksisterende data -> set type til 'a' og udkommenter writer.writeheader()
def write_to_csv(harvested_items, FILENAME, newFile=False):
    logging.info("Saving data to .csv")
    filename = os.path.join(FILE_DIR, FILENAME)
    write_append_format = 'a' if not newFile else 'w'
    try:
        with open(filename, write_append_format, newline='', encoding='utf-8') as file:
            writer = csv.DictWriter(file, fieldnames=CSV_COLUMNS, delimiter='|')
            if newFile:
                writer.writeheader()
            for data in harvested_items:
                writer.writerow(data)
            logging.info("Saving data to .csv completed: " + datetime.strftime(datetime.now(), DATE_TIME_FORMAT))
    except IOError as e:
        logging.warning("Failed to write .csv" + str(e))

# ----------- DBA -----------

DBA_LINK = "https://www.dba.dk/"
DBA_TV_LINK = "https://www.dba.dk/billede-og-lyd/tv-og-tilbehoer/tv/?soeg=tv"
DBA_TV_LINK_M_SIDE_P_LOW = "https://www.dba.dk/billede-og-lyd/tv-og-tilbehoer/tv/side-{}/?soeg=tv&sort=price&pris=(200-1000)"
DBA_TV_LINK_M_SIDE_P_HIGH = "https://www.dba.dk/billede-og-lyd/tv-og-tilbehoer/tv/side-{}/?soeg=tv&sort=price&pris=(1001-100000)"
FILE_NAME_DBA = 'Predictering_Data_TV_DBA.csv'

# ----------- Facebook -----------

FB_STARTPAGE_LINK = "https://da-dk.facebook.com/"
FB_LOGIN_LINK = 'https://da-dk.facebook.com/login/device-based/regular/login/'
EMAIL = 'Datasamperen@outlook.dk'
PASSWORD = 'D@t@samlerenpaafacebook'


# ----------- Gul&Gratis -----------

GOG_GENERAL_LINK = "https://www.guloggratis.dk/"
GOG_FJERNSYN_LINK = "https://www.guloggratis.dk/s/q-fjernsyn/"
GOG_TV_LINK = "https://www.guloggratis.dk/elektronik/radio-tv/tv/?order=asc&price=100-&sort=price"
GOG_TV_LINK_WPAGES = "https://www.guloggratis.dk/elektronik/radio-tv/tv/?order=asc&page={}&price=100-&sort=price"
GOG_FJERNSYN_LINK_WPAGES = "https://www.guloggratis.dk/s/q-fjernsyn/?order=asc&price=100-&page={}&sort=price"
FILE_NAME_GOG = 'Predictering_Data_TV_GOG.csv'




