package common

import (
	"log"
	"os"
)

func InitLogger(logPath string) error {
	file, err := os.OpenFile(logPath, os.O_RDWR|os.O_CREATE|os.O_APPEND, 0666)
	if err != nil {
		return err
	}
	log.SetOutput(file)
	return nil
}
