#!/bin/bash

mvn clean && mvn package

java -jar smart-gomoku-dashboard/target/smart-gomoku-dashboard-*-jar-with-dependencies.jar -player ./player.properties

