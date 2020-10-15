#!/bin/sh

# Variables
# ~~~~~~
# Colours
C_RESET='\033[0m'
C_RED='\033[0;31m'
C_GREEN='\033[0;32m'
C_YELLOW='\033[1;33m'

# Set distribution folder from first argument, otherwise default to `dist`
DIST_FOLDER=dist
if [ $# -gt 0 ] ; then
    DIST_FOLDER=$1
fi

# Graal version and path
GRAAL_JAVA_VERSION=java11
GRAAL_DIR=scripts/graal
GRAAL_VERSION=`cat ${GRAAL_DIR}/VERSION.txt`
GRAAL_HOME=${GRAAL_DIR}/sdk/graalvm-ce-${GRAAL_JAVA_VERSION}-${GRAAL_VERSION}
GRAAL_BIN=''

# Choose Graal SDK distribution for supported OSes
OS_NAME="`uname`"
set_graal_dist() {
    case "${OS_NAME}" in
        'Linux')
            echo "${C_YELLOW}Using Graal linux distribution${C_RESET}"
            GRAAL_BIN='bin'
            ;;
        'Darwin')
            echo "${C_YELLOW}Using Graal OS X distribution${C_RESET}"
            GRAAL_BIN='Contents/Home/bin'
            ;;
        *)
            echo "${C_RED}No Graal distribution for your operating system yet (${OS_NAME})${C_RESET}"
            exit 1
            ;;
    esac
}

# Create distribution folder if it doesn't exist, and clear the contents
clean_dist_folder() {
    mkdir -p ${DIST_FOLDER}
    rm -rf ${DIST_FOLDER}/*
}

# Create an uber-jar and copy the artifact to the distribution folder
create_assembly() {
    echo "${C_YELLOW}Packaging into universal distribution format${C_RESET}"
    sbt -mem 4096 stage
    cp target/universal/stage/lib/*.jar ${DIST_FOLDER}
}

# Create native image in the distribution folder
create_native() {
    if [ ! -d "${GRAAL_HOME}" ] ; then
        echo "${C_RED}GRAAL_HOME does not exist${C_RESET}"
        echo "Please run './scripts/graal/bin/setup.sh' to setup Graal"
        exit 1
    fi

    echo "${C_YELLOW}Packaging into native image${C_RESET}"
    (cd ${DIST_FOLDER} \
        && ../${GRAAL_HOME}/${GRAAL_BIN}/native-image \
              --no-server \
              --class-path "*" \
              com.hhandoko.realworld.Main)
}

# Run
# ~~~~~~
set_graal_dist
clean_dist_folder
create_assembly
create_native
