GOPATH := $(PWD)/:$(GOPATH)

GOCMD=$(shell which go)
GOBUILD=$(GOCMD) build
GOCLEAN=$(GOCMD) clean
GOTEST=$(GOCMD) test
GOGET=$(GOCMD) get

BINARY_NAME=alpha_gomoku_zero
SRC=$(shell find src -name *.go -type f)

all: test build

build:
	$(GOBUILD) -o $(BINARY_NAME) -v main

test:
	$(GOTEST) -v ./...

clean:
	$(GOCLEAN)
	rm -f $(BINARY_NAME)

