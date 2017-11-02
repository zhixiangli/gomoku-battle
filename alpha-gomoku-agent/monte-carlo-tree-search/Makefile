GOPATH := $(shell dirname $(abspath $(lastword $(MAKEFILE_LIST))))
export GOPATH

GOCMD=$(shell which go)
GOBUILD=$(GOCMD) build
GOCLEAN=$(GOCMD) clean
GOTEST=$(GOCMD) test
GOGET=$(GOCMD) get

BINARY_NAME=monte-carlo-tree-search

all: test build

build:
	$(GOBUILD) -o $(BINARY_NAME) -v main

test:
	@echo $(GOPATH)
	$(GOTEST) -v ./...

clean:
	$(GOCLEAN)
	rm -f $(BINARY_NAME)

