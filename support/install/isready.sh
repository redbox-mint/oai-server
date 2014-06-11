#!/bin/sh
#Copyright (C) 2014 Queensland Cyber Infrastructure Foundation (http://www.qcif.edu.au/)

#This program is free software: you can redistribute it and/or modify
#it under the terms of the GNU General Public License as published by
#the Free Software Foundation; either version 2 of the License, or
#(at your option) any later version.

#This program is distributed in the hope that it will be useful,
#but WITHOUT ANY WARRANTY; without even the implied warranty of
#MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#GNU General Public License for more details.

#You should have received a copy of the GNU General Public License along
#with this program; if not, write to the Free Software Foundation, Inc.,
#51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#
#
#----------------------------------------------------------------

MAIN_LOG_FILE=$1
LABEL=$2
SEARCHTXT=$3 

echo "Waiting for $LABEL to be ready..."

STARTUP_COMPLETED=
TIMEOUT=300 # max 5 minutes
TIMER=0
while [ $TIMER -lt $TIMEOUT ]; do

    if [ -r "$MAIN_LOG_FILE" ]; then
    grep "$SEARCHTXT" "$MAIN_LOG_FILE" >/dev/null
        if [ $? -eq 0 ]; then
            # Found the message: assume it has started
            # Is there might be a better (and accurate) way to do this?
            STARTUP_COMPLETED=yes
            break;
        fi
    fi
    sleep 1
    TIMER=`expr $TIMER + 1`
done

if [ -n "$STARTUP_COMPLETED" ]; then
    echo " done (${TIMER}s)"
    exit 0
else
    echo
    echo "Warning: timeout: $LABEL not fully running" >&2
    exit 1
fi