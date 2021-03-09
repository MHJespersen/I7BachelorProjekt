from abc import abstractmethod
import logging
from sys import stderr

# Quick fix
logging.info = print


class IHarvester:
    def __init__(self):
        logging.basicConfig(stream=stderr, level=logging.DEBUG)
        #self.harvested_items = []

    @abstractmethod
    def harvest(self):
        """
        Interface for implementing harvester, to scrape data from web applications
        """
        #return self.harvested_items


class IParser:
    def __init__(self):
        logging.basicConfig(stream=stderr, level=logging.DEBUG)
        self.parsed_items = []

    @abstractmethod
    def parse(self, data):
        """
        Interface for implementing parser to convert harvested data
        """
        return self.parsed_items
