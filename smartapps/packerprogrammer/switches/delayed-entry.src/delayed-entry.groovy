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
 *  Delayed Entry
 * 
 *	Description: I wrote this to for entry delay in the built in home monitoring app by SmartThings. It simply monitors a contact and operates a switch
 *  to the on position after the specified delay. You can then choose to have it automatically reset or stay on if you like. The idea is to not monitor the
 * 	main contact in the Smart Home Monitor, but rather the follower (usually a virtual switch) instead. That way if you open your front door, your alarm doesn't
 *  go off immediately, you can set yourself up a reminder to disarm the alarm when you open the door in away mode. That way you have sometime to keep that siren
 *  from waking the neighbors.
 *
 *  Author: Garrett Hensley (packerprogrammer)
 */

definition(
    name: "Delayed Entry",
    namespace: "packerprogrammer/switches",
    author: "Garrett Hensley",
    description: "When an input device is activated (i.e. contact open) another switch is turned on after a pre-defined delay (typically a virtual switch). It can be turned off" +
    				"automatically as well.",
    category: "My Apps",
    
    parent: "packerprogrammer/parent:Let's Switch",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)

preferences {
	section("Setup Entry Delay"){
		input "contactMaster", "capability.contactSensor", title: "Master Contact Sensor"
        input "switches", "capability.switch", title: "Switch to Control", multiple: true
        input "delayOn", "number", title: "Seconds to Delay On"
        input "autoOff", "bool", title: "Auto Off?"
        input "delayOff", "number", title: "Seconds to Delay Off"
	}
	
    
    
}

def installed() {
	subscribe(contactMaster, "contact", contactHandler)
    switches.off()
    log.debug "installed"
}

def updated() {
	unsubscribe()
	subscribe(contactMaster, "contact", contactHandler)
    switches.off()
    log.debug "refresh"
}

def contactHandler(evt) {
	log.debug "$evt.name: $evt.value"
	if (evt.value == "open") {
		log.debug "start timing"
        runIn(delayOn, operateSwitches, [overwrite:false])
	} else if (evt.value == "closed") {
    	
	}
}

def operateSwitches() {
	log.debug "turn on switches"
    switches.on()
    log.debug "autOff = " + autoOff
    if (autoOff == true) {
    	log.debug "turn off switches (automatic)"
    	switches.off([delay: (delayOff + 1) * 1000])
    }    
}