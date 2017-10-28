#!/bin/bash

mvn clean && mvn package

java -jar alpha-gomoku-dashboard/target/alpha-gomoku-dashboard-*-jar-with-dependencies.jar -player ./player.properties

