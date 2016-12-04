cd ../src
find -name "*.java" > sources.txt
javac -cp "../lib/paragraph-1.0-SNAPSHOT.jar" -d "../target/" @sources.txt
rm sources.txt
cd ../target
java -cp "../lib/paragraph-1.0-SNAPSHOT.jar:." -Xmx4000m pt.ist.rc.dss.DSSimulator

