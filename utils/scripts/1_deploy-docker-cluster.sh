#!/bin/bash
set -e

cd "$WORKING_DIR" || {
    echo "Error moving to the application's root directory."
    exit 1
}

### ASK TO PRUNE DOCKER SYSTEM
"$WORKING_DIR"/utils/scripts/common/docker-system-prune.sh

echo ""
echo "STARING DOCKER COMPOSE..."
echo ""
docker compose up --build -d
echo ""
echo "DONE!"

echo ""
echo "NOTE: Consider to run the following command in a new terminal to see the docker service's logs:"
echo "      docker compose logs -f"
echo ""
read -r -p 'Press [Enter] to continue...'
