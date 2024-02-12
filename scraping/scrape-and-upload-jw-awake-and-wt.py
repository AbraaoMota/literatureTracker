import boto3
import requests
from bs4 import BeautifulSoup
import json
import os
import re
from urllib.parse import urljoin, urlparse


os.environ['AWS_ACCESS_KEY_ID'] = # removed
os.environ['AWS_SECRET_ACCESS_KEY'] = # removed

region = "us-east-1"
table_name = 'literatureRefData'

# Initialize AWS DynamoDB client
dynamodb = boto3.resource('dynamodb', region_name=region)  # Replace 'your_region' with your AWS region

# Specify the DynamoDB table name
table = dynamodb.Table(table_name)

# Function to clean up special characters from the title
def clean_title(title, pub_filter):
    if title:
        # Remove non-alphanumeric characters and extra whitespaces
        cleaned_title = re.sub(r'[^a-zA-Z0-9\s]', '', title)
        # Add prefix based on pub_filter
        if pub_filter == 'g':
            cleaned_title = f'Awake {cleaned_title}'
        elif pub_filter == 'wp':
            cleaned_title = f'Watchtower {cleaned_title}'
        return ' '.join(cleaned_title.split())
    else:
        return None

# Function to extract publication details
def extract_publications(url, pub_filter):
    response = requests.get(url)
    soup = BeautifulSoup(response.text, 'html.parser')

    publications = []

    for publication_div in soup.find_all('div', class_='synopsis'):
        publication_desc_div = publication_div.find('div', class_='publicationDesc')
        if publication_desc_div:
            title_element = publication_desc_div.find('h3')
            title = title_element.text.strip() if title_element else None

            link_element = publication_desc_div.find('a', href=True)
            link = 'https://www.jw.org' + link_element['href'] if link_element else None
        else:
            title = link = None

        syn_img_div = publication_div.find('div', class_='syn-img')
        image_element = syn_img_div.find('img', src=True) if syn_img_div else None
        image = image_element['src'] if image_element and 'src' in image_element.attrs else None

        # Create a full URL for the image
        if image:
            image = urljoin(url, image)

        if image:
            image = urlparse(image)._replace(netloc='', scheme='').geturl()
            image = 'https://www.jw.org' + image

        # Extract year from the URL
        year = int(url.split('yearFilter=')[-1])

        # Extract publication number from the title using a more flexible approach
        if title and isinstance(title, str):
            publication_number_match = re.search(r'No\.\s*(\d+)', title)
            publication_number = int(publication_number_match.group(1)) if publication_number_match else None
        else:
            publication_number = None

        # Define the litCode using the provided strategy
        litCode = f"{pub_filter}{str(year)[-2:]}.{publication_number}" if publication_number else None

        # Clean up special characters from the title
        cleaned_title = clean_title(title, pub_filter)

        # Ignore entries with null title and null litCode
        if cleaned_title is not None and litCode is not None:
            publication_info = {
                'title': cleaned_title,
                'link': link,
                'image': image,
                'year': year,
                'litCode': litCode,
            }

            publications.append(publication_info)

    return publications


# Upload publications to DynamoDB
def upload_to_dynamodb(publications):
    for publication in publications:
        # Upload the item to DynamoDB
        table.put_item(
            Item={
                'title': publication.get('title', ''),
                'link': publication.get('link', ''),
                'image': publication.get('image', ''),
                'year': publication.get('year', ''),
                'litCode': publication.get('litCode', ''),
            }
        )

if __name__ == "__main__":
    base_url = 'https://www.jw.org/en/library/magazines/?contentLanguageFilter=en&pubFilter={}&yearFilter={}'

    # Define filters
    pub_filters = ['g', 'wp']
    years_range = range(2018, 2025)

    # Iterate over combinations
    for pub_filter in pub_filters:
        for year in years_range:
            url = base_url.format(pub_filter, year)

            # Extract publications
            publications_data = extract_publications(url, pub_filter)
            # publications_data = extract_publications_selenium(url, pub_filter)
            print(json.dumps(publications_data, indent=2))


            # Upload to DynamoDB
            upload_to_dynamodb(publications_data)
            print("Publications uploaded to DynamoDB successfully for URL: " + url)

print("Publications uploaded to DynamoDB successfully.")