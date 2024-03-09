import json
import os


def generate_markdown(json_directory, output_markdown_file):
    json_files = [f for f in os.listdir(json_directory) if f.endswith('.json')]
    markdown_content = ""

    for file_name in json_files:
        file_path = os.path.join(json_directory, file_name)
        with open(file_path, 'r') as json_file:
            data = json.load(json_file)

        # Extract the root name from the file_name for the markdown section title
        root_name = file_name.split('.')[0]
        markdown_content += f"## {root_name.capitalize()}\n\n"

        markdown_content += "| Conditions | Anticonditions |\n"
        markdown_content += "|------------|----------------|\n"

        conditions = data.get("condition", {})
        anticonditions = data.get("anticondition", {})

        # Collect all unique keys from conditions and anticonditions
        all_keys = set(conditions.keys()) | set(anticonditions.keys())

        for key in all_keys:
            condition_values = conditions.get(key, [])
            anticondition_values = anticonditions.get(key, [])

            # Ensure values are list for uniform processing
            if not isinstance(condition_values, list):
                condition_values = [condition_values]
            if not isinstance(anticondition_values, list):
                anticondition_values = [anticondition_values]

            # Check if the key is in conditions or anticonditions
            if key in conditions:
                # Prepend the key to the conditions column
                markdown_content += f"| *{key}:* {'; '.join([f'`{v}`' for v in condition_values])} | {'; '.join([f'`{v}`' for v in anticondition_values])} |\n"
            else:
                # Prepend the key to the anticonditions column
                markdown_content += f"| {'; '.join([f'`{v}`' for v in condition_values])} | *{key}:* {'; '.join([f'`{v}`' for v in anticondition_values])} |\n"

    # Write markdown content to the specified output file
    with open(output_markdown_file, 'w') as output_file:
        output_file.write(markdown_content)


# Example usage
json_directory = '../../common/src/main/resources/data/cobblemon/spawn_detail_presets'
output_markdown_file = '../../docs/cobblemon-tags/spawnPresetList.md'
generate_markdown(json_directory, output_markdown_file)
