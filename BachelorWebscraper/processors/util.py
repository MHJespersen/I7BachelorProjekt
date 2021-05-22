import logging
import os
import csv

# ----------- GENERAL -----------

SALES_ITEM_TEMPLATE = {'Overskrift': '', 'Mærke': '', 'Type': '', 'Model': '', 'Tommer': '', 'Stand': '', 'Pris': ''}
CSV_COLUMNS = ['Overskrift', 'Mærke', 'Type', 'Model', 'Tommer', 'Stand', 'Pris']
FILE_DIR = "C:\\Users\\mathi\\Aarhus Universitet\\Marcus Bech Lorenzen - 6.Semester\\Bachelor\\MachineLearning\\Data"
DATE_TIME_FORMAT = '%d-%m-%Y %H:%M:%S'
LOGGING_NAME = "ScraperLogging"


# Overskriv til ny fil -> set type til 'w' og sæt writer.writeheader()
# Tilføj til eksisterende data -> set type til 'a' og udkommenter writer.writeheader()
def write_to_csv(items, FILENAME, newFile=False):
    logger = logging.getLogger(LOGGING_NAME)
    logger.info("Saving data to .csv")
    filename = os.path.join(FILE_DIR, FILENAME)
    write_append_format = 'a' if not newFile else 'w'
    try:
        with open(filename, write_append_format, newline='', encoding='utf-8') as file:
            writer = csv.DictWriter(file, fieldnames=CSV_COLUMNS, delimiter='|')
            if newFile:
                writer.writeheader()
            for data in items:
                writer.writerow(data)
            logger.info("Saving data to .csv completed")
    except IOError as e:
        logger.warning("Failed to write .csv" + str(e))


# ----------- DBA -----------

dba_payload = {
    "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                  "(KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36",
    "Accept": "*/*",
    "Accept-Encoding": "gzip, deflate, br",
    "Connection": "keep-alive"
}

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
GOG_TV_LINK = "https://www.guloggratis.dk/elektronik/radio-tv/tv/?order=asc&price=100-&sort=price"
GOG_FJERNSYN_LINK_WPAGES = "https://www.guloggratis.dk/s/q-fjernsyn/?order=asc&price=100-&page={}&sort=price"
FILE_NAME_GOG = 'Predictering_Data_TV_GOG.csv'
