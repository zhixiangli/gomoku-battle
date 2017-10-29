package common

import (
	"log"
	"os"
)

var (
	logFile *os.File
)

func InitLogger(logPath string) (err error) {
	logFile, err = os.OpenFile(logPath, os.O_RDWR|os.O_CREATE|os.O_APPEND, 0666)
	if err != nil {
		return
	}
	log.SetOutput(logFile)
	return
}

func DelLogger() {
	logFile.Close()
}
