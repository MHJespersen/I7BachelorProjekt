from abc import abstractmethod
from processors.util import LOGGING_NAME
from processors.logging import logging
import logging


class IHarvester:
    def __init__(self):
        self.harvested_items = []
        self.logger = logging.getLogger(LOGGING_NAME)

    @abstractmethod
    def harvest(self):
        """
        Interface for implementing harvester, to scrape data from web applications
        """
        return self.harvested_items


class IParser:
    def __init__(self):
        self.logger = logging.getLogger(LOGGING_NAME)
        self.parsed_items = []

    @abstractmethod
    def parse(self, data):
        """
        Interface for implementing parser to convert harvested data
        """
        return self.parsed_items
