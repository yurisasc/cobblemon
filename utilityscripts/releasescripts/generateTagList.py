import os
import json

# Define base paths relative to the script's location in utilityscripts/releasescripts
base_paths = [
    "../../common/src/main/resources/data/cobblemon/tags",
    "../../common/src/main/resources/data/minecraft/tags"
]

# Define output paths for each category, relative to the script's location
output_paths = {
    'blocks': "../../docs/cobblemon-tags/BlocksTags.md",
    'entity_types': "../../docs/cobblemon-tags/EntityTypesTags.md",
    'items': "../../docs/cobblemon-tags/ItemTags.md",
    'biome': "../../docs/cobblemon-tags/BiomeTags.md",
}

# Ensure the output directory exists
os.makedirs(os.path.dirname(next(iter(output_paths.values()))), exist_ok=True)

# Open file handles for each category with UTF-8 encoding
output_files = {key: open(path, "w", encoding='utf-8') for key, path in output_paths.items()}


def determine_category(root):
    # Example logic based on directory names, adjust as needed
    if 'blocks' in root:
        return 'blocks'
    elif 'entity_types' in root:
        return 'entity_types'
    elif 'items' in root:
        return 'items'
    elif 'biome' in root:
        return 'biome'
    # Extend with more conditions as necessary
    return None


def process_json_file(json_path, file_handle):
    with open(json_path, 'r', encoding='utf-8') as json_file:
        data = json.load(json_file)
        tags = [entry['id'] if isinstance(entry, dict) and 'id' in entry else entry for entry in data.get('values', [])]
        tag_directory = os.path.basename(os.path.dirname(json_path))
        json_file_base = os.path.basename(json_path)
        # Adjusting this line to be the summary for the collapsible content
        file_handle.write(f"\n<details>\n<summary><b>Tag:</b> #{tag_directory}:{json_file_base[:-5]}</summary>\n\n")
        for tag in tags:
            file_handle.write(f"- {tag}\n")
        file_handle.write("\n</details>\n")


# Iterate through each base path
for base_path in base_paths:
    for root, dirs, files in os.walk(base_path):
        category = determine_category(root)
        if category and category in output_files:
            for file_name in files:
                if file_name.endswith('.json'):
                    process_json_file(os.path.join(root, file_name), output_files[category])

# Close all file handles
for file_handle in output_files.values():
    file_handle.close()
