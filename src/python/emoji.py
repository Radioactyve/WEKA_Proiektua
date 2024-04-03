# -*- coding: utf-8 -*-

import requests
from bs4 import BeautifulSoup
import json

emojiPath = 'src/x_out/emoji.txt'

# Beharrezkoak diren emojiak dituzten url-en lista
urls = {'https://emojipedia.org/es/smileys#list', 'https://emojipedia.org/es/personas#list', 'https://emojipedia.org/es/search?q=corazón'}

emoji_izenak = []


# URL bakoitzeko Web Scraping bat egin emoji lista lortzeko
for url in urls:
    # Eskaeraren egitura
    metodoa = "GET"
    goiburuak = goiburuak = {'Host': 'emojipedia.org'}
    erantzuna = requests.request(metodoa, url, headers=goiburuak, allow_redirects=False)

    # Eskaeraren erantzuna parseatu elemenyuak bilatzeko
    html = erantzuna.content
    soup = BeautifulSoup(html, 'html.parser')

    # id=__NEXT_DATA__ duen Script elementua lortu, hau json bat izango du nahi dugun informaziarekin
    script_tag = soup.find('script', id='__NEXT_DATA__')

    if script_tag:
        # Datuak dituen JSON-etik datuak erauzi
        json_data = json.loads(script_tag.string)

        kategoriak = json_data['props']['pageProps']['dehydratedState']['queries']

        if url == 'https://emojipedia.org/es/search?q=corazón': # URL hau kasu berezi bat denez era ezberdin batean egingo da
            kategoriak = json_data['props']['pageProps']['dehydratedState']['queries'][3]['state']['data']
            for emoji in kategoriak:
                izena = emoji['title'].lower()
                print(izena)
                emoji_izenak.append(izena)
        else:
            for idx in range(len(kategoriak)):
                try:
                    kategoriak = json_data['props']['pageProps']['dehydratedState']['queries'][idx]['state']['data']['subCategories']
                    for kategoria in kategoriak:
                        for emoji in kategoria['emoji']:
                            izena = emoji['title'].lower()
                            print(izena)
                            if izena != 'cara sonriendo':
                                emoji_izenak.append(izena)
                    pass
                except Exception as e:
                    pass

# Gorde emojien lista emoji.txt artxiboan
def safe_print(content):
    try:
        print(content)
    except UnicodeEncodeError:
        encoded_string = content.encode("utf-8", errors="replace")
        print(encoded_string.decode("utf-8", errors="replace"))

# Uso de la función safe_print en lugar de print
for emoji_name in emoji_izenak:
    safe_print(emoji_name + '\n')
