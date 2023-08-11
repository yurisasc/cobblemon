#Make sure to pip install openpyxl, also have all spreadsheets in current working directory
#Also make sure to pip install requests
from openpyxl import Workbook, load_workbook
import json
import requests

def stuff():
	berry_data_wb = load_workbook(filename = "Cobblemon Berry Data.xlsx", data_only=True)
	berry_dict = {}
	berry_file_name = ""
	for row in berry_data_wb.active.iter_rows(2):
		berry_num = int(row[0].value)
		berry_name = row[1].value
		berry_prefix = berry_name[:-6].lower()
		berry_file_name = berry_name.lower().replace(" ", "_") + ".json"
		baseYields = row[4].value.split("-")
		berry_dict["baseYield"] = {
			"min": int(baseYields[0]),
			"max": int(baseYields[1])
		}
		baseGrowthTimes = int(row[7].value)
		growthVar = int(row[8].value) // 2
		berry_dict["growthTime"] = {
			"min": baseGrowthTimes - growthVar,
			"max": baseGrowthTimes + growthVar
		}
		refreshRate = int(row[9].value)
		refreshRateVar = int(row[10].value) // 2
		berry_dict["refreshRate"] = {
			"min": refreshRate - refreshRateVar,
			"max": refreshRate + refreshRateVar
		}
		biomeTags = row[2].value.split(", ")
		betterYields = row[5].value.split("-")
		berry_dict["growthFactors"] = [
			{
				"variant": "cobblemon:biome",
				"biomeTags": [f"#cobblemon:is_{x[1:].lower()}" for x in biomeTags],
				"bonusYield": {
					"min": int(betterYields[0]) - int(baseYields[0]),
					"max": int(betterYields[1]) - int(baseYields[1])
				}
			}
		]
		berry_dict["growthPoints"] = get_growth_points(row[0].row)
		berry_dict["mutations"] = get_mutations(berry_prefix)
		berry_dict["sproutShape"] = [
			{
		      "minX": 7,
		      "minY": -1,
		      "minZ": 7,
		      "maxX": 9,
		      "maxY": 0,
		      "maxZ": 9
    		}
		]
		berry_dict["matureShape"] = [
			{
		    	"minX": 6,
		    	"minY": -1,
		    	"minZ": 6,
		    	"maxX": 10,
		    	"maxY": 0,
		    	"maxZ": 10
		    },
		    {
		    	"minX": 7,
		    	"minY": 0,
		    	"minZ": 7,
		    	"maxX": 9,
		    	"maxY": 11,
		    	"maxZ": 9
		    },
		    {
		    	"minX": 7.5,
		    	"minY": 11,
		    	"minZ": 7.5,
		    	"maxX": 8.5,
		    	"maxY": 22,
		    	"maxZ": 8.5
		    }
		]
		spicyVal = int(row[13].value)
		dryVal = int(row[14].value)
		sweetVal = int(row[15].value)
		bitterVal = int(row[16].value)
		sourVal = int(row[17].value)
		flavorDict = {}
		if spicyVal > 0:
			flavorDict["SPICY"] = spicyVal
		if dryVal > 0:
			flavorDict["DRY"] = dryVal
		if sweetVal > 0:
			flavorDict["SWEET"] = sweetVal
		if bitterVal > 0:
			flavorDict["BITTER"] = bitterVal
		if sourVal > 0:
			flavorDict["SOUR"] = sourVal
		berry_dict["flavors"] = flavorDict
		berry_dict["tintIndexes"] = get_tints(berry_num, berry_prefix)
		berry_dict["flowerModel"] = f"cobblemon:{berry_prefix}_flower.geo"
		berry_dict["flowerTexture"] = f"cobblemon:textures/berries/{berry_prefix}.png"
		berry_dict["fruitModel"] = f"cobblemon:{berry_prefix}_berry.geo"
		berry_dict["fruitTexture"] = f"cobblemon:textures/berries/{berry_prefix}.png"
		with open(f"berries/{berry_file_name}", "w+") as file:
			file.write(json.dumps(berry_dict, indent=2))


def get_growth_points(rowNum):
	berry_placements_wb = load_workbook(filename = "Berry Placements.xlsx", data_only=True)
	row = berry_placements_wb.active[rowNum + 1]
	numSpots = int(row[4].value)
	result = [
		{
			"position": {
				"x": float(row[7 + i*6].value),
				"y": float(row[8 + i*6].value),
				"z": float(row[9 + i*6].value)
			},
			"rotation": {
				"x": float(row[10 + i*6].value),
				"y": float(row[11 + i*6].value),
				"z": float(row[12 + i*6].value)
			}
		} for i in range(0, numSpots)
	]
	return result

def get_mutations(berry_name):
	berry_mutations_wb = load_workbook(filename = "Berry Mutations (v2).xlsx", data_only=True)
	result = {}
	for row in berry_mutations_wb.active.iter_rows(2):
		add_mutation = False
		berryIndex = 0
		for i, cell in enumerate(row[0:3]):
			if cell.value.lower() == berry_name:
				add_mutation = True
				berryIndex = i
		if add_mutation:
			other_berry = "cobblemon:" + (row[2].value.lower() if berryIndex == 0 else row[0].value.lower())
			result_berry = "cobblemon:" + (row[4].value.lower())
			result[other_berry] = result_berry
	return result

def get_tints(berry_num, berry_name):
	preceding_zero = "0" if berry_num < 10 else ""
	url = f"https://gitlab.com/cable-mc/cobblemon-assets/-/raw/master/blockbench/berry_trees/{preceding_zero}{berry_num}_{berry_name}/{berry_name}_tints.json?ref_type=heads"
	req = requests.get(url)
	result = {}
	if req.status_code == 200:
		tint_list = req.json()
		for i,x in enumerate(tint_list):
			result[str(i)] = x
	else:
		print(f"Couldn't get tintIndexes for {berry_name} berry")
	return result

if __name__ == "__main__":
	stuff()