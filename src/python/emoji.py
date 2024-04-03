import sys

import requests
from bs4 import BeautifulSoup
import json

csvPath = '../x_in/' + sys.argv[1]
outputPath1 = '../x_out/' + sys.argv[2]
outputPath2 = '../x_out/' + sys.argv[3]
emojiPath = '../x_out/emoji.txt'
print(csvPath)
print(outputPath1)
print(outputPath2)