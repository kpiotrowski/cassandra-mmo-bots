#!/bin/bash
docker-entrypoint.sh "$@"
sleep 1
run_scripts.sh