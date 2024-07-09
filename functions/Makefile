all: build-CityBasicDataFunction

build-CityBasicDataFunction:
	echo "Building CityBasicDataFunction..."
	./mvnw -T 4C clean native:compile -Pnative -f ./city-data-function/pom.xml -DskipTests -Ddependency-check.skip=true
	cp ./city-data-function/target/native $(ARTIFACTS_DIR)
	cp ./city-data-function/tools/shell/bootstrap $(ARTIFACTS_DIR)
	chmod 755 $(ARTIFACTS_DIR)/bootstrap
