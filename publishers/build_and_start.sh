#!/bin/bash

docker build -f service.Dockerfile -t app:2 .
docker-compose up

docker container prune
