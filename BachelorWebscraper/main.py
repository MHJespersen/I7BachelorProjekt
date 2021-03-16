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
    # ---- Gul og Gratis ----
    gog_harvester = GOGHarvester()
    gog_parser = GOGParser()
    rawdata = gog_harvester.harvest()
    gog_parser.parse(rawdata)

    # ---- DBA ----
    #dba_harvester = DBAHarvester()
    #dba_parser = DBAParser()
    #rawdata = dba_harvester.harvest()
    #dba_parser.parse(rawdata)
