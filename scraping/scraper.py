import requests
from bs4 import BeautifulSoup

url = 'https://wol.jw.org/en/wol/library/r1/lp-e/all-publications/brochures-and-booklets'
response = requests.get(url)
soup = BeautifulSoup(response.text, 'html.parser')

# Extract information based on HTML structure
publications = []

for publication in soup.find_all('div', class_='publication'):
    title = publication.find('h2').text.strip()
    link = publication.find('a')['href']
    image = publication.find('img')['src']

    # Add more fields as needed

    publication_info = {
        'title': title,
        'link': link,
        'image': image,
        # Add more fields as needed
    }

    publications.append(publication_info)

# Now, 'publications' list contains information about each publication
print(publications)