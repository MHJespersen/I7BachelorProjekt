from processors.DBAharvester import DBAHarvester
from processors.FBHarvester import FBHarvester
from processors.harvester import GOGHarvester
from processors.util import *
import sys

sys.argv = [
    __file__,
    'arg1',
    'arg2'
]

if __name__ == '__main__':
    #DBAHarvester.harvest(DBAHarvester)
    GOGHarvester.harvest(GOGHarvester)
