from processors.DBAharvester import DBAHarvester
from processors.GOGharvester import GOGHarvester
from processors.GOGparser import GOGParser
from processors.DBAparser import DBAParser
from urllib.request import urlopen
from bs4 import BeautifulSoup
import unittest
from processors.util import DBA_TV_LINK, GOG_TV_LINK, dba_payload
import sys
import requests

sys.argv = [
    __file__,
    'arg1',
    'arg2'
]

# -- Unit tests ---
# https://www.tutorialspoint.com/python_web_scraping/python_web_scraping_testing_with_scrapers.htm


class UnitTests(unittest.TestCase):
    DBARes = None
    GOGRes = None
    bsGOG = None
    bsDBA = None

    def setUp(self):
        UnitTests.DBARes = requests.get(DBA_TV_LINK, headers=dba_payload)
        UnitTests.GOGRes = requests.get(GOG_TV_LINK)
        UnitTests.bsDBA = BeautifulSoup(UnitTests.DBARes.content, 'html.parser')
        UnitTests.bsGOG = BeautifulSoup(UnitTests.GOGRes.content, 'html.parser')

    def testTiles(self):
        self.assertEqual('Find Tv i TV - Køb brugt på DBA ', UnitTests.bsDBA.find('title').get_text(),
                         "Title for DBA has changed. There might be changes on the website")

        self.assertEqual('TV | Nye og brugte fjernsyn billigt til salg på GulogGratis.dk',
                         UnitTests.bsGOG.find('title').get_text(),
                         "Title for GOG has changed. There might be changes on the website")

    def testConnection(self):
        self.assertEqual(UnitTests.DBARes.status_code, 200, "DBA unittest did not return status_code 200")
        self.assertEqual(UnitTests.GOGRes.status_code, 200, "GOG unittest did not return status_code 200")


if __name__ == '__main__':
    # ---- Unit tests ----
    # Prevent unit tests to read command line arguments and run unittests
    del sys.argv[1:]
    unittest.main()

    # ---- DBA ----
    #dba_harvester = DBAHarvester()
    #rawdata = dba_harvester.harvest()
    # dba_parser = DBAParser()
    # dba_parser.parse(rawdata)

    # ---- Gul og Gratis ----
    #gog_harvester = GOGHarvester()
    #rawdata = gog_harvester.harvest()
    #gog_parser = GOGParser()
    #gog_parser.parse(rawdata)
