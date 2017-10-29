package common

import (
	"encoding/json"
	"io/ioutil"
)

type Config struct {
	LogPath        string
	ConsecutiveNum int
	Row            int
	Column         int
	AroundRange    int
	SearchCount    int
}

var Conf *Config

func InitConfig(configPath string) error {
	data, err := ioutil.ReadFile(configPath)
	if err != nil {
		return err
	}
	Conf = &Config{}
	err = json.Unmarshal(data, Conf)
	if err != nil {
		return err
	}
	return nil
}
