import json
import os
from io import StringIO
import pandas as pd
from mutagen.oggvorbis import OggVorbis
from cobblemon_drops_csv_to_json import download_spreadsheet_data
from scriptutils import printCobblemonHeader, print_list_filtered, print_cobblemon_script_description, \
    print_cobblemon_script_footer, print_problems_and_paths, print_warning, sanitize_pokemon, print_separator

# Download the CSV file
ASSETS_CSV_URL = "https://docs.google.com/spreadsheets/d/e/2PACX-1vSTisDTkJvV0GzKV1zKjAPdMAQAO7znWxjEWXrM1gZPUVmsTU91oy54aJGMpbbvOqAOg03ER1wl7eeA/pub?gid=0&single=true&output=csv"


def main(print_missing_models=True, print_missing_animations=True):
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
    scriptdescription = "This script checks the consistency between the cry status of Pokémon in a CSV file and the actual presence of their cry audio files in a specific directory. It also checks for the presence of the corresponding model and animation files for each Pokémon. The function prints out any inconsistencies it finds. WARNING: G-Max and Mega Pokémon are not checked yet."

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
    cries_on_repo = df.iloc[3:, 53]  # Column BB
    cries_in_game = df.iloc[3:, 54]  # Column BC [Cry Audio | In-Game]

    # Initialize lists for false positives and negatives
    false_positives = []
    false_negatives = []
    implemented_and_not_marked = []
    invalid_model_files = []
    invalid_animation_files = []
    all_warnings_combined = []
    pokemon_ready_to_be_added_list = []

    # Iterate over the DataFrame rows
    for pokemon_name, gen_number, dex_number, cry_in_game, this_pokemon_in_game, this_cry_on_repo in zip(pokemon_names,
                                                                                                         gen_numbers,
                                                                                                         dex_numbers,
                                                                                                         cries_in_game,
                                                                                                         pokemon_in_game,
                                                                                                         cries_on_repo):
        cry_in_game = str(cry_in_game).strip()  # remove whitespace
        # make the first letter of the Pokémon name uppercase and remove all spaces
        sanitized_pokemon_name_lower = sanitize_pokemon(pokemon_name)
        # if [ ] are present in the name, remove them and anything in between
        sanitized_pokemon_name_lower = sanitized_pokemon_name_lower.split("[")[0]
        sanitized_pokemon_name = sanitized_pokemon_name_lower.capitalize()
        pokemon_form = pokemon_name.split("[")[1].split("]")[0].lower() if "[" in pokemon_name else None

        # Construct the path to the audio file
        if pokemon_form:
            audio_file_path = f"../common/src/main/resources/assets/cobblemon/sounds/pokemon/{sanitized_pokemon_name_lower}/{sanitized_pokemon_name_lower}_{pokemon_form}_cry.ogg"
        else:
            audio_file_path = f"../common/src/main/resources/assets/cobblemon/sounds/pokemon/{sanitized_pokemon_name_lower}/{sanitized_pokemon_name_lower}_cry.ogg"

        checks = {
            'in_repo': False,
            'import_correct': False,
            'override_correct': False,
            'in_game': False,
            'audio_file_exists': False,
            'sound_effects': False,
            'keyframe_exists': False
        }
        # Construct the path to the model file
        # Try to convert gen_number to an integer and handle ValueError
        try:
            model_file_path = f"../common/src/main/kotlin/com/cobblemon/mod/common/client/render/models/blockbench/pokemon/gen{int(gen_number.strip())}/{sanitized_pokemon_name}Model.kt"
        except ValueError:
            all_warnings_combined.append(
                f"⚠️ Warning: Invalid gen_number for pokemon {pokemon_name}. Skipping this line.")
            continue  # Construct the path to the animation.json file
        dex_number = str(dex_number).zfill(4)  # prepend with 0s to make it 4 digits long
        animation_file_path = f"../common/src/main/resources/assets/cobblemon/bedrock/pokemon/animations/{dex_number}_{sanitized_pokemon_name_lower.lower()}/{sanitized_pokemon_name_lower.lower()}.animation.json"

        # Check if the audio file exists
        checks["audio_file_exists"] = os.path.isfile(audio_file_path)
        # if the length of the audio file is smaller or equals to 0.11725 seconds, it is considered empty
        if checks["audio_file_exists"]:
            audio = OggVorbis(audio_file_path)
            if audio.info.length <= 0.11725:
                checks["audio_file_exists"] = False

        # Check the cry status
        if cry_in_game == "✔":
            checks["in_game"] = True

        # Check if the Model.kt file exists and contains the required import and cryAnimation override
        if os.path.isfile(model_file_path):
            with (open(model_file_path, 'r', encoding='utf-8-sig') as file):
                content = file.read()
                if "import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider" in content:
                    checks["import_correct"] = True
                if f'override val cryAnimation = CryProvider {{ _, _ -> bedrockStateful("{sanitized_pokemon_name_lower}", "cry") }}' in content:
                    checks["override_correct"] = True

        # Check if the animation.json file exists and contains the correct effect
        if os.path.isfile(animation_file_path):
            try:
                with open(animation_file_path, 'r', encoding="utf-8-sig") as file:
                    data = json.load(file)
                    # Iterate through all animations and check if the cry effect is present
                    for animation_name, animation_data in data['animations'].items():
                        if animation_name == f"animation.{sanitized_pokemon_name_lower}.cry":
                            # Check if the "sound_effects" field is present
                            checks["sound_effects"] = check_sound_effects(animation_data, sanitized_pokemon_name_lower)

            except json.decoder.JSONDecodeError:
                print_warning("Invalid JSON in " + animation_file_path.replace(
                    '../common/src/main/resources/assets/cobblemon/bedrock/pokemon/animations/', ""))

        # Check the condition
        if this_pokemon_in_game == "✔" and this_cry_on_repo == "✔" and cry_in_game != "✔":
            # check if all checks are true
            if not all(checks.values()):
                pokemon_ready_to_be_added_list.append(pokemon_name)

        # Construct results
        if not checks["in_game"] and checks["audio_file_exists"]:
            false_negatives.append(f"{pokemon_name}")
        elif checks["in_game"] and not checks["audio_file_exists"]:
            false_positives.append(f"{pokemon_name}")

        if "Mega" in pokemon_name or "G-Max" in pokemon_name:
            continue

        # If any of the checks failed, add the Pokemon to the corresponding lists, but not if all checks are false
        if all(value is False for value in checks.values()) and checks["in_game"] is False:
            continue
        if not checks["in_game"]:
            implemented_and_not_marked.append(f"{pokemon_name}")
        if all(value is True for value in checks.values()):
            continue


        if not checks["override_correct"] and not checks["import_correct"]:
            invalid_model_files.append((pokemon_name, " [Problems with both import and override]"))
            all_warnings_combined.append((pokemon_name, "⚠️ Warning: Model.kt import and override not correct"))
        elif not checks["override_correct"]:
            invalid_model_files.append((pokemon_name, " [problem with override]"))
            all_warnings_combined.append((pokemon_name, "⚠️ Warning: Model.kt override not correct"))
        elif not checks["import_correct"]:
            invalid_model_files.append((pokemon_name, " [problem  with  import]"))
            all_warnings_combined.append((pokemon_name, "⚠️ Warning: Model.kt import not correct"))

        # If any of the remaining checks failed, add the Pokemon to the all_warnings_combined list
        if not checks["sound_effects"]:
            invalid_animation_files.append((pokemon_name, " [problem with Sound Effects in Animation.json]"))
            all_warnings_combined.append((pokemon_name, "⚠️ Warning: Sound effect in Animation.json not correct"))

    # Check if lists are empty
    if not all_warnings_combined and not false_positives and not false_negatives and not invalid_model_files and not invalid_animation_files and not pokemon_ready_to_be_added_list:
        print("No issues found. All cries are in order! ♪♫")
    else:
        # Print out pokemon cries ready to be added to the game
        if pokemon_ready_to_be_added_list:
            print_separator()
            print("\nPokemon that have cries ready to be added to the game:")
            print_list_filtered(pokemon_ready_to_be_added_list)

        # Print out the lists of false positives and negatives
        if false_positives:
            print_separator()
            print("\nFalse positives (cry marked as in-game but no audio file found):")
            print_list_filtered(false_positives)

        if false_negatives:
            print_separator()
            print(
                "\nFalse negatives (audio file found but cry not marked as in-game):")
            # create a list of entries that do not contain G-Max or Mega
            false_negatives_filtered_no_gmax_mega = [x for x in false_negatives if "G-Max" not in x and "Mega" not in x]
            print_list_filtered(false_negatives_filtered_no_gmax_mega)

            print("\nFalse negatives (G-Max and Mega are Not Implemented Yet):")
            # Only print entries that contain G-Max or Mega
            false_negatives_filtered = [x for x in false_negatives if "G-Max" in x or "Mega" in x]
            print_list_filtered(false_negatives_filtered)

        if implemented_and_not_marked:
            print_separator()
            print("\nPokemon that are (at least partially) implemented in the game but not marked as cry in-game in the spreadsheet:")
            print_list_filtered(implemented_and_not_marked)

        # Print out the lists of invalid Model.kt and animation.json files
        if invalid_model_files:
            print_separator()
            print(
                "\nInvalid Model.kt files: [Located in common/src/main/kotlin/com/cobblemon/mod/common/client/render/models/blockbench/pokemon/]")
            if print_missing_models:
                print_problems_and_paths(invalid_model_files)
            else:
                print_warning("WARNING: [[ main(print_missing_models=False) :: not showing missing Model.kt files]]")
                print_problems_and_paths(invalid_model_files, "Missing Model.kt")

        if invalid_animation_files:
            print_separator()
            print(
                "Invalid animation.json files: [Located in common/src/main/resources/assets/cobblemon/bedrock/pokemon/animations/]")
            if print_missing_animations:
                print_problems_and_paths(invalid_animation_files)
            else:
                print_warning(
                    "WARNING: [[ main(print_missing_animations=False) :: not showing missing animation.json files]]")
                print_problems_and_paths(invalid_animation_files, "Missing animation.json")

        if all_warnings_combined:
            print_separator()
            print("\nAll warnings combined in one list:")
            print_problems_and_paths(all_warnings_combined)

    print_cobblemon_script_footer("Thanks for using the Cobblemon cry checker, provided to you by Waldleufer!")


def check_sound_effects(animation_data, sanitized_pokemon_name_lower):
    sound_effects = animation_data.get('sound_effects')
    if sound_effects is not None:
        # Iterate over the sound effects
        for _time_point, effect_data in sound_effects.items():
            # Check if the "effect" field is present and has the correct value
            effect = effect_data.get('effect')
            if effect == f"pokemon.{sanitized_pokemon_name_lower}.cry":
                return True
        return False
    else:
        return False


if (__name__ == "__main__"):
    main()
    input("Press Enter to continue...")
