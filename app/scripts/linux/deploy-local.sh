#/bin/bash
WAR_FILE_NAME="tidy-duck-*.war"
DEST_FILE_NAME="ROOT.war"
BASE_DIR="$(dirname $0)/../../"

date +"[%Y-%m-%d %H:%M:%S]"
cd $BASE_DIR
./gradlew war
cd -
sudo cp $BASE_DIR/build/libs/$WAR_FILE_NAME /var/lib/tomcat8/webapps/$DEST_FILE_NAME
sudo chown tomcat8:tomcat8 /var/lib/tomcat8/webapps/$DEST_FILE_NAME
