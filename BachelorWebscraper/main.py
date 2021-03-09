from processors.DBAharvester import DBAHarvester
from processors.FBHarvester import FBHarvester
from processors.GOGharvester import GOGHarvester
from processors.parser import Parser
from processors.util import *
import sys

sys.argv = [
    __file__,
    'arg1',
    'arg2'
]

if __name__ == '__main__':
    #DBAHarvester.harvest(DBAHarvester)
    rawdata = GOGHarvester.harvest(GOGHarvester)
    Parser.parse(Parser, rawdata)
