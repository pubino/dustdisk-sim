# Dust Disk Simulator Makefile
SHELL := /bin/bash
MIN_JAVA_VERSION := 25
PROJECT_DIR := $(shell pwd)
BUILD_DIR := $(PROJECT_DIR)/build
DIST_DIR := $(PROJECT_DIR)/dist
SCRIPTS_DIR := $(PROJECT_DIR)/scripts
JAVAC := javac
JAVA := java
JAR := jar
JPACKAGE := jpackage
JAVAC_FLAGS := -Xlint:-deprecation -Xlint:-removal
JAR_FILE := $(DIST_DIR)/DustDiskSim.jar
APP_BUNDLE := $(DIST_DIR)/DustDiskSimulator.app
MAIN_CLASS := astro.galaxy.DustDisk
SOURCES := astro/galaxy/DustDisk.java astro/galaxy/DustDiskCanvas.java astro/galaxy/Body3d.java astro/galaxy/Data3d.java myutil/AppletFrame.java myutil/GL.java myutil/SL.java myutil/Format.java
APP_NAME := DustDiskSimulator
DISPLAY_NAME := Dust Disk Simulator
BUNDLE_ID := edu.princeton.vanderbei.dustdisk-sim
VERSION := 1.0.0
# Check for Java availability (actually test javac, not just if it exists)
JAVA_WORKS := $(shell javac -version 2>&1 | grep -q "javac" && echo yes)
JAVA_VERSION := $(shell javac -version 2>&1 | grep -o '[0-9]\+' | head -1)

.PHONY: check-java
check-java:
ifneq ($(JAVA_WORKS),yes)
	@echo "Error: Java compiler (javac) not found."
ifeq ($(shell uname),Darwin)
	@echo "Install Java on macOS with brew install openjdk and add it to your PATH."
else
	@echo "Please install a Java Development Kit (JDK)."
endif
	@exit 1
else ifeq ($(shell test $(JAVA_VERSION) -lt $(MIN_JAVA_VERSION) && echo yes),yes)
	@echo "Error: Java $(MIN_JAVA_VERSION)+ required, but found Java $(JAVA_VERSION)."
ifeq ($(shell uname),Darwin)
	@echo "Install Java on macOS with brew install openjdk and add it to your PATH."
else
	@echo "Please install a Java Development Kit (JDK)."
endif
	@exit 1
endif

.PHONY: all
all: jar

.PHONY: jar
jar: check-java $(JAR_FILE)

$(JAR_FILE): $(SOURCES)
	@mkdir -p $(BUILD_DIR) $(DIST_DIR)
	$(JAVAC) -d $(BUILD_DIR) -sourcepath . $(JAVAC_FLAGS) $(SOURCES)
	cd $(BUILD_DIR) && $(JAR) cfe $(JAR_FILE) $(MAIN_CLASS) astro/ myutil/
	@echo "Build complete: $(JAR_FILE)"
	@read -p "Would you like to run the application? [y/N] " answer; \
	if [ "$$answer" = "y" ] || [ "$$answer" = "Y" ]; then \
		if command -v java >/dev/null 2>&1; then \
			echo "=== Running $(JAR_FILE) ==="; \
			java -jar $(JAR_FILE); \
		else \
			echo "Error: Java not found in PATH"; \
			echo ""; \
			echo "To install Java via Homebrew:"; \
			echo "  brew install openjdk"; \
			echo ""; \
			echo "Or install a specific version:"; \
			echo "  brew install openjdk@21"; \
			echo ""; \
			echo "After installation, you may need to add it to your PATH:"; \
			echo "  echo 'export PATH=\"/opt/homebrew/opt/openjdk/bin:\$$PATH\"' >> ~/.zshrc"; \
		fi \
	fi

.PHONY: app
app: $(APP_BUNDLE)

$(APP_BUNDLE): $(JAR_FILE)
	@rm -rf $(APP_BUNDLE)
	$(JPACKAGE) --type app-image --name "$(APP_NAME)" --input $(DIST_DIR) --main-jar DustDiskSim.jar --main-class $(MAIN_CLASS) --dest $(DIST_DIR) --app-version $(VERSION) --vendor "Vanderbei" --mac-package-identifier $(BUNDLE_ID) --mac-package-name "$(DISPLAY_NAME)" --java-options "-Xmx512m"

.PHONY: run
run: jar
	$(JAVA) -jar $(JAR_FILE)

.PHONY: clean
clean:
	rm -rf $(BUILD_DIR) $(JAR_FILE)

.PHONY: distclean
distclean: clean
	rm -rf $(DIST_DIR)

.PHONY: help
help:
	@echo "Targets: all jar app run clean distclean help"
