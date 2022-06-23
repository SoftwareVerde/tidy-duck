#!/bin/bash

# Copy jars
cp -r app/build/libs/* out/bin/. || exit 1
chmod 770 out/bin/*.jar

## Copy Config
mkdir -p out/conf
cp -R conf/* out/conf/.

# Copy Web Files
cp -R www out/.

# Create run script
echo -e "#!/bin/bash\n\nexec java -jar bin/tidy-duck-*.jar\n" > out/run.sh
chmod 770 out/run.sh

