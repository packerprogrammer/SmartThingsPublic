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
	definition (name: "Simulated Door Sensor", namespace: "packerprogrammer", author: "Garrett Hensley") {
		capability "Contact Sensor"
		capability "Sensor"
                capability "Actuator"
        capability "Light"
        capability "Switch"
        command "open"
		command "close"        

		
	}

	// simulator metadata
	simulator {
		}

	// UI tile definitions
	tiles {
		standardTile("contact", "device.contact", width: 2, height: 2) {
			state "open", label: '${name}', icon: "st.contact.contact.open", backgroundColor: "#e86d13"
			state "closed", label: '${name}', icon: "st.contact.contact.closed", backgroundColor: "#00A0DC"
		}

		main "contact"
		details "contact"
	}
}
def on() {
    
    sendEvent(name: "contact", value: "open")
}

def off() {
    
    sendEvent(name: "contact", value: "closed")
}

// Parse incoming device messages to generate events
def parse(String description) {
	def resMap
	if (description.startsWith("zone")) {
		resMap = createEvent(name: "contact", value: zigbee.parseZoneStatus(description).isAlarm1Set() ? "open" : "closed")
	}
	
	log.debug "Parse returned $resMap"
	return resMap
}