from processors.DBAharvester import DBAHarvester
from processors.FBHarvester import FBHarvester
from processors.GOGharvester import GOGHarvester
from processors.GOGparser import GOGParser
from processors.DBAparser import DBAParser
from processors.util import *
import sys

sys.argv = [
    __file__,
    'arg1',
    'arg2'
]

if __name__ == '__main__':
    # ---- Gul og Gratis ----
    #rawdata = GOGHarvester.harvest(GOGHarvester)
    #GOGParser.parse(GOGParser, rawdata)
    # ---- DBA ----
    rawdata = DBAHarvester.harvest(DBAHarvester)
    DBAParser.parse(DBAParser, rawdata)
