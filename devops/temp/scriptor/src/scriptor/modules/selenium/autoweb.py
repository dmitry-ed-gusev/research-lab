import requests
import webbrowser

from loguru import logger

from selenium import webdriver
from webdriver_manager.core.os_manager import ChromeType
from selenium.webdriver.chrome.service import Service as ChromeService
from selenium.webdriver.chrome.service import Service as ChromiumService
from selenium.webdriver.chrome.service import Service as EdgeService
from selenium.webdriver.chrome.service import Service as FirefoxService

from webdriver_manager.chrome import ChromeDriverManager
from webdriver_manager.microsoft import EdgeChromiumDriverManager
from webdriver_manager.firefox import GeckoDriverManager


# open web-browser
# webbrowser.open('http://inventwithpython.com/')

# file downloading
# res = requests.get('https://automatetheboringstuff.com/files/rj.txt')
# res.raise_for_status()
# playFile = open('RomeoAndJuliet.txt', 'wb')
# for chunk in res.iter_content(100000):
#     playFile.write(chunk)
#     playFile.close()

# downloading and loading driver (Chrome)
driver = webdriver.Chrome(service=ChromeService(ChromeDriverManager().install()))
logger.debug("Loaded driver: Chrome")

# downloading and loading driver (Chromium)
driver = webdriver.Chrome(service=ChromiumService(ChromeDriverManager(chrome_type=ChromeType.CHROMIUM).install()))
logger.debug("Loaded driver: Chromium")

# downloading and loading driver (Edge)
driver = webdriver.Edge(service=EdgeService(EdgeChromiumDriverManager().install()))
logger.debug("Loaded driver: Edge")

# downloading and loading driver (Firefox)
driver = webdriver.Firefox(service=FirefoxService(GeckoDriverManager().install()))
logger.debug("Loaded driver: Firefox")
