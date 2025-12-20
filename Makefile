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
JAVA_VERSION_RAW := $(shell $(JAVAC) -version 2>&1 | head -1 | sed 's/javac //' | cut -d. -f1)
JAVA_VERSION := $(shell echo $(JAVA_VERSION_RAW) | grep -o '^[0-9]*')

.PHONY: check-java
check-java:
	@if [ -z "$(JAVA_VERSION)" ]; then echo "ERROR: Java compiler not found."; exit 1; \
	elif [ "$(JAVA_VERSION)" -lt "$(MIN_JAVA_VERSION)" ]; then echo "ERROR: Java $(MIN_JAVA_VERSION)+ required."; exit 1; fi

.PHONY: all
all: jar

.PHONY: jar
jar: check-java $(JAR_FILE)

$(JAR_FILE): $(SOURCES)
	@mkdir -p $(BUILD_DIR) $(DIST_DIR)
	$(JAVAC) -d $(BUILD_DIR) -sourcepath . $(JAVAC_FLAGS) $(SOURCES)
	cd $(BUILD_DIR) && $(JAR) cfe $(JAR_FILE) $(MAIN_CLASS) astro/ myutil/
	@echo "Build complete: $(JAR_FILE)"

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
