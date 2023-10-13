import os
import time
import requests
import pandas as pd
from io import StringIO
from sqlalchemy import create_engine, Text, Float


def main():
    # Configuration data
    spreadsheet_csv_url = 'YOUR_SPREADSHEET_PUBLISHED_CSV_URL'
    conversion_csv_url = 'https://docs.google.com/spreadsheets/d/e/2PACX-1vRmvHzUc6_UUKbcvRche7AVebNoljqC1bf3iccusJqW9-C3k0KtESJxOCXShykSejIarAB2jHJ2bHCb/pub?gid=0&single=true&output=csv'
    pokemon_data_dir = '../common/src/main/resources/data/cobblemon/species'
    sqlite_db_name = 'pokemon_drops_data.sqlite'
    sqlite_table_name = 'pokemon_drops'

    # TODO: Download the CSV from the Google Spreadsheet
    # csv_data = download_spreadsheet_data(spreadsheet_csv_url)

    # For now: just load the data from a local files
    csv_data = open('Cobblemon_Drops.csv', 'r', encoding="utf8").read()
    csv_data_for_matching = download_spreadsheet_data(conversion_csv_url)

    # Load the data into a DataFrame
    df = load_data_from_csv(csv_data)
    mapping_df = load_data_from_csv(csv_data_for_matching)

    # Create a mapping dictionary from the Pokémon names to the Minecraft IDs
    mapping_dict = dict(zip(mapping_df['natural_name'], mapping_df['minecraft_ID']))

    # Remove any rows where Drops is NaN
    df = df[df['Drops'].notna()]

    # Replace the Item names with the Minecraft IDs
    df['Drops'] = df['Drops'].apply(lambda x: replace_names_in_string(x, mapping_dict))

    # Filter filenames from ..\common\src\main\resources\data\cobblemon\species based on Pokémon names
    filenames = filter_filenames_by_pokemon_names(pokemon_data_dir, df['Pokémon'])

    print(filenames)

    # Your next processing steps...
    # ...

    # Save data to SQLite
    write_to_sqlite(df, sqlite_db_name, sqlite_table_name)


def replace_names_in_string(drop_str, mapping_dict):
    for natural_name, minecraft_id in mapping_dict.items():
        drop_str = drop_str.replace(natural_name, minecraft_id)
    return drop_str


def load_and_filter_data(filename, columns, filter_column, filter_value):
    df = pd.read_csv(filename, sep=',', usecols=columns)
    df = df[df[filter_column] == filter_value]
    return df

def download_spreadsheet_data(url, max_retries=5):
    delay = 1
    for attempt in range(max_retries):
        try:
            response = requests.get(url)
            response.raise_for_status()  # will raise an HTTPError if the HTTP request returned an unsuccessful status code
            return response.content.decode('utf-8')
        except requests.RequestException as e:
            if attempt < max_retries - 1:
                time.sleep(delay)
                delay *= 2
            else:
                raise e


def load_data_from_csv(csv_data):
    print(csv_data)
    return pd.read_csv(StringIO(csv_data), encoding='utf8', engine='python', dtype={'Pokémon': str, 'Drops': str})


def filter_filenames_by_pokemon_names(directory, pokemon_names):
    all_files = os.listdir(directory)
    print(all_files)
    filtered_files = [file for file in all_files if file[:-4].lower() in pokemon_names.str.lower().tolist()]
    return filtered_files


def write_to_sqlite(df, db_name, table_name):
    engine = create_engine(f'sqlite:///{db_name}', echo=True)
    df.to_sql(table_name, con=engine, if_exists='replace', index=False, dtype={
        # Adjust these data types according to the columns in your Pokémon DataFrame
        "pokemon_name": Text,
        "stat_1": Float,
        "stat_2": Float,
        # ...
    })


if __name__ == "__main__":
    main()
