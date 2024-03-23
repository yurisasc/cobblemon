import json
import os
from io import StringIO
import pandas as pd
from mutagen.oggvorbis import OggVorbis
from cobblemon_drops_csv_to_json import download_spreadsheet_data
from cobblemon_spawn_csv_to_json import sanitize_pokemon
from scriptutils import printCobblemonHeader, print_list_filtered, print_cobblemon_script_description, \
    print_cobblemon_script_footer, print_problems_and_paths, print_warning

# Download the CSV file
ASSETS_CSV_URL = "https://docs.google.com/spreadsheets/d/e/2PACX-1vSTisDTkJvV0GzKV1zKjAPdMAQAO7znWxjEWXrM1gZPUVmsTU91oy54aJGMpbbvOqAOg03ER1wl7eeA/pub?gid=0&single=true&output=csv"


def main(print_missing_models=False, print_missing_animations=False):
    """
    cobblemon_cry_checker.py checks the consistency between the cry status of Pokémon in a CSV file and the actual
    presence of their cry audio files in a specific directory. It also checks for the presence of the corresponding
    model and animation files for each Pokémon. The function prints out any inconsistencies it finds.

    Parameters:
    print_missing_models (bool): If set to True, the function will print out the names of Pokémon for which model files are missing.
    print_missing_animations (bool): If set to True, the function will print out the names of Pokémon for which animation files are missing.

    Author: Waldleufer
    """

    printCobblemonHeader()

    scriptname = "Cobblemon Cry Checker"
    scriptdescription = "This script checks the consistency between the cry status of Pokémon in a CSV file and the actual presence of their cry audio files in a specific directory. It also checks for the presence of the corresponding model and animation files for each Pokémon. The function prints out any inconsistencies it finds."

    print_cobblemon_script_description(scriptname, scriptdescription)
    # Load the CSV data into a DataFrame
    csv_data = download_spreadsheet_data(ASSETS_CSV_URL)
    csv_file = StringIO(csv_data)
    # Load the data into a DataFrame while specifying the types of the columns, skip NaN values
    df = pd.read_csv(csv_file, dtype={0: str, 1: str, 2: str, 53: str}, skip_blank_lines=True)

    # Extract the required columns
    gen_numbers = df.iloc[3:, 0]  # Column A
    dex_numbers = df.iloc[3:, 1]  # Column B
    pokemon_names = df.iloc[3:, 2]  # Column C
    pokemon_in_game = df.iloc[3:, 7]  # Column H
    cry_on_repo = df.iloc[3:, 53]  # Column BB
    cry_statuses = df.iloc[3:, 54]  # Column BC [Cry Audio | In-Game]

    # Initialize lists for false positives and negatives
    false_positives = []
    false_negatives = []
    invalid_model_files = []
    invalid_animation_files = []
    other_warnings = []
    pokemon_ready_to_be_added_list = []

    # Iterate over the DataFrame rows
    for pokemon_name, gen_number, dex_number, cry_status, this_pokemon_in_game, this_cry_on_repo in zip(pokemon_names, gen_numbers, dex_numbers, cry_statuses, pokemon_in_game, cry_on_repo ):
        cry_status = str(cry_status).strip()  # remove whitespace
        # make the first letter of the Pokémon name uppercase and remove all spaces
        sanitized_pokemon_name_lower = sanitize_pokemon(pokemon_name).replace("é", "e")
        # if [ ] are present in the name, remove them and anything in between
        sanitized_pokemon_name_lower = sanitized_pokemon_name_lower.split("[")[0]
        sanitized_pokemon_name = sanitized_pokemon_name_lower.capitalize()
        # Construct the path to the audio file
        audio_file_path = f"../common/src/main/resources/assets/cobblemon/sounds/pokemon/{sanitized_pokemon_name_lower}/{sanitized_pokemon_name_lower}_cry.ogg"

        # Construct the path to the model file
        # Try to convert gen_number to an integer and handle ValueError
        try:
            model_file_path = f"../common/src/main/kotlin/com/cobblemon/mod/common/client/render/models/blockbench/pokemon/gen{int(gen_number.strip())}/{sanitized_pokemon_name}Model.kt"
        except ValueError:
            other_warnings.append(f"⚠️ Warning: Invalid gen_number for pokemon {pokemon_name}. Skipping this line.")
            continue  # Construct the path to the animation.json file
        dex_number = str(dex_number).zfill(4)  # prepend with 0s to make it 4 digits long
        animation_file_path = f"../common/src/main/resources/assets/cobblemon/bedrock/pokemon/animations/{dex_number}_{sanitized_pokemon_name_lower.lower()}/{sanitized_pokemon_name_lower.lower()}.animation.json"

        # Check if the audio file exists
        audio_file_exists = os.path.isfile(audio_file_path)
        # if the length of the audio file is smaller or equals to 0.11725 seconds, it is considered empty
        if audio_file_exists:
            audio = OggVorbis(audio_file_path)
            if audio.info.length <= 0.11725:
                audio_file_exists = False

        # Check the cry status and update the lists of false positives and negatives
        if cry_status == "✔" and not audio_file_exists:
            false_positives.append(pokemon_name)
        elif cry_status in ["🛇", "❌"] and audio_file_exists:
            # Check if the file is not a blank audio file
            audio = OggVorbis(audio_file_path)  # Check if the duration is 0
            # Check if the duration is 0
            if audio.info.length > 0.11725:
                # The audio file is not empty
                false_negatives.append(pokemon_name)

        # Check if the Model.kt file exists and contains the required import and cryAnimation override
        if cry_status == "✔":
            if os.path.isfile(model_file_path):
                with (open(model_file_path, 'r', encoding='utf-8-sig') as file):
                    content = file.read()
                    if "import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider" not in content:
                        invalid_model_files.append(("Missing import", model_file_path.replace(
                            '../common/src/main/kotlin/com/cobblemon/mod/common/client/render/models/blockbench/pokemon/',
                            "")))
                    elif f'override val cryAnimation = CryProvider {{ _, _ -> bedrockStateful("{sanitized_pokemon_name_lower}", "cry") }}' not in content:
                        if "override val cryAnimation = CryProvider {" not in content or 'bedrockStateful("' not in content:
                            invalid_model_files.append(("Missing cryAnimation override", model_file_path.replace(
                                '../common/src/main/kotlin/com/cobblemon/mod/common/client/render/models/blockbench/pokemon/',
                                "")))
            else:
                invalid_model_files.append(("Missing Model.kt", model_file_path.replace(
                    '../common/src/main/kotlin/com/cobblemon/mod/common/client/render/models/blockbench/pokemon/', "")))

            # Check if the animation.json file exists and contains the correct effect
            if os.path.isfile(animation_file_path):
                try:
                    with open(animation_file_path, 'r', encoding="utf-8-sig") as file:
                        data = json.load(file)
                        # Iterate through all animations and check if the cry effect is present
                        for animation_name, animation_data in data['animations'].items():
                            if animation_name == f"animation.{sanitized_pokemon_name_lower}.cry":
                                # Check if the "sound_effects" field is present
                                if animation_name == f"animation.{sanitized_pokemon_name_lower}.cry":
                                    check_sound_effects(animation_data, sanitized_pokemon_name_lower,
                                                        animation_file_path, invalid_animation_files)
                except json.decoder.JSONDecodeError:
                    invalid_animation_files.append(("Invalid JSON in ", animation_file_path.replace(
                        '../common/src/main/resources/assets/cobblemon/bedrock/pokemon/animations/', "")))

        # Check the condition
        if this_pokemon_in_game == "✔" and this_cry_on_repo == "✔" and cry_status != "✔":
            pokemon_ready_to_be_added_list.append(pokemon_name)

    # Check if lists are empty
    if not other_warnings and not false_positives and not false_negatives and not invalid_model_files and not invalid_animation_files and not pokemon_ready_to_be_added_list:
        print("No issues found. All cries are in order! ♪♫")
    else:
        # Print out pokemon cries ready to be added to the game
        if pokemon_ready_to_be_added_list:
            print("\nPokemon that have cries ready to be added to the game:")
            print_list_filtered(pokemon_ready_to_be_added_list)

        # Print out the lists of false positives and negatives
        if false_positives:
            print("\nFalse positives (cry marked as done but no audio file found):")
            print_list_filtered(false_positives)

        if false_negatives:
            print("\nFalse negatives (audio file found but cry marked as not done):")
            # create a list of entries that do not contain G-Max or Mega
            false_negatives_filtered_no_gmax_mega = [x for x in false_negatives if "G-Max" not in x and "Mega" not in x]
            print_list_filtered(false_negatives_filtered_no_gmax_mega)

            print("\nFalse negatives (G-Max and Mega are Not Implemented Yet):")
            # Only print entries that contain G-Max or Mega
            false_negatives_filtered = [x for x in false_negatives if "G-Max" in x or "Mega" in x]
            print_list_filtered(false_negatives_filtered)

        # Print out the lists of invalid Model.kt and animation.json files
        if invalid_model_files:
            print(
                "\nInvalid Model.kt files: [Located in common/src/main/kotlin/com/cobblemon/mod/common/client/render/models/blockbench/pokemon/]")
            if print_missing_models:
                print_problems_and_paths(invalid_model_files)
            else:
                print_warning("[[not showing missing Model.kt files]]")
                print_problems_and_paths(invalid_model_files, "Missing Model.kt")

        if invalid_animation_files:
            print(
                "\nInvalid animation.json files: [Located in common/src/main/resources/assets/cobblemon/bedrock/pokemon/animations/]")
            if print_missing_animations:
                print_problems_and_paths(invalid_animation_files)
            else:
                print_warning("[[not showing missing animation.json files]]")
                print_problems_and_paths(invalid_animation_files, "Missing animation.json")

        if other_warnings:
            print("\nOther warnings:")
            print_list_filtered(other_warnings)

    print_cobblemon_script_footer("Thanks for using the Cobblemon cry checker, provided to you by Waldleufer!")


def check_sound_effects(animation_data, sanitized_pokemon_name_lower, animation_file_path, invalid_animation_files):
    sound_effects = animation_data.get('sound_effects')
    if sound_effects is not None:
        # Iterate over the sound effects
        for _time_point, effect_data in sound_effects.items():
            # Check if the "effect" field is present and has the correct value
            effect = effect_data.get('effect')
            if effect == f"pokemon.{sanitized_pokemon_name_lower}.cry":
                return
        invalid_animation_files.append(("Wrong \"effect\": ... in ", animation_file_path.replace(
            '../common/src/main/resources/assets/cobblemon/bedrock/pokemon/animations/', "")))
    else:
        invalid_animation_files.append(("Missing sound_effects in ", animation_file_path.replace(
            '../common/src/main/resources/assets/cobblemon/bedrock/pokemon/animations/', "")))


if (__name__ == "__main__"):
    main()
    input("Press Enter to continue...")

input()