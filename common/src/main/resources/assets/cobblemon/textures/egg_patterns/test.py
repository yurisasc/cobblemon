import os
import json

files = os.listdir()
for f in files:
	if (f.startswith("pattern_")):
		pattern_name = f[8:-4]
		with open(f"{pattern_name}.json", "w+") as file:
			obj = dict()
			obj["model"] = "cobblemon:egg"
			obj["baseTexturePath"] = f"cobblemon:base"
			obj["overlayTexturePath"] = f"cobblemon:pattern_{pattern_name}"
			obj["baseInvSpritePath"] = f"cobblemon:test_pattern_base"
			obj["overlayInvSpritePath"] = f"cobblemon:test_pattern_overlay"
			file.write(json.dumps(obj, indent=2))
			file.close()

