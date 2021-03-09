import logging
from processors.util import DATE_TIME_FORMAT, LOGGING_NAME
from datetime import datetime

# https://docs.python.org/3/howto/logging-cookbook.html

logger = logging.getLogger(LOGGING_NAME)
logger.setLevel(logging.INFO)
# create file handler which logs even debug messages
fh = logging.FileHandler('scraper.log')
fh.setLevel(logging.DEBUG)
# create console handler with a higher log level
ch = logging.StreamHandler()
ch.setLevel(logging.ERROR)
# create formatter and add it to the handlers
formatter = logging.Formatter(f'{datetime.now().strftime(DATE_TIME_FORMAT)} - %(name)s - %(levelname)s - %(message)s')
fh.setFormatter(formatter)
ch.setFormatter(formatter)
# add the handlers to the logger
logger.addHandler(fh)
logger.addHandler(ch)
