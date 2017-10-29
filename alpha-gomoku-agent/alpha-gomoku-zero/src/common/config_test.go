package common

import (
	"encoding/json"
	"fmt"
	"testing"
)

func TestInitConfig(t *testing.T) {
	conf := Config{"./alpha-gomoku-zero.log", 5, 15, 15, 2, 100000}
	if bytes, err := json.Marshal(conf); err == nil {
		fmt.Print(string(bytes))
	}
}
