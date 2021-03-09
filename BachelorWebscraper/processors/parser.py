from processors.base.base import IParser
import logging


class Parser(IParser):
    def __init__(self):
        super().__init__()

    def parse(self, data):
        logging.info("Parsing stared")
        data_ = data
