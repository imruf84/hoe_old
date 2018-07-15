CC		:= gcc
#C_FLAGS := -Wall -Wextra
C_FLAGS := -Wextra

BIN		:= bin
SRC		:= $(shell find ./src -name '*.c')
INCLUDE	:= include
LIB		:= lib

LIBRARIES := -lGLU -lOSMesa -lm

ifeq ($(OS),Windows_NT)
EXECUTABLE	:= hoerender.exe
else
EXECUTABLE	:= hoerender
endif

all: $(BIN)/$(EXECUTABLE)

clean:
	-$(RM) $(BIN)/$(EXECUTABLE)

run: all
	./$(BIN)/$(EXECUTABLE)

$(BIN)/$(EXECUTABLE): $(SRC)
	$(CC) $(C_FLAGS) -I$(INCLUDE) -L$(LIB) $^ -o $@ $(LIBRARIES)