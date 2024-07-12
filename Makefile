all: build-CityDataFunction

build-CityDataFunction:
	echo "Building City Data Function..."
	./mvnw -T 4C clean native:compile -Pnative -DskipTests -f ./functions/city-data-function -Ddependency-check.skip=true
	cp ./functions/city-data-function/target/native $(ARTIFACTS_DIR)
	cp ./functions/city-data-function/tools/shell/bootstrap $(ARTIFACTS_DIR)
	chmod 755 $(ARTIFACTS_DIR)/bootstrap
