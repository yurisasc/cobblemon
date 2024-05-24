const path = require("path");

const fs = require("fs").promises;

async function run() {
  const start = Date.now();
  const files = await fs.readdir(__dirname);
  await Promise.all(files.map(async (file) => {
    if (!file.endsWith(".json")) return;

    const inData = require(path.join(__dirname, file));
    const outData = {
      item: inData.item,
      effects: {}
    };

    for (const [type, { subcategory, ...effect }] of Object.entries(inData.effects)) {
      outData.effects[type] = effect;
      switch (type) {
        case "cobblemon:nature":
          outData.effects[type].nature = subcategory;
          break;
        case "cobblemon:iv":
          outData.effects[type].ivs = [subcategory];
          break;
        case "cobblemon:gender_chance":
          outData.effects[type].gender = subcategory;
          break;
        case "cobblemon:tera":
          outData.effects[type].tera = subcategory;
          break;
        case "cobblemon:tera":
          outData.effects[type].tera = subcategory;
          break;
      }
    }

    await fs.writeFile(path.join(__dirname, file), JSON.stringify(outData, null, 2));
  }));
  console.log(`Took ${Date.now() - start}`);
}

run();