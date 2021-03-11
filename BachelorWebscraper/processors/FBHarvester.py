import requests
import logging
from processors.util import FB_LOGIN_LINK, FB_STARTPAGE_LINK, SALES_ITEM_TEMPLATE, CSV_COLUMNS, FILE_DIR,\
    DATE_TIME_FORMAT, PASSWORD, EMAIL
from processors.base.base import IHarvester
from bs4 import BeautifulSoup
from datetime import datetime
import browser_cookie3


class FBHarvester(IHarvester):
    def __init__(self):
        IHarvester.__init__(self)

    def harvest(self):
        logging.info("Harvesting started: " + datetime.strftime(datetime.now(), DATE_TIME_FORMAT))

        # Create session so we keep state and cookies
        session = requests.session()
        # Get cookies from chrome browser as this simplifies login a lot
        cookies = browser_cookie3.chrome(domain_name='.facebook.com')

        response = session.get(FB_STARTPAGE_LINK, cookies=cookies)
        soup = BeautifulSoup(response.content, "html.parser")

        # find values necessary for form data - these change with each session
        jazoest = soup.find('input', attrs={"name": "jazoest"})['value']
        lsd = soup.find('input', attrs={"name": "lsd"})['value']
        cuid = soup.find('a', attrs={'title': 'Mike Tennant'})['href']

        form_data = {
            "jazoest": jazoest,
            "lsd": lsd,
            "cuid": cuid.split("/?")[1].split("&next")[0],
            "pass": PASSWORD,
            "cred_type": "137",
            "login_source": "device_based_login",
            "email": EMAIL,
            "next": "",
            "persistent": ""
        }
        headers = {
            "accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
            "accept-encoding": "gzip, deflate",
            "accept-language": "da-DK,da;q=0.9,en-US;q=0.8,en;q=0.7",
            "content-type": "application/x-www-form-urlencoded",
            "origin": "https://da-dk.facebook.com",
            "referer": "https://da-dk.facebook.com/",
            "sec-fetch-dest": "document",
            "sec-fetch-mode": "navigate",
            "sec-fetch-site": "same-origin",
            "sec-fetch-user": "?1",
            "upgrade-insecure-requests": "1",
            "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.190 Safari/537.36",
            "Access-Control-Allow-Origin": "https://da-dk.facebook.com/"
        }

        login_request = session.post(FB_LOGIN_LINK, cookies=cookies, data=form_data, headers=headers)
        print(login_request.text)
        # Facebook bruger lazyloading, så vi får kun javascript kald tilbage.. ville kunne løses med selinium eller at
        # lave kaldende som javascriptene laver for at loade elementerne