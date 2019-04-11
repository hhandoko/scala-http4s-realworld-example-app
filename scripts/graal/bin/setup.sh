#!/bin/sh

# Variables
# ~~~~~~
# Colours
C_RESET='\033[0m'
C_RED='\033[0;31m'
C_GREEN='\033[0;32m'
C_YELLOW='\033[1;33m'

# Temp folder
TEMP_DIR=tmp

# Graal version and path
GRAAL_DIR=scripts/graal
GRAAL_VERSION=`cat ${GRAAL_DIR}/VERSION.txt`
GRAAL_HOME=${GRAAL_DIR}/sdk/graalvm-ce-${GRAAL_VERSION}

# Graal download and tarball name
GRAAL_FILE=''
GRAAL_SITE=https://github.com/oracle/graal/releases/download

# Choose Graal SDK distribution for supported OSes
OS_NAME="`uname`"
set_graal_dist() {
    case "${OS_NAME}" in
        'Linux')
            echo "${C_YELLOW}Using Graal linux distribution${C_RESET}"
            GRAAL_FILE=graalvm-ce-${GRAAL_VERSION}-linux-amd64.tar.gz
            ;;
        'Darwin')
            echo "${C_YELLOW}Using Graal OS X distribution${C_RESET}"
            GRAAL_FILE=graalvm-ce-${GRAAL_VERSION}-macos-amd64.tar.gz
            ;;
        *)
            echo "${C_RED}No Graal distribution for your operating system yet (${OS_NAME})${C_RESET}"
            exit 1
            ;;
    esac
}

# Download Graal SDK
setup_graal() {
    if [ ! -d "${GRAAL_HOME}" ] ; then
        if [ ! -f "${TEMP_DIR}/${GRAAL_FILE}" ] ; then
            echo "${C_YELLOW}Downloading Graal tarball${C_RESET}"
            (mkdir -p ${TEMP_DIR} \
                && cd ${TEMP_DIR} \
                && curl -L -O ${GRAAL_SITE}/vm-${GRAAL_VERSION}/${GRAAL_FILE})
        fi

        echo "${C_YELLOW}Unpacking Graal${C_RESET}"
        (mkdir -p ${GRAAL_HOME} \
            && cd ${TEMP_DIR} \
            && tar -xvzf ${GRAAL_FILE} -C ../${GRAAL_DIR}/sdk)
    else
        echo "${C_YELLOW}Skipping setup: Graal already downloaded${C_RESET}"
    fi
}

# Run
# ~~~~~~
set_graal_dist
setup_graal
