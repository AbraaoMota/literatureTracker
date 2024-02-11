import requests
from bs4 import BeautifulSoup
import json
import boto3
import os

os.environ['AWS_ACCESS_KEY_ID'] = # removed
os.environ['AWS_SECRET_ACCESS_KEY'] = # removed

region = "us-east-1"
table_name = 'literatureRefData'

# Initialize AWS DynamoDB client
dynamodb = boto3.resource('dynamodb', region_name=region)  # Replace 'your_region' with your AWS region

# Specify the DynamoDB table name
table = dynamodb.Table(table_name)

# Function to extract publication details
def extract_publications(url):
    response = requests.get(url)
    soup = BeautifulSoup(response.text, 'html.parser')

    publications = []

    for publication in soup.find_all('li', class_='row card'):
        title_element = publication.find('div', class_='cardTitleBlock').find('div', class_='cardLine1')
        title_text = title_element.text.strip() if title_element else None

        title, _, code = title_text.partition('(')
        title = title.strip()
        code = code.rstrip(')').strip() if ')' in title_text else None

        link_element = publication.find('a', class_='jwac')
        link = 'https://wol.jw.org/' + link_element['href'] if link_element else None

        image_element = publication.find('img', class_='cardThumbnailImage')
        image = 'https://wol.jw.org/' + image_element['src'] if image_element else None

        year_element = publication.find('div', class_='cardTitleDetail')
        year = year_element.text.strip() if year_element else None

        publication_info = {
            'title': title,
            'code': code,
            'link': link,
            'image': image,
            'year': year,
        }

        publications.append(publication_info)

    return publications

# Upload publications to DynamoDB
def upload_to_dynamodb(publications):
    for publication in publications:
        # Use 'code' as the partition key
        partition_key_value = publication.get('code', '')

        # Skip items where 'code' is None
        if partition_key_value is None:
            continue

        # Upload the item to DynamoDB
        table.put_item(
            Item={
                'litCode': partition_key_value,
                'title': publication.get('title', ''),
                'link': publication.get('link', ''),
                'image': publication.get('image', ''),
                'year': publication.get('year', ''),
            }
        )

if __name__ == "__main__":
    urlList = [
        'https://wol.jw.org/en/wol/library/r1/lp-e/all-publications/bibles',
        'https://wol.jw.org/en/wol/library/r1/lp-e/all-publications/books',
        'https://wol.jw.org/en/wol/library/r1/lp-e/all-publications/brochures-and-booklets',
        'https://wol.jw.org/en/wol/library/r1/lp-e/all-publications/tracts',
        'https://wol.jw.org/en/wol/library/r1/lp-e/all-publications/tracts/kingdom-news',
        'https://wol.jw.org/en/wol/library/r1/lp-e/all-publications/article-series'
    ]

    for url in urlList:

        # Extract publications
        publications_data = extract_publications(url)
        print(json.dumps(publications_data, indent=2))


        # Upload to DynamoDB
        upload_to_dynamodb(publications_data)
        print("Publications uploaded to DynamoDB successfully for URL: " + url)

    print("All Publications uploaded to DynamoDB successfully.")
