/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Simulated Garage Sensor", namespace: "packerprogrammer", author: "Garrett Hensley") {
		capability "Contact Sensor"
		capability "Sensor"
        capability "Door Control"
                capability "Actuator"
        capability "Light"
        //capability "Switch"	
        preferences {
    	input name: "delay", type: "number", title: "Delay Time", description: "Delay in Seconds", range:"0..999", required: true,
        	displayDuringSetup: true
    	}
        //attribute "DoorState", "string", ["open", "closed", "OPEN DELAY", "CLOSE DELAY"]
	}

	// simulator metadata
	simulator {
		}

	// UI tile definitions
	tiles {
		standardTile("toggle", "device.door", width: 2, height: 2) {
			state("closed", label:'${name}', action:"door control.open", icon:"st.doors.garage.garage-closed", backgroundColor:"#00A0DC", nextState:"OPEN DELAY")
			state("open", label:'${name}', action:"door control.close", icon:"st.doors.garage.garage-open", backgroundColor:"#e86d13", nextState:"CLOSE DELAY")
			state("OPEN DELAY", label:'${name}', icon:"st.doors.garage.garage-opening", backgroundColor:"#e86d13")
			state("CLOSE DELAY", label:'${name}', icon:"st.doors.garage.garage-closing", backgroundColor:"#00A0DC")
			
		}
		standardTile("open", "device.door", inactiveLabel: false, decoration: "flat") {
			state "default", label:'open', action:"door control.open", icon:"st.doors.garage.garage-opening"
		}
		standardTile("close", "device.door", inactiveLabel: false, decoration: "flat") {
			state "default", label:'close', action:"door control.close", icon:"st.doors.garage.garage-closing"
		}

		main "toggle"
		details(["toggle", "open", "close"])
	}
}
def on() {
	log.trace "ON was sent"
    // if the current state is open, change the state to close delay. 
    // I don't want to do this if the close command is still timing
    def doorState = device.currentState("door").getValue()
    log.debug "current state is " + doorState
    if (doorState == "CLOSED") {
    	sendEvent(name: "door", value: "OPEN DELAY")
    }
    runIn(120, finishOpening)
}

def off() {
	log.trace "OFF was sent"
    // gets the user-defined label for this device
	def doorState = device.currentState("door").getValue()
    log.debug "current state is " + doorState
    // if the current state is open, change the state to close delay. 
    // I don't want to do this if the open command is still timing
    if (doorState == "OPEN") {
        runIn(1, finishClosing)
    }
    else {
    	runIn(10, checkState)
    }
}
def open() {
	log.trace "OPEN command sent"
	sendEvent(name: "door", value: "OPEN DELAY")
    runIn(5, finishOpening)
}

def close() {
	log.debug "CLOSE command sent"
    sendEvent(name: "door", value: "CLOSE DELAY")
	runIn(5, finishClosing)
}
def parse(String description) {
	log.trace "parse($description)"
}

def finishOpening() {
    sendEvent(name: "door", value: "OPEN")
    sendEvent(name: "contact", value: "open")
    log.trace "Contact Open"
}

def finishClosing() {
    sendEvent(name: "door", value: "CLOSED")
    sendEvent(name: "contact", value: "closed")
    log.trace "Contact Closed"
}
def checkState() {
	log.debug "checking state"
	runIn(1,off)
}