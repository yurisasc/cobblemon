import os
import json

# base paths relative to the script's location in utilityscripts/releasescripts
base_paths = [
    "../../common/src/main/resources/data/cobblemon/tags",
    "../../common/src/main/resources/data/minecraft/tags"
]

# Path to the output markdown file, relative to the script's location
output_md_path = "../../docs/TagCollection.md"

def main():
    # Ensure the output directory exists
    os.makedirs(os.path.dirname(output_md_path), exist_ok=True)
    # Create or open the TagCollection.md file
    with open(output_md_path, "w", encoding='utf-8') as tag_collection:

        # Iterate through each base path
        for base_path in base_paths:
            base_depth = len(base_path.split(os.sep))
            # Walk through the directory
            for root, dirs, files in os.walk(base_path):
                if files:  # Only proceed if there are JSON files in the directory
                    relative_path = os.path.relpath(root, base_path)
                    depth = determine_depth(relative_path)
                    # Determine the heading level based on depth
                    heading_level = '#' * (depth + 1)
                    tag_directory = os.path.basename(root)
                    # Write a new chapter or subchapter for the tag directory
                    tag_collection.write(f"\n{heading_level} {tag_directory}\n")
                    for file_name in files:
                        if file_name.endswith('.json'):
                            with open(os.path.join(root, file_name), 'r') as json_file:
                                data = json.load(json_file)
                                # Extract tags from both strings and dictionaries
                                tags = [entry['id'] if isinstance(entry, dict) and 'id' in entry else entry for entry in
                                        data['values']]
                                # Write the tag and filename
                                tag_collection.write(
                                    f"\n*Tag:* #{'/'.join(relative_path.split(os.sep))}:{file_name[:-5]}\n")
                                # Write the tags in a collapsible section
                                tag_collection.write("<details>\n<summary>Biome-Tags ▼</summary>\n\n")
                                for tag in tags:
                                    tag_collection.write(f"- {tag}\n")
                                tag_collection.write("\n</details>\n")


# Function to determine the depth of a directory relative to the base path
def determine_depth(relative_path):
    return len(relative_path.split(os.sep)) - 1

if __name__ == "__main__":
    main()
