from processors.DBAharvester import DBAHarvester
from processors.GOGharvester import GOGHarvester
from processors.GOGparser import GOGParser
from processors.DBAparser import DBAParser

import sys

sys.argv = [
    __file__,
    'arg1',
    'arg2'
]

if __name__ == '__main__':
    # ---- DBA ----
    dba_harvester = DBAHarvester()
    rawdata = dba_harvester.harvest()
    # Test Raw data
    dba_parser = DBAParser()
    dba_parser.parse(rawdata)


    # ---- Gul og Gratis ----
    #gog_harvester = GOGHarvester()
    #rawdata = gog_harvester.harvest()
    #gog_parser = GOGParser()
    # Test Raw data
    #gog_parser.parse(rawdata)

