import json
import copy
from pathlib import Path

file_path = input('Enter file path:')
speed_scalar = float(input('Enter speed scalar:'))
destination = input('Enter destination:')

file_data = Path(file_path).read_text()
file_json = json.loads(file_data)
animations = file_json['animations']
animations_copy = copy.deepcopy(animations)
for animationName in animations:
    for bone in animations[animationName]['bones']:
        if 'position' in animations[animationName]['bones'][bone].keys():
            for frame in animations[animationName]['bones'][bone]['position']:
                animations_copy[animationName]['bones'][bone]['position'][str(float(frame) / speed_scalar)] = animations[animationName]['bones'][bone]['position'][frame]
        if 'rotation' in animations[animationName]['bones'][bone].keys():
            for frame in animations[animationName]['bones'][bone]['rotation']:
                animations_copy[animationName]['bones'][bone]['rotation'][str(float(frame) / speed_scalar)] = animations[animationName]['bones'][bone]['rotation'][frame]
file_json['animations'] = animations_copy
with open(destination, 'w') as outfile:
    json.dump(file_json, outfile, indent=4)
print("Parsing complete.")